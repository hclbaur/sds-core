package be.baur.sds.validation;

import java.util.Base64;
import java.util.Iterator;
import java.util.regex.Pattern;

import be.baur.sda.ComplexNode;
import be.baur.sda.Node;
import be.baur.sda.SimpleNode;
import be.baur.sds.ComplexType;
import be.baur.sds.ComponentType;
import be.baur.sds.ModelGroup;
import be.baur.sds.Schema;
import be.baur.sds.SimpleType;
import be.baur.sds.common.Attribute;
import be.baur.sds.common.Date;
import be.baur.sds.common.DateTime;
import be.baur.sds.common.Interval;
import be.baur.sds.common.NaturalInterval;
import be.baur.sds.content.AnyType;
import be.baur.sds.content.BinaryType;
import be.baur.sds.content.BooleanType;
import be.baur.sds.content.RangedType;
import be.baur.sds.content.StringType;
import be.baur.sds.model.ChoiceGroup;
import be.baur.sds.model.Group;
import be.baur.sds.serialization.SchemaException;

/**
 * This class provides methods to check if an SDA document is both well-formed
 * and valid, e.g. if it conforms to a particular schema (SDS).
 */
public class Validator {

	/**
	 * This method accepts an SDA document {@link Node} and validates it against an
	 * SDS {@link Schema} definition. The returned {@link ErrorList} is empty if the
	 * document is valid, or contains one or more validation errors otherwise.
	 * 
	 * @throws SchemaException
	 */
	public static ErrorList validate(Node document, Schema schema) throws SchemaException {

		ErrorList errors = new ErrorList();
		
		/*
		 * Find the global component to validate the document against. If the schema has
		 * no designated root type, we look for a global type matching the document root.
		 */
		ComponentType component;
		String type = schema.getGlobalType();

		if (type != null) {
			component = (ComponentType) schema.get(type).get(1);
			if (component == null)  // should never happen for a proper schema
				throw new SchemaException(schema, SchemaException.AttributeInvalid, 
					Attribute.TYPE.tag, type, "no such global type");
		} else {
			component = (ComponentType) schema.get(document.name).get(1);
			if (component == null) {
				errors.add(new Error(document, Error.GLOBAL_TYPE_NOT_FOUND, document.name));
				return errors; // no point in further validation
			}
		}
		
		// this is by definition a match, but there may be errors of course
		matchNode(document, component, errors);
		return errors;
	}

	
	/**
	 * Validation of a node roughly works like this: we first try to match the node
	 * against its corresponding schema component by comparing the name tags. If
	 * there is no match, we return false and it is up to the caller of this method
	 * to decide if that constitutes a validation error. If there is a match, we
	 * assert that the node content is valid, or add an error to the list otherwise.
	 * This does not apply "any" type components; those are never validated.
	 */
	private static boolean matchNode(Node node, ComponentType component, ErrorList errors) {
		
		String compname = component.getName();
		boolean namesmatch = node.name.equals(compname); // does the node match the expected component?
		
		if (component instanceof AnyType) {
			// if the name is no match for an explicitly named "any" type, we return false
			if (! namesmatch && ((AnyType) component).isNamed()) return false;
			return true;  // otherwise we return true without further validation
		}
		if (! namesmatch) return false; // specific type; if names differ, there is no match
		
		if (node instanceof SimpleNode) {
			
			if (! (component instanceof SimpleType)) {  //  we were expecting complex content
				errors.add(new Error(node, Error.EXPECTING_NODE_TYPE, compname, "complex"));
				return true;
			}
			errors.add(validateSimpleNode((SimpleNode) node, (SimpleType) component));
			return true;
		}
		
		if (! (component instanceof ComplexType)) {  // we were expecting simple content
			errors.add(new Error(node, Error.EXPECTING_NODE_TYPE, compname, "simple"));
			return true;
		}
		errors.add(validateComplexNode((ComplexNode) node, (ComplexType) component, errors));
		return true;
	}


	/**
	 * Validating a simple node means we have to check if its value is appropriate
	 * with respect to the components content type, and any facets that may apply.
	 * This method returns a validation error, or null otherwise.
	 */
	private static Error validateSimpleNode(SimpleNode node, SimpleType component) {
		
		// empty values are allowed only for nullable types.
		if (node.value.isEmpty() && ! component.isNullable())
			return new Error(node, Error.EMPTY_VALUE_NOT_ALLOWED, node.name);
		
		if (component instanceof StringType) {
			Error error = validateStringValue(node, (StringType) component);
			if (error != null) return error;
		}
		
		if (component instanceof RangedType) {
			Error error = validateRangedValue(node, (RangedType<?>) component);
			if (error != null) return error;
		}
		
		if (component instanceof BooleanType) {
			if (! (node.value.equals(BooleanType.TRUE) || node.value.equals(BooleanType.FALSE)) )
				return new Error(node, Error.INVALID_BOOLEAN_VALUE, node.value);
		}
			
		Pattern pattern = component.getPattern();
		if (pattern != null && ! pattern.matcher(node.value).matches())
			return new Error(node, Error.VALUE_DOES_NOT_MATCH, node.value, component.getPatternExpr());
		
		return null;
	}

	/**
	 * Any string is by definition a valid string representation of a string type.
	 * However, this may not be true for a binary string type. Also, we check the
	 * length (in characters for a string and bytes for a binary).
	 */
	private static Error validateStringValue(SimpleNode node, StringType component) {
		
		int length;

		// Not the most efficient, but the easiest way to validate a binary string
		// is to decode it, and we need to determine its length anyway.
		if (component instanceof BinaryType) {
			try {
				length = Base64.getDecoder().decode(node.value).length;
			} catch (IllegalArgumentException e) {
				return new Error(node, Error.INVALID_BINARY_VALUE, node.name, e.getMessage());
			}
		}
		else length = node.value.length();   // otherwise it is a regular string
		
		// Check if the length is within the acceptable range.
		NaturalInterval range = component.getLength();
		if (range != null) {
			int contains = range.contains(length);
			String val = node.value.length() > 32 ? node.value.substring(0,32) + "..." : node.value;
			if (contains < 0) 
				return new Error(node, Error.LENGTH_SUBCEEDS_MIN, val, length, range.lower);
			if (contains > 0) 
				return new Error(node, Error.LENGTH_EXCEEDS_MAX, val, length, range.upper);
		}
		return null;
	}
	
	/**
	 * We assert that the node value is a valid string representation of this
	 * content type by creating an instance, and check whether it is in range.
	 */
	private static Error validateRangedValue(SimpleNode node, RangedType<?> component) {

		Comparable<?> value = null;
		try {
			switch (component.getContentType()) {
				case INTEGER  : value = new Integer(node.value); break;
				case DECIMAL  : value = new Double(node.value); break;
				case DATETIME : value = new DateTime(node.value); break;
				case DATE     : value = new Date(node.value); break;
				default: // we will never get here, unless we forgot to implement something
					throw new RuntimeException("validation of '" + component.getContentType() + "' not implemented!");
			}
		} catch (Exception e) {
			return new Error(node, Error.INVALID_VALUE_FOR_TYPE, node.value, component.getContentType(), e.getMessage());
		}
		
		Interval<?> range = component.getRange(); 
		if (range != null) {
			int contains = range.contains(value);
			if (contains < 0) 
				return new Error(node, Error.VALUE_SUBCEEDS_MIN, value, range.lower);
			if (contains > 0) 
				return new Error(node, Error.VALUE_EXCEEDS_MAX, value, range.upper);
		}
		return null;
	}
	
	
	/**
	 * Validating a complex node against a complex type implies validating all child
	 * nodes against the children of the complex type. Which is easier said than
	 * done, because there is more than one way to do this, depending on how smart
	 * we want the validation process to be.
	 * 
	 * Here we iterate through all child components and try to match each one to as
	 * many child nodes as possible within the constraints set by the multiplicity
	 * of the current child component. An error is returned immediately if there is
	 * no match for a mandatory child component.
	 * 
	 * But mismatch is not always an error. If a component defines optional content,
	 * the current child node may not be a match, but the next component might match
	 * it, so we keep on trying to match until we run out of components.
	 * 
	 * If we run out of components before all nodes have been matched, that implies
	 * a validation error. If we run out of nodes while there is still mandatory
	 * content expected, that is also a validation error.
	 */
	private static Error validateComplexNode(ComplexNode node, ComplexType component, ErrorList errors) {
		
		Iterator<Node> inode = node.get().iterator(); // iterator for child nodes
		Node childnode = inode.hasNext() ? inode.next() : null; // first child node (or none)
		
		//System.out.println("validateComplexNode: matching children of " + node.getName());
		Iterator<Node> icomp = component.get().iterator();
		while (icomp.hasNext()) {
			
			ComponentType childcomp = (ComponentType) icomp.next(); 
			int curmatches = 0;  // number of matches so far for this child component
			int maxmatches = childcomp.maxOccurs(); // maximum number of matches allowed
			
			// Start matching the current child component as many times as possible
			while (curmatches < maxmatches) {
			
				if (childnode == null) { // oops, we have run out of nodes
					
					if (curmatches < childcomp.minOccurs())  // but we expect at least one (more) node
						if (! (childcomp instanceof ModelGroup || childcomp instanceof AnyType) )
							return new Error(node, Error.MANDATORY_NODE_EXPECTED, childcomp.getName(), node.name);
						else return new Error(node, Error.CONTENT_MISSING_AT_END, node.name);

					break; // or if it is optional, we break out and match the next child component
				}

				boolean match;
				//System.out.println("validateComplexNode: matching " + ((childnode instanceof SimpleNode) ? childnode : childnode.name + "{}") + " to " + childcomp.getName());
				if (childcomp instanceof ModelGroup)
					match = matchModelGroup(inode, childnode, (ModelGroup) childcomp, errors);
				else match = matchNode(childnode, childcomp, errors);
				
				//System.out.println("validateComplexNode: node " + childnode.name + (match ? " == " : " <> ") + "component " + childcomp.getName());
				if (match) { // count match and get the next node (or none) to match against this component
					childnode = inode.hasNext() ? inode.next() : null;
					++curmatches; continue;
				}
				
				// The node is no match - if it is mandatory, we return an error
				if (curmatches < childcomp.minOccurs()) {
					if (! (childcomp instanceof ModelGroup || childcomp instanceof AnyType) )
						return new Error(childnode, Error.EXPECTING_NODE_BUT_GOT, childcomp.getName(), childnode.name);
					return new Error(childnode, Error.CONTENT_MISSING_BEFORE, childnode.name);
				}
				break; // and if it is optional, we match the next component to this node (if any)
			}	
		}

		//System.out.println("Matched all child components of " + component.getName());
		if (childnode != null) // if we have an unmatched node, that is a validation error
			errors.add(new Error(childnode, Error.NODE_NOT_EXPECTED_IN, childnode.getName(), childnode.getParent().name));
		
		// and each remaining node is also a validation error
		inode.forEachRemaining( n -> { errors.add(new Error(n, Error.NODE_NOT_EXPECTED_IN, n.getName(), n.getParent().name)); });
		
		return null;
	}


	/**
	 * Model groups are handled different from regular nodes. This method is the
	 * generic entry point to match all model group groups, and may be called
	 * recursively to match nodes against nested model groups. When matching a model
	 * group we may consider more than one node, which is why this method accepts an
	 * iterator to access subsequent nodes.
	 */
	private static boolean matchModelGroup(Iterator<Node> inode, Node node, ModelGroup group, ErrorList errors) {

		if (group instanceof ChoiceGroup) 
			return matchChoiceGroup(inode, node, (ChoiceGroup) group, errors);
		if (group instanceof Group) 
			return matchGroup(inode, node, (Group) group, errors);
		 // Should never happen, unless we forgot a model group (like the unordered group...)
		throw new RuntimeException("'" + group.getName() + "' not implemented!");
	}


	/**
	 * When matching a node to a choice group, we attempt to match it against each
	 * component within the group, until we have a match or reach the end without
	 * one. Or, in other words, a choice group is considered an overall match if one
	 * of its child components is a match. Note that we may encounter other model
	 * groups within the choice group.
	 */
	private static boolean matchChoiceGroup(Iterator<Node> inode, Node node, ChoiceGroup choice, ErrorList errors) {

		for (Node child : choice.get()) {
			
			ComponentType childcomp = (ComponentType) child; boolean match;
			//System.out.println("matchChoice: matching " + ((node instanceof SimpleNode) ? node : node.name + "{}") + " to " + childcomp.getName());
			if (childcomp instanceof ModelGroup)
				match = matchModelGroup(inode, node, (ModelGroup) childcomp, errors);
			else match = matchNode(node, childcomp, errors);
			//System.out.println("matchChoice: node " + curnode.name + (match ? " == " : " <> ") + "component " + childcomp.getName());
			if (match) return true;
		}
		return false;
	}


	/**
	 * Matching nodes to a model group is similar to validating a complex type, in
	 * the sense that we are matching several nodes to the children of a component,
	 * using the same overall iteration logic, but with a few differences.<br>
	 * A group is considered an overall match (or "invoked") once we encounter a
	 * matching child component, in which case all remaining child components are
	 * matched until there is a validation error or we run out of nodes or
	 * components.<br>
	 * Conversely, it is considered an overall no-match (not "invoked") the moment
	 * we encounter a non-matching mandatory child component, in which case we
	 * abandon the method and return an overall no-match (false).<br>
	 * Otherwise, we just keep on matching. If we run out of nodes while there is
	 * still mandatory content expected, that is a validation error. If we run out
	 * components while we still have nodes, that just marks the end of the group,
	 * and we return, causing the remaining nodes to be matched against the
	 * context of the parent component.
	 */
	private static boolean matchGroup(Iterator<Node> inode, Node node, Group group, ErrorList errors) {

		boolean groupmatch = false; // overall match for this group, initially false
		Node parent = node.getParent(); // save the parent of the node(s)
		
		Iterator<Node> icomp = group.get().iterator();
		while (icomp.hasNext()) {
			
			ComponentType childcomp = (ComponentType) icomp.next();
			int curmatches = 0; // number of matches so far for this child component
			int maxmatches = childcomp.maxOccurs();  // maximum number of matches allowed
			
			// Start matching the current child component as many times as possible
			while (curmatches < maxmatches) {
			
				if (node == null) {  // oops, we have run out of nodes
					
					if (curmatches < childcomp.minOccurs()) { // but we expect at least one (more) node
						if (groupmatch) {
							// if the group was invoked; a missing node implies a validation error
							if (! (childcomp instanceof ModelGroup) )
								errors.add(new Error(parent, Error.MANDATORY_NODE_EXPECTED, childcomp.getName(), parent.name));
							else errors.add(new Error(parent, Error.CONTENT_MISSING_AT_END, parent.name));
						}
						return groupmatch; // in either case we return (true or false)
					}
					break; // or if it is optional, we break out and match the next child component
				}
				
				boolean match;
				//System.out.println("matchGroup: matching " + ((node instanceof SimpleNode) ? node : node.name + "{}") + " to " + childcomp.getName());
				if (childcomp instanceof ModelGroup)
					match = matchModelGroup(inode, node, (ModelGroup) childcomp, errors);
				else match = matchNode(node, childcomp, errors);
				
				//System.out.println("node " + node.name + (match ? " == " : " <> ") + "component " + childcomp.getName());
				if (match) { // count match and get the next node to match against this component (if there is one)
					if (icomp.hasNext()) node = inode.hasNext() ? inode.next() : null;
					groupmatch = true; ++curmatches; continue;
				}
				
				// The node is no match - if it is mandatory and the group is already invoked, 
				// add an error and return an overall match. Otherwise, return an overall no-match.
				if (curmatches < childcomp.minOccurs()) {
					if (groupmatch) {
						if (! (childcomp instanceof ModelGroup) )
							errors.add(new Error(node, Error.EXPECTING_NODE_BUT_GOT, childcomp.getName(), node.name));
						else errors.add(new Error(node, Error.CONTENT_MISSING_BEFORE, node.name));
					}
					return groupmatch; // in either case we return (true or false)
				}
				break; // and if it is optional, we match the next component to this node (if any)
			}	
		}
		
		// We have run out of child components; return the overall match result
		// System.out.println("Matched all children of " + group.getName());
		return groupmatch;
	}
	
}

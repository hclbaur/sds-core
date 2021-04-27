package be.baur.sds.validation;

import java.util.Base64;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import be.baur.sda.ComplexNode;
import be.baur.sda.Node;
import be.baur.sda.NodeSet;
import be.baur.sda.SimpleNode;
import be.baur.sds.ComplexType;
import be.baur.sds.ComponentType;
import be.baur.sds.Schema;
import be.baur.sds.SimpleType;
import be.baur.sds.common.Date;
import be.baur.sds.common.DateTime;
import be.baur.sds.common.Interval;
import be.baur.sds.common.NaturalInterval;
import be.baur.sds.content.AbstractStringType;
import be.baur.sds.content.AnyType;
import be.baur.sds.content.BinaryType;
import be.baur.sds.content.BooleanType;
import be.baur.sds.content.RangedType;
import be.baur.sds.model.ChoiceGroup;
import be.baur.sds.model.Group;
import be.baur.sds.model.AbstractGroup;

/**
 * The default {@link Validator} to validate an SDA document against a schema.
 */
public final class SDAValidator implements Validator {

	private static final String GLOBAL_TYPE_NOT_FOUND = "global type '%s' not found";
	private static final String EXPECTING_NODE_OF_TYPE = "expecting '%s' to be a %s type";
	private static final String CONTENT_MISSING_AT_END = "content missing at end of '%s'; expecting %s";
	private static final String GOT_NODE_BUT_EXPECTED = "got '%s', but expected %s instead";
	private static final String NODE_NOT_EXPECTED_IN = "'%s' not expected in '%s'";
	
	private static final String INVALID_VALUE_FOR_TYPE = "value '%s' is invalid for type %s: %s";
	private static final String EMPTY_VALUE_NOT_ALLOWED = "empty value not allowed; '%s' is not nullable";
	private static final String VALUE_DOES_NOT_MATCH= "value '%s' does not match pattern '%s'";
	private static final String INVALID_BINARY_VALUE = "'%s' has an invalid binary value: %s";
	private static final String INVALID_BOOLEAN_VALUE = "value '%s' is not a valid boolean";
	private static final String LENGTH_SUBCEEDS_MIN = "value '%s' has length %d but %d is the minimum";
	private static final String LENGTH_EXCEEDS_MAX = "value '%s' has length %d but %d is the maximum";
	private static final String VALUE_SUBCEEDS_MIN = "value '%s' subceeds the minimum of %s";
	private static final String VALUE_EXCEEDS_MAX = "value '%s' exceeds the maximum of %s";
	private static final String VALUE_NOT_INCLUSIVE = "value '%s' is not inclusive";
	
	
	public ErrorList validate(Node document, Schema schema, String type) {

		ComponentType component = null;
		
		if (type == null || type.isEmpty()) {
			
			if (schema.nodes.size() == 1)
				component = (ComponentType) schema.nodes.get(1);
			else if (schema.getRootType() != null) 
				component = (ComponentType) schema.nodes.get(schema.getRootType()).get(1);
		}	
		else component = (ComponentType) schema.nodes.get(type).get(1);
			
		if (component == null)
			throw new IllegalArgumentException(String.format(GLOBAL_TYPE_NOT_FOUND, type));
		
		// Recursively validate the entire document.
		ErrorList errors = new ErrorList();	
		if (! matchNode(document, component, errors))
			errors.add(new Error(document, GOT_NODE_BUT_EXPECTED, document.getName(), quoteName((Node) component)));
		
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
		
		String nodename = node.getName();
		boolean namesmatch = nodename.equals(component.getName());
		
		if (component instanceof AnyType) {
			// if the name is no match for an explicitly named "any" type, we return false
			if (! namesmatch && ((AnyType) component).isNamed()) return false;
			return true;  // otherwise we return true without further validation
		}
		if (! namesmatch) return false; // specific type; if names differ, there is no match
		
		if (node instanceof SimpleNode) {
			
			if (! (component instanceof SimpleType)) {  //  we were expecting complex content
				errors.add(new Error(node, EXPECTING_NODE_OF_TYPE, nodename, "complex"));
				return true;
			}
			errors.add(validateSimpleNode((SimpleNode) node, (SimpleType) component));
			return true;
		}
		
		if (! (component instanceof ComplexType)) {  // we were expecting simple content
			errors.add(new Error(node, EXPECTING_NODE_OF_TYPE, nodename, "simple"));
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
		if (node.getValue().isEmpty() && ! component.isNullable())
			return new Error(node, EMPTY_VALUE_NOT_ALLOWED, node.getName());
		
		if (component instanceof AbstractStringType) {
			Error error = validateStringValue(node, (AbstractStringType) component);
			if (error != null) return error;
		}
		
		if (component instanceof RangedType) {
			Error error = validateRangedValue(node, (RangedType<?>) component);
			if (error != null) return error;
		}
		
		if (component instanceof BooleanType) {
			if (! (node.getValue().equals(BooleanType.TRUE) || node.getValue().equals(BooleanType.FALSE)) )
				return new Error(node, INVALID_BOOLEAN_VALUE, node.getValue());
		}
			
		Pattern pattern = component.getPattern();
		if (pattern != null && ! pattern.matcher(node.getValue()).matches())
			return new Error(node, VALUE_DOES_NOT_MATCH, node.getValue(), component.getPatternExpr());
		
		return null;
	}

	/**
	 * Any string is by definition a valid string representation of a string type.
	 * However, this may not be true for a binary string type. Also, we check the
	 * length (in characters for a string and bytes for a binary).
	 */
	private static Error validateStringValue(SimpleNode node, AbstractStringType component) {
		
		int length;

		// Not the most efficient, but the easiest way to validate a binary string
		// is to decode it, and we need to determine its length anyway.
		if (component instanceof BinaryType) {
			try {
				length = Base64.getDecoder().decode(node.getValue()).length;
			} catch (IllegalArgumentException e) {
				return new Error(node, INVALID_BINARY_VALUE, node.getName(), e.getMessage());
			}
		}
		else length = node.getValue().length();   // otherwise it is a regular string
		
		// Check if the length is within the acceptable range.
		NaturalInterval range = component.getLength();
		if (range != null) {
			String val = node.getValue().length() > 32 ? node.getValue().substring(0,32) + "..." : node.getValue();
			if (length < component.minLength()) 
				return new Error(node, LENGTH_SUBCEEDS_MIN, val, length, range.min);
			if (length > component.maxLength()) 
				return new Error(node, LENGTH_EXCEEDS_MAX, val, length, range.max);
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
				case INTEGER  : value = new Integer(node.getValue()); break;
				case DECIMAL  : value = new Double(node.getValue()); break;
				case DATETIME : value = new DateTime(node.getValue()); break;
				case DATE     : value = new Date(node.getValue()); break;
				default: // we will never get here, unless we forgot to implement something
					throw new RuntimeException("validation of '" + component.getContentType() + "' not implemented!");
			}
		} catch (Exception e) {
			return new Error(node, INVALID_VALUE_FOR_TYPE, node.getValue(), component.getContentType(), e.getMessage());
		}
		
		Interval<?> range = component.getRange(); 
		if (range != null) {
			int contains = range.contains(value);
			if (contains < 0) {
				if (value.equals(range.min)) 
					return new Error(node, VALUE_NOT_INCLUSIVE, value);
				return new Error(node, VALUE_SUBCEEDS_MIN, value, range.min);
			}
			if (contains > 0) {
				if (value.equals(range.max)) 
					return new Error(node, VALUE_NOT_INCLUSIVE, value);
				return new Error(node, VALUE_EXCEEDS_MAX, value, range.max);
			}
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
		
		Iterator<Node> inode = node.nodes.iterator(); // iterator for child nodes
		Node childnode = inode.hasNext() ? inode.next() : null; // first child node (or none)
		
		//System.out.println("validateComplexNode: matching children of " + node.getName());
		Iterator<Node> icomp = component.nodes.iterator();
		while (icomp.hasNext()) {
			
			ComponentType childcomp = (ComponentType) icomp.next(); 
			int curmatches = 0;  // number of matches so far for this child component
			int maxmatches = childcomp.maxOccurs(); // maximum number of matches allowed
			
			// Start matching the current child component as many times as possible
			while (curmatches < maxmatches) {
			
				if (childnode == null) { // oops, we have run out of nodes
					
					if (curmatches < childcomp.minOccurs()) // but if we expect another node
						return missingNodeError(node, childcomp); // an error is returned

					break; // otherwise we break out and match the next child component
				}

				boolean match;
				//System.out.println("validateComplexNode: matching " + ((childnode instanceof SimpleNode) ? childnode : childnode.getName() + "{}") + " to " + childcomp.getName());
				if (childcomp instanceof AbstractGroup)
					match = matchModelGroup(inode, childnode, (AbstractGroup) childcomp, errors);
				else match = matchNode(childnode, childcomp, errors);
				
				//System.out.println("validateComplexNode: node " + childnode.name + (match ? " == " : " <> ") + "component " + childcomp.getName());
				if (match) { // count match and get the next node (or none) to match against this component
					childnode = inode.hasNext() ? inode.next() : null;
					++curmatches; continue;
				}
				
				// The child node is no match - so if the child component is mandatory
				if (curmatches < childcomp.minOccurs()) // return an error
					return unexpectedNodeError(childnode, childcomp);

				break; // otherwise, we match the next component to this node (if any)
			}	
		}

		//System.out.println("Matched all child components of " + component.getName());
		if (childnode != null) // if we have an unmatched node, that is a validation error
			errors.add(new Error(childnode, 
				NODE_NOT_EXPECTED_IN, childnode.getName(), childnode.getParent().getName()));
		
		// and each remaining node is also a validation error
		inode.forEachRemaining( n -> { 
			errors.add(new Error(n, NODE_NOT_EXPECTED_IN, n.getName(), n.getParent().getName())); 
		});
		
		return null;
	}


	/**
	 * Model groups are handled different from regular nodes. This method is the
	 * generic entry point to match all model group groups, and may be called
	 * recursively to match nodes against nested model groups. When matching a model
	 * group we may consider more than one node, which is why this method accepts an
	 * iterator to access subsequent nodes.
	 */
	private static boolean matchModelGroup(Iterator<Node> inode, Node node, AbstractGroup group, ErrorList errors) {

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

		for (Node child : choice.nodes) {
			
			ComponentType childcomp = (ComponentType) child; boolean match;
			//System.out.println("matchChoice: matching " + ((node instanceof SimpleNode) ? node : node.name + "{}") + " to " + childcomp.getName());
			if (childcomp instanceof AbstractGroup)
				match = matchModelGroup(inode, node, (AbstractGroup) childcomp, errors);
			else match = matchNode(node, childcomp, errors);
			//System.out.println("matchChoice: node " + curnode.name + (match ? " == " : " <> ") + "component " + childcomp.getName());
			if (match) return true;
		}
		return false;
	}


	/**
	 * Matching nodes to a group is similar to validating a complex type, in the
	 * sense that we are matching several nodes to the children of a component,
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
	 * and we return, causing the remaining nodes to be matched against the context
	 * of the parent component.
	 */
	private static boolean matchGroup(Iterator<Node> inode, Node node, Group group, ErrorList errors) {

		boolean groupmatch = false; // overall match for this group, initially false
		Node parent = node.getParent(); // save the parent of the node(s)
		
		Iterator<Node> icomp = group.nodes.iterator();
		while (icomp.hasNext()) {
			
			ComponentType childcomp = (ComponentType) icomp.next();
			int curmatches = 0; // number of matches so far for this child component
			int maxmatches = childcomp.maxOccurs();  // maximum number of matches allowed
			
			// Start matching the current child component as many times as possible
			while (curmatches < maxmatches) {
			
				if (node == null) {  // oops, we have run out of nodes
					
					if (curmatches < childcomp.minOccurs()) { // but if we expect another node
						
						if (groupmatch) // and the group was invoked, we add a validation error
							errors.add(missingNodeError(parent, childcomp));
						return groupmatch; // in either case we return (true or false)
					}
					break; // otherwise we break out and match the next child component
				}
				
				boolean match;
				//System.out.println("matchGroup: matching " + ((node instanceof SimpleNode) ? node : node.name + "{}") + " to " + childcomp.getName());
				if (childcomp instanceof AbstractGroup)
					match = matchModelGroup(inode, node, (AbstractGroup) childcomp, errors);
				else match = matchNode(node, childcomp, errors);
				
				//System.out.println("node " + node.name + (match ? " == " : " <> ") + "component " + childcomp.getName());
				if (match) { // count match and get the next node to match against this component (if there is one)
					if (icomp.hasNext()) node = inode.hasNext() ? inode.next() : null;
					groupmatch = true; ++curmatches; continue;
				}
				
				// The child node is no match - so if the child component is mandatory
				if (curmatches < childcomp.minOccurs()) {
					
					if (groupmatch) // and the group was invoked, add a validation error
						errors.add(unexpectedNodeError(node, childcomp));
					return groupmatch; // in either case we return (true or false)
				}
				break; // otherwise, we match the next component to this node (if any)
			}	
		}
		
		// We have run out of child components; return the overall match result
		// System.out.println("Matched all children of " + group.getName());
		return groupmatch;
	}
	
	
	//
	// Convenience methods below this line.
	//
	
	/** This returns an error specifying a missing node at the end of a context node. */
	private static Error missingNodeError(Node context, ComponentType comp) {
		if (comp instanceof AbstractGroup)
			return new Error(context, CONTENT_MISSING_AT_END, context.getName(), quoteNames(expectedNodes(comp)));
		else return new Error(context, CONTENT_MISSING_AT_END, context.getName(), quoteName((Node)comp));
	}
	
	
	/** This returns an error specifying an unexpected node. */
	private static Error unexpectedNodeError(Node node, ComponentType comp) {
		if (comp instanceof AbstractGroup)
			return new Error(node, GOT_NODE_BUT_EXPECTED, node.getName(), quoteNames(expectedNodes(comp)));
		else return new Error(node, GOT_NODE_BUT_EXPECTED, node.getName(), quoteName((Node) comp));
	}
	
	
	/**
	 * When we expect content to match a complex or simple component, it is obvious
	 * what node is expected. But if the component is a <em>model group<em>, things
	 * are a bit more complicated, depending on the group type, and the level of
	 * nesting (model groups within model groups). This recursive method returns a
	 * set of component types that may match the current content.
	 */
	private static NodeSet expectedNodes(ComponentType comp) {
		
		// if not a group, just return the component itself, ending the recursion
		if (! (comp instanceof AbstractGroup)) return NodeSet.from((Node)comp);

		AbstractGroup group = (AbstractGroup) comp;
		
		// for a choice, we return all possible components, recursively flat-mapped
		if (group instanceof ChoiceGroup) {
			NodeSet result = group.nodes.stream()
				.flatMap(n -> expectedNodes((ComponentType)n).stream()).collect(Collectors.toCollection(NodeSet::new));
			return result;
		}
		
		// for a group, we return all child components up to and including the first mandatory one
		if (group instanceof Group) {
			NodeSet result = new NodeSet();
			for (Node n : group.nodes) {
				for (Node e : expectedNodes((ComponentType)n)) result.add(e); // is this is correct?
				if (((ComponentType) n).minOccurs() > 0) break;
			}
			return result;
		}
		
		 // should never reach this, unless we forgot a model group (like the unordered group...)
		throw new RuntimeException("'" + group.getName() + "' not implemented!");
	}
	
	
	/** Returns list of quoted node names in the format: 'a', .. 'b' or 'c'. */
	private static String quoteNames(NodeSet set) {
		
		String result = set.stream()
			.map(n -> quoteName(n)).collect(Collectors.joining(","));
		int i = result.lastIndexOf(','); 
		return (i == -1) ? result : result.substring(0, i) + " or " + result.substring(i+1);	
	}
	
	
	/** Returns the node name in single quotes, or "any node" for an unnamed {@link AnyType}. */
	private static String quoteName(Node node) {
		
		return (node instanceof AnyType && !((AnyType) node).isNamed()) 
			? "any node" : "'" + node.getName() + "'";
	}
}

package be.baur.sds.validation;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import be.baur.sda.Node;
import be.baur.sda.DataNode;
import be.baur.sds.Component;
import be.baur.sds.DataType;
import be.baur.sds.NodeType;
import be.baur.sds.Schema;
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
import be.baur.sds.model.ModelGroup;
import be.baur.sds.model.SequenceGroup;
import be.baur.sds.model.UnorderedGroup;

/**
 * This is the default validator; used to validate an SDA document against a
 * {@code Schema}. For example, when a schema (in SDS notation) looks like
 * 
 * <pre>
 * schema { 
 *    node "greeting" { 
 *       node "message" { type "string" } 
 *    }
 * }
 * </pre>
 * 
 * and the validator is presented with the following SDA input,
 * 
 * <pre>
 * greeting { 
 *    text "hello world" 
 * }
 * </pre>
 * 
 * an error is returned, reporting an unexpected 'text' node in 'greeting'.
 * <p>
 * A validator is obtained from a schema, by calling {@link Schema#newValidator()}.
 * @see Schema
 */
public abstract class Validator {

	private static final String NO_DEFAULT_TYPE_FOUND = "no default type found";
	private static final String GLOBAL_TYPE_NOT_FOUND = "global type '%s' not found";
	private static final String CONTENT_EXPECTED_FOR_NODE = "%s is expected for node '%s'";
	private static final String CONTENT_MISSING_AT_END = "content missing at end of '%s'; expected %s";
	private static final String GOT_NODE_BUT_EXPECTED = "got '%s', but %s was expected";
	private static final String NODE_NOT_EXPECTED_IN = "'%s' was not expected in '%s'";
	
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


	/**
	 * Returns the {@code Schema} associated with this validator.
	 * 
	 * @return a schema, not null
	 * @see Schema
	 */
	protected abstract Schema getSchema();

	
	/**
	 * Validates an SDA document (node). This method validates a node (and its child
	 * nodes) against a global type within the schema. The global type name may be
	 * null or empty if the schema has a default type. An exception is thrown if no
	 * appropriate type can be found.
	 * 
	 * @param node       the node to be validated
	 * @param globaltype a global type name, may be null
	 * @return an error list, empty if no validation errors were found
	 * @throws IllegalArgumentException - if the type is not found
	 */
	public ErrorList validate(DataNode node, String globalType) {

		final Schema schema = getSchema();  // the schema we are associated with
		NodeType type = null;  // the type to validate the node against
		
		if (globalType == null || globalType.isEmpty()) {
			
			final String defaultType = schema.getDefaultType();
			if (defaultType != null) // if we have a default type, we take that
				type = schema.get(t -> ((NodeType) t).getTypeName().equals(defaultType));
			
			// otherwise if there is only one (node type), that must be the one
			else if (schema.nodes().size() == 1)
				type = (NodeType) schema.nodes().get(0);
			
			else // otherwise we do not know nor guess
				throw new IllegalArgumentException(String.format(NO_DEFAULT_TYPE_FOUND));
		}	
		else // get the specified type (or null if not found)
			type = schema.get(t -> ((NodeType) t).getTypeName().equals(globalType));
			
		if (type == null)
			throw new IllegalArgumentException(String.format(GLOBAL_TYPE_NOT_FOUND, globalType));
		
		// we have a type, now recursively validate the entire document
		ErrorList errors = new ErrorList();	
		if (! matchNodeType(node, type, errors))
			errors.add(new Error(node, GOT_NODE_BUT_EXPECTED, node.getName(), quoteName(type)));
		
		return errors;
	}

	
	/**
	 * Validation of a node roughly works like this: we try to match the node
	 * against its corresponding schema component by comparing the name tags. If
	 * there is no match, we return false and it is up to the caller of this method
	 * to decide if that constitutes a validation error. After all, the current
	 * component could be optional, and node might match the next component.<br>
	 * If there is a match, we assert that the node content is valid, or add an
	 * error to the list otherwise. This does not apply to "any" type components.
	 */
	private static boolean matchNodeType(DataNode node, NodeType type, ErrorList errors) {
		
		String nodename = node.getName();
		boolean namesmatch = nodename.equals(type.getTypeName());
		
		if (type instanceof AnyType) {
			// if the name is no match for an explicitly named "any" type, we return false
			if (! namesmatch && ((AnyType) type).isNamed()) return false;
			return true;  // otherwise we return true without further validation
		}
		if (! namesmatch) return false; // specific type; if names differ, there is no match
		
		// we have a match, now proceed to check the content
		
		if (! (type instanceof DataType)) { // we are expecting complex content ONLY
			
			if (node.isLeaf() || ! node.getValue().isEmpty())  // but we got something with a value
				errors.add(new Error(node, CONTENT_EXPECTED_FOR_NODE, "only complex content", nodename));

			if (! node.isLeaf()) // validate complex content if we have it
				errors.add(validateComplexContent(node, type, errors));

			return true;
		}
		
		// we are expecting simple content
		if (! node.isLeaf()) {
			if (type.isLeaf()) // no complex content is expected
				errors.add(new Error(node, CONTENT_EXPECTED_FOR_NODE, "no complex content", nodename));
			else // validate complex content if we have it
				errors.add(validateComplexContent(node, type, errors));
		} 
		else if (! type.isLeaf()) // report missing complex content
			errors.add(new Error(node, CONTENT_EXPECTED_FOR_NODE, "complex content", nodename));
	
		// validate the simple content we were expecting
		errors.add(validateSimpleContent(node, (DataType) type));
		return true;
	}


	/**
	 * Validating simple node content means we have to check if the node value is
	 * appropriate with respect to this components content type, and any facets that
	 * may apply. This method returns a validation error, or null otherwise.
	 */
	private static Error validateSimpleContent(DataNode node, DataType type) {
		
		String value = node.getValue(); // need this a few times times
		
		// empty values are allowed only for null-able types.
		if (value.isEmpty() && ! type.isNullable())
			return new Error(node, EMPTY_VALUE_NOT_ALLOWED, node.getName());
		
		if (type instanceof AbstractStringType) {
			Error error = validateStringValue(node, (AbstractStringType) type);
			if (error != null) return error;
		}
		
		if (type instanceof RangedType) {
			Error error = validateRangedValue(node, (RangedType<?>) type);
			if (error != null) return error;
		}
		
		if (type instanceof BooleanType) {
			if (! (value.equals(BooleanType.TRUE) || value.equals(BooleanType.FALSE)) )
				return new Error(node, INVALID_BOOLEAN_VALUE, value);
		}
			
		Pattern pattern = type.getPattern();
		if (pattern != null && ! pattern.matcher(value).matches())
			return new Error(node, VALUE_DOES_NOT_MATCH, value, type.getPattern().toString());
		
		return null;
	}

	/**
	 * Any string is by definition a valid string representation of a string type.
	 * However, this may not be true for a binary string type. Also, we check the
	 * length (in characters for a string and bytes for a binary).
	 */
	private static Error validateStringValue(DataNode node, AbstractStringType type) {
		
		int length;

		// The easiest way (probably not the most efficient) to validate a binary string
		// is to decode it - and we need to determine its length anyway.
		if (type instanceof BinaryType) {
			try {
				length = Base64.getDecoder().decode(node.getValue()).length;
			} catch (IllegalArgumentException e) {
				return new Error(node, INVALID_BINARY_VALUE, node.getName(), e.getMessage());
			}
		}
		else length = node.getValue().length();   // otherwise it is a regular string
		
		// Check if the length is within the acceptable range.
		NaturalInterval range = type.getLength();
		int contains = range.contains(length);
		
		if (contains == 0) return null; // length is OK, so leave
		
		String val = node.getValue().length() > 32 ? // trunc'ed value for error message
			node.getValue().substring(0,32) + "..." : node.getValue();

		if (contains > 0)
			return new Error(node, LENGTH_EXCEEDS_MAX, val, length, range.min);
		else
			return new Error(node, LENGTH_SUBCEEDS_MIN, val, length, range.max);
	}

	
	/**
	 * We assert that the node value is a valid string representation of this
	 * content type by creating an instance, and check whether it is in range.
	 */
	private static Error validateRangedValue(DataNode node, RangedType<?> type) {

		Comparable<?> value = null;
		try {
			switch (type.getContentType()) {
				case INTEGER  : value = new Integer(node.getValue()); break;
				case DECIMAL  : value = new Double(node.getValue()); break;
				case DATETIME : value = new DateTime(node.getValue()); break;
				case DATE     : value = new Date(node.getValue()); break;
				default: // we will never get here, unless we forgot to implement something
					throw new RuntimeException("validation of '" + type.getContentType() + "' not implemented!");
			}
		} catch (Exception e) {
			return new Error(node, INVALID_VALUE_FOR_TYPE, node.getValue(), type.getContentType(), e.getMessage());
		}
		
		Interval<?> range = type.getRange(); 
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
		return null;
	}
	
	
	/**
	 * Validating a complex node against a complex type implies validating all child
	 * nodes against the child components of the type. Which is easier said than done,
	 * because there is more than one way to do this, depending on how smart we want
	 * the validation process to be.<br>
	 * For now, we iterate through all child components and try to match each one to
	 * as many child nodes as possible within the constraints set by the effective
	 * multiplicity of the current child component. An error is returned immediately
	 * if there is no match for a mandatory child component.<br>
	 * But mismatch is not always an error. If a component defines optional content,
	 * the current child node may not be a match, but the next component might match
	 * it, so we keep on trying to match until we run out of components.<br>
	 * If we run out of components before all nodes have been matched, that implies
	 * a validation error. If we run out of nodes while there is still mandatory
	 * content expected, that is also a validation error.
	 */
	private static Error validateComplexContent(DataNode node, NodeType type, ErrorList errors) {
		
		NodeIterator<DataNode> inode = new NodeIterator<DataNode>(node.nodes()); // iterator for child nodes
		DataNode childnode = inode.hasNext() ? inode.next() : null; // first child node (or none)
		
		//System.out.println("validateComplex: matching children of " + node.getName()+"{}");
		Iterator<Node> icomp = type.nodes().iterator();
		while (icomp.hasNext()) {
			
			Component childcomp = (Component) icomp.next(); 
			int curmatches = 0;  // number of matches so far for this child component
			int maxmatches = childcomp.maxOccurs(); // maximum number of matches allowed
			
			// start matching the current child component as many times as possible
			while (curmatches < maxmatches) {
			
				if (childnode == null) { // oops, we have run out of nodes
					
					if (curmatches < childcomp.minOccurs()) // but if we expect another node
						return missingNodeError(node, childcomp); // an error is returned

					break; // otherwise we break out and match the next child component
				}

				boolean match;
				//System.out.println("validateComplex: matching " + (! childnode.isLeaf() ? childnode.getName() + "{}" : childnode) + " to " + childcomp.getName());
				if (childcomp instanceof ModelGroup)
					match = matchGroup(inode, childnode, (ModelGroup) childcomp, errors);
				else match = matchNodeType(childnode, (NodeType) childcomp, errors);
				
				//System.out.println("validateComplex: " + (! childnode.isLeaf() ? childnode.getName() + "{}" : childnode) + (match ? " == " : " <> ") + "component " + childcomp.getName());
				if (match) { // count match and get the next node (or none) to match against this component
					childnode = inode.hasNext() ? inode.next() : null;
					++curmatches; continue;
				}
				
				// The child node is no match (we leave but maybe we should match the rest?)
				if (curmatches < childcomp.minOccurs()) // if the child component is mandatory
					return unexpectedNodeError(childnode, childcomp); // return an error

				break; // otherwise, we match the next component to this node (if any)
			}	
		}

		//System.out.println("validateComplex: matched all child components of " + component.getName());
		if (childnode != null) { // if we have an unmatched node, that is a validation error
			errors.add(new Error(childnode, 
				NODE_NOT_EXPECTED_IN, childnode.getName(), childnode.getParent().getName()));

			// each remaining node is also a validation error, but maybe thats for ABUNDANT mode.
//			inode.forEachRemaining( n -> { 
//				errors.add(new Error(n, NODE_NOT_EXPECTED_IN, n.getName(), n.getParent().getName())); 
//			});
		}
		return null;
	}


	/**
	 * Model groups are handled differently from regular types. This method is used
	 * to match all model groups, and it may be called recursively to match nodes
	 * against nested model groups. When matching a model group we may consider more
	 * than one node, so this method accepts an iterator to access subsequent nodes.
	 */
	private static boolean matchGroup(NodeIterator<DataNode> inode, DataNode node, ModelGroup group, ErrorList errors) {

		if (group instanceof ChoiceGroup) 
			return matchChoice(inode, node, (ChoiceGroup) group, errors);
		if (group instanceof SequenceGroup) 
			return matchSequence(inode, node, (SequenceGroup) group, errors);
		if (group instanceof UnorderedGroup) 
			return matchUnordered(inode, node, (UnorderedGroup) group, errors);
		 // Should never happen, unless we forgot a model group
		throw new RuntimeException("'" + group.getName() + "' not implemented!");
	}


	/**
	 * When matching a node to a choice group, we attempt to match it against each
	 * component within the group, until we have a match or reach the end without
	 * one. Or, in other words, a choice group is considered an overall match if one
	 * of its child components is a match. Note that we may encounter other model
	 * groups within the choice group.
	 */
	private static boolean matchChoice(NodeIterator<DataNode> inode, DataNode node, ChoiceGroup choice, ErrorList errors) {

		//System.out.println("matchChoice: matching children of " + choice.getName()+"{}");
		for (Node child : choice.nodes()) {
			
			Component component = (Component) child; 
			boolean match;
			//System.out.println("matchChoice: matching " + ((node instanceof SimpleNode) ? node : node.getName() + "{}") + " to " + component.getName());
			if (component instanceof ModelGroup)
				match = matchGroup(inode, node, (ModelGroup) component, errors);
			else match = matchNodeType(node, (NodeType) component, errors);
			//System.out.println("matchChoice: " + ((node instanceof SimpleNode) ? node : node.getName() + "{}") + (match ? " == " : " <> ") + "component " + component.getName());
			if (match) return true;  // return true at the first match
		}
		return false;
	}


	/**
	 * Matching nodes to a sequence group is similar to validating a complex type,
	 * in the sense that we are matching nodes to the children of a component, using
	 * the same overall iteration logic, but with a few differences.<br>
	 * The group is considered an overall match ("invoked") once we encounter a
	 * matching component. Then, all remaining components are matched until there is
	 * a validation error, or we run out of nodes or components.<br>
	 * If we run out of nodes while there is still mandatory content expected, that
	 * is a validation error. If we run out components while we still have nodes,
	 * that just marks the end of the group and we return, causing the remaining
	 * nodes to be matched against the parent component context.
	 */
	private static boolean matchSequence(NodeIterator<DataNode> inode, DataNode node, SequenceGroup group, ErrorList errors) {

		boolean invoked = false; // overall match for this group, initially false
		final DataNode parent = node.getParent(); // save the parent of the node(s) for later use
		
		//System.out.println("matchSequence: matching children of " + group.getName()+"{}");
		boolean match = false;
		Iterator<Node> icomp = group.nodes().iterator();
		
		while (icomp.hasNext()) { // main / outer component loop
			
			Component component = (Component) icomp.next();
			int curmatches = 0; // number of matches so far for this component
			int maxmatches = component.maxOccurs();  // maximum number of matches allowed
			
			// inner component loop: match the component as many times as possible
			while (curmatches < maxmatches) {
			
				if (node == null) {  // oops, we have run out of nodes
					if (curmatches < component.minOccurs()) { // if we expect another node
						if (invoked) // and the group was invoked, we add a validation error
							errors.add(missingNodeError(parent, component));
						return invoked; // in either case we return (true or false)
					}
					break; // break out of inner loop and match the next component
				}
				
				//System.out.println("matchSequence: matching " + ((node instanceof SimpleNode) ? node : node.getName() + "{}") + " to " + component.getName());
				if (component instanceof ModelGroup)
					match = matchGroup(inode, node, (ModelGroup) component, errors);
				else match = matchNodeType(node, (NodeType) component, errors);
				
				//System.out.println("matchSequence: " + node + (match ? " == " : " <> ") + "component " + component.getName());
				if (match) { 
					// count match and get the next node to match against this component
					if (icomp.hasNext()) // I forgot why this is important
						node = inode.hasNext() ? inode.next() : null;
					invoked = true; ++curmatches; 
					continue; // inner loop
				}
				
				/*
				 * We have a non-matching node, and one of two situations applies: (1) we
				 * expected a match (component is mandatory and possibly repeating). If the
				 * group was already invoked, we add a validation error and leave (returning
				 * true). If the group was not yet invoked, this implies that the group is no
				 * match and the node should be matched against the parent component context, so
				 * we leave returning false. Or, (2) we do NOT expect a match if the component
				 * is optional or was matched the required number of times. In this case we
				 * break out of the inner loop and match the next component to the node.
				 */
				if (curmatches < component.minOccurs()) {
					if (invoked) errors.add(unexpectedNodeError(node, component));
					return invoked; // true or false
				}
				break; // break out of inner loop and match the next component to this node
			} // inner component loop
			
			/*
			 * We get here if we broke out of the inner loop (e.g. there was no match), or
			 * if we matched the node the maximum allowed number of times already. In either
			 * case, we continue to match the current node against the next component.
			 */
			//System.out.println("matchSequence: match " + match);
		} // outer component loop

		/*
		 * We get here if we have run out of components for this group. Any remaining
		 * nodes should be matched against the parent component context. It is possible
		 * that none of the components was a match, in which case invoked will be false.
		 * If the last node was not a match but the group was invoked, returning true
		 * will cause the node (that might match components in the parent context) to be
		 * skipped by the caller rather than evaluated. To prevent this, we revert it.
		 */
		//System.out.println("matchSequence: leaving, match: "+ match + ", invoked: " + invoked);
		if (! match && invoked) inode.revert(); 
		return invoked; // true or false
	}
	
	
	/**
	 * Matching nodes to an unordered group is less straight-forward. Just like a
	 * sequence group, an unordered one is considered an overall match ("invoked")
	 * once we encounter a matching component.<br>
	 * The difference is that the nodes may occur in any order, so we cannot just
	 * iterate the components in order and check if they match up with subsequent
	 * nodes. Instead, we must keep a list of components that are to be matched, and
	 * cross each off after a successful match, until we have run out of nodes or
	 * components.<br>
	 * If we run out of nodes while there is still mandatory components on the list,
	 * that is a validation error. If we run out of components, that marks the end
	 * of the group and we return, causing the remaining nodes to be matched against
	 * the parent component context. If we encounter a non-matching node, that is
	 * usually an error.
	 */
	private static boolean matchUnordered(NodeIterator<DataNode> inode, DataNode node, UnorderedGroup group, ErrorList errors) {
		
		boolean invoked = false; // whether this group was invoked, initially false
		final DataNode parent = node.getParent(); // save the parent of the node(s)
		
		// make a list (set) of components to be matched (by definition at least two)
		// we remove components during iteration, so we use a concurrent write list. 
		List<Component> components =  new CopyOnWriteArrayList<>(group.nodes());

		node_loop:
		while (node != null) { // while there are still nodes

			boolean matchinlist = false;
			Component component = null;
			Iterator<Component> icomp = components.iterator();
				
			while (icomp.hasNext()) { // outer component loop (while the list is not empty)
			
				component = (Component) icomp.next(); 
				int curmatches = 0; // number of matches so far for this component
				int maxmatches = component.maxOccurs();  // maximum number of matches allowed
				
				// Match the (possibly repeating) component as many times as possible
				while (curmatches < maxmatches) { // inner component loop
				
					boolean match;
					//System.out.println("matchUnordered: matching " + ((node instanceof SimpleNode) ? node : node.getName() + "{}") + " to " + component.getName());
					if (component instanceof ModelGroup)
						match = matchGroup(inode, node, (ModelGroup) component, errors);
					else match = matchNodeType(node, (NodeType) component, errors);
	
					/*
					 * If we have a match, count it. If there are more nodes to be matched, resume
					 * the inner loop and attempt the next node against the current component. If
					 * there are no more nodes available, the component is removed from the list, as
					 * it cannot be matched again anyway. If additional matches were expected, we
					 * raise a validation error. In any case, we break out of the main loop. If
					 * there was no match to begin with, we break out of the inner loop.
					 */
					if (match) { 
						++curmatches; matchinlist = true; invoked = true; 
						if (inode.hasNext()) {
							node = inode.next();
							continue; // inner loop
						}
						components.remove((Node) component);
						if (curmatches < component.minOccurs())
							errors.add(missingNodeError(parent, component));
						break node_loop; // all the way down}
					}
					break; // no match, break out of inner loop
				
				} // inner component loop
				
				/*
				 * If we get here we either completed the inner loop (meaning the component was
				 * matched the maximum number of times) or we broke out of it because we ran
				 * into a non-matching node. In any case, if the component was matched at least
				 * once, we remove it from the list before we resume the outer loop to match the
				 * next component.
				 */
				if (curmatches > 0) components.remove((Node) component);
				
			} // outer component loop
			
			/*
			 * We completed the outer component loop, matching components on the list
			 * against nodes. If at this point the list is empty, we are done and return.
			 * Otherwise, there are two possibilities: (1) there was a match and the current
			 * node is the next node to be matched against the list of remaining components,
			 * so we resume the node loop. Or (2), the current node is a no match for any of
			 * the components on the list <- UNCLEAR
			 */
			
			//System.out.println("matchUnordered: match: " + match + " invoked: " + invoked + " components: " + components.size());
			if (components.isEmpty()) {
				inode.revert(); // needs to be explained
				return invoked; // invoked will be true
			}
			
			if (matchinlist) {
				//node = inode.hasNext() ? inode.next() : null; // get the next node
				continue; //  resume node loop
			}
			
			/*
			 * We have a non-matching node. If the group was invoked, and there are still
			 * required (!) components left to be matched, we add a validation error for the
			 * current node, and resume matching nodes against the remaining components, as
			 * all required ones should ultimately match. If there are no nodes left, we
			 * must remove the current component from the list since it cannot be matched
			 * anymore (and an error was already reported).
			 */
			if (invoked) {
				List<Component> required = 
					components.stream().filter(n -> n.minOccurs() > 0)
					.collect(Collectors.toCollection(ArrayList<Component>::new));
				if (! required.isEmpty()) {
					Error error = new Error(node, GOT_NODE_BUT_EXPECTED, 
						node.getName(), quoteNames(expectedTypes(required)) );
					errors.add(error);
					node = inode.hasNext() ? inode.next() : null; // get the next node
					if (node == null) components.remove((Node) component);
					continue; // resume node loop
				}
			}
			
			/*
			 * If we get here, one of two situations applies: (1) the group was not yet
			 * invoked, so the non-matching node must belong to the parent context against
			 * which it should be matched. In that case we leave (returning false). Or (2)
			 * the group was invoked but only optional components are left to be matched. In
			 * this case, returning true causes the non-matching node (that may still match
			 * components in the parent context) to be skipped by the caller rather than
			 * evaluated. To prevent this, we revert it.
			 */
			//System.out.println("matchUnordered: leaving, invoked: " + invoked + " components: " + components.size());
			if (invoked) inode.revert(); 
			return invoked; // true or false
			
		} // node loop
		
		/*
		 * Finally, if we get here, we have run out of nodes to match. We should assert
		 * that there are no more required components and add an error otherwise.
		 */
		if (invoked) {
			List<Component> required = 
				components.stream().filter(n -> n.minOccurs() > 0)
				.collect(Collectors.toCollection(ArrayList<Component>::new));
			if (! required.isEmpty()) {
				Error error = new Error(parent, CONTENT_MISSING_AT_END, 
					parent.getName(), quoteNames(expectedTypes(required)) );
				errors.add(error);
			}
		}
		return invoked; // true or false
	}


	
	//
	// Convenience methods below this line.
	//


	/** Returns the type name in single quotes, or "any node" for an unnamed {@code AnyType}. */
	private static String quoteName(NodeType type) {
		
		return (type instanceof AnyType && !((AnyType) type).isNamed()) 
			? "any node" : "'" + type.getTypeName() + "'";
	}


	/** Returns list of quoted type names in the format: 'a'[, 'b' ...] or 'z'. */
	private static String quoteNames(List<NodeType> list) {
		
		String result = list.stream()
			.map(n -> quoteName(n)).collect(Collectors.joining(","));
		int i = result.lastIndexOf(','); 
		return (i == -1) ? result : result.substring(0, i) + " or " + result.substring(i+1);	
	}


	/**
	 * When we match content to a type, it is obvious what is expected. But if the
	 * component is a <em>model group</em>, things are a bit more complicated,
	 * depending on the group type, and the level of nesting (model groups within
	 * model groups). This recursive method returns a set of candidate types when
	 * matching a particular component (type or model group).
	 */
	private static List<NodeType> expectedTypes(Component comp) {

		if (comp instanceof NodeType)  // ends recursion 
			return Collections.singletonList( (NodeType) comp );

		// continue with a group
		ModelGroup group = (ModelGroup) comp;
		
		/*
		 * For a choice, any one of the alternatives may follow, so we recursively
		 * return all of them, flat-mapped into a single stream. The same approach
		 * applies to an unordered group, because any of the child components may
		 * follow (in any order).
		 */
		if (group instanceof ChoiceGroup || group instanceof UnorderedGroup) {
			List<NodeType> result = group.nodes().stream()
				.flatMap(n -> expectedTypes( (Component) n ).stream())
				.collect(Collectors.toCollection(ArrayList<NodeType>::new));
			return result;
		}
		
		/*
		 * For sequence groups, the logic is different. Any number of optional
		 * components and/or a mandatory component may follow, but nothing beyond
		 * that. So we recursively return anything up to and including the first
		 * mandatory component.
		 */
		if (group instanceof SequenceGroup) {
			List<NodeType> result = new ArrayList<>();
			for (Node n : group.nodes()) {
				for (NodeType t : expectedTypes((Component) n)) result.add(t); // correct?
				if (((Component) n).minOccurs() > 0) break;
			}
			return result;
		}
		
		 // We should never reach this, unless we forgot a model group...
		throw new RuntimeException("'" + group.getName() + "' not implemented!");
	}


	/**
	 * Similar to {@code expectedTypes(Component)}, which expects a single
	 * component, this method returns a set of candidate types based on a
	 * <i>list</i> of (equally applicable) components.
	 */
	private static List<NodeType> expectedTypes(List<Component> list) {
		
		List<NodeType> result = 
			list.stream().flatMap(n -> expectedTypes(n).stream())
			.collect(Collectors.toCollection(ArrayList<NodeType>::new));
		return result;
	}


	/** This returns an error specifying a missing type at the end of a context node. */
	private static Error missingNodeError(DataNode context, Component comp) {
		
		if (comp instanceof ModelGroup)
			return new Error(context, CONTENT_MISSING_AT_END, context.getName(), quoteNames(expectedTypes(comp)));
		else return new Error(context, CONTENT_MISSING_AT_END, context.getName(), quoteName((NodeType) comp));
	}


	/** This returns an error specifying an unexpected node and the expected type. */
	private static Error unexpectedNodeError(DataNode node, Component comp) {
		
		if (comp instanceof ModelGroup)
			return new Error(node, GOT_NODE_BUT_EXPECTED, node.getName(), quoteNames(expectedTypes(comp)));
		else return new Error(node, GOT_NODE_BUT_EXPECTED, node.getName(), quoteName((NodeType) comp));
	}

}

package be.baur.sds.validation;

import java.util.Base64;
import java.util.Collection;
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
import be.baur.sds.model.AbstractGroup;
import be.baur.sds.model.ChoiceGroup;
import be.baur.sds.model.Group;
import be.baur.sds.model.UnorderedGroup;

/**
 * The default {@link Validator} to validate an SDA document against a schema.
 */
public final class SDAValidator implements Validator {

	private static final String GLOBAL_TYPE_NOT_FOUND = "global type '%s' not found";
	private static final String EXPECTING_NODE_OF_TYPE = "expecting '%s' to be a %s type";
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
	
	
	public ErrorList validate(Node document, Schema schema, String type) {

		ComponentType component = null;
		
		if (type == null || type.isEmpty()) {
			
			if (schema.getNodes().size() == 1)
				component = (ComponentType) schema.getNodes().get(1);
			else if (schema.getRootType() != null) 
				component = (ComponentType) schema.getNodes().get(schema.getRootType()).get(1);
			// else throw something like "schema has no designated type"
			// to prevent:  global type 'null' not found
		}	
		else component = (ComponentType) schema.getNodes().get(type).get(1);
			
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
			
			if (! (component instanceof SimpleType)) {  // we were expecting complex content
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
		
		// Empty values are allowed only for null-able types.
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

		// The easiest way (probably not the most efficient) to validate a binary string
		// is to decode it - and we need to determine its length anyway.
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
	 * nodes against the children of the type. Which is easier said than done,
	 * because there is more than one way to do this, depending on how smart we want
	 * the validation process to be.<br>
	 * Here we iterate through all child components and try to match each one to as
	 * many child nodes as possible within the constraints set by the multiplicity
	 * of the current child component. An error is returned immediately if there is
	 * no match for a mandatory child component.<br>
	 * But mismatch is not always an error. If a component defines optional content,
	 * the current child node may not be a match, but the next component might match
	 * it, so we keep on trying to match until we run out of components.<br>
	 * If we run out of components before all nodes have been matched, that implies
	 * a validation error. If we run out of nodes while there is still mandatory
	 * content expected, that is also a validation error.
	 */
	private static Error validateComplexNode(ComplexNode node, ComplexType component, ErrorList errors) {
		
		NodeIterator inode = new NodeIterator(node.getNodes()); // iterator for child nodes
		Node childnode = inode.hasNext() ? inode.next() : null; // first child node (or none)
		
		//System.out.println("validateComplex: matching children of " + node.getName()+"{}");
		Iterator<Node> icomp = component.getNodes().iterator();
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
				//System.out.println("validateComplex: matching " + ((childnode instanceof SimpleNode) ? childnode : childnode.getName() + "{}") + " to " + childcomp.getName());
				if (childcomp instanceof AbstractGroup)
					match = matchGroup(inode, childnode, (AbstractGroup) childcomp, errors);
				else match = matchNode(childnode, childcomp, errors);
				
				//System.out.println("validateComplex: " + ((childnode instanceof SimpleNode) ? childnode : childnode.getName()+"{}") + (match ? " == " : " <> ") + "component " + childcomp.getName());
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

			// and in ABUNDANT mode each remaining node is also a validation error
//			inode.forEachRemaining( n -> { 
//				errors.add(new Error(n, NODE_NOT_EXPECTED_IN, n.getName(), n.getParent().getName())); 
//			});
		}
		return null;
	}


	/**
	 * Model groups are handled different from regular nodes. This method is used to
	 * match all model groups, and may be called recursively to match nodes against
	 * nested model groups. When matching a model group we may consider more than
	 * one node, so this method accepts an iterator to access subsequent nodes.
	 */
	private static boolean matchGroup(NodeIterator inode, Node node, AbstractGroup group, ErrorList errors) {

		if (group instanceof ChoiceGroup) 
			return matchChoice(inode, node, (ChoiceGroup) group, errors);
		if (group instanceof Group) 
			return matchSequence(inode, node, (Group) group, errors);
		if (group instanceof UnorderedGroup) 
			return matchUnordered(inode, node, (UnorderedGroup) group, errors);
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
	private static boolean matchChoice(NodeIterator inode, Node node, ChoiceGroup choice, ErrorList errors) {

		//System.out.println("matchChoice: matching children of " + choice.getName()+"{}");
		for (Node child : choice.getNodes()) {
			
			ComponentType component = (ComponentType) child; boolean match;
			//System.out.println("matchChoice: matching " + ((node instanceof SimpleNode) ? node : node.getName() + "{}") + " to " + component.getName());
			if (component instanceof AbstractGroup)
				match = matchGroup(inode, node, (AbstractGroup) component, errors);
			else match = matchNode(node, component, errors);
			//System.out.println("matchChoice: " + ((node instanceof SimpleNode) ? node : node.getName() + "{}") + (match ? " == " : " <> ") + "component " + component.getName());
			if (match) return true;
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
	private static boolean matchSequence(NodeIterator inode, Node node, Group group, ErrorList errors) {

		boolean invoked = false; // overall match for this group, initially false
		Node parent = node.getParent(); // save the parent of the node(s)
		
		//System.out.println("matchSequence: matching children of " + group.getName()+"{}");
		boolean match = false;
		Iterator<Node> icomp = group.getNodes().iterator();
		while (icomp.hasNext()) {
			
			ComponentType component = (ComponentType) icomp.next();
			int curmatches = 0; // number of matches so far for this component
			int maxmatches = component.maxOccurs();  // maximum number of matches allowed
			
			// Start matching the current component as many times as possible
			while (curmatches < maxmatches) {
			
				if (node == null) {  // oops, we have run out of nodes
					if (curmatches < component.minOccurs()) { // but if we expect another node
						if (invoked) // and the group was invoked, we add a validation error
							errors.add(missingNodeError(parent, component));
						return invoked; // in either case we return (true or false)
					}
					break; // otherwise we break out and match the next component
				}
				
				//System.out.println("matchSequence: matching " + ((node instanceof SimpleNode) ? node : node.getName() + "{}") + " to " + component.getName());
				if (component instanceof AbstractGroup)
					match = matchGroup(inode, node, (AbstractGroup) component, errors);
				else match = matchNode(node, component, errors);
				
				//System.out.println("matchSequence: " + node + (match ? " == " : " <> ") + "component " + component.getName());
				if (match) { // count match and get the next node to match against this component
					if (icomp.hasNext()) node = inode.hasNext() ? inode.next() : null;
					invoked = true; ++curmatches; continue;
				}
				
				/*
				 * We have a non-matching node, and one of two situations applies: 1) we expect
				 * a match (due to the component being mandatory and possibly repeating). In
				 * this case, if the group was already invoked, we add a validation error and
				 * leave (returning true). If the group was not yet invoked, this implies that
				 * the group is no match and the node should be matched against the parent
				 * component context, so we leave returning false. Or 2) we do not expect a
				 * match at all, because the component is optional or was matched the required
				 * number of times. In this case we break out of this loop and try to match the
				 * next component to the node. REALLY NEED TO CHECK THIS LOGIC AND THE CODE!
				 */
				if (curmatches < component.minOccurs()) {
					if (invoked) errors.add(unexpectedNodeError(node, component));
					return invoked; // true or false
				}
				break; // break out and match the next component to this node
			}
			/*
			 * If we get here, it is because we broke out of the matching loop (e.g. there
			 * was no match), or because we matched the node the maximum allowed number of
			 * times already. In either case, we continue to match the current node against
			 * the next component.
			 */
			//System.out.println("matchSequence: match " + match);
		}

		/*
		 * If we get here, we have run out of components for this group. Any remaining
		 * nodes should be matched against the parent component context. It is possible
		 * that none of the components was a match, in which case invoked will be false.
		 * If the last node was not a match but the group was invoked, returning true
		 * will cause the node (that might match components in the parent context) to be
		 * skipped by the caller rather than evaluated. To prevent this, we revert it.
		 */
		//System.out.println("matchSequence: leaving, match: "+ match + ", invoked: " + invoked);
		if (! match && invoked) inode.revert(); return invoked; // true or false
	}
	
	
	/**
	 * Matching nodes to an unordered group is not at all straight-forward. Like a
	 * sequence group, an unordered group is considered an overall match ("invoked")
	 * once we encounter a matching component.<br>
	 * The difference with a sequence group is that components may occur in any
	 * order, so we cannot just iterate the components in order and see if they
	 * match up with subsequent nodes. Instead, we need to keep a list of components
	 * that are to be matched, crossing them off once we encounter a match, until we
	 * run out of nodes or components.<br>
	 * If we run out of nodes while there is still mandatory components on the list,
	 * that is a validation error. If we run out of components, that marks the end
	 * of the group and we return, causing the remaining nodes to be matched against
	 * the parent component context. If we encounter a non-matching node, that is
	 * usually an error. Or, in pseudo-code:
	 * 
	 * <pre>
	 * Make List of Components
	 * While Nodes Do
	 *    Get Next Node
	 *    While Components on List Do
	 *        Get Next Component
	 *        Node Matches Component?
	 *        NO -> Continue Get Next Component
	 *        YES -> 
	 *           Remove Component from List
	 *           Continue Get Next Node
	 *    Done Components
	 *    RETURN (no match for Node, may Add Error)
	 * Done Nodes
	 * RETURN (all nodes matched, may Add Error)
	 * </pre>
	 * 
	 * Note: this is a simplification because it does not take into account that a
	 * component can be repeating and may match more than one node.
	 * 
	 */
	private static boolean matchUnordered(NodeIterator inode, Node node, UnorderedGroup group, ErrorList errors) {
		
		boolean invoked = false; 	// overall match for this group, initially false
		Node parent = node.getParent(); // save the parent of the node(s)
		
		// Components to be matched (by definition, there are at least two components).
		NodeSet components =  new NodeSet(); components.addAll(group.getNodes());

		while (node != null) { // while there are still nodes
		
			Iterator<Node> icomp = components.iterator();
			
			boolean match = false;
			while (icomp.hasNext()) { // while there are still components on the list
			
				ComponentType component = (ComponentType) icomp.next();
				//System.out.println("matchUnordered: matching " + ((node instanceof SimpleNode) ? node : node.getName() + "{}") + " to " + component.getName());
				if (component instanceof AbstractGroup)
					match = matchGroup(inode, node, (AbstractGroup) component, errors);
				else match = matchNode(node, component, errors);

				// remove component from the to-be-matched list and break out
				if (match) {
					invoked = true; components.remove((Node) component);
					break; // NOTE: for now we ignore the fact it may be a repeating component!
				}
			}
			
			/*
			 * If we get here, one of two situations applies: 1) we matched a node against a
			 * component and removed it from the to-be-matched list, which may be empty
			 * afterwards. If it is, we matched all components and leave. If it is not, we
			 * proceed to match the next node against all components on the list. Or 2) we
			 * evaluated all components on the to-be-matched list and none was a match for
			 * the current node.
			 */
			//System.out.println("matchUnordered: match: " + match + " invoked: " + invoked + " components: " + components.size());
			if (components.isEmpty()) return invoked; // always true at this point
			
			if (match) {
				node = inode.hasNext() ? inode.next() : null; // get the next node
				continue; // and resume the node loop to process it
			}
			
			/*
			 * We have a non-matching node (situation 2). If the group was invoked, and
			 * there are still required components left to be matched, we add a validation
			 * error for the non-matching node, and resume matching nodes against the
			 * remaining components, as all required ones must match by definition.
			 */
			if (invoked) {
				NodeSet required = components.get(n -> ((ComponentType) n).minOccurs() > 0);
				if (! required.isEmpty()) {
					@SuppressWarnings("unchecked")
					Error error = new Error(node, GOT_NODE_BUT_EXPECTED, node.getName(), 
						quoteNames(expectedTypes( (Collection<? extends ComponentType>) required) ));
					errors.add(error);
					node = inode.hasNext() ? inode.next() : null; // get the next node
					continue; // and resume the node loop to process it
				}
			}
			
			/*
			 * If we get here, again one of two situations applies: 1) the group was not yet
			 * invoked, so the non-matching node must belong to the parent context against
			 * which it should be matched. In that case we leave (returning false). Or, 2)
			 * the group was invoked but only optional components are left to be matched. In
			 * this case, we also leave, but returning true will cause the non-matching node
			 * (which might match components in the parent context) to be skipped by the
			 * caller rather than evaluated. To prevent this, we revert it.
			 */
			//System.out.println("matchUnordered: leaving, invoked: " + invoked + " components: " + components.size());
			if (invoked) inode.revert(); return invoked; // true or false
		}
		
		/*
		 * Finally, if we get here, we have run out of nodes to match. We should assert
		 * that there are no more required components and add an error otherwise.
		 */
		if (invoked) {
			NodeSet required = components.get(n -> ((ComponentType) n).minOccurs() > 0);
			if (! required.isEmpty()) {
				@SuppressWarnings("unchecked")
				Error error = new Error(parent, CONTENT_MISSING_AT_END, parent.getName(), 
					quoteNames(expectedTypes( (Collection<? extends ComponentType>) required) ));
				errors.add(error);
			}
		}
		return invoked; // true or false
	}
	
//				For repeating components...
//
//				groupmatch = true; //components.remove((Node) component);
//				int curmatches = 1; // number of matches so far for this component, got one already
//				int maxmatches = component.maxOccurs();  // maximum number of matches allowed
//
//				node = inode.hasNext() ? inode.next() : null;
//				while (curmatches < maxmatches) {
//				
//					if (node == null) {  // oops, we have run out of nodes
//						if (curmatches < component.minOccurs()) // but if we expect another node
//							errors.add(missingNodeError(parent, component)); // we add a validation error
//						return groupmatch; // in any case we return (true or false)
//					}
//					
//					boolean match2;
//					//System.out.println("matchUnordered: matching " + ((node instanceof SimpleNode) ? node : node.name + "{}") + " to " + component.getName());
//					if (component instanceof AbstractGroup)
//						match2 = matchModelGroup(inode, node, (AbstractGroup) component, errors);
//					else match2 = matchNode(node, component, errors);
//				
//					//System.out.println("node " + node.name + (match ? " == " : " <> ") + "component " + component.getName());
//					if (match2) { // count match and get the next node to match against this component (if there is one)
//						if (icomp.hasNext()) node = inode.hasNext() ? inode.next() : null;
//						++curmatches; continue;
//					}
//					
//					// The node is no match - so if we still expect more of them
//					if (curmatches < component.minOccurs()) {
//						errors.add(unexpectedNodeError(node, component)); // we add a validation error
//						return groupmatch; // and return (true or false)
//					}

	
	//
	// Convenience methods below this line.
	//


	/** Returns the node name in single quotes, or "any node" for an unnamed {@link AnyType}. */
	private static String quoteName(Node node) {
		
		return (node instanceof AnyType && !((AnyType) node).isNamed()) 
			? "any node" : "'" + node.getName() + "'";
	}


	/** Returns list of quoted node names in the format: 'a', .. 'b' or 'c'. */
	private static String quoteNames(NodeSet set) {
		
		String result = set.stream()
			.map(n -> quoteName(n)).collect(Collectors.joining(","));
		int i = result.lastIndexOf(','); 
		return (i == -1) ? result : result.substring(0, i) + " or " + result.substring(i+1);	
	}


	/**
	 * When we expect content to match a complex or simple component, it is obvious
	 * what node is expected. But if the component is a <em>model group</em>, things
	 * are a bit more complicated, depending on the group type, and the level of
	 * nesting (model groups within model groups). This recursive method returns a
	 * set of candidate component types.
	 */
	private static NodeSet expectedTypes(ComponentType comp) {
		
		// If not a group, just return the component itself, ending the recursion.
		if (! (comp instanceof AbstractGroup)) return NodeSet.of((Node)comp);

		AbstractGroup group = (AbstractGroup) comp;
		
		/*
		 * For a choice, any one of the alternatives may follow, so we recursively
		 * return all of them, flat-mapped into a single stream. The same approach
		 * applies to an unordered group, because any of the child components may
		 * follow, in any order.
		 */
		if (group instanceof ChoiceGroup || group instanceof UnorderedGroup) {
			NodeSet result = group.getNodes().stream()
				.flatMap(n -> expectedTypes((ComponentType)n).stream())
				.collect(Collectors.toCollection(NodeSet::new));
			return result;
		}
		
		/*
		 * For a regular group, the logic is somewhat different. Any number of optional
		 * components or the first mandatory component may follow, but nothing beyond
		 * that. So we recursively return anything up to and including the first
		 * mandatory component.
		 */
		if (group instanceof Group) {
			NodeSet result = new NodeSet();
			for (Node n : group.getNodes()) {
				for (Node e : expectedTypes((ComponentType)n)) result.add(e); // is this is correct?
				if (((ComponentType) n).minOccurs() > 0) break;
			}
			return result;
		}
		
		 // We should never reach this, unless we forgot a model group...
		throw new RuntimeException("'" + group.getName() + "' not implemented!");
	}


	/**
	 * Like expectedTypes(ComponentType), this method returns a set of candidate
	 * component types based on a collection of (equally applicable) types.
	 */
	private static NodeSet expectedTypes(Collection<? extends ComponentType> collection) {
		NodeSet result = collection.stream()
			.flatMap(n -> expectedTypes((ComponentType)n).stream())
			.collect(Collectors.toCollection(NodeSet::new));
		return result;
	}


	/** This returns an error specifying a missing node at the end of a context node. */
	private static Error missingNodeError(Node context, ComponentType comp) {
		if (comp instanceof AbstractGroup)
			return new Error(context, CONTENT_MISSING_AT_END, context.getName(), quoteNames(expectedTypes(comp)));
		else return new Error(context, CONTENT_MISSING_AT_END, context.getName(), quoteName((Node) comp));
	}


	/** This returns an error specifying an unexpected node. */
	private static Error unexpectedNodeError(Node node, ComponentType comp) {
		if (comp instanceof AbstractGroup)
			return new Error(node, GOT_NODE_BUT_EXPECTED, node.getName(), quoteNames(expectedTypes(comp)));
		else return new Error(node, GOT_NODE_BUT_EXPECTED, node.getName(), quoteName((Node) comp));
	}

}

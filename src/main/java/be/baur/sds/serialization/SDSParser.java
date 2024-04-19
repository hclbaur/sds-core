package be.baur.sds.serialization;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import be.baur.sda.DataNode;
import be.baur.sda.Node;
import be.baur.sda.SDA;
import be.baur.sda.serialization.Parser;
import be.baur.sda.serialization.SDAParseException;
import be.baur.sds.AnyType;
import be.baur.sds.Component;
import be.baur.sds.DataType;
import be.baur.sds.NodeType;
import be.baur.sds.Schema;
import be.baur.sds.common.Interval;
import be.baur.sds.common.NaturalInterval;
import be.baur.sds.content.AbstractStringType;
import be.baur.sds.content.BooleanType;
import be.baur.sds.content.RangedType;
import be.baur.sds.model.ChoiceGroup;
import be.baur.sds.model.ModelGroup;
import be.baur.sds.model.SequenceGroup;
import be.baur.sds.model.UnorderedGroup;


/**
 * This is the default SDS parser; used to read and parse SDS content to create
 * a {@code Schema}. For example, when processing the following input:
 * 
 * <pre>
 * schema { 
 *    node "greeting" { 
 *       node "message" { type "string" } 
 *    }
 * }
 * </pre>
 * 
 * the parser returns a <code>Schema</code> describing a SDA node named
 * 'greeting' with a single child node 'message' and a string value, like:
 * 
 * <pre>
 * greeting { 
 *    message "hello world" 
 * }
 * </pre>
 * 
 * @see Schema
 */
public final class SDSParser implements Parser<Schema> {

	private static final String A_NODE_IS_EXPECTED = "a '%s' node is expected";
	private static final String A_NODE_MUST_HAVE = "a '%s' node must have %s";

	private static final String COMPONENT_NOT_ALLOWED = "component '%s' is not allowed here";
	private static final String COMPONENT_INCOMPLETE = "component '%s' is incomplete";
	private static final String COMPONENT_UNKNOWN = "component '%s' is unknown";

	private static final String ATTRIBUTE_NOT_SINGULAR = "attribute '%s' can occur only once";
	private static final String ATTRIBUTE_NOT_ALLOWED = "attribute '%s' is not allowed here";
	private static final String ATTRIBUTE_UNKNOWN = "attribute '%s' is unknown";
	private static final String ATTRIBUTE_MISSING = "attribute '%s' is missing";
	private static final String ATTRIBUTE_EMPTY = "attribute '%s' is empty";
	private static final String ATTRIBUTE_INVALID = "%s '%s' is invalid; %s";

	private static final String TYPE_IS_UNKNOWN = "type '%s' is unknown";
	private static final String NODE_NAME_INVALID = "'%s' is not a valid node name";
	private static final String NAME_NOT_EXPECTED = "name '%s' is not expected";
	private static final String NAME_IS_EXPECTED = "a name is expected";
	
	/**
	 * Creates a schema from a character input stream in SDS format.
	 * 
	 * @return a schema
	 * @throws IOException       if an I/O operation failed
	 * @throws SDSParseException if an SDS parse exception occurs
	 */
	@Override
	public Schema parse(Reader input) throws IOException, SDSParseException {

		DataNode sds = null;
		try {
			sds = SDA.parse(input);
		} catch (SDAParseException e) {
			throw new SDSParseException(null, e);
		}
		return parse(sds);
	}


	/**
	 * Creates a schema from an SDA node representing a schema (what an SDA parser
	 * returns upon processing an input stream in SDS format).
	 * 
	 * @param sds a node with a schema definition
	 * @return a schema
	 * @throws SDSParseException if a schema exception occurs
	 */
	public static Schema parse(DataNode sds) throws SDSParseException {
		
		if (! sds.getName().equals(Schema.TAG))
			throw exception(sds, A_NODE_IS_EXPECTED, Schema.TAG);

		if (! sds.isParent()) // a schema must have components
			throw exception(sds, A_NODE_MUST_HAVE, sds.getName(), "content");
		
//		// a schema must not have attributes, except for an optional type reference
//		List<Node> alist = sds.find(n -> n.isLeaf() && ! n.getName().equals(Attribute.TYPE.tag));
		
		// a schema must not have attributes
		List<Node> alist = sds.find(n -> n.isLeaf());
		
		if (! alist.isEmpty()) { // An unknown or forbidden attribute was found.
			Node a = alist.get(0);
			if (Attribute.get(a.getName()) == null)
				throw exception(a, ATTRIBUTE_UNKNOWN, a.getName());
			throw exception(a, ATTRIBUTE_NOT_ALLOWED, a.getName());
		}
		
		// build the schema
		Schema schema = new Schema();

		// parse global types, and add them to the schema (if all is in order).
		for (Node node : sds.find(n -> ! n.isLeaf())) {
			
			if (Components.get(node.getName()) == null) // component is unknown
				throw exception(node, COMPONENT_UNKNOWN, node.getName());
			
			if (! node.getName().equals(Components.NODE.tag)) // only node definitions are allowed here
				throw exception(node, COMPONENT_NOT_ALLOWED, node.getName());
			
			// global types must not have a multiplicity attribute
			getAttribute((DataNode) node, Attribute.OCCURS, null);
			
			schema.add(parseComponent((DataNode) node, false));
		}

		return schema;
	}


	/**
	 * This method parses an SDA node representing an SDS component, and returns a
	 * Component (NodeType or ModelGroup) which itself may contain other components
	 * or model groups. It is called by parse, parseTypeReference and ParseComponent
	 * itself. A shallow parse means that no child nodes are parsed, which is used
	 * in the parsing of type references.
	 */
	private static Component parseComponent(DataNode sds, boolean shallow) throws SDSParseException {
		/*
		 * Whatever we get must be a valid component, and contain attributes, types
		 * and/or model groups. This method is called recursively and must deal with
		 * every possible component.
		 */

		if (Components.get(sds.getName()) == null) // component must have a known name tag
			throw exception(sds, COMPONENT_UNKNOWN, sds.getName());

		if (! sds.isParent()) // components must have attributes and/or child components
			throw exception(sds, COMPONENT_INCOMPLETE, sds.getName());
		
		for (Node node : sds.find(n -> n.isLeaf()))
			if (Attribute.get(node.getName()) == null) // all attributes must have a known name tag
				throw exception(node, ATTRIBUTE_UNKNOWN, node.getName());
		
		/*
		 * A component is either a node type (simple and/or complex), a model group, an
		 * any type, or a type reference. If there are no child nodes with complex
		 * content, it must be a simple type or a reference, which looks like a simple
		 * type but refers to a global type.
		 */
		boolean isNodeType = sds.getName().equals(Components.NODE.tag); // will be false for a model group
		List<Node> complexChildren = sds.find(n -> ! n.isLeaf()); // list of complex children (if any)
		
		// Simple types and references MUSt have a content type, complex types MAY have one
		DataNode type = getAttribute(sds, Attribute.TYPE, isNodeType && complexChildren.isEmpty());
		boolean isAnyType = (type == null) ? false : type.getValue().equals(AnyType.NAME);
		boolean isDataType = (type == null) ? false : Schema.isDataType(type.getValue());
		
		Component component; // the component to be returned at the end of this method
		
		if (! isNodeType) { // component is a model group
			component = parseModelGroup(sds);
		} 
		else {  // component is a node type (of any kind)
			
			if (isAnyType) { // an any type cannot have components or attributes (except NAME and OCCURS)
				
				if (! complexChildren.isEmpty())
					throw exception(sds, ATTRIBUTE_INVALID, Attribute.TYPE.tag, AnyType.NAME, "node defines content");
				
				List<Node> alist = sds.find(n -> n.isLeaf() && 
					! (n.getName().equals(Attribute.OCCURS.tag) || n.getName().equals(Attribute.TYPE.tag)));
				if (! alist.isEmpty())
					throw exception(sds, ATTRIBUTE_NOT_ALLOWED, alist.get(0).getName());
				
				String name = sds.getValue();  // a name is optional, but if there is one it must be valid
				if (! name.isEmpty() && ! SDA.isName(name))
					throw exception(sds, NODE_NAME_INVALID, name);
				
				component = new AnyType(name);
			}
			
			else if (isDataType || type == null) // a known data type or complex type
				component = parseNodeType(sds, type);
			
			else // component must be a type reference
				component = parseTypeReference(sds, type);
		}

		
		// We have a component, so set the (optional) multiplicity
		DataNode occurs = getAttribute(sds, Attribute.OCCURS, false);
		try {
			if (occurs != null)
				component.setMultiplicity(NaturalInterval.from(occurs.getValue()));
		} catch (IllegalArgumentException e) {
			throw exception(occurs, ATTRIBUTE_INVALID, Attribute.OCCURS.tag, occurs.getValue(), e.getMessage());
		}
		
		// And finally, recursively parse and add any child components
		if (! shallow) // unless this is a shallow parse
			for (Node node : complexChildren)
				component.add(parseComponent((DataNode) node, false));

		return component;
	}


	/**
	 * This method is called by parseComponent() to parse a node representing a
	 * model group in SDS syntax. For example, a choice group:
	 * 
	 * <pre>
	 * choice {
	 *     node "firstname" { type "string" }
	 *     node "lastname" { type "string" }
	 * }
	 * </pre>
	 * 
	 * @param sds a schema node
	 * @returns a {@link SequenceGroup}, {@link ChoiceGroup} or
	 *          {@link UnorderedGroup},
	 */
	private static ModelGroup parseModelGroup(DataNode sds) throws SDSParseException {
		/*
		 * Preconditions: the caller (parseComponent) has already verified that this
		 * component has a valid tag, attributes with valid tags only, and one or more
		 * child components.
		 * Postcondition: the caller will set the multiplicity on the returned type.
		 */

		// Model groups should not have attributes other than OCCURS (maybe NAME in the future).
//		Optional<Node> attribute = sds.getNodes().find(n -> n.isLeaf()).stream()
//			.filter(n -> ! (/* n.getName().equals(Attribute.TYPE.tag) 
//				|| */ n.getName().equals(Attribute.OCCURS.tag)) ).findFirst();
		
		List<Node> alist = sds.find(n -> n.isLeaf() && ! ( /* n.getName().equals(Attribute.TYPE.tag) || */ 
			n.getName().equals(Attribute.OCCURS.tag)) );
				
		if (! alist.isEmpty())
			throw exception(sds, ATTRIBUTE_NOT_ALLOWED, alist.get(0).getName());

		// model groups are not allowed to have names (maybe in the future)
		String name = sds.getValue();
		if (! name.isEmpty())
			throw exception(sds, NAME_NOT_EXPECTED, name);

		// in a model group, there must be at least two components
		if (sds.find(n -> ! n.isLeaf()).size() < 2)
			throw exception(sds, COMPONENT_INCOMPLETE, sds.getName());

		ModelGroup mgroup;

		switch (Components.get(sds.getName())) {
			case GROUP		: mgroup = new SequenceGroup(); break;
			case CHOICE		: mgroup = new ChoiceGroup(); break;
			case UNORDERED	: mgroup = new UnorderedGroup(); break;
			default: // we should never get here, unless we screwed up pretty bad
				throw new RuntimeException("Model group '" + sds.getName() + "' not implemented!");
		}
		
		return mgroup;
	}


	/**
	 * A reference refers to a previously defined global type. If the name is
	 * omitted, it is assumed to be equal to the name of the referenced type. In
	 * terms of SDS, the difference is this:<br>
	 * <br>
	 * <code>node "mobile" { type "phone" }</code> (explicitly named "mobile") <br>
	 * versus<br>
	 * <code>node { type "phone" }</code> (name will be "phone" as well)<br>
	 * <br>
	 * assuming that <code>phone</code> was defined as a global type.
	 */
	private static Component parseTypeReference(DataNode sds, DataNode type) throws SDSParseException {
		/*
		 * A reference is not a real component, but just a convenient shorthand way to
		 * refer to a global type in SDS notation. When we encounter one, we create a
		 * component from the global type it refers to and set its name (if explicitly
		 * named). We also set the global type on this component, so that when rendering
		 * back to SDS, we can recreate the correct reference. 
		 * Preconditions: the caller (parseComponentType) has already verified that this 
		 * node has a valid tag, no child components, only attributes with valid tags,
		 * and that there is a type attribute (e.g. type is not null).
		 * Postcondition: the caller will set the multiplicity on the returned type.
		 */
		
		// References should not have attributes other than type and occurs.
		List<Node> alist = sds.find(n -> n.isLeaf() && ! ( n.getName().equals(Attribute.TYPE.tag) 
			||  n.getName().equals(Attribute.OCCURS.tag) ));
		
		if (! alist.isEmpty())
			throw exception(sds, ATTRIBUTE_NOT_ALLOWED, alist.get(0).getName());
		
		Node root = sds.root();
		if (root.equals(sds)) // if we are the root ourself, we bail out right away.
			throw exception(type, TYPE_IS_UNKNOWN, type.getValue());
		
		// search all node declarations in the schema root for the referenced type
		Node refNode = null;
		for (Node cnode : root.find(n -> ! n.isLeaf() && n.getName().equals(Components.NODE.tag))) {
			if ( ((DataNode) cnode).getValue().equals(type.getValue()) ) refNode = cnode;
		}
		if (refNode == null || refNode.equals(sds)) // if we found nothing or ourself, we raise an error.
			throw exception(type, TYPE_IS_UNKNOWN, type.getValue());
		
		/*
		 * If we get here, we can parse the referenced type into a new component, but
		 * this poses a problem for self- and circular referencing types. If we keep on
		 * adding types that reference themselves or each other, we will ultimately run
		 * into a stack overflow. In order to support recursive references, we do not
		 * parse any child nodes in the global type, but use a late binding technique to
		 * return them when they are referenced. For details see NodeType.nodes().
		 */
		Component refComp = parseComponent((DataNode) refNode, true);
		refComp.setGlobalType(type.getValue());  // set the type we were created from
		
		// if a valid name is specified (different or equal to the type name) we set it
		String name = sds.getValue();
		if (! name.isEmpty()) {
			if (! SDA.isName(name))	throw exception(sds, NODE_NAME_INVALID, name);

			((NodeType) refComp).setTypeName(name);  // references to model groups not yet supported
		}
		
		return refComp;
	}


	/**
	 * This method is called from parseComponent() to create a NodeType from an SDS
	 * type definition, for both simple and complex types. The type parameter is a
	 * valid data type attribute, or null for complex types with node content only.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static //<T extends Comparable<? super T>> 
		NodeType parseNodeType(DataNode sds, DataNode type) throws SDSParseException {
		/*
		 * Preconditions: the caller has already verified this node has a valid tag, 
		 * and that all attributes have valid tags as well. This method is NOT called
		 * for an AnyType.
		 * Postcondition: the caller will set the multiplicity on the returned type.
		 */

		String name = sds.getValue(); // a name is required and should be valid
		if (name.isEmpty())	throw exception(sds, NAME_IS_EXPECTED);
		if (! SDA.isName(name)) throw exception(sds, NODE_NAME_INVALID, name);
		
		/*
		 * If type is null, it is a complex type without a data type, so data type
		 * validations do not apply. The only attribute allowed in a complex type is
		 * OCCURS, so we do check that before we return a node type.
		 */
		if (type == null) {

			List<Node> alist = sds.find(n -> n.isLeaf() && ! n.getName().equals(Attribute.OCCURS.tag) );
			if (! alist.isEmpty())
				throw exception(sds, ATTRIBUTE_NOT_ALLOWED, alist.get(0).getName());
			
			return new NodeType(name); // remaining code does not apply in this case
		}
		
		/*
		 * Get an instance of the requested data type and handle remaining attributes.
		 */
		DataType dataType = Schema.getDataType(type.getValue(), name);
		
		// Set the optional null-ability.
		DataNode nullable = getAttribute(sds, Attribute.NULLABLE, false);
		if (nullable != null) switch(nullable.getValue()) {
			case BooleanType.TRUE : dataType.setNullable(true); break;
			case BooleanType.FALSE : dataType.setNullable(false); break;
			default : 
				throw exception(nullable, ATTRIBUTE_INVALID, 
					Attribute.NULLABLE.tag, nullable.getValue(), "must be 'true' or 'false'");
		}
		
		// Set the optional pattern.
		DataNode regexp = getAttribute(sds, Attribute.PATTERN, false);
		if ( regexp != null) 
		try { 
			dataType.setPattern( Pattern.compile(regexp.getValue()) ); 
		} catch (PatternSyntaxException e) {
			throw exception(regexp, 
				ATTRIBUTE_INVALID, Attribute.PATTERN.tag, regexp.getValue(), e.getMessage());
		}
		
		// Set the length (only allowed on string and binary types).
		DataNode length = getAttribute(sds, Attribute.LENGTH, dataType instanceof AbstractStringType ? false : null);
		if (length != null) {
			try {
				NaturalInterval interval = NaturalInterval.from(length.getValue());
				((AbstractStringType) dataType).setLength(interval);
			} catch (IllegalArgumentException e) {
				throw exception(length, ATTRIBUTE_INVALID, 
					Attribute.LENGTH.tag, length.getValue(), e.getMessage());
			}
		}
		
		// Set the value range (only allowed on ranged types).
		DataNode range = getAttribute(sds, Attribute.VALUE, dataType instanceof RangedType ? false : null);
		if (range != null) {
			Interval interval;
			RangedType rangedType = (RangedType) dataType;
			try {	
				interval = Interval.from(range.getValue(), rangedType.valueConstructor());
			} catch (IllegalArgumentException e) {
				throw exception(range, 
					ATTRIBUTE_INVALID, Attribute.VALUE.tag, range.getValue(), e.getMessage());
			}
			rangedType.setRange(interval);
		}
		
		return dataType;
	}

	
	/**
	 * This helper method gets a specific attribute from a component node.
	 * 
	 * @param sds is a schema node, with child nodes.
	 * @param att is the attribute we want to retrieve.
	 * @param req controls the behavior:<br>
	 *	when <em>true</em>, the attribute is required and an exception is thrown if absent.<br>
	 *	when <em>false</em>, the attribute is optional and <code>null</code> is returned if absent.<br>
	 *	when <em>null</em>, the attribute is forbidden and an exception is thrown if present.<br>
	 *	An exception is also thrown if more than one attribute is found or if it has an empty value.
	 * 
	 * @return {@link Node} or <code>null</code>, or
	 * @throws SDSParseException
	 */
	private static DataNode getAttribute(DataNode sds, Attribute att, Boolean req) throws SDSParseException {

		List<DataNode> alist = sds.find(n -> n.isLeaf() && n.getName().equals(att.tag) );
		
		int size = alist.size();
		if (size == 0) {
			if (req == null || req == false) return null;
			throw exception(sds, ATTRIBUTE_MISSING, att.tag);
		}
		if (req == null)
			throw exception(sds, ATTRIBUTE_NOT_ALLOWED, att.tag);
		
		DataNode node = alist.get(0);
		if (node.getValue().isEmpty())
			throw exception(node, ATTRIBUTE_EMPTY, att.tag);
		if (size > 1)
			throw exception(node, ATTRIBUTE_NOT_SINGULAR, att.tag);
		
		return node;
	}
	
	
	/**
	 * Returns an SDS parse exception with an error node and formatted message.
	 * 
	 * @param node   the node where the error was found
	 * @param format a format message, and
	 * @param args arguments, as in {@link String#format}
	 * @return 
	 */
	private static SDSParseException exception(Node node, String format, Object... args) {
		return new SDSParseException(node, String.format(format, args));
	}
}

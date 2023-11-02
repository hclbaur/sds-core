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
import be.baur.sda.serialization.SDAParser;
import be.baur.sds.Component;
import be.baur.sds.DataType;
import be.baur.sds.NodeType;
import be.baur.sds.Schema;
import be.baur.sds.common.Content;
import be.baur.sds.common.Date;
import be.baur.sds.common.DateTime;
import be.baur.sds.common.Interval;
import be.baur.sds.common.NaturalInterval;
import be.baur.sds.content.AbstractStringType;
import be.baur.sds.content.AnyType;
import be.baur.sds.content.BinaryType;
import be.baur.sds.content.BooleanType;
import be.baur.sds.content.DateTimeType;
import be.baur.sds.content.DateType;
import be.baur.sds.content.DecimalType;
import be.baur.sds.content.IntegerType;
import be.baur.sds.content.RangedType;
import be.baur.sds.content.StringType;
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
 * The internal representation of the schema would be:
 * 
 * <pre>
 * Schema { 
 *    NodeType("greeting") { 
 *       StringType("message") 
 *   } 
 * }
 * </pre>
 * 
 * This parser relies on the default SDA parser.
 * <p>
 * @see Schema
 * @see SDAParser
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

	private static final String CONTENT_TYPE_UNKNOWN = "content type '%s' is unknown";
	private static final String NODE_NAME_INVALID = "'%s' is not a valid node name";
	private static final String NAME_NOT_EXPECTED = "name '%s' is not expected";
	private static final String NAME_IS_EXPECTED = "a name is expected";
	
	/**
	 * Creates a schema from a character input stream.
	 * 
	 * @return a schema
	 * @throws SDAParseException if an SDA parse exception occurs
	 * @throws SDSParseException if an SDS parse exception occurs
	 */
	public Schema parse(Reader input) throws IOException, SDAParseException, SDSParseException {

		DataNode sds = SDA.parser().parse(input);
		
//		if (sds.isLeaf())  // A schema node must have node content.
//			throw new SDSParseException(sds, String.format(SCHEMA_NODE_EXPECTED, Schema.TAG));

		return parse(sds);
	}


	/**
	 * Creates a schema from an SDA node obtained by parsing a schema in SDS
	 * notation.
	 * 
	 * @param sds a node with a schema definition
	 * @return a schema
	 * @throws SDSParseException if a schema exception occurs
	 */
	public static Schema parse(DataNode sds) throws SDSParseException {
		
		if (! sds.getName().equals(Schema.TAG))
			throw new SDSParseException(sds, String.format(A_NODE_IS_EXPECTED, Schema.TAG));
		
		if (! sds.isParent()) // a schema must have components
			throw new SDSParseException(sds, String.format(A_NODE_MUST_HAVE, sds.getName(), "content"));
		
		// a schema must not have attributes, except for an optional type reference
		List<Node> alist = sds.find(n -> n.isLeaf() && ! n.getName().equals(Attribute.TYPE.tag));
		
		if (! alist.isEmpty()) { // An unknown or forbidden attribute was found.
			Node a = alist.get(0);
			if (Attribute.get(a.getName()) == null)
				throw new SDSParseException(a, String.format(ATTRIBUTE_UNKNOWN, a.getName()));
			throw new SDSParseException(a, String.format(ATTRIBUTE_NOT_ALLOWED, a.getName()));
		}
		
		// build the schema
		Schema schema = new Schema();

		// parse global types, and add them to the schema (if all is in order).
		for (Node node : sds.find(n -> ! n.isLeaf())) {
			
			if (Components.get(node.getName()) == null) // component is unknown
				throw new SDSParseException(node, String.format(COMPONENT_UNKNOWN, node.getName()));
			
			if (! node.getName().equals(Components.NODE.tag)) // only node definitions are allowed here
				throw new SDSParseException(node, String.format(COMPONENT_NOT_ALLOWED, node.getName()));
			
			// global types must not have a multiplicity attribute
			getAttribute((DataNode) node, Attribute.OCCURS, null);
			
			schema.add(parseComponent((DataNode) node, false));
		}

		// set the designated root type reference (if specified and valid)
		DataNode type = getAttribute(sds, Attribute.TYPE, false);
		
		if (type != null) try {
			schema.setDefaultType(type.getValue());
		}
		catch (IllegalArgumentException e) {
			throw new SDSParseException(sds, String.format(ATTRIBUTE_INVALID, 
				Attribute.TYPE.tag, type.getValue(), e.getMessage()));
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
			throw new SDSParseException(sds, String.format(COMPONENT_UNKNOWN, sds.getName()));

		if (! sds.isParent()) // components must have attributes and/or child components
			throw new SDSParseException(sds, String.format(COMPONENT_INCOMPLETE, sds.getName()));
		
		for (Node node : sds.find(n -> n.isLeaf()))
			if (Attribute.get(node.getName()) == null) // all attributes must have a known name tag
				throw new SDSParseException(node, String.format(ATTRIBUTE_UNKNOWN, node.getName()));
		
		/*
		 * A component is either a node type (simple and/or complex), a model group, or
		 * a type reference. If there are no child nodes with complex content, it must
		 * be a simple type or a reference, which looks like a simple type but refers to
		 * a global type rather than a string, integer, boolean, etc.
		 */
		Component component; // the component to be returned at the end of this method
		boolean isNodeType = sds.getName().equals(Components.NODE.tag); // will be false for a model group
		List<Node> complexChildren = sds.find(n -> ! n.isLeaf()); // empty if simple type or reference
		
		// simple types and references MUSt have a content type, complex types MAY have one
		DataNode type = getAttribute(sds, Attribute.TYPE, isNodeType && complexChildren.isEmpty());
		Content content = (type == null) ? null : Content.get(type.getValue());
		
		if (isNodeType && complexChildren.isEmpty()) {
			
			if (content != null) // simple content type
				component = parseNodeType(sds, content); 
			else // otherwise it must be a reference
				component = parseTypeReference(sds, type);
		}
		else if (isNodeType) { // a complex type
			if (content == Content.ANY) // any type cannot contain type definitions
				throw new SDSParseException(sds, String.format(ATTRIBUTE_INVALID, 
					Attribute.TYPE.tag, Content.ANY.type, "node defines content"));
			component = parseNodeType(sds, content);
		}
		else // a model group
			component = parseModelGroup(sds);
		
		// Set the (optional) multiplicity of this component
		DataNode occurs = getAttribute(sds, Attribute.OCCURS, false);
		try {
			if (occurs != null)
				component.setMultiplicity(NaturalInterval.from(occurs.getValue()));
		} catch (IllegalArgumentException e) {
			throw new SDSParseException(occurs, 
				String.format(ATTRIBUTE_INVALID, Attribute.OCCURS.tag, occurs.getValue(), e.getMessage()));
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
	private static Component parseModelGroup(DataNode sds) throws SDSParseException {
		/*
		 * Preconditions: the caller (parseComponent) has already verified that this
		 * component has a valid tag, attributes with valid tags only, and one or more
		 * child components.
		 * Postcondition: the caller will set the multiplicity on the returned type.
		 */

		// Model groups should not have attributes other than OCCURS (maybe TYPE in the future).
//		Optional<Node> attribute = sds.getNodes().find(n -> n.isLeaf()).stream()
//			.filter(n -> ! (/* n.getName().equals(Attribute.TYPE.tag) 
//				|| */ n.getName().equals(Attribute.OCCURS.tag)) ).findFirst();
		
		List<Node> alist = sds.find(n -> n.isLeaf() && ! ( /* n.getName().equals(Attribute.TYPE.tag) || */ 
			n.getName().equals(Attribute.OCCURS.tag)) );
				
		if (! alist.isEmpty())
			throw new SDSParseException(sds, String.format(ATTRIBUTE_NOT_ALLOWED, alist.get(0).getName()));

		// model groups are not allowed to have names (maybe in the future)
		String name = sds.getValue();
		if (! name.isEmpty())
			throw new SDSParseException(sds, String.format(NAME_NOT_EXPECTED, name));

		// in a model group, there must be at least two components
		if (sds.find(n -> ! n.isLeaf()).size() < 2)
			throw new SDSParseException(sds, String.format(COMPONENT_INCOMPLETE, sds.getName()));

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
		 * node has a valid tag, no child components, and only attributes with valid tags. 
		 * Postcondition: the caller will set the multiplicity on the returned type.
		 */
		
		// References should not have attributes other than type and occurs.
		List<Node> alist = sds.find(n -> n.isLeaf() && ! ( n.getName().equals(Attribute.TYPE.tag) 
			||  n.getName().equals(Attribute.OCCURS.tag) ));
		
		if (! alist.isEmpty())
			throw new SDSParseException(sds, String.format(ATTRIBUTE_NOT_ALLOWED, alist.get(0).getName()));
		
		Node root = sds.root();
		if (root.equals(sds)) // if we are the root ourself, we bail out right away.
			throw new SDSParseException(type, String.format(CONTENT_TYPE_UNKNOWN, type.getValue()));
		
		// search all node declarations in the schema root for the referenced type
		Node refNode = null;
		for (Node cnode : root.find(n -> ! n.isLeaf() && n.getName().equals(Components.NODE.tag))) {
			if ( ((DataNode) cnode).getValue().equals(type.getValue()) ) refNode = cnode;
		}
		if (refNode == null || refNode.equals(sds)) // if we found nothing or ourself, we raise an error.
			throw new SDSParseException(type, String.format(CONTENT_TYPE_UNKNOWN, type.getValue()));
		
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
			if (! SDA.isName(name))
				throw new SDSParseException(sds, String.format(NODE_NAME_INVALID, name));
			((NodeType) refComp).setTypeName(name);  // references to model groups not yet supported
		}
		
		return refComp;
	}


	/**
	 * This method is called from parseComponent() to create a NodeType from an SDS
	 * type definition. Note that this method is called for both simple and complex
	 * types. For complex types without simple content, the content (type) will be
	 * null, whereas for data types, it will be a known simple content type.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T extends Comparable> 
		NodeType parseNodeType(DataNode sds, Content content) throws SDSParseException {
		/*
		 * Preconditions: the caller has already verified this node has a valid tag, 
		 * and that all attributes have valid tags as well. 
		 * Postcondition: the caller will set the multiplicity on the returned type.
		 */
		
		boolean isAnyType = (content == Content.ANY); // we need this a few times

		/*
		 * A name is required for regular types only, and optional for the "any" type,
		 * but if there IS a name, it should always be a valid node name.
		 */
		String name = sds.getValue();
		if (! isAnyType || ! name.isEmpty()) {
			if (name.isEmpty())
				throw new SDSParseException(sds, String.format(NAME_IS_EXPECTED));
			if (! SDA.isName(name))
				throw new SDSParseException(sds, String.format(NODE_NAME_INVALID, name));
		}
		
		/*
		 * If content type is null, it is a complex type definition and content type
		 * validations do not apply. The only attribute allowed in a complex type is
		 * OCCURS, so we do check that before we return a node type.
		 */
		if (content == null) {

			List<Node> alist = sds.find(n -> n.isLeaf() && ! n.getName().equals(Attribute.OCCURS.tag) );
			if (! alist.isEmpty())
				throw new SDSParseException(sds, String.format(ATTRIBUTE_NOT_ALLOWED, alist.get(0).getName()));
			
			return new NodeType(name); // remaining code does not apply in this case
		}
		
		DataType dataType;	// the type returned at the end of this method
		
		switch (content) {
			case STRING   : dataType = new StringType(name); break;
			case BINARY   : dataType = new BinaryType(name); break;
			case BOOLEAN  : dataType = new BooleanType(name); break;
			case INTEGER  : dataType = new IntegerType(name); break;
			case DECIMAL  : dataType = new DecimalType(name); break;
			case DATETIME : dataType = new DateTimeType(name); break;
			case DATE     : dataType = new DateType(name); break;
			case ANY      : dataType = new AnyType(name); break;
			default: // will never get here, unless we forgot to implement something...
				throw new RuntimeException("SDS type '" + content + "' not implemented!");
		}	
			
		// Handle remaining attributes, some of which are forbidden on the "any" type !
		
		// Set the null-ability (not allowed on the any type).
		DataNode nullable = getAttribute(sds, Attribute.NULLABLE, isAnyType? null : false);
		if (nullable != null) switch(nullable.getValue()) {
			case BooleanType.TRUE : dataType.setNullable(true); break;
			case BooleanType.FALSE : dataType.setNullable(false); break;
			default : 
				throw new SDSParseException(nullable, String.format(ATTRIBUTE_INVALID, 
					Attribute.NULLABLE.tag, nullable.getValue(), "must be 'true' or 'false'"));
		}
		
		// Set the pattern (not allowed on the any type).
		DataNode regexp = getAttribute(sds, Attribute.PATTERN, isAnyType ? null : false);
		if ( regexp != null) 
		try { 
			dataType.setPattern( Pattern.compile(regexp.getValue()) ); 
		} catch (PatternSyntaxException e) {
			throw new SDSParseException(regexp, 
				String.format(ATTRIBUTE_INVALID, Attribute.PATTERN.tag, regexp.getValue(), e.getMessage()));
		}
		
		// Set the length (only allowed on string and binary types).
		DataNode length = getAttribute(sds, Attribute.LENGTH, dataType instanceof AbstractStringType ? false : null);
		if (length != null) {
			try {
				NaturalInterval interval = NaturalInterval.from(length.getValue());
				((AbstractStringType) dataType).setLength(interval);
			} catch (IllegalArgumentException e) {
				throw new SDSParseException(length, String.format(ATTRIBUTE_INVALID, 
					Attribute.LENGTH.tag, length.getValue(), e.getMessage()));
			}
		}
		
		// Set the value range (only allowed on ranged types).
		DataNode range = getAttribute(sds, Attribute.VALUE, dataType instanceof RangedType ? false : null);
		if (range != null) {
			try {	
				Interval<T> interval;
				switch (content) {
					case INTEGER  : interval = (Interval<T>) Interval.from(range.getValue(), Integer.class); break;
					case DECIMAL  : interval = (Interval<T>) Interval.from(range.getValue(), Double.class); break;
					case DATETIME : interval = (Interval<T>) Interval.from(range.getValue(), DateTime.class); break;
					case DATE     : interval = (Interval<T>) Interval.from(range.getValue(), Date.class); break;
					default: // we will never get here, unless we forgot to implement something
						throw new RuntimeException("SDS type '" + content + "' not implemented!");
				}
				((RangedType<Comparable<?>>) dataType).setRange((Interval<T>) interval);
			} catch (IllegalArgumentException e) {
				throw new SDSParseException(range, 
					String.format(ATTRIBUTE_INVALID, Attribute.VALUE.tag, range.getValue(), e.getMessage()));
			}
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
			throw new SDSParseException(sds, String.format(ATTRIBUTE_MISSING, att.tag));
		}
		if (req == null)
			throw new SDSParseException(sds, String.format(ATTRIBUTE_NOT_ALLOWED, att.tag));
		
		DataNode node = alist.get(0);
		if (node.getValue().isEmpty())
			throw new SDSParseException(node, String.format(ATTRIBUTE_EMPTY, att.tag));
		if (size > 1)
			throw new SDSParseException(node, String.format(ATTRIBUTE_NOT_SINGULAR, att.tag));
		
		return node;
	}
}

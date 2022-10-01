package be.baur.sds.serialization;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.Optional;
import java.util.regex.PatternSyntaxException;

import be.baur.sda.Node;
import be.baur.sda.NodeSet;
import be.baur.sda.SDA;
import be.baur.sds.Component;
import be.baur.sds.NodeType;
import be.baur.sds.Schema;
import be.baur.sds.common.Attribute;
import be.baur.sds.common.Components;
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
 * <code>
 * schema { 
 *    node "greeting" { 
 *       node "message" { type "string" } 
 *    }
 * }
 * </code>
 * </pre>
 * 
 * the parser returns a <code>Schema</code> describing a SDA node named
 * 'greeting' with a single child node 'message' and a string value, like:
 * 
 * <pre>
 * <code>
 * greeting { 
 *    message "hello world" 
 * }
 * </code>
 * </pre>
 * 
 * The internal representation of the schema would be:
 * 
 * <pre>
 * <code>
 * Schema { 
 *    NodeType("greeting") { 
 *       StringType("message") 
 *   } 
 * }
 * </code>
 * </pre>
 * 
 * See also {@link Schema}.
 */
public final class SDSParser implements Parser {

	private static final String A_NODE_IS_EXPECTED = "a '%s' node is expected";
	private static final String A_NODE_MUST_HAVE = "a '%s' node must have %s";

	private static final String COMPONENT_NOT_ALLOWED = "component '%s' is not allowed here";
	private static final String COMPONENT_INCOMPLETE = "component '%s' is incomplete";
	private static final String COMPONENT_UNKNOWN = "component '%s' is unknown";
//	private static final String COMPONENT_EMPTY = "component '%s' is empty";

	private static final String ATTRIBUTE_NOT_SINGULAR = "attribute '%s' can occur only once";
	private static final String ATTRIBUTE_NOT_ALLOWED = "attribute '%s' is not allowed here";
	private static final String ATTRIBUTE_UNKNOWN = "attribute '%s' is unknown";
	private static final String ATTRIBUTE_MISSING = "attribute '%s' is missing";
	private static final String ATTRIBUTE_EMPTY = "attribute '%s' is empty";
	private static final String ATTRIBUTE_INVALID = "%s '%s' is invalid; %s";

	private static final String CONTENT_TYPE_UNKNOWN = "content type '%s' is unknown";
	private static final String NODE_NAME_INVALID = "'%s' is not a valid node name";
	private static final String NAME_NOT_ALLOWED = "name '%s' is not allowed here";
	
	
	public Schema parse(Reader input) throws IOException, ParseException, SchemaException  {

		Node sds = SDA.parser().parse(input);
		
//		if (! sds.isComplex())  // A schema node must have complex content.
//			throw new SchemaException(sds, String.format(SCHEMA_NODE_EXPECTED, Schema.TAG));

		return SDSParser.parse(sds);
	}


	/**
	 * Creates a schema from an SDA node obtained by parsing a schema in SDS
	 * notation.
	 * 
	 * @param sds a node with a schema definition
	 * @return a schema
	 * @throws SchemaException if a schema exception occurs
	 */
	public static Schema parse(Node sds) throws SchemaException {
		
		if (! sds.getName().equals(Schema.TAG))
			throw new SchemaException(sds, String.format(A_NODE_IS_EXPECTED, Schema.TAG));
		
		if (! sds.isParent()) // a schema must have components
			throw new SchemaException(sds, String.format(A_NODE_MUST_HAVE, sds.getName(), "components"));
		
		// a schema must not have attributes, except for an optional type reference
		Optional<Node> att = sds.getNodes().get(n -> ! n.isComplex()).stream()
			.filter(n -> ! n.getName().equals(Attribute.TYPE.tag)).findFirst();
			
		if (att.isPresent()) { // An unknown or forbidden attribute was found.
			if (Attribute.get(att.get().getName()) == null)
				throw new SchemaException(att.get(), String.format(ATTRIBUTE_UNKNOWN, att.get().getName()));
			throw new SchemaException(att.get(), String.format(ATTRIBUTE_NOT_ALLOWED, att.get().getName()));
		}
		
		// build the schema
		Schema schema = new Schema();

		// parse global types, and add them to the schema (if all is in order).
		for (Node node : sds.getNodes().get(n -> n.isComplex())) {
			
			if (Components.get(node.getName()) == null) // component is unknown
				throw new SchemaException(node, String.format(COMPONENT_UNKNOWN, node.getName()));
			
			if (! node.getName().equals(Components.NODE.tag)) // only node definitions are allowed here
				throw new SchemaException(node, String.format(COMPONENT_NOT_ALLOWED, node.getName()));
			
			// global types must not have a multiplicity attribute
			getAttribute(node, Attribute.OCCURS, null);
			
			schema.add((Node) parseComponent(node, false));
		}

		// set the designated root type reference (if specified and valid)
		Node type = getAttribute(sds, Attribute.TYPE, false);
		
		if (type != null) try {
			schema.setDefaultType(type.getValue());
		}
		catch (IllegalArgumentException e) {
			throw new SchemaException(schema, String.format(ATTRIBUTE_INVALID, 
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
	private static Component parseComponent(Node sds, boolean shallow) throws SchemaException {

		/*
		 * Whatever we get must be a valid component, and contain attributes, types
		 * and/or model groups. This method is called recursively and must deal with
		 * every possible component.
		 */
		if (Components.get(sds.getName()) == null) // component must have a known name tag
			throw new SchemaException(sds, String.format(COMPONENT_UNKNOWN, sds.getName()));

		if (! sds.isParent()) // components must have attributes and/or child components
			throw new SchemaException(sds, String.format(COMPONENT_INCOMPLETE, sds.getName()));
		
		for (Node node : sds.getNodes().get(n -> ! n.isComplex()))
			if (Attribute.get(node.getName()) == null) // all attributes must have a known name tag
				throw new SchemaException(node, String.format(ATTRIBUTE_UNKNOWN, node.getName()));
		
		/*
		 * A component is either a node type (simple and/or complex), a model group, or
		 * a type reference. If there are no child nodes with complex content, it must
		 * be a simple type or a reference, which looks like a simple type but refers to
		 * a global type rather than a string, integer, boolean, etc.
		 */
		Component component; // the component to be returned at the end of this method
		boolean isNodeType = sds.getName().equals(Components.NODE.tag); // will be false for a model group
		NodeSet complexChildren = sds.getNodes().get(n -> n.isComplex()); // empty if simple type or reference
		
		// simple types and references MUSt have a content type, complex/mixed types MAY have one
		Node type = getAttribute(sds, Attribute.TYPE, isNodeType && complexChildren.isEmpty());
		Content content = (type == null) ? null : Content.get(type.getValue());
		
		if (isNodeType && complexChildren.isEmpty()) {
			
			if (content != null) // simple content type
				component = parseNodeType(sds, content); 
			else // otherwise it must be a reference
				component = parseTypeReference(sds, type);
		}
		else if (isNodeType) // a complex or mixed type
			//component = parseComplexType(sds);
			component = parseNodeType(sds, content);
		else // a model group
			component = parseModelGroup(sds);
		
		// Set the (optional) multiplicity of this component
		Node occurs = getAttribute(sds, Attribute.OCCURS, false);
		try {
			if (occurs != null) {
				NaturalInterval interval = NaturalInterval.from(occurs.getValue());
				component.setMultiplicity(interval);
			}
		} catch (IllegalArgumentException e) {
			throw new SchemaException(occurs, 
				String.format(ATTRIBUTE_INVALID, Attribute.OCCURS.tag, occurs.getValue(), e.getMessage()));
		}
		
		// And finally, recursively parse and add any child components
		if (! shallow) // unless this is a shallow parse
			for (Node node : complexChildren)
				component.add((Node) parseComponent(node, false));

		return component;
	}


	/**
	 * This method is called by <code>parseComponent</code> to parse a node
	 * representing a model group in SDS syntax. For example, a choice group:
	 * 
	 * <pre>
	 * choice {
	 *     node "firstname" { type "string" }
	 *     node "lastname" { type "string" }
	 * }
	 * </pre>
	 * 
	 * @param sds a schema node
	 * @returns a {@link SequenceGroup}, {@link ChoiceGroup} or {@link UnorderedGroup}, 
	 */
	private static Component parseModelGroup(Node sds) throws SchemaException {
		/*
		 * Preconditions: the caller (parseComponent) has already verified that this
		 * component has a valid tag, attributes with valid tags only, and one or more
		 * child components.
		 * Postcondition: the caller will set the multiplicity on the returned type.
		 */

		// Model groups should not have attributes other than OCCURS (maybe TYPE in the future).
		Optional<Node> attribute = sds.getNodes().get(n -> ! n.isComplex()).stream()
			.filter(n -> ! (/* n.getName().equals(Attribute.TYPE.tag) 
				|| */ n.getName().equals(Attribute.OCCURS.tag)) ).findFirst();
				
		if (attribute.isPresent())
			throw new SchemaException(sds, String.format(ATTRIBUTE_NOT_ALLOWED, attribute.get().getName()));

		// Model groups are not allowed to have names (maybe in the future).
		String name = sds.getValue();
		if (! name.isEmpty()) // but for model groups it is not allowed
			throw new SchemaException(sds, String.format(NAME_NOT_ALLOWED, name));

		// Within a model group, there must be at least two components.
		if (sds.getNodes().get(n -> n.isComplex()).size() < 2)
			throw new SchemaException(sds, String.format(COMPONENT_INCOMPLETE, sds.getName()));

		ModelGroup mgroup;

		switch (Components.get(sds.getName())) {
			case GROUP		: mgroup = new SequenceGroup(); break;
			case CHOICE		: mgroup = new ChoiceGroup(); break;
			case UNORDERED	: mgroup = new UnorderedGroup(); break;
			default: // we should never get here, unless we screwed up pretty bad
				throw new RuntimeException("SDS component '" + sds.getName() + "' is not a model group!");
		}
		
		return mgroup;
	}


	/**
	 * A reference refers to a previously defined global type. If the name is
	 * omitted, it is assumed to be equal to the name of the referenced type.
	 * In terms of SDS, the difference is this:<br>
	 * <br>
	 * <code>node "mobile" { type "phone" }</code> (explicitly named "mobile")
	 * <br>versus<br>
	 * <code>node { type "phone" }</code> (name will be "phone" as well)<br>
	 * <br>
	 * assuming that <code>phone</code> was defined as a global type.
	 */
	private static Component parseTypeReference(Node sds, Node type) throws SchemaException {
		/*
		 * A reference is not a real component, but just a convenient shorthand way to
		 * refer to a global type in SDS notation. When we encounter one, we create a
		 * component from the global type it refers to and set its name (if explicitly
		 * named). We also set the global type on this component, so that when rendering
		 * back to SDS, we can recreate the correct reference.
		 * Preconditions: the caller (parseComponentType) has already verified that this
		 * node has no complex child types, and that all simple child types have valid
		 * attribute tags.
		 * Postcondition: the caller will set the multiplicity on the returned type.
		 */
		
		// References should not have attributes other than type and occurs.
		Optional<Node> attribute = sds.getNodes().get(n -> ! n.isComplex()).stream()
			.filter(n -> ! ( n.getName().equals(Attribute.TYPE.tag) 
				||  n.getName().equals(Attribute.OCCURS.tag) )).findFirst();
//      static final List<String> REFTAGS = Arrays.asList(Attribute.TYPE.tag, Attribute.NAME.tag, Attribute.OCCURS.tag);
//		Optional<Node> attribute = sds.getNodes().get(n -> ! n.isComplex()).stream()
//			.filter(n -> ! REFTAGS.contains(n.getName())).findFirst();
		if (attribute.isPresent())
			throw new SchemaException(sds, String.format(ATTRIBUTE_NOT_ALLOWED, attribute.get().getName()));
		
		Node root = sds.root();
		if (root.equals(sds)) // if we are the root ourself, we bail out right away.
			throw new SchemaException(type, String.format(CONTENT_TYPE_UNKNOWN, type.getValue()));
		
		// Search all node declarations in the schema root for the referenced type.
		Node refNode = null;
		for (Node cnode : root.getNodes().get(n -> n.isComplex()).get(Components.NODE.tag)) {
//			for (Node snode : cnode.getNodes().get(n -> ! n.isComplex()).get(Attribute.NAME.tag)) {
//				if ( snode.getValue().equals(type.getValue()) ) refNode = cnode; break;
//			}
//			if (refNode != null) break;
			if ( cnode.getValue().equals(type.getValue()) ) refNode = cnode;
		}
		if (refNode == null || refNode.equals(sds)) // if we found nothing or ourself, we raise an error.
			throw new SchemaException(type, String.format(CONTENT_TYPE_UNKNOWN, type.getValue()));
		
		/*
		 * If we get here, we can parse the referenced type into a new component, but
		 * this poses a problem for self- and circular referencing types. If we keep on
		 * adding types that reference themselves or each other, we will ultimately run
		 * into a stack overflow. In order to support recursive references, we do not
		 * parse any child nodes in the global type, but use a late binding technique to
		 * return them when they are referenced. For details see ComplexType.getNodes().
		 */
		Component refComp = parseComponent(refNode, true);
		refComp.setGlobalType(type.getValue());  // set the type we were created from
		
		// If a name is specified (different or equal to the type name) we set it
//		Node name = getAttribute(sds, Attribute.NAME, false);
//		if (name != null) {
//			if (! SDA.isName(name.getValue())) 
//				throw new SchemaException(name, String.format(NODE_NAME_INVALID, name.getValue()));
//			refComp.setName(name.getValue());
//		}
		String name = sds.getValue();
		if (! name.isEmpty()) {
			if (! SDA.isName(name)) // MUST BE IMPROVED, LOOKS STRANGE FOR EMPTY NAMES
				throw new SchemaException(sds, String.format(NODE_NAME_INVALID, name));
			refComp.setName(name);
		}
		
		return refComp;
	}

	
	/**
	 * This creates a {@link ComplexType} from an SDS node defining a complex SDA
	 * node or a {@link ModelGroup}. This method is called by parseComponent.
	 */
	private static NodeType parseComplexType(Node sds) throws SchemaException {

		/*
		 * Preconditions: the caller (parseComponentType) has already verified that this
		 * node has a valid tag, one or more complex child nodes, and that all of the
		 * simple child nodes have valid attribute tags. Postcondition: the caller will
		 * set the multiplicity on the returned type.
		 */

		// Complex types should not have attributes other than TYPE and OCCURS.
		Optional<Node> attribute = sds.getNodes().get(n -> ! n.isComplex()).stream()
			.filter(n -> ! (n.getName().equals(Attribute.TYPE.tag) 
				|| n.getName().equals(Attribute.OCCURS.tag)) ).findFirst();
				
		if (attribute.isPresent())
			throw new SchemaException(sds, String.format(ATTRIBUTE_NOT_ALLOWED, attribute.get().getName()));

		// A valid name is required
		String name = sds.getValue();
		if (! SDA.isName(name))  // MUST BE IMPROVED, LOOKS STRANGE FOR EMPTY NAMES
			throw new SchemaException(sds, String.format(NODE_NAME_INVALID, name));

		return new NodeType(name);
	}
	

	/**
	 * This creates a NodeType from an SDS type definition. This method is called by
	 * parseComponent(). Note that this method is called for both simple and complex
	 * (or mixed) types. For complex types, the content (type) will be null, whereas
	 * for simple and mixed types, it will be a known simple content type.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T extends Comparable> NodeType parseNodeType(Node sds, Content content) throws SchemaException {

		/*
		 * Preconditions: the caller (parseComponentType) has already verified that this
		 * node has a valid tag, and all attributes have valid tags as well.
		 * Postcondition: the caller will set the multiplicity on the returned type.
		 */
		boolean isAnyType = (content == Content.ANY); // we need this a few times

		String name = sds.getValue(); // MUST BE IMPROVED, LOOKS STRANGE FOR EMPTY NAMES
		if ((! isAnyType || ! name.isEmpty()) && ! SDA.isName(name))
			throw new SchemaException(sds, String.format(NODE_NAME_INVALID, name));

		NodeType nodeType;	// the node type returned at the end of this method
		
		if (content != null)
			switch (content) { // simple or mixed type
				case STRING   : nodeType = new StringType(name); break;
				case BINARY   : nodeType = new BinaryType(name); break;
				case BOOLEAN  : nodeType = new BooleanType(name); break;
				case INTEGER  : nodeType = new IntegerType(name); break;
				case DECIMAL  : nodeType = new DecimalType(name); break;
				case DATETIME : nodeType = new DateTimeType(name); break;
				case DATE     : nodeType = new DateType(name); break;
				case ANY      : nodeType = new AnyType(name); break;
				default: // will never get here, unless we forgot to implement something...
					throw new RuntimeException("SDS type '" + content + "' not implemented!");
			}	
		else { // content is null, so a complex type  MUST REFORMAT A LITTLE
		
		Optional<Node> attribute = sds.getNodes().get(n -> ! n.isComplex()).stream()
				.filter(n -> ! (n.getName().equals(Attribute.TYPE.tag) 
					|| n.getName().equals(Attribute.OCCURS.tag)) ).findFirst();
					
			if (attribute.isPresent())
				throw new SchemaException(sds, String.format(ATTRIBUTE_NOT_ALLOWED, attribute.get().getName()));
		
			return new NodeType(name); // remaining code does not apply
		}	
			
		// Handle remaining attributes, some of which are forbidden on the "any" type !
		
		// Set the null-ability (not allowed on the any type).
		Node nullable = getAttribute(sds, Attribute.NULLABLE, isAnyType? null : false);
		if (nullable != null) switch(nullable.getValue()) {
			case BooleanType.TRUE : nodeType.setNullable(true); break;
			case BooleanType.FALSE : nodeType.setNullable(false); break;
			default : 
				throw new SchemaException(nullable, String.format(ATTRIBUTE_INVALID, 
					Attribute.NULLABLE.tag, nullable.getValue(), "must be 'true' or 'false'"));
		}
		
		// Set the pattern (not allowed on the any type).
		Node pattern = getAttribute(sds, Attribute.PATTERN, isAnyType? null : false);
		try { 
			if (pattern != null) nodeType.setPatternExpr(pattern.getValue()); 
		} catch (PatternSyntaxException e) {
			throw new SchemaException(pattern, 
				String.format(ATTRIBUTE_INVALID, Attribute.PATTERN.tag, pattern.getValue(), e.getMessage()));
		}
		
		// Set the length (only allowed on string and binary types).
		Node length = getAttribute(sds, Attribute.LENGTH, nodeType instanceof AbstractStringType ? false : null);
		if (length != null) {
			try {
				NaturalInterval interval = NaturalInterval.from(length.getValue());
				((AbstractStringType) nodeType).setLength(interval);
			} catch (IllegalArgumentException e) {
				throw new SchemaException(length, String.format(ATTRIBUTE_INVALID, 
					Attribute.LENGTH.tag, length.getValue(), e.getMessage()));
			}
		}
		
		// Set the value range (only allowed on ranged types).
		Node range = getAttribute(sds, Attribute.VALUE, nodeType instanceof RangedType ? false : null);
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
				((RangedType<Comparable<?>>) nodeType).setRange((Interval<T>) interval);
			} catch (IllegalArgumentException e) {
				throw new SchemaException(range, 
					String.format(ATTRIBUTE_INVALID, Attribute.VALUE.tag, range.getValue(), e.getMessage()));
			}
		}
		
		return nodeType;
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
	 * @throws SchemaException
	 */
	private static Node getAttribute(Node sds, Attribute att, Boolean req) throws SchemaException {

		NodeSet attributes = sds.getNodes().get(n -> ! n.isComplex()).get(att.tag);
		int size = attributes.size();
		if (size == 0) {
			if (req == null || req == false) return null;
			throw new SchemaException(sds, String.format(ATTRIBUTE_MISSING, att.tag));
		}
		if (req == null)
			throw new SchemaException(sds, String.format(ATTRIBUTE_NOT_ALLOWED, att.tag));
		
		Node node = attributes.get(1);
		if (node.getValue().isEmpty())
			throw new SchemaException(node, String.format(ATTRIBUTE_EMPTY, att.tag));
		if (size > 1)
			throw new SchemaException(node, String.format(ATTRIBUTE_NOT_SINGULAR, att.tag));
		
		return node;
	}
}

package be.baur.sds.serialization;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;
import java.util.regex.PatternSyntaxException;

import be.baur.sda.Node;
import be.baur.sda.NodeSet;
import be.baur.sda.SDA;
import be.baur.sda.serialization.SyntaxException;
import be.baur.sds.ComplexType;
import be.baur.sds.ComponentType;
import be.baur.sds.Schema;
import be.baur.sds.SimpleType;
import be.baur.sds.common.Attribute;
import be.baur.sds.common.Component;
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
import be.baur.sds.model.AbstractGroup;
import be.baur.sds.model.ChoiceGroup;
import be.baur.sds.model.Group;
import be.baur.sds.model.UnorderedGroup;


/**
 * The default SDS parser. Converts SDS input into a {@link Schema}. For
 * example, when processing the following SDS definition:<br>
 * 
 * <pre>
 * schema { 
 * 	node { 
 * 		name "greeting" 
 * 		node { name "message" type "string" } 
 * 	} 
 * }
 * </pre>
 * 
 * the parser returns a <code>Schema</code> describing a SDA node named
 * 'greeting' with a single child named 'message' and a string value, such as:
 * 
 * <pre>
 * greeting { 
 * 	message "hello world" 
 * }
 * </pre>
 */
public final class SDSParser implements Parser {

	private static final String SCHEMA_NODE_EXPECTED = "a complex '%s' node is expected";
	private static final String SCHEMA_NODE_EMPTY = "a '%s' node cannot be empty";

	private static final String COMPONENT_NOT_ALLOWED = "component '%s' is not allowed here";
	private static final String COMPONENT_INCOMPLETE = "component '%s' is incomplete";
	private static final String COMPONENT_UNKNOWN = "component '%s' is unknown";
	private static final String COMPONENT_EMPTY = "component '%s' is empty";

	private static final String ATTRIBUTE_NOT_SINGULAR = "attribute '%s' can occur only once";
	private static final String ATTRIBUTE_NOT_ALLOWED = "attribute '%s' is not allowed here";
	private static final String ATTRIBUTE_UNKNOWN = "attribute '%s' is unknown";
	private static final String ATTRIBUTE_MISSING = "attribute '%s' is missing";
	private static final String ATTRIBUTE_EMPTY = "attribute '%s' is empty";
	private static final String ATTRIBUTE_INVALID = "%s '%s' is invalid; %s";

	private static final String CONTENT_TYPE_UNKNOWN = "content type '%s' is unknown";
	private static final String NODE_NAME_INVALID = "'%s' is not a valid node name";
	private static final String NAME_NOT_ALLOWED = "name '%s' is not allowed here";
	
	/**
	 * Parses a character stream with SDS content and return a <code>Schema</code>.
	 */
	public Schema parse(Reader input) throws IOException, SyntaxException, SchemaException  {

		Node sds = SDA.parser().parse(input);
		
		if (sds.getNodes() == null)  // A schema node must have complex content.
			throw new SchemaException(sds, String.format(SCHEMA_NODE_EXPECTED, Schema.TAG));

		return SDSParser.parse(sds);
	}


	/**
	 * Creates a {@link Schema} from an SDA node representing an SDS definition.
	 * @throws SchemaException
	 */
	public static Schema parse(Node sds) throws SchemaException {
		
		if (! sds.getName().equals(Schema.TAG))
			throw new SchemaException(sds, String.format(SCHEMA_NODE_EXPECTED, Schema.TAG));
		
		if (! sds.hasNodes()) // A schema must have child nodes.
			throw new SchemaException(sds, String.format(SCHEMA_NODE_EMPTY, sds.getName()));
		
		// Schema nodes must not have any attributes, except for an optional type reference.
		Optional<Node> att = sds.getNodes().get(n -> n.getNodes() == null).stream()
			.filter(n -> ! n.getName().equals(Attribute.TYPE.tag)).findFirst();
			
		if (att.isPresent()) { // An unknown or forbidden attribute was found.
			if (Attribute.get(att.get().getName()) == null)
				throw new SchemaException(att.get(), String.format(ATTRIBUTE_UNKNOWN, att.get().getName()));
			throw new SchemaException(att.get(), String.format(ATTRIBUTE_NOT_ALLOWED, att.get().getName()));
		}
		
		//if (sds.getNodes().isEmpty()) // A schema can never be empty.
		//	throw new SchemaException(sds, String.format(SCHEMA_NODE_EMPTY, sds.getName()));
		
		Schema schema = new Schema();

		// Parse global types, and add them to the schema (if all is in order).
		for (Node node : sds.getNodes().get(n -> n.getNodes() != null)) {
			
			if (Component.get(node.getName()) == null) // Component is unknown.
				throw new SchemaException(node, String.format(COMPONENT_UNKNOWN, node.getName()));
			
			if (! node.getName().equals(Component.NODE.tag)) // Only node definitions are allowed here.
				throw new SchemaException(node, String.format(COMPONENT_NOT_ALLOWED, node.getName()));
			
			// Global types must not have multiplicity.
			getAttribute(node, Attribute.OCCURS, null);
			
			schema.getNodes().add((Node) parseComponent(node, false));
		}

		// When done, set the designated root type reference (if specified and valid).
		Node type = getAttribute(sds, Attribute.TYPE, false);
		
		if (type != null) try {
			schema.setRootType(type.getValue());
		}
		catch (IllegalArgumentException e) {
			throw new SchemaException(schema, String.format(ATTRIBUTE_INVALID, 
				Attribute.TYPE.tag, type.getValue(), e.getMessage()));
		}

		return schema;
	}


	/**
	 * This parses an SDA node representing an SDS component, and returns a
	 * {@link SimpleType} or {@link ComplexType} which in turn may contain other
	 * components or model groups. It is called by <code>parse()</code> to parse an
	 * entire schema. A <code>shallow</code> parse means that no child nodes are
	 * parsed, which is used in the parsing of type references.
	 */
	private static ComponentType parseComponent(Node sds, boolean shallow) throws SchemaException {

		/*
		 * Whatever we get must be a valid component, and contain attributes, types
		 * and/or model groups. This method is called recursively and must deal with
		 * every possible component.
		 */
		if (Component.get(sds.getName()) == null) // Components must have a valid name.
			throw new SchemaException(sds, String.format(COMPONENT_UNKNOWN, sds.getName()));

		for (Node node : sds.getNodes().get(n -> n.getNodes() == null))
			if (Attribute.get(node.getName()) == null) // Attributes must have a valid name.
				throw new SchemaException(node, String.format(ATTRIBUTE_UNKNOWN, node.getName()));
		
		if (sds.getNodes().isEmpty()) // Components should never be empty.
			throw new SchemaException(sds, String.format(COMPONENT_EMPTY, sds.getName()));
		
		/*
		 * A component is either a simple type, a complex type, a model group, or a
		 * reference. If there are no complex child nodes, it is either a simple type or
		 * a reference, which looks like a simple type but refers to a custom content
		 * type (e.g. not one of string, integer, boolean, etc.
		 */
		ComponentType component; // The component to be returned at the end of this method.
		boolean isNode = sds.getName().equals(Component.NODE.tag); // False for a model group.
		NodeSet complexChildren = sds.getNodes().get(n -> n.getNodes() != null); // Empty if simple type or reference.
		
		if (isNode && complexChildren.isEmpty()) {
			
			Node type = getAttribute(sds, Attribute.TYPE, true);
			Content content = Content.get(type.getValue());
			
			if (content == null) // Custom content type, must be a reference...
				component = parseTypeReference(sds, type);
			else // ... or a simple type otherwise...
				component = parseSimpleType(sds, content); 
		}
		else // or else a complex type or model group.
			component = parseComplexType(sds);

		// Set the (optional) multiplicity of this component
		Node multi = getAttribute(sds, Attribute.OCCURS, false);
		try {
			if (multi != null) {
				NaturalInterval interval = NaturalInterval.from(multi.getValue());
				component.setMultiplicity(interval);
			}
		} catch (IllegalArgumentException e) {
			throw new SchemaException(multi, 
				String.format(ATTRIBUTE_INVALID, Attribute.OCCURS.tag, multi.getValue(), e.getMessage()));
		}
		
		// And finally, for a ComplexType, recursively parse and add all child components,
		if (component instanceof ComplexType && ! shallow) // unless it's a shallow parse.
			for (Node node : complexChildren)
				((ComplexType) component).getNodes().add((Node) parseComponent(node, false));

		return component;
	}


	/**
	 * A reference refers to a previously defined global type. If the name is
	 * omitted, it is assumed to be equal to the name of the referenced type.
	 * In terms of SDS, the difference is this:<br>
	 * <br>
	 * <code>node{ name "mobile" type "phone" }</code> (explicitly named "mobile")
	 * <br>versus<br>
	 * <code>node{ type "phone" }</code> (name will be "phone" as well)<br>
	 * <br>
	 * assuming that <code>phone</code> was defined as a global type, e.g.:<br>
	 * <br>
	 * <code>node{ name "phone" type "string" }</code><br>
	 * <br>
	 */
	private static ComponentType parseTypeReference(Node sds, Node type) throws SchemaException {
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
		Optional<Node> attribute = sds.getNodes().get(n -> n.getNodes() == null).stream()
			.filter(n -> ! ( n.getName().equals(Attribute.TYPE.tag) 
				||  n.getName().equals(Attribute.OCCURS.tag) )).findFirst();
//      static final List<String> REFTAGS = Arrays.asList(Attribute.TYPE.tag, Attribute.NAME.tag, Attribute.OCCURS.tag);
//		Optional<Node> attribute = sds.getNodes().get(n -> n.getNodes() == null).stream()
//			.filter(n -> ! REFTAGS.contains(n.getName())).findFirst();
		if (attribute.isPresent())
			throw new SchemaException(sds, String.format(ATTRIBUTE_NOT_ALLOWED, attribute.get().getName()));
		
		Node root = sds.root();
		if (root.equals(sds)) // if we are the root ourself, we bail out right away.
			throw new SchemaException(type, String.format(CONTENT_TYPE_UNKNOWN, type.getValue()));
		
		// Search all node declarations in the schema root for the referenced type.
		Node refNode = null;
		for (Node cnode : root.getNodes().get(n -> n.getNodes() != null).get(Component.NODE.tag)) {
//			for (Node snode : cnode.getNodes().get(n -> n.getNodes() == null).get(Attribute.NAME.tag)) {
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
		 * return them when referenced. For details see ComplexType.getNodes().
		 */
		ComponentType refComp = parseComponent(refNode, true);
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
			if (! SDA.isName(name)) 
				throw new SchemaException(sds, String.format(NODE_NAME_INVALID, name));
			refComp.setName(name);
		}
		
		return refComp;
	}

	
	/**
	 * This creates a {@link ComplexType} from an SDS node defining a complex SDA
	 * node or a {@link AbstractGroup}. This method is called by <code>parseComponent()</code>.
	 */
	private static ComplexType parseComplexType(Node sds) throws SchemaException {
		/*
		 * Preconditions: the caller (parseComponentType) has already verified that this
		 * node has a valid tag, one or more complex child nodes, and that all of the
		 * simple child nodes have valid attribute tags. 
		 * Postcondition: the caller will set the multiplicity on the returned type.
		 */

		// Complex types should not have attributes other than name and occurs.
//		Optional<Node> attribute = sds.getNodes().get(n -> n.getNodes() == null).stream()
//			.filter(n -> ! (n.getName().equals(Attribute.NAME.tag) 
//				|| n.getName().equals(Attribute.OCCURS.tag)) ).findFirst();
		// Complex types should not have attributes other than type and occurs.
		Optional<Node> attribute = sds.getNodes().get(n -> n.getNodes() == null).stream()
			.filter(n -> ! (n.getName().equals(Attribute.TYPE.tag) 
				|| n.getName().equals(Attribute.OCCURS.tag)) ).findFirst();
				
		if (attribute.isPresent())
			throw new SchemaException(sds, String.format(ATTRIBUTE_NOT_ALLOWED, attribute.get().getName()));

//		Node name = getAttribute(sds, Attribute.NAME, 
//			sds.getName().equals(Component.NODE.tag) ? true : null);
//		if (name != null && !SDA.isName(name.getValue())) 
//			throw new SchemaException(name, String.format(NODE_NAME_INVALID, name.getValue()));
		// A valid name is required if we are a node type
		String name = sds.getValue();
		
		if (sds.getName().equals(Component.NODE.tag)) {
			if (! SDA.isName(name))
				throw new SchemaException(sds, String.format(NODE_NAME_INVALID, name));
		}
		else if (! name.isEmpty()) // but for model groups it is not allowed
			throw new SchemaException(sds, String.format(NAME_NOT_ALLOWED, name));
				
		ComplexType complex;	// the complex type that will be returned at the end of this method.

		switch (Component.get(sds.getName())) {
			case NODE		: complex = new ComplexType(name); break;
			case GROUP		: complex = new Group(); break;
			case CHOICE		: complex = new ChoiceGroup(); break;
			case UNORDERED	: complex = new UnorderedGroup(); break;
			default: // will never get here, unless we forgot to implement something...
				throw new RuntimeException("SDS component '" + sds.getName() + "' not implemented!");
		}
		
		// Within a model group, we must have at least two components.
		if (complex instanceof AbstractGroup && sds.getNodes().get(n -> n.getNodes() != null).size() < 2)
			throw new SchemaException(sds, String.format(COMPONENT_INCOMPLETE, complex.getName()));
		
		return complex;
	}


	/**
	 * This creates a {@link SimpleType} from an SDS node defining a simple SDA
	 * node. This method is called by <code>parseComponent()</code>.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T extends Comparable> SimpleType parseSimpleType(Node sds, Content content) throws SchemaException {
		/*
		 * Preconditions: the caller (parseComponentType) has already verified that this
		 * node has no complex child nodes, that all simple child nodes have valid
		 * attribute tags, and have a valid simple content type.
		 * Postcondition: the caller will set the multiplicity on the returned type.
		 */
		boolean isAnyType = (content == Content.ANY); // we need this a few times
		
		// A name is mandatory except for the "any" type. But if present, it must be valid.
//		Node name = getAttribute(sds, Attribute.NAME, !isAnyType);
//		if (name != null && !SDA.isName(name.getValue())) 
//			throw new SchemaException(name, String.format(NODE_NAME_INVALID, name.getValue()));

		String name = sds.getValue();
		if ((! isAnyType || ! name.isEmpty()) && ! SDA.isName(name))
			throw new SchemaException(sds, String.format(NODE_NAME_INVALID, name));

		SimpleType simple;	// The simple type that will be returned at the end of this method.
		
		switch (content) {
			case STRING   : simple = new StringType(name); break;
			case BINARY   : simple = new BinaryType(name); break;
			case BOOLEAN  : simple = new BooleanType(name); break;
			case INTEGER  : simple = new IntegerType(name); break;
			case DECIMAL  : simple = new DecimalType(name); break;
			case DATETIME : simple = new DateTimeType(name); break;
			case DATE     : simple = new DateType(name); break;
			case ANY      : simple = new AnyType(name); break;
			default: // will never get here, unless we forgot to implement something...
				throw new RuntimeException("SDS type '" + content + "' not implemented!");
		}
		
		// Handle remaining attributes, some of which are forbidden on the "any" type !
		
		// Set the null-ability (not allowed on the any type).
		Node nullable = getAttribute(sds, Attribute.NULLABLE, isAnyType? null : false);
		if (nullable != null) switch(nullable.getValue()) {
			case BooleanType.TRUE : simple.setNullable(true); break;
			case BooleanType.FALSE : simple.setNullable(false); break;
			default : 
				throw new SchemaException(nullable, String.format(ATTRIBUTE_INVALID, 
					Attribute.NULLABLE.tag, nullable.getValue(), "must be 'true' or 'false'"));
		}
		
		// Set the pattern (not allowed on the any type).
		Node pattern = getAttribute(sds, Attribute.PATTERN, isAnyType? null : false);
		try { 
			if (pattern != null) simple.setPatternExpr(pattern.getValue()); 
		} catch (PatternSyntaxException e) {
			throw new SchemaException(pattern, 
				String.format(ATTRIBUTE_INVALID, Attribute.PATTERN.tag, pattern.getValue(), e.getMessage()));
		}
		
		// Set the length (only allowed on string and binary types).
		Node length = getAttribute(sds, Attribute.LENGTH, simple instanceof AbstractStringType ? false : null);
		if (length != null) {
			try {
				NaturalInterval interval = NaturalInterval.from(length.getValue());
				((AbstractStringType) simple).setLength(interval);
			} catch (IllegalArgumentException e) {
				throw new SchemaException(length, String.format(ATTRIBUTE_INVALID, 
					Attribute.LENGTH.tag, length.getValue(), e.getMessage()));
			}
		}
		
		// Set the value range (only allowed on ranged types).
		Node range = getAttribute(sds, Attribute.VALUE, simple instanceof RangedType ? false : null);
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
				((RangedType<Comparable<?>>) simple).setRange((Interval<T>) interval);
			} catch (IllegalArgumentException e) {
				throw new SchemaException(range, 
					String.format(ATTRIBUTE_INVALID, Attribute.VALUE.tag, range.getValue(), e.getMessage()));
			}
		}
		
		return simple;
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

		NodeSet attributes = sds.getNodes().get(n -> n.getNodes() == null).get(att.tag);
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

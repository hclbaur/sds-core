package be.baur.sds.serialization;

import java.io.Reader;
import java.util.Optional;
import java.util.regex.PatternSyntaxException;

import be.baur.sda.ComplexNode;
import be.baur.sda.Node;
import be.baur.sda.NodeSet;
import be.baur.sda.SDA;
import be.baur.sda.SimpleNode;
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
import be.baur.sds.model.ChoiceGroup;
import be.baur.sds.model.Group;
import be.baur.sds.model.AbstractGroup;
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
 * it returns a <code>Schema</code> describing a complex SDA node named
 * 'greeting' with a single child node named 'message' and a value of a simple
 * string type, such as:
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
	
	/**
	 * Parses a character stream with SDS content and return a <code>Schema</code>.
	 * @throws Exception
	 */
	public Schema parse(Reader input) throws Exception {

		Node sds = SDA.parser().parse(input);
		
		if (! (sds instanceof ComplexNode))  // Schema node must be complex.
			throw new SchemaException(sds, String.format(SCHEMA_NODE_EXPECTED, Schema.TAG));

		return SDSParser.parse((ComplexNode) sds);
	}


	/**
	 * Creates a {@link Schema} from an SDA node representing an SDS definition.
	 * @throws SchemaException
	 */
	public static Schema parse(ComplexNode sds) throws SchemaException {
		
		if (! sds.getName().equals(Schema.TAG))
			throw new SchemaException(sds, String.format(SCHEMA_NODE_EXPECTED, Schema.TAG));
		
		// Schema nodes must not have any attributes, except for an optional type reference.
		Optional<Node> att = sds.nodes.get(SimpleNode.class).stream()
			.filter(n -> ! n.getName().equals(Attribute.TYPE.tag)).findFirst();
			
		if (att.isPresent()) { // An unknown or forbidden attribute was found.
			if (Attribute.get(att.get().getName()) == null)
				throw new SchemaException(att.get(), String.format(ATTRIBUTE_UNKNOWN, att.get().getName()));
			throw new SchemaException(att.get(), String.format(ATTRIBUTE_NOT_ALLOWED, att.get().getName()));
		}
		
		if (sds.nodes.isEmpty()) // A schema can never be empty.
			throw new SchemaException(sds, String.format(SCHEMA_NODE_EMPTY, sds.getName()));
		
		Schema schema = new Schema();

		// Parse global types, and add them to the schema (if all is in order).
		for (Node node : sds.nodes.get(ComplexNode.class)) {
			
			if (Component.get(node.getName()) == null) // Component is unknown.
				throw new SchemaException(node, String.format(COMPONENT_UNKNOWN, node.getName()));
			
			if (! node.getName().equals(Component.NODE.tag)) // Only node definitions are allowed here.
				throw new SchemaException(node, String.format(COMPONENT_NOT_ALLOWED, node.getName()));
			
			// Global types must not have multiplicity.
			getAttribute((ComplexNode) node, Attribute.OCCURS, null);
			
			schema.nodes.add((Node) parseComponent((ComplexNode) node));
		}

		// When done, set the designated root type reference (if specified and valid).
		SimpleNode type = getAttribute(sds, Attribute.TYPE, false);
		
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
	 * This parses a node representing an SDS component, and returns a {@link SimpleType} 
	 * or {@link ComplexType} which in turn may contain other components or model groups.
	 * This method is called by <code>parse()</code> to parse an entire schema.
	 */
	private static ComponentType parseComponent(ComplexNode sds) throws SchemaException {

		/*
		 * Whatever we get must be a valid component, and contain attributes, types
		 * and/or model groups. This method is called recursively and must deal with
		 * every possible component.
		 */
		if (Component.get(sds.getName()) == null) // Components must have a valid name.
			throw new SchemaException(sds, String.format(COMPONENT_UNKNOWN, sds.getName()));

		for (Node node : sds.nodes.get(SimpleNode.class))
			if (Attribute.get(node.getName()) == null) // Attributes must have a valid name.
				throw new SchemaException(node, String.format(ATTRIBUTE_UNKNOWN, node.getName()));
		
		if (sds.nodes.isEmpty()) // Components should never be empty.
			throw new SchemaException(sds, String.format(COMPONENT_EMPTY, sds.getName()));
		
		/*
		 * A component is either a simple type, a complex type, a model group, or a
		 * reference. If there are no complex child nodes, it is either a simple type or
		 * a reference, which looks like a simple type but refers to a custom content
		 * type (e.g. not one of string, integer, boolean, etc.
		 */
		ComponentType component; // The component to be returned at the end of this method.
		boolean isNode = sds.getName().equals(Component.NODE.tag); // False for a model group.
		NodeSet complexChildren = sds.nodes.get(ComplexNode.class); // Empty if simple type or reference.
		
		if (isNode && complexChildren.isEmpty()) {
			
			SimpleNode type = getAttribute(sds, Attribute.TYPE, true);
			Content content = Content.get(type.getValue());
			
			if (content == null) // Custom content type, must be a reference...
				component = parseTypeReference(sds, type);
			else // ... or a simple type otherwise...
				component = parseSimpleType(sds, content); 
		}
		else // or else a complex type or model group.
			component = parseComplexType(sds);

		// Set the (optional) multiplicity of this component
		SimpleNode multi = getAttribute(sds, Attribute.OCCURS, false);
		try {
			if (multi != null) {
				NaturalInterval interval = NaturalInterval.from(multi.getValue());
				component.setMultiplicity(interval);
			}
		} catch (IllegalArgumentException e) {
			throw new SchemaException(multi, 
				String.format(ATTRIBUTE_INVALID, Attribute.OCCURS.tag, multi.getValue(), e.getMessage()));
		}
		
		// And finally, for a ComplexType, recursively parse and add all child components.
		if (component instanceof ComplexType) for (Node node : complexChildren)
			((ComplexType) component).nodes.add((Node) parseComponent((ComplexNode) node));

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
	private static ComponentType parseTypeReference(ComplexNode sds, SimpleNode type) throws SchemaException {
		/*
		 * The reference is not a real component, but just a convenient shorthand way to
		 * refer to a global type in SDS notation. When we encounter one, we create a
		 * component from the global type it refers to and set its multiplicity and name
		 * (if explicitly named). In addition, we set the global type on this component,
		 * so that when rendering back to SDS, we can recreate the correct reference.
		 * Preconditions: the caller (parseComponentType) has already verified that this
		 * node has no complex child nodes, and that all simple child nodes have valid
		 * attribute tags.
		 * Postcondition: the caller will set the multiplicity on the returned type.
		 */
		
		// References should not have attributes other than type, name and occurs.
		Optional<Node> attribute = sds.nodes.get(SimpleNode.class).stream()
			.filter(n -> ! ( n.getName().equals(Attribute.TYPE.tag) 
				|| n.getName().equals(Attribute.NAME.tag) || n.getName().equals(Attribute.OCCURS.tag)
			)).findFirst();
		if (attribute.isPresent())
			throw new SchemaException(sds, String.format(ATTRIBUTE_NOT_ALLOWED, attribute.get().getName()));
		
		ComplexNode root = (ComplexNode) sds.root();
		if (root.equals(sds)) // if we are the root ourself, we bail out right away.
			throw new SchemaException(type, String.format(CONTENT_TYPE_UNKNOWN, type.getValue()));
		
		// We now search all node declarations in the root for the referenced type.
		ComplexNode refNode = null;
		for (Node cnode : root.nodes.get(ComplexNode.class).get(Component.NODE.tag)) {
			for (Node snode : ((ComplexNode) cnode).nodes.get(SimpleNode.class).get(Attribute.NAME.tag)) {
				if ( ((SimpleNode) snode).getValue().equals(type.getValue()) ) refNode = (ComplexNode) cnode; break;
			}
			if (refNode != null) break;
		}
		if (refNode == null || refNode.equals(sds)) // if we found nothing or ourself, we raise an error.
			throw new SchemaException(type, String.format(CONTENT_TYPE_UNKNOWN, type.getValue()));
		
		// If we get here, we can parse the referenced node into a new component
		ComponentType refComp = parseComponent(refNode);
		refComp.setGlobalType(type.getValue());  // set the type we were created from
		
		// If a name is specified (different or equal to the type name) we set it
		SimpleNode name = getAttribute(sds, Attribute.NAME, false);
		if (name != null) {
			if (!SDA.isName(name.getValue())) 
				throw new SchemaException(name, String.format(NODE_NAME_INVALID, name.getValue()));
			refComp.setName(name.getValue());
		}
		
		return refComp;
	}

	
	/**
	 * This creates a {@link ComplexType} from an SDS node defining a complex SDA
	 * node or a {@link AbstractGroup}. This method is called by <code>parseComponent()</code>.
	 */
	private static ComplexType parseComplexType(ComplexNode sds) throws SchemaException {
		/*
		 * Preconditions: the caller (parseComponentType) has already verified that this
		 * node has a valid tag, one or more complex child nodes, and that all of the
		 * simple child nodes have valid attribute tags. 
		 * Postcondition: the caller will set the multiplicity on the returned type.
		 */

		// Complex types should not have attributes other than name and occurs.
		Optional<Node> attribute = sds.nodes.get(SimpleNode.class).stream()
			.filter(n -> ! (n.getName().equals(Attribute.NAME.tag) 
					|| n.getName().equals(Attribute.OCCURS.tag)) )
			.findFirst();
		
		if (attribute.isPresent())
			throw new SchemaException(sds, String.format(ATTRIBUTE_NOT_ALLOWED, attribute.get().getName()));

		// A valid name attribute is required unless we are a model group, in which case it is forbidden.
		SimpleNode name = getAttribute(sds, Attribute.NAME, 
			sds.getName().equals(Component.NODE.tag) ? true : null);
		if (name != null && !SDA.isName(name.getValue())) 
			throw new SchemaException(name, String.format(NODE_NAME_INVALID, name.getValue()));
		
		ComplexType complex;	// the complex type that will be returned at the end of this method.

		switch (Component.get(sds.getName())) {
			case NODE		: complex = new ComplexType(name.getValue()); break;
			case GROUP		: complex = new Group(); break;
			case CHOICE		: complex = new ChoiceGroup(); break;
			case UNORDERED	: complex = new UnorderedGroup(); break;
			default: // will never get here, unless we forgot to implement something...
				throw new RuntimeException("SDS component '" + sds.getName() + "' not implemented!");
		}
		
		// Within a model group, we must have at least two components.
		if (complex instanceof AbstractGroup && sds.nodes.get(ComplexNode.class).size() < 2)
			throw new SchemaException(sds, String.format(COMPONENT_INCOMPLETE, complex.getName()));
		
		return complex;
	}


	/**
	 * This creates a {@link SimpleType} from an SDS node defining a simple SDA
	 * node. This method is called by <code>parseComponent()</code>.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T extends Comparable> SimpleType parseSimpleType(ComplexNode sds, Content content) throws SchemaException {
		/*
		 * Preconditions: the caller (parseComponentType) has already verified that this
		 * node has no complex child nodes, that all simple child nodes have valid
		 * attribute tags, and have a valid simple content type.
		 * Postcondition: the caller will set the multiplicity on the returned type.
		 */
		boolean isAnyType = (content == Content.ANY); // we need this a few times
		
		// A name attribute is mandatory except for the "any" type. And it must be valid.
		SimpleNode name = getAttribute(sds, Attribute.NAME, !isAnyType);
		if (name != null && !SDA.isName(name.getValue())) 
			throw new SchemaException(name, String.format(NODE_NAME_INVALID, name.getValue()));
		
		SimpleType simple;	// The simple type that will be returned at the end of this method.
		
		switch (content) {
			case STRING   : simple = new StringType(name.getValue()); break;
			case BINARY   : simple = new BinaryType(name.getValue()); break;
			case BOOLEAN  : simple = new BooleanType(name.getValue()); break;
			case INTEGER  : simple = new IntegerType(name.getValue()); break;
			case DECIMAL  : simple = new DecimalType(name.getValue()); break;
			case DATETIME : simple = new DateTimeType(name.getValue()); break;
			case DATE     : simple = new DateType(name.getValue()); break;
			case ANY      : simple = new AnyType(name == null ? null : name.getValue()); break;
			default: // will never get here, unless we forgot to implement something...
				throw new RuntimeException("SDS type '" + content + "' not implemented!");
		}
		
		// Handle remaining attributes, some of which are forbidden on the "any" type !
		
		// Set the null-ability (not allowed on the any type).
		SimpleNode nullable = getAttribute(sds, Attribute.NULLABLE, isAnyType? null : false);
		if (nullable != null) switch(nullable.getValue()) {
			case BooleanType.TRUE : simple.setNullable(true); break;
			case BooleanType.FALSE : simple.setNullable(false); break;
			default : 
				throw new SchemaException(nullable, String.format(ATTRIBUTE_INVALID, 
					Attribute.NULLABLE.tag, nullable.getValue(), "must be 'true' or 'false'"));
		}
		
		// Set the pattern (not allowed on the any type).
		SimpleNode pattern = getAttribute(sds, Attribute.PATTERN, isAnyType? null : false);
		try { 
			if (pattern != null) simple.setPatternExpr(pattern.getValue()); 
		} catch (PatternSyntaxException e) {
			throw new SchemaException(pattern, 
				String.format(ATTRIBUTE_INVALID, Attribute.PATTERN.tag, pattern.getValue(), e.getMessage()));
		}
		
		// Set the length (only allowed on string and binary types).
		SimpleNode length = getAttribute(sds, Attribute.LENGTH, simple instanceof AbstractStringType ? false : null);
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
		SimpleNode range = getAttribute(sds, Attribute.VALUE, simple instanceof RangedType ? false : null);
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
				((RangedType<T>) simple).setRange((Interval<T>) interval);
			} catch (IllegalArgumentException e) {
				throw new SchemaException(range, 
					String.format(ATTRIBUTE_INVALID, Attribute.VALUE.tag, range.getValue(), e.getMessage()));
			}
		}
		
		return simple;
	}

	
	/**
	 * This helper method gets a specific attribute node from a component node.
	 * 
	 * @param sds is a schema node.
	 * @param att is the desired attribute.
	 * @param req controls the behavior:<br>
	 *	when <em>true</em>, the attribute is required and an exception is thrown if absent.<br>
	 *	when <em>false</em>, the attribute is optional and <code>null</code> is returned if absent.<br>
	 *	when <em>null</em>, the attribute is forbidden and an exception is thrown if present.<br>
	 *	An exception is also thrown if more than one attribute is found or if it has an empty value.
	 * 
	 * @return {@link SimpleNode} or <code>null</code>, or
	 * @throws SchemaException
	 */
	private static SimpleNode getAttribute(ComplexNode sds, Attribute att, Boolean req) throws SchemaException {

		NodeSet attributes = sds.nodes.get(SimpleNode.class).get(att.tag);
		int size = attributes.size();
		if (size == 0) {
			if (req == null || req == false) return null;
			throw new SchemaException(sds, String.format(ATTRIBUTE_MISSING, att.tag));
		}
		if (req == null)
			throw new SchemaException(sds, String.format(ATTRIBUTE_NOT_ALLOWED, att.tag));
		
		SimpleNode node = (SimpleNode) attributes.get(1);
		if (node.getValue().isEmpty())
			throw new SchemaException(node, String.format(ATTRIBUTE_EMPTY, att.tag));
		if (size > 1)
			throw new SchemaException(node, String.format(ATTRIBUTE_NOT_SINGULAR, att.tag));
		
		return node;
	}
}

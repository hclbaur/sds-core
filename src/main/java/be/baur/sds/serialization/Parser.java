package be.baur.sds.serialization;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

import be.baur.sda.ComplexNode;
import be.baur.sda.Node;
import be.baur.sda.NodeSet;
import be.baur.sda.SimpleNode;
import be.baur.sda.parse.SyntaxException;
import be.baur.sds.ComplexType;
import be.baur.sds.ComponentType;
import be.baur.sds.ModelGroup;
import be.baur.sds.Schema;
import be.baur.sds.SimpleType;
import be.baur.sds.common.Attribute;
import be.baur.sds.common.Component;
import be.baur.sds.common.Content;
import be.baur.sds.common.Date;
import be.baur.sds.common.DateTime;
import be.baur.sds.common.Interval;
import be.baur.sds.common.NaturalInterval;
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
import be.baur.sds.model.UnorderedGroup;

/**
 * The default SDS parser. Reads SDS input and returns a {@link Schema}. For
 * example, when processing the following schema definition:<br>
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
 * 'greeting' and a single simple child node named 'message' that can have 
 * any string value, such as:
 * 
 * <pre>
 * greeting { 
 * 	message "hello world" 
 * }
 * </pre>
 */
public class Parser {

	/**
	 * Parse a character stream with SDS content and return a {@link Schema}. Since
	 * SDS is conveniently written in SDA, we use the SDA {@link Parser} to parse
	 * the input, and then convert all {@link Node}s into schema component classes
	 * (which are also subclasses from SDA nodes).
	 * 
	 * @throws IOException
	 * @throws SyntaxException
	 * @throws SchemaException
	 */
	public static Schema parse(Reader input) throws SyntaxException, IOException, SchemaException {

		Node sds = (new be.baur.sda.parse.Parser()).parse(input);
		return parse(sds);
	}


	/**
	 * Create {@link Schema} from an SDA node representing an SDS definition.
	 * 
	 * @throws SchemaException
	 */
	public static Schema parse(Node sds) throws SchemaException {
		
		if (! (sds instanceof ComplexNode))
			throw new SchemaException(sds, SchemaException.ComponentExpected, Component.SCHEMA.tag);
		ComponentType component = parseComponent((ComplexNode) sds);

		if (! (component instanceof Schema))
			throw new SchemaException(sds, SchemaException.ComponentExpected, Component.SCHEMA.tag);
		Schema schema = (Schema) component;
		
		// Afterwards, check the designated root type reference (if set).
		if (schema.getGlobalType() != null && schema.get(schema.getGlobalType()).isEmpty())
			throw new SchemaException(schema, SchemaException.AttributeInvalid, 
				Attribute.TYPE.tag, schema.getGlobalType(), "no such global type");
		
		return schema;
	}


	/**
	 * This parses a node representing an SDS component, and returns a
	 * {@link SimpleType} or a {@link ComplexType} which may contain other
	 * components or model groups. For example:<br>
	 * 
	 * <pre>
	 * node { 
	 * 	name "firstname" type "string" 
	 * }
	 * </pre>
	 * 
	 * defines a simple SDA node with string content. Likewise,
	 * 
	 * <pre>
	 * node { 
	 * 	name "contact"
	 * 	node{ name "firstname" type "string" }
	 * 	node{ name "lastname" type "string" }
	 * }
	 * </pre>
	 * 
	 * defines the schema for a complex SDA node representing a "contact".<br>
	 * <br>
	 * This method is called by <code>parse()</code> to parse an entire schema.
	 */
	private static ComponentType parseComponent(ComplexNode sds) throws SchemaException {

		/*
		 * Whatever we get must be a valid component, and contain attributes, types
		 * and/or model groups. This method is called recursively and must deal with
		 * every possible component, including the (top-level) schema component itself.
		 */
		if (Component.get(sds.name) == null) // components must have a valid name.
			throw new SchemaException(sds, SchemaException.ComponentUnknown, sds.name);

		NodeSet children = sds.get();
		for (Node node : children.get(SimpleNode.class))
			if (Attribute.get(node.name) == null) // attributes must have a valid name.
				throw new SchemaException(node, SchemaException.AttributeUnknown, node.name);
		
		ComponentType returnType = null;
		/*
		 * The schema component must be the SDS root node, and if it has the type
		 * attribute, we set the designated root type. Other attributes not allowed.
		 */
		if (sds.name.equals(Component.SCHEMA.tag)) {
			
			if (sds.getRoot() != sds)
				throw new SchemaException(sds, SchemaException.ComponentNotAllowed, sds.name);

			Optional<Node> att = sds.get().get(SimpleNode.class).stream()
				.filter(n -> ! n.name.equals(Attribute.TYPE.tag)).findFirst();
			if (att.isPresent())
				throw new SchemaException(sds, SchemaException.AttributeNotAllowed, att.get().name);
			
			returnType = new Schema(); 
			SimpleNode type = getAttribute(sds, Attribute.TYPE, false);
			if (type != null) ((Schema) returnType).setGlobalType(type.value);
		}

		if (children.isEmpty()) // components should never be empty.
			throw new SchemaException(sds, SchemaException.ComponentEmpty, sds.name);
		
		// on the schema level, only node definitions are allowed (and no model groups).
		boolean isNode = sds.name.equals(Component.NODE.tag);
		if (! isNode && sds.getParent() != null && sds.getParent().name.equals(Component.SCHEMA.tag))
			throw new SchemaException(sds, SchemaException.ComponentNotAllowed, sds.name);
		
		/*
		 * If we get here, and it is not a schema node, it must be either a simple or or
		 * a complex type (or model group). If the node has no complex children, it is a
		 * simple type... or a reference, which looks like a simple type but refers to a
		 * custom content type (e.g. not one of string, integer, boolean, etc.
		 */
		NodeSet complexChildren = children.get(ComplexNode.class);
		if (isNode && complexChildren.isEmpty()) {
			
			SimpleNode type = getAttribute(sds, Attribute.TYPE, true);
			Content content = Content.get(type.value);
			if (content == null) return parseTypeReference(sds, type);
			return parseSimpleType(sds, content);
		}
		
		// Create a complex type, unless we already have it (Schema node, see above)
		if (returnType == null) returnType = parseComplexType(sds);
		
		// Recursively parse and add all child components
		for (Node node : complexChildren)
			((ComplexType) returnType).add((Node) parseComponent((ComplexNode) node));

		return returnType;
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
		 * The reference is a bit of an odd-ball. It is not a real component, but just a
		 * convenient shorthand way to refer to a global type in SDS notation. When we
		 * encounter one, we create a component from the global type it refers to and
		 * set its multiplicity and name (if explicitly named). In addition, we set the 
		 * global type on this component, so that when rendering back to SDS later, we
		 * can recreate the correct reference.
		 */
		
		// References should not have attributes other than type, name and multiplicity.
		Optional<Node> attribute = sds.get().get(SimpleNode.class).stream()
			.filter(n -> ! ( n.name.equals(Attribute.TYPE.tag) 
				|| n.name.equals(Attribute.NAME.tag) || n.name.equals(Attribute.MULTIPLICITY.tag)
			)).findFirst();
		if (attribute.isPresent())
			throw new SchemaException(sds, SchemaException.AttributeNotAllowed, attribute.get().name);
		
		ComplexNode root = (ComplexNode) sds.getRoot();
		if (root.equals(sds)) // if we are the root ourself, we bail out right away.
			throw new SchemaException(type, SchemaException.ContentTypeUnknown, type.value);
		
		// We now search all node declarations in the root for the referenced type.
		ComplexNode refNode = null;
		for (Node cnode : root.get().get(ComplexNode.class).get(Component.NODE.tag)) {
			for (Node snode : ((ComplexNode) cnode).get().get(SimpleNode.class).get(Attribute.NAME.tag)) {
				if ( ((SimpleNode) snode).value.equals(type.value) ) refNode = (ComplexNode) cnode; break;
			}
			if (refNode != null) break;
		}
		if (refNode == null || refNode.equals(sds)) // if we found nothing or ourself, we raise an error.
			throw new SchemaException(type, SchemaException.ContentTypeUnknown, type.value);
		
		// If we get here, we can parse the referenced node into a new component
		ComponentType refComp = parseComponent(refNode);
		refComp.setGlobalType(type.value);  // set the type we were created from
		
		// If a name is specified (different or equal to the type name) we set it
		SimpleNode name = getAttribute(sds, Attribute.NAME, false);
		if (name != null) refComp.setName(name.value);
		
		// Get the multiplicity of the type reference and set it on the component
		SimpleNode multiplicity = getAttribute(sds, Attribute.MULTIPLICITY, false);
		try {
			if (multiplicity != null) {
				NaturalInterval interval = NaturalInterval.from(multiplicity.value);
				refComp.setMultiplicity(interval);
			}
		} catch (Exception e) {
			throw new SchemaException(multiplicity, 
				SchemaException.AttributeInvalid, multiplicity.name, multiplicity.value, e.getMessage());
		}
		
		return refComp;
	}

	/**
	 * This creates a {@link ComplexType} from a node representing complex content,
	 * for example:
	 * 
	 * <pre>
	 * node{
	 * 	name "contact" 
	 * 	node{ 
	 * 		name "firstname" type "string" 
	 * 	}
	 * }
	 * </pre>
	 * 
	 * defines a complex SDA node with name "contact" containing a simple
	 * string-type node with name "firstname".<br>
	 * <br>
	 * This method is called by <code>parseComponent()</code> and may also return a {@link ModelGroup}.
	 */
	private static ComplexType parseComplexType(ComplexNode sds) throws SchemaException {
		/*
		 * Sanity checks: the caller has already verified that we have a valid tag, one
		 * or more complex child nodes, and that all of our simple nodes (attributes)
		 * have valid tags.
		 */

		// Complex types should not have attributes other than name and multiplicity.
		Optional<Node> attribute = sds.get().get(SimpleNode.class).stream()
			.filter(n -> ! (n.name.equals(Attribute.NAME.tag) 
					|| n.name.equals(Attribute.MULTIPLICITY.tag)) )
			.findFirst();
		if (attribute.isPresent())
			throw new SchemaException(sds, SchemaException.AttributeNotAllowed, attribute.get().name);

		// The name attribute is required unless we are a model group, in which case it is forbidden.
		SimpleNode name = getAttribute(sds, Attribute.NAME, 
				sds.name.equals(Component.NODE.tag) ? true : null);

		ComplexType cType;	// the complex type that will be returned at the end of this method.

		switch (Component.get(sds.name)) {
			case NODE		: cType = new ComplexType(name.value); break;
			case GROUP		: cType = new Group(); break;
			case CHOICE		: cType = new ChoiceGroup(); break;
			case UNORDERED	: cType = new UnorderedGroup(); break;
			default: // will never get here, unless we forgot to implement something...
				throw new RuntimeException("SDS component '" + sds.name + "' not implemented!");
		}
		
		// In a model group, we must have at least two other components.
		if (cType instanceof ModelGroup && sds.get().get(ComplexNode.class).size() < 2)
			throw new SchemaException(sds, SchemaException.ComponentIncomplete, cType.name);
		
		// Get the multiplicity, which is optional but forbidden on a global type.
		SimpleNode multiplicity = getAttribute(sds, Attribute.MULTIPLICITY, 
			(sds.getParent() != null && sds.getParent().name.equals(Component.SCHEMA.tag)) ? null : false);
		try {
			if (multiplicity != null) {
				NaturalInterval interval = NaturalInterval.from(multiplicity.value);
				cType.setMultiplicity(interval);
			}
		} catch (Exception e) {
			throw new SchemaException(multiplicity, 
				SchemaException.AttributeInvalid, multiplicity.name, multiplicity.value, e.getMessage());
		}
		
		return cType;
	}


	/**
	 * This creates a {@link SimpleType} from a node representing simple content,
	 * for example:
	 * 
	 * <pre>
	 * node{ name "firstname" type "string" }
	 * </pre>
	 * 
	 * defines a simple SDA node "firstname" with string content.<br>
	 * <br>
	 * This method is called by <code>parseComponent()</code> and supports all
	 * content types known in SDS, including the "any" type and type references.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T extends Comparable> SimpleType parseSimpleType(ComplexNode sds, Content content) throws SchemaException {
		/*
		 * Sanity checks: the caller has already verified that we have no complex child
		 * nodes, that all of our simple nodes (attributes) have valid tags, and have a 
		 * valid simple content type.
		 */

		// A name attribute is mandatory except for the "any" type.
		boolean isAnyType = (content == Content.ANY);
		SimpleNode name = getAttribute(sds, Attribute.NAME, !isAnyType);
		
		SimpleType sType;	// the simple type that will be returned at the end of this method.
		
		switch (content) {
			case STRING   : sType = new StringType(name.value); break;
			case BINARY   : sType = new BinaryType(name.value); break;
			case BOOLEAN  : sType = new BooleanType(name.value); break;
			case INTEGER  : sType = new IntegerType(name.value); break;
			case DECIMAL  : sType = new DecimalType(name.value); break;
			case DATETIME : sType = new DateTimeType(name.value); break;
			case DATE     : sType = new DateType(name.value); break;
			case ANY      : sType = new AnyType(name == null ? null : name.value); break;
			default: // will never get here, unless we forgot to implement something...
				throw new RuntimeException("SDS type '" + content + "' not implemented!");
		}
		
		// Handle remaining attributes, some of which are forbidden on the "any" type!
		
		// Get the multiplicity, which is optional but forbidden on a global type.
		SimpleNode multiplicity = getAttribute(sds, Attribute.MULTIPLICITY, 
			(sds.getParent() != null && sds.getParent().name.equals(Component.SCHEMA.tag)) ? null : false);
		try {
			if (multiplicity != null) {
				NaturalInterval interval = NaturalInterval.from(multiplicity.value);
				sType.setMultiplicity(interval);
			}
		} catch (Exception e) {
			throw new SchemaException(multiplicity, 
				SchemaException.AttributeInvalid, multiplicity.name, multiplicity.value, e.getMessage());
		}
		
		// Set the null-ability (not allowed on the any type).
		SimpleNode nullable = getAttribute(sds, Attribute.NULLABLE, isAnyType? null : false);
		if (nullable != null) switch(nullable.value) {
			case BooleanType.TRUE : sType.setNullable(true); break;
			case BooleanType.FALSE : sType.setNullable(false); break;
			default : throw new SchemaException(nullable, 
				SchemaException.AttributeInvalid, nullable.name, nullable.value, "must be 'true' or 'false'");
		}
		
		// Set the pattern (not allowed on the any type).
		SimpleNode pattern = getAttribute(sds, Attribute.PATTERN, isAnyType? null : false);
		try { 
			if (pattern != null) sType.setPatternExpr(pattern.value); 
		} catch (Exception e) {
			throw new SchemaException(pattern, 
				SchemaException.AttributeInvalid, pattern.name, pattern.value, e.getMessage());
		}
		
		// Set the length (only allowed on string and binary types).
		SimpleNode length = getAttribute(sds, Attribute.LENGTH, sType instanceof StringType ? false : null);
		if (length != null) {
			try {
				NaturalInterval interval = NaturalInterval.from(length.value);
				((StringType) sType).setLength(interval);
			} catch (Exception e) {
				throw new SchemaException(length, SchemaException.AttributeInvalid, length.name, length.value, e.getMessage());
			}
		}
		
		// Set the value range (only allowed on ranged types).
		SimpleNode range = getAttribute(sds, Attribute.VALUE, sType instanceof RangedType ? false : null);
		if (range != null) {
			try {	
				Interval<T> interval;
				switch (content) {
					case INTEGER  : interval = (Interval<T>) Interval.from(range.value, Integer.class); break;
					case DECIMAL  : interval = (Interval<T>) Interval.from(range.value, Double.class); break;
					case DATETIME : interval = (Interval<T>) Interval.from(range.value, DateTime.class); break;
					case DATE     : interval = (Interval<T>) Interval.from(range.value, Date.class); break;
					default: // we will never get here, unless we forgot to implement something
						throw new RuntimeException("SDS type '" + content + "' not implemented!");
				}
				((RangedType<T>) sType).setRange((Interval<T>) interval);
			} catch (Exception e) {
				throw new SchemaException(range, 
					SchemaException.AttributeInvalid, range.name, range.value, e.getMessage());
			}
		}
		
		return sType;
	}

	
//	/**
//	 * This helper method returns all attribute nodes with a particular tag 
//	 * from a component, or an empty set if no such attribute was found.
//	 */
//	private static NodeSet findNodes(ComplexNode sds, String tag) {
//
//		return sds.get().get(SimpleNode.class).get(tag);
//	}
	
	
	/**
	 * This helper method returns a specific attribute node from a complex node.
	 * 
	 * @param sds is a schema node.
	 * @param att is the desired attribute.
	 * @param req controls the behavior:<br>
	 *	when <em>true</em>, the attribute is required and an exception is thrown if absent.<br>
	 *	when <em>false</em>, the attribute is optional and <code>null</code> is returned if absent.<br>
	 *	when <em>null</em>, the attribute is forbidden and an exception is thrown if present.<br>
	 *	An exception is also thrown if more than one attribute is found or if it has an empty value.
	 * 
	 * @return {@link SimpleNode} or <code>null</code>.
	 */
	private static SimpleNode getAttribute(ComplexNode sds, Attribute att, Boolean req) throws SchemaException {

		NodeSet set = sds.get().get(SimpleNode.class).get(att.tag); //findNodes(sds, att.tag);
		int size = set.size();
		if (size == 0) {
			if (req == null || req == false) return null;
			throw new SchemaException(sds, SchemaException.AttributeMissing, att.tag);
		}
		if (req == null)
			throw new SchemaException(sds, SchemaException.AttributeNotAllowed, att.tag);
		
		SimpleNode node = (SimpleNode) set.get(1);
		if (node.value.isEmpty())
			throw new SchemaException(node, SchemaException.AttributeEmpty, att.tag);
		if (size > 1)
			throw new SchemaException(node, SchemaException.AttributeNotSingular, att.tag);
		return node;
	}
}

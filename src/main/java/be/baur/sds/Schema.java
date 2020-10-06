package be.baur.sds;

import java.io.IOException;
import java.io.Reader;

import be.baur.sda.ComplexNode;
import be.baur.sda.Node;
import be.baur.sda.NodeSet;
import be.baur.sda.SimpleNode;
import be.baur.sda.parse.Parser;
import be.baur.sda.parse.SyntaxException;
import be.baur.sds.common.Attribute;
import be.baur.sds.common.Component;

/**
 * A <code>Schema</code> is a class representing the entire schema, converted
 * from SDS notation into an object structure. Since SDS is conveniently written
 * in SDA, we typically use the SDA parser to read the schema, and then convert
 * all {@link Node}s into schema component classes (which are also subclasses
 * from SDA nodes). Because the <code>Schema</code> is a container for types, it
 * extends {@link ComplexNode}.
 */
public class Schema extends ComplexNode {
	
	private String root = null;   // name of the designated root node
	
	public Schema() {
		super(Component.SCHEMA.tag);
	}
	
	/** Create schema from an input stream with SDS content. */
	public static Schema parse(Reader input) throws IOException, SyntaxException, SchemaException {
		
		Node sds = (new Parser()).parse(input);
		//System.out.println("sds in: " + sds);
		if (! (sds instanceof ComplexNode) )
			throw new SchemaException(sds, SchemaException.ComponentExpected, Component.SCHEMA.tag);

		return Schema.from((ComplexNode) sds);
	}

	/** Create schema from a complex node representing an SDS structure. */
	public static Schema from(ComplexNode sds) throws SchemaException 
	{
		// If the root node is not called "schema", we throw up right away :)
		if (!sds.name.equals(Component.SCHEMA.tag))
			throw new SchemaException(sds, SchemaException.ComponentExpected, Component.SCHEMA.tag);

		NodeSet children = sds.get();
		if (children.isEmpty()) // A schema must not be empty...
			throw new SchemaException(sds, SchemaException.ComponentEmpty, sds.name);

		Schema schema = new Schema();
		SimpleNode type = Attribute.getNode(sds, Attribute.TYPE, false);
		if (type != null ) schema.root = type.value; // set the root type
		
		/**
		 * Iterate all children. We expect to find only type definitions, e.g. nodes
		 * that look like "node { ... }" and no attributes except the type attribute
		 * designating the root node (which we will explicitly look for).
		 */
		boolean foundRoot = false;
		for (Node node : children) 
		{	
			if (node instanceof SimpleNode) {
				if (Attribute.get(node.name) == null)
					throw new SchemaException(node, SchemaException.AttributeUnknown, node.name);
				if (! node.name.equals(Attribute.TYPE.tag))
					throw new SchemaException(node, SchemaException.AttributeNotAllowed, node.name);
				continue;
			}
			
			// If we get here we are dealing with a component rather than an attribute 
			if (Component.get(node.name) == null)
				throw new SchemaException(node, SchemaException.ComponentUnknown, node.name);
			if (! node.name.equals(Component.NODE.tag)) // model groups are not allowed here
				throw new SchemaException(node, SchemaException.ComponentNotAllowed, node.name);

			// Get the name attribute of this component and check it against the root type
			SimpleNode name = Attribute.getNode((ComplexNode)node, Attribute.NAME, false);
			if (name != null && schema.root != null && ! foundRoot) 
				foundRoot = name.value.equals(schema.root);
			
			// The multiplicity attribute is not allowed for global types.
			Attribute.getNode((ComplexNode)node, Attribute.MULTIPLICITY, null);

			// If we get here, it must be a type definition, so parse it and add it to the schema.
			schema.add((Node) Type.from((ComplexNode) node));
		}
		
		// At the end, check if we encountered the root type
		if (schema.root != null && ! foundRoot)
			throw new SchemaException(type, SchemaException.AttributeInvalid, 
					type.name, schema.root, "no such root node");
		
		return schema;
	}

	
	public ComplexNode toNode() {
		
		ComplexNode node = new ComplexNode(Component.SCHEMA.tag);
		if (root != null) node.add(new SimpleNode(Attribute.TYPE.tag, root));
		
		for (Node child : this.get())
			node.add(((Type) child).toNode());

		return node;
	}
	
	public String toString() {
		return toNode().toString();
	}
	
}

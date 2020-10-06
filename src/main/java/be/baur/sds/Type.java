package be.baur.sds;

import be.baur.sda.ComplexNode;
import be.baur.sda.Node;
import be.baur.sda.NodeSet;
import be.baur.sda.SimpleNode;
import be.baur.sds.common.Attribute;
import be.baur.sds.common.Component;
import be.baur.sds.common.NaturalInterval;

/**
 * This is the interface that should be implemented by classes representing a
 * schema definition, such as {@link SimpleType} and {@link ComplexType}.
 */
public interface Type {

	/**
	 * This is a factory method to create a <code>Type</code> from a complex node
	 * representing an SDS component. The type can be a <code>SimpleType</code> or a
	 * <code>ComplexType</code>, which can contain other components. For
	 * example:<br>
	 * <br>
	 * <code>node{ name "firstname" type "string" }</code> defines a (simple) SDA
	 * node with string content. Likewise,<br>
	 * <br>
	 * <code>node{ name "contact"
	 * <pre>
	 * node{ name "firstname" type "string" }
	 * node{ name "lastname" type "string" }</pre>
	 * }</code> defines the schema for a (complex) SDA node representing a
	 * "contact".<br>
	 * <br>
	 * This method is called by {@link Schema}<code>.from()</code> to parse an
	 * entire schema, but can also be called to parse individual components.
	 */
	static Type from(ComplexNode sds) throws SchemaException {
		/*
		 * Sanity checks: whatever we get, it must be a valid component, typically
		 * containing attributes, simple or complex types and/or model groups. It can
		 * not be empty and it can not be a schema node, because we are not allowed to
		 * nest schema.
		 */
		if (Component.get(sds.name) == null)
			throw new SchemaException(sds, SchemaException.ComponentUnknown, sds.name);

		if (sds.name.equals(Component.SCHEMA.tag))
			throw new SchemaException(sds, SchemaException.ComponentNotAllowed, sds.name);

		NodeSet children = sds.get();
		if (children.isEmpty())
			throw new SchemaException(sds, SchemaException.ComponentEmpty, sds.name);

		// All attributes (simple nodes) must have valid tags.
		for (Node node : children.get(SimpleNode.class))
			if (Attribute.get(node.name) == null)
				throw new SchemaException(node, SchemaException.AttributeUnknown, node.name);

		/*
		 * The idea is that we now call the respective from() methods of the Complex-
		 * and SimpleType classes since these will know best how to build and validate
		 * themselves. But how do we know whether a component defines a complex or a
		 * simple type, if both start with a 'node' tag? If there are no complex
		 * children, we assume it is a simple type, since a complex type definition must
		 * have child components.
		 */
		NodeSet complexChildren = children.get(ComplexNode.class);
		if (complexChildren.isEmpty() && sds.name.equals(Component.NODE.tag))
			return SimpleType.from(sds);

		// If we get here we create a complex type, and recursively add all child types.
		ComplexType complexType = ComplexType.from(sds);
		for (Node node : complexChildren) {
			complexType.add((Node) Type.from((ComplexNode) node));
		}

		return complexType;
	}


	/**
	 * Returns the multiplicity field of this component. The default value is
	 * <code>null</code>, which means the component must occur exactly once.
	 * Convenience methods <code>minOccurs()</code> and <code>maxOccurs()</code>
	 * return the lower and upper multiplicity limits.
	 */
	NaturalInterval getMultiplicity();

	/** Sets the multiplicity of this component. */
	void setMultiplicity(NaturalInterval multiplicity);

	/** The minimum number of times this component must occur within its context. */
	default int minOccurs() {
		return getMultiplicity() != null ? getMultiplicity().lower : 1;
	}

	/** The maximum number of times this component may occur within its context. */
	default int maxOccurs() {
		return getMultiplicity() != null ? getMultiplicity().upper : 1;
	}

	/** Represent this type as an SDS node. */
	public ComplexNode toNode();

	/** Represent this type as an SDS string. */
	public String toString();
}

package be.baur.sds;

import be.baur.sda.ComplexNode;
import be.baur.sda.Node;
import be.baur.sda.SimpleNode;
import be.baur.sds.common.Attribute;

/**
 * A <code>Schema</code> represents a entire SDA schema definition, converted
 * from (for example) SDS notation into schema components. It acts as a
 * container for global node definitions or types (of {@link ComponentType}).
 */
public final class Schema extends ComplexNode {


	public static final String TAG = "schema";
	
	private String rootType = null; // The designated root type.
	

	/** Creates a schema. */
	public Schema() {
		super(TAG);
	}


	/** Returns the designated root type or <code>null</code> if not set. */
	public String getRootType() {
		return rootType;
	}

	
	/**
	 * Sets the designated root <code>type</code>. The argument must refer to an
	 * existing global type, or an exception will be thrown. A <code>null</code>
	 * value is allowed, and will effectively clear the root type attribute.
	 * @throws IllegalArgumentException if the referenced type is unknown.
	 */
	public void setRootType(String type) {
 
		if (type != null && this.nodes.get(type).isEmpty())
			throw new IllegalArgumentException("no such global type (" + type + ")");
		this.rootType = type; 
	}

	
	/**
	 * Returns an SDA node structure that represents this schema. In other words,
	 * what an SDA parser would return upon processing an input stream describing
	 * the schema in SDS notation.
	 * 
	 */
	public ComplexNode toNode() {
		
		ComplexNode node = new ComplexNode(TAG);
		
		if (rootType != null) // Render the type attribute if we have one.
			node.nodes.add(new SimpleNode(Attribute.TYPE.tag, rootType));

		for (Node component : this.nodes) // Render all components.
			node.nodes.add(((ComponentType) component).toNode());

		return node;
	}
	
	
	/** Returns the string representation of this schema in SDS syntax. */
	public String toString() {
		return toNode().toString();
	}
}

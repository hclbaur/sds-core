package be.baur.sds;

import be.baur.sda.Node;
import be.baur.sds.common.Attribute;

/**
 * A <code>Schema</code> represents a entire SDA schema definition, converted
 * from (for example) SDS notation into schema components. It is a container for
 * "components" (see {@link ComponentType}) like node types or model groups.
 */
public final class Schema extends Node {


	public static final String TAG = "schema";
	
	private String rootType = null; // The designated root type.
	// rename to defaultType?

	/** Creates a schema node. */
	public Schema() {
		super(TAG); add(null); // by definition, a schema has child nodes.
	}

	// Should override addNode() to accept only ComponentType?

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
 
		if (type != null && this.getNodes().get(type).isEmpty())
			throw new IllegalArgumentException("no such global type (" + type + ")");
		this.rootType = type; 
	}

	
	/**
	 * Returns an SDA node structure that represents this schema. In other words,
	 * what an SDA parser would return upon processing an input stream describing
	 * the schema in SDS notation.
	 * 
	 */
	public Node toNode() {
		
		Node node = new Node(TAG);
		
		if (rootType != null) // Render the type attribute if we have one.
			node.add(new Node(Attribute.TYPE.tag, rootType));

		for (Node component : this.getNodes()) // Render all components.
			node.add(((ComponentType) component).toNode());

		return node;
	}
	
	
	/** Returns the string representation of this schema in SDS syntax. */
	public String toString() {
		return toNode().toString();
	}
}

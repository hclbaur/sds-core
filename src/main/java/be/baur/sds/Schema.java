package be.baur.sds;

import be.baur.sda.Node;
import be.baur.sda.serialization.SDAFormatter;
import be.baur.sds.common.Attribute;
import be.baur.sds.model.ModelGroup;

/**
 * A <code>Schema</code> represents an SDA document definition, that is, a
 * structure that defines SDA content. It is a container for "components" like
 * node types and model groups. Schema is usually not created "by hand" but read
 * and parsed from input in SDS notation.<br>
 * See also {@link ComponentType}, {@link SDSParser}.
 */
public final class Schema extends Node {

	public static final String TAG = "schema";	
	
	private String defaultType = null; // the designated root node
	

	/** Creates a schema node. */
	public Schema() {
		super(TAG); // extends Node so it must have a tag, even if we do not really need or use it
		add(null);  // a schema should have child nodes so we initialize it with an empty node set
	}


	/**
	 * Returns the default type for this schema. This method returns null if no
	 * default type has been set.
	 * 
	 * @return the name of the default type, may be null
	 */
	public String getDefaultType() {
		return defaultType;
	}

	
	/**
	 * Sets the default type for this schema. The argument must refer to an existing
	 * global type, or an exception will be thrown. A null value is allowed, and
	 * will effectively clear the default type.
	 * 
	 * @param type the name of the default type, may be null
	 * @throws IllegalArgumentException if the referenced type is unknown
	 */
	public void setDefaultType(String type) {
 
		if (type != null && this.getNodes().get(type).isEmpty())
			throw new IllegalArgumentException("no such global type (" + type + ")");
		this.defaultType = type; 
	}

	
	/**
	 * Returns an SDA node structure that represents this schema. In other words,
	 * this is what an SDA parser would return upon processing an input stream
	 * describing the schema in SDS notation.
	 * 
	 * @returns an SDA node representing the schema 
	 */
	public Node toNode() {
		
		Node node = new Node(TAG);
		
		if (defaultType != null) // Render the type attribute if we have one.
			node.add(new Node(Attribute.TYPE.tag, defaultType));

		for (Node component : this.getNodes()) // Render all components.
			node.add(((ComponentType) component).toNode());

		return node;
	}
	
	
	/**
	 * Returns the string representation of this schema in SDS notation. For
	 * example:
	 * 
	 * <pre>
	 * <code>schema { node "greeting" { node "message" { type "string" } } }</code>
	 * </pre>
	 * 
	 * Note that the returned string is formatted as a single line of text. For a
	 * more readable output, use the {@link #toNode} method and render the output
	 * node using an {@link SDAFormatter}.
	 * 
	 * @return an SDS representation of this node
	 */
	@Override
	public String toString() {
		return toNode().toString();
	}
}

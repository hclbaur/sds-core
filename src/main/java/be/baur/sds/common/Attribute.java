package be.baur.sds.common;

import be.baur.sda.ComplexNode;
import be.baur.sda.NodeSet;
import be.baur.sda.SimpleNode;
import be.baur.sds.SchemaException;

/**
 * This enumeration defines the tags of all SDS attributes.
 */
public enum Attribute {

	NAME("name"), TYPE("type"), MULTIPLICITY("multiplicity"), 
	LENGTH("length"), VALUE("value"), PATTERN("pattern"),
	NULLABLE("nullable");

	public final String tag;

	Attribute(String tag) {
		this.tag = tag;
	}

	public String toString() {
		return tag;
	}

	/** Return an instance by its tag or <code>null</code> if not found. */
	public static Attribute get(String tag) {
		for (Attribute a : values())
			if (a.tag.equals(tag)) return a;
		return null;
	}

	
	/**
	 * This helper method returns all attribute nodes with a particular tag 
	 * from a component, or an empty set if no such attribute was found.
	 */
	private static NodeSet findNodes(ComplexNode sds, String tag) {

		return sds.get().get(SimpleNode.class).get(tag);
	}
	
	
	/**
	 * This method returns a specific attribute node from an SDS component.
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
	public static SimpleNode getNode(ComplexNode sds, Attribute att, Boolean req) throws SchemaException {

		NodeSet set = findNodes(sds, att.tag);
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

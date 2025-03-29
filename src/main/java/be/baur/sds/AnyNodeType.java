package be.baur.sds;

import be.baur.sda.DataNode;
import be.baur.sda.SDA;
import be.baur.sds.parsing.Attribute;
import be.baur.sds.parsing.Components;

/**
 * This type defines an SDA node with any value and/or child nodes, and any
 * (valid) node name. For example:
 * <p>
 * <code>node "name" { type "any" }</code>
 * <p>
 * defines a node with any content and the specified name, whereas
 * <p>
 * <code>node { type "any" }</code>
 * <p>
 * defines one with any content and any (valid) name.
 */
public final class AnyNodeType extends AbstractNodeType {

	public static final String NAME = "any";
	
	private boolean named = false; // true if the any type is explicitly named


	/**
	 * Creates a type that defines a node with any content. If the supplied name is
	 * null or empty, the name of the node can be any valid node name. Otherwise it
	 * must be equal to the specified name.
	 * 
	 * @param name a valid node name, may be null or empty
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public AnyNodeType(String name) {
		this.setTypeName(name);
	}

	
	/**
	 * Returns the name of the node defined by this type or an empty string for a
	 * type defining a node with any (valid) name.
	 * 
	 * @return a valid node name, or empty
	 */
	@Override
	public final String getTypeName() {
		return named ? super.getTypeName() : "";
	}

	
	/**
	 * Sets the name of the node defined by this type. Null or an empty string may
	 * be supplied for a type defining a node with any (valid) name.
	 * 
	 * @param name a valid node name, may be null or empty
	 * @throws IllegalArgumentException if the name is invalid
	 */
	@Override
	public final void setTypeName(String name) {
		if (name == null || name.isEmpty()) {
			named = false; return;
		}
		if (! SDA.isName(name)) 
			throw new IllegalArgumentException("invalid type name (" + name + ")");
		super.setTypeName(name);
		named = true;
	}

	
	/**
	 * Returns true if this type defines an explicitly named node.
	 * 
	 * @return true or false
	 */
	public boolean isNamed() {
		return named;
	}
	
	
	@Override
	public DataNode toSDA() {

		final DataNode node = new DataNode(Components.NODE.tag);
		
		// Set the name only if explicitly named
		if (named) node.setValue(getTypeName());
	
		node.add(new DataNode(Attribute.TYPE.tag, NAME));
		
		// Render the multiplicity if not default
		if (getMultiplicity().min != 1 || getMultiplicity().max != 1) 
			node.add(new DataNode(Attribute.OCCURS.tag, getMultiplicity().toString()));
		
		// There should be no facets or child components
		return node;
	}
}

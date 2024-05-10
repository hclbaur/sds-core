package be.baur.sds;

import be.baur.sda.DataNode;
import be.baur.sda.SDA;
import be.baur.sds.serialization.Attribute;
import be.baur.sds.serialization.Components;

/**
 * An {@code AnyType} defines any SDA node, with any simple or complex content,
 * and possibly any (valid) node name. For example:
 * <p>
 * <code>node "name" { type "any" }</code>
 * <p>
 * where {@code name} is a valid node name, or
 * <p>
 * <code>node { type "any" }</code>
 * <p>
 * to define an unnamed type (with any valid node name).
 */
public final class AnyType extends Type {

	public static final String NAME = "any";
	
	private boolean named = false; // true if the any type is explicitly named


	/**
	 * Creates a type with the specified name. The name may be null or empty to
	 * create an unnamed type, otherwise it must be a valid node name.
	 * 
	 * @see SDA#isName
	 * 
	 * @param name a valid node name, may be null or empty
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public AnyType(String name) {
		setTypeName(name);
	}

	
	/**
	 * Returns the name of this type. A type defines an instance of a data node, so
	 * this method returns a valid node name or (in case of an unnamed type) an
	 * empty string.
	 * 
	 * @return a valid node name, or empty
	 */
	@Override
	public final String getTypeName() {
		return named ? super.getTypeName() : "";
	}

	
	/**
	 * Sets the name of this type. A type defines an instance of a data node, so the
	 * name of this type is restricted to valid node names. Null or an empty string
	 * may be supplied for an unnamed type.
	 * 
	 * @see SDA#isName
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
	 * Returns true if this type is explicitly named.
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

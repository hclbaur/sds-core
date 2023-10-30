package be.baur.sds.content;

import be.baur.sds.DataType;
import be.baur.sds.common.Content;

/**
 * An {@code AnyType} represents any SDA node, with any simple or complex
 * content, and possibly any (valid) node name. Or, in SDS notation:<br>
 * <br>
 * <code>node "name" { type "any" }</code> (where 'name' must be a valid node
 * name)<br>
 * <br>
 * <code>node { type "any" }</code> (unnamed; any valid node name is allowed).
 */
public final class AnyType extends DataType {

	private final boolean named; // true if the any type is explicitly named
	

	/**
	 * Creates this with the specified name. The name may be null or empty to
	 * create an unnamed type.
	 * 
	 * @param name a valid node name, may be null or empty
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public AnyType(String name) {
		super("any"); 
		named = !(name == null || name.isEmpty()); 
		if (named) setTypeName(name);
	}

	
	/**
	 * Returns true if the type is explicitly named.
	 * 
	 * @return true or false
	 */
	public boolean isNamed() {
		return named;
	}
	

	public Content getContentType() {
		return Content.ANY;
	}
}

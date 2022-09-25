package be.baur.sds.content;

import be.baur.sds.NodeType;
import be.baur.sds.common.Content;

/**
 * An {@code AnyType} represents any SDA node, with any simple or complex
 * content, and possibly any (valid) node name. Or, in SDS notation:<br>
 * <br>
 * <code>node "something" { type "any" }</code> (explicitly named, only
 * 'something' is a valid name)<br>
 * <br>
 * <code>node { type "any" }</code> (not explicitly named, any valid name is
 * allowed).
 */
public final class AnyType extends NodeType {

	private final boolean named;	// true if the any type is explicitly named
	

	/**
	 * Creates the type with the specified name. The name may be null or empty to
	 * create an unnamed type.
	 * 
	 * @param name a valid node name, may be null or empty
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public AnyType(String name) {
		super("any"); named = !(name == null || name.isEmpty()); 
		if (named) setName(name);
	}

	
	/**
	 * Returns true if the type is explicitly named.
	 * 
	 * @return true or false
	 */
	public boolean isNamed() {
		return named;
	}
	
	
	@Override
	public Content getContentType() {
		return Content.ANY;
	}
}

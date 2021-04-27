package be.baur.sds.content;

import be.baur.sds.SimpleType;
import be.baur.sds.common.Content;

/**
 * A type representing any SDA node, simple or complex, with any
 * content and even any (valid) node name. Or, in terms of SDS:<br>
 * <br>
 * <code>node{ name "stuff" type "any" }</code>
 * (explicitly named, only 'stuff' is accepted)<br>
 * <br>
 * <code>node{ type "any" }</code> 
 * (not explicitly named, any valid name is allowed).
 */
public final class AnyType extends SimpleType {

	private final boolean named;	// true if the any type is explicitly named
	
	
	/** Creates any type. The name can be null or empty to create an unnamed type. */
	public AnyType(String name) {
		super("any"); named = !(name == null || name.isEmpty()); 
		if (named) setName(name);
	}

	
	/** Returns true if the type was explicitly named. */
	public boolean isNamed() {
		return named;
	}
	
	
	public Content getContentType() {
		return Content.ANY;
	}
}

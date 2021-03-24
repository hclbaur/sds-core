package be.baur.sds.content;

import be.baur.sds.SimpleType;
import be.baur.sds.common.Content;

/**
 * An <code>AnyType</code> is a type representing any SDA node, simple or
 * complex. If the name is unspecified (null or empty), any (valid) node name
 * will be accepted. In terms of SDS, the difference is this:<br>
 * <br>
 * <code>node{ name "stuff" type "any" }</code> (explicitly named, only 'stuff' is accepted)<br>
 * <br>
 * <code>node{ type "any" }</code> (not explicitly named, any valid name will be allowed)
 */
public class AnyType extends SimpleType {

	private final boolean named;	// true if the any type is explicitly named
	
	/** Create any type. The name can be null or empty to create an unnamed type. */
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

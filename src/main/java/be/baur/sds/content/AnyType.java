package be.baur.sds.content;

import be.baur.sds.SimpleType;
import be.baur.sds.common.Content;

/**
 * An <code>AnyType</code> is a type representing any SDA node, simple or complex.
 * If the name is unspecified (null or empty), any (valid) node name will be accepted.
 * In terms of SDS, the difference is this:<br><br>
 * <code>node{ name "anything" type "any" } (explicitly named type)</code><br><br>
 * <code>node{ type "any" } (unnamed type)</code>
 */
public class AnyType extends SimpleType {

	private final boolean named;	// if true, the type was explicitly named
	
	/** Create any type. The name may be null or empty to create an unnamed type. */
	public AnyType(String name) {
		super(name); named = !(name == null || name.isEmpty());
	}

	/** Returns true if the type was explicitly named. */
	public boolean isNamed() {
		return named;
	}
	
	@Override
	public String getType() {
		return Content.ANY.type;
	}
}

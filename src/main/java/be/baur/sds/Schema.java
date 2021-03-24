package be.baur.sds;

import be.baur.sds.common.Component;

/**
 * A <code>Schema</code> is a class representing the entire schema, converted
 * from SDS notation into an object structure. It is not a type in itself, but 
 * acts as a container for global node definitions (types), or in other words, 
 * it is a {@link ModelGroup}.
 */
public class Schema extends ModelGroup {
	
	public Schema() {
		super(Component.SCHEMA.tag);
	}
	
}

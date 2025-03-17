package be.baur.sds.types;

import java.util.function.Function;

import be.baur.sds.SDS;
import be.baur.sds.DataNodeType;

/**
 * A <code>BooleanNodeType</code> defines an SDA node with a boolean value
 * (the allowed values are merely "true" and "false", and nothing else).
 */
public final class BooleanNodeType extends DataNodeType <Boolean> {

	
	/**
	 * Creates a boolean node type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public BooleanNodeType(String name) {
		super(name);
	}


	@Override
	public String getDataType() {
		return SDS.BOOLEAN_TYPE;
	}

	
	@Override
	public Function<String, Boolean> valueConstructor() {
		return SDS.BOOLEAN_CONSTRUCTOR;
	}

}

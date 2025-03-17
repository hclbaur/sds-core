package be.baur.sds.types;

import java.util.function.Function;

import be.baur.sds.SDS;

/**
 * A <code>StringNodeType</code> defines an SDA node with a string value. This is
 * the most basic type of all, without restrictions on the value; any string is a
 * valid string, and unlike other node types it is null-able by default.
 */
public final class StringNodeType extends CharacterNodeType <String> {


	/**
	 * Creates a string node type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public StringNodeType(String name) {
		super(name); setNullable(true);
	}


	@Override
	public String getDataType() {
		return SDS.STRING_TYPE;
	}
	
	
	@Override
	public Function<String, String> valueConstructor() {
		return SDS.STRING_CONSTRUCTOR;
	}
	
	
	@Override
	public int valueLength(String value) {
		return value.length();
	}
}

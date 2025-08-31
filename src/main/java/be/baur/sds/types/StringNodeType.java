package be.baur.sds.types;

import java.util.function.Function;

/**
 * A <code>StringNodeType</code> defines an SDA node with a string value. This is
 * the most basic type of all, without restrictions on the value; any string is a
 * valid string, and unlike other node types it is null-able by default.
 */
public final class StringNodeType extends CharacterNodeType <String> {

	
	/** Name of the SDS string type. */
	public static final String TYPE_NAME = "string";
	
	/** Function to construct an SDS string value from a string. */
	public static final Function<String, String> TYPE_CONSTRUCTOR = s -> {
		return s; // strings are immutable so just return the original
	};


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
		return TYPE_NAME;
	}
	
	
	@Override
	public Function<String, String> valueConstructor() {
		return TYPE_CONSTRUCTOR;
	}
	
	
	@Override
	public int valueLength(String value) {
		return value.length();
	}
}

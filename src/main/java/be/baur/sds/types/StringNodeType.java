package be.baur.sds.types;

import java.util.function.Function;

/**
 * A <code>StringNodeType</code> defines an SDA node with a string value. This is
 * the most basic type of all, without restrictions on the value; any string is a
 * valid string, and unlike other node types it is nullable by default.
 */
public final class StringNodeType extends CharacterNodeType <String> {

	/** The SDS name of this data type. */
	public static final String NAME = "string";

	/** A function that constructs an string value from a string. */
	public static final Function<String, String> VALUE_CONSTRUCTOR = s -> {
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
	public String getValueType() {
		return NAME;
	}
	
	
	@Override
	public Function<String, String> valueConstructor() {
		return VALUE_CONSTRUCTOR;
	}
	
	
	@Override
	public int valueLength(String value) {
		return value.length();
	}
}

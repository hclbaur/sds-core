package be.baur.sds.content;

import java.util.function.Function;

/**
 * A <code>StringType</code> defines an SDA node with character data.
 */
public final class StringType extends CharacterType<String> {

	/** The SDS name of this data type. */
	public static final String NAME = "string";

	/** A function that constructs an string value from a string. */
	public static final Function<String, String> VALUE_CONSTRUCTOR = s -> {
		return s; // strings are immutable so just return the original
	};


	/**
	 * Creates the type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public StringType(String name) {
		super(name); //setNullable(true);
	}


	@Override
	public String getType() {
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

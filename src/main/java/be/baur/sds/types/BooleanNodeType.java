package be.baur.sds.types;

import java.util.function.Function;

import be.baur.sds.ValueNodeType;

/**
 * A <code>BooleanNodeType</code> defines an SDA node with a boolean value
 * (the allowed values are merely "true" and "false", and nothing else).
 */
public final class BooleanNodeType extends ValueNodeType <Boolean> {

	/** The SDS name of this data type. */
	public static final String NAME = "boolean";

	/** A function that constructs a boolean value from a string. */
	public static final Function<String, Boolean> VALUE_CONSTRUCTOR = s -> {
		if (s != null && (s.equals("true") || s.equals("false")))
			return Boolean.valueOf(s);
		throw new IllegalArgumentException("either true or false is expected");
	};
	
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
	public String getValueType() {
		return NAME;
	}

	
	@Override
	public Function<String, Boolean> valueConstructor() {
		return VALUE_CONSTRUCTOR;
	}

}

package be.baur.sds.content;

import java.util.function.Function;

/**
 * A <code>IntegerType</code> defines an SDA node with integer content.
 */
public final class IntegerType extends RangedType<Integer> {

	/** The SDS name of this data type. */
	public static final String NAME = "integer";

	/** A function that constructs an integer value from a string. */
	public static final Function<String, Integer> VALUE_CONSTRUCTOR = Integer::new;

	/**
	 * Creates the type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public IntegerType(String name) {
		super(name);
	}


	@Override
	public String getType() {
		return NAME;
	}


//	@Override
//	public Class<Integer> valueClass() {
//		return Integer.class;
//	}


	@Override
	public Function<String, Integer> valueConstructor() {
		return Integer::new;
	}
	
	
	/**
	 * Returns an Integer if the supplied string is within the lexical space of this type.
	 * 
	 * @param s the string to be converted
	 * @return an Integer
	 * @throws NumberFormatException if conversion is not possible
	 */
	public static Integer valueOf(String s) {
		return VALUE_CONSTRUCTOR.apply(s);
	}
}

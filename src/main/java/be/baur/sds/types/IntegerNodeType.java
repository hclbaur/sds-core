package be.baur.sds.types;

import java.util.function.Function;

/**
 * A <code>IntegerNodeType</code> defines an SDA node with an integer value.
 */
public final class IntegerNodeType extends ComparableNodeType <Integer> {

	/** The SDS name of this data type. */
	public static final String NAME = "integer";

	/** A function that constructs an integer value from a string. */
	public static final Function<String, Integer> VALUE_CONSTRUCTOR = Integer::new;


	/**
	 * Creates an integer node type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public IntegerNodeType(String name) {
		super(name);
	}


	@Override
	public String getValueType() {
		return NAME;
	}


	@Override
	public Function<String, Integer> valueConstructor() {
		return VALUE_CONSTRUCTOR;
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

package be.baur.sds.content;

import java.util.function.Function;

/**
 * A <code>DecimalType</code> defines an SDA node with decimal content.
 */
public final class DecimalType extends RangedType<Double> {

	/** The SDS name of this data type. */
	public static final String NAME = "decimal";
	
	/** A function that constructs a decimal value from a string. */
	public static final Function<String, Double> VALUE_CONSTRUCTOR = Double::new;


	/**
	 * Creates the type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public DecimalType(String name) {
		super(name);
	}
	

	@Override
	public String getType() {
		return NAME;
	}


//	@Override
//	public Class<Double> valueClass() {
//		return Double.class;
//	}
	

	@Override
	public Function<String, Double> valueConstructor() {
		return VALUE_CONSTRUCTOR;
	}
	
	
	/**
	 * Returns a Double if the supplied string is within the lexical space of this type.
	 * 
	 * @param s the string to be converted
	 * @return a Double
	 * @throws NumberFormatException if conversion is not possible
	 */
	public static Double valueOf(String s) {
		return VALUE_CONSTRUCTOR.apply(s);
	}
}

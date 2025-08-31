package be.baur.sds.types;

import java.util.function.Function;

/**
 * A <code>DecimalNodeType</code> defines an SDA node with a decimal value.
 */
public final class DecimalNodeType extends ComparableNodeType <Double> {


	/** Name of the SDS decimal type. */
	public static final String TYPE_NAME = "decimal";

	/**
	 * Function to construct an SDS decimal value from a string.
	 * @throws NumberFormatException if the string cannot be converted to a number.
	 */
	public static final Function<String, Double> TYPE_CONSTRUCTOR = Double::new;


	/**
	 * Creates a decimal node type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public DecimalNodeType(String name) {
		super(name);
	}
	

	@Override
	public String getDataType() {
		return TYPE_NAME;
	}
	

	@Override
	public Function<String, Double> valueConstructor() {
		return TYPE_CONSTRUCTOR;
	}

}

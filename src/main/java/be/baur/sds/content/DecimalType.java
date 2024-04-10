package be.baur.sds.content;

import java.util.function.Function;

/**
 * A <code>DecimalType</code> represents an SDA node with decimal content.
 */
public final class DecimalType extends RangedType<Double> {

	public static final String TYPE = "decimal";


	/**
	 * Creates the type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public DecimalType(String name) {
		super(name);
	}
	

	public String getType() {
		return TYPE;
	}


	public Class<Double> valueClass() {
		return Double.class;
	}
	

	public Function<String, Double> valueConstructor() {
		return Double::new;
	}
}

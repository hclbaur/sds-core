package be.baur.sds.content;

import java.util.function.Function;

/**
 * A <code>DecimalType</code> represents an SDA node with decimal content.
 */
public final class DecimalType extends RangedType<Double> {

	public static final String NAME = "decimal";


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


	@Override
	public Class<Double> valueClass() {
		return Double.class;
	}
	

	@Override
	public Function<String, Double> valueConstructor() {
		return Double::new;
	}
}

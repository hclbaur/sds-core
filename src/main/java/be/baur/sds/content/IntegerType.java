package be.baur.sds.content;

import java.util.function.Function;

/**
 * A <code>IntegerType</code> represents an SDA node with integer content.
 */
public final class IntegerType extends RangedType<Integer> {

	public static final String NAME = "integer";


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


	@Override
	public Class<Integer> valueClass() {
		return Integer.class;
	}


	@Override
	public Function<String, Integer> valueConstructor() {
		return Integer::new;
	}
}

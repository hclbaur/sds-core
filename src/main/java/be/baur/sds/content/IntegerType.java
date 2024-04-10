package be.baur.sds.content;

import java.util.function.Function;

/**
 * A <code>IntegerType</code> represents an SDA node with integer content.
 */
public final class IntegerType extends RangedType<Integer> {

	public static final String TYPE = "integer";


	/**
	 * Creates the type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public IntegerType(String name) {
		super(name);
	}


	public String getType() {
		return TYPE;
	}


	public Class<Integer> valueClass() {
		return Integer.class;
	}


	public Function<String, Integer> valueConstructor() {
		return Integer::new;
	}
}

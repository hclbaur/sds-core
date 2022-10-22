package be.baur.sds.content;

/**
 * A <code>IntegerType</code> represents an SDA node with integer content.<br>
 * See also {@link DecimalType}.
 */
public final class IntegerType extends RangedType<Integer> {


	/**
	 * Creates the type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public IntegerType(String name) {
		super(name);
	}
	

	public Content getContentType() { 
		return Content.INTEGER; 
	}
}

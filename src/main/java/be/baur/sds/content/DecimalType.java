package be.baur.sds.content;

/**
 * A <code>DecimalType</code> represents an SDA node with decimal content.<br>
 * See also {@link IntegerType}.
 */
public final class DecimalType extends RangedType<Double> {


	/**
	 * Creates the type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public DecimalType(String name) {
		super(name);
	}
	

	public Content getContentType() { 
		return Content.DECIMAL; 
	}
}

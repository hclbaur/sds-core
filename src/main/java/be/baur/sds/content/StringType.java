package be.baur.sds.content;

/**
 * A <code>StringType</code> defines an SDA node with string content. When
 * setting an allowed length interval, note that length is counted in number of
 * characters.
 */
public final class StringType extends AbstractStringType {

	/** The SDS name of this data type. */
	public static final String NAME = "string";


	/**
	 * Creates the type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public StringType(String name) {
		super(name);
	}
	
	
	/**
	 * @param str a String
	 * @return a String or null
	 */
	//@Override
	public String valueOf(String str) {
		// any string that is not null is allowed
		return str; // may be null
	}


	@Override
	public String getType() {
		return NAME;
	}
}

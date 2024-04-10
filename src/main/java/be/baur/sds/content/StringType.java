package be.baur.sds.content;

/**
 * A <code>StringType</code> represents an SDA node with string content. When
 * setting an allowed length interval, note that length is counted in number of
 * characters.
 */
public final class StringType extends AbstractStringType {

	public static final String TYPE = "string";


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


	public String getType() {
		return TYPE;
	}
}

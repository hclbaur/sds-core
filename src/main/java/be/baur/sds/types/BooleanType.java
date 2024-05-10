package be.baur.sds.types;

import be.baur.sds.DataType;

/**
 * A <code>BooleanType</code> defines an SDA node with a boolean value - that is
 * either "true" or "false".
 */
public final class BooleanType extends DataType {

	/** The SDS name of this data type. */
	public static final String NAME = "boolean";
	public static final String TRUE = "true";
	public static final String FALSE = "false";

	
	/**
	 * Creates the type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public BooleanType(String name) {
		super(name);
	}


	@Override
	public String getType() {
		return NAME;
	}
	
	
	/**
	 * Returns a Boolean if the supplied string is within the lexical space of this type.
	 * 
	 * @param s the string to be converted
	 * @return a Boolean
	 * @throws IllegalArgumentException if conversion is not possible
	 */
	public static Boolean valueOf(String s) {
		if (s != null && (s.equals(TRUE) || s.equals(FALSE)))
			return Boolean.valueOf(s);
		throw new IllegalArgumentException("either true or false is expected");
	}
}

package be.baur.sds.content;

import be.baur.sds.DataType;

/**
 * A <code>BooleanType</code> defines an SDA node with a boolean value - that is
 * either "true" or "false".
 */
public final class BooleanType extends DataType {

	public static final String TYPE = "boolean";
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


	/**
	 * @param str a String representing a boolean value
	 * @return a Boolean or null
	 */
	//@Override
	public Boolean valueOf(String str) {
		// only "true" or "false" is allowed in SDS
		if (str != null && (str.equals(TRUE) || str.equals(FALSE)))
			return Boolean.valueOf(str);
		return null;
	}


	public String getType() {
		return TYPE;
	}
}

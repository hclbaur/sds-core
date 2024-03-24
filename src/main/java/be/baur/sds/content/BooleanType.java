package be.baur.sds.content;

import be.baur.sds.DataType;
import be.baur.sds.common.Content;

/**
 * A <code>BooleanType</code> is a data type representing an SDA node with a
 * boolean value - that is either "true" or "false".
 * 
 * @see DataType
 */
public final class BooleanType extends DataType {

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
	 * Returns a Boolean value if the supplied string is within the lexical space of
	 * an SDS boolean type.
	 * 
	 * @param str a String
	 * @return a Boolean
	 */
	public static Boolean valueFrom(String str) {

		if (str != null && (str.equals(TRUE) || str.equals(FALSE)))
			return Boolean.valueOf(str);
		return null;
	}

	public Content getContentType() {
		return Content.BOOLEAN;
	}
}

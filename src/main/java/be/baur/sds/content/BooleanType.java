package be.baur.sds.content;

import be.baur.sds.DataType;
import be.baur.sds.common.Content;

/**
 * A <code>BooleanType</code> is a simple type representing an SDA node with
 * boolean content,that is "true" or "false". No other values are allowed.
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


	public Content getContentType() {
		return Content.BOOLEAN;
	}
}

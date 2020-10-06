package be.baur.sds.content;

import be.baur.sds.SimpleType;
import be.baur.sds.common.Content;

/**
 * A <code>BooleanType</code> is a simple type representing an SDA node with
 * boolean content, e.g. "true" or "false". No other values are allowed.
 */
public class BooleanType extends SimpleType {


	/** Create a boolean type. */
	public BooleanType(String name) {
		super(name);
	}

	@Override
	public String getType() {
		return Content.BOOLEAN.type;
	}
}

package be.baur.sds.content;

import be.baur.sds.NodeType;
import be.baur.sds.common.Content;

/**
 * A <code>BooleanType</code> is a simple type representing an SDA node with
 * boolean content, e.g. "true" or "false". No other values are allowed.
 */
public final class BooleanType extends NodeType {

	public static final String TRUE = "true";
	public static final String FALSE = "false";

	
	/** Creates a boolean type with the supplied <code>name</code>. */
	public BooleanType(String name) {
		super(name);
	}

	
	public Content getContentType() {
		return Content.BOOLEAN;
	}
}

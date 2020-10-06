package be.baur.sds.content;

import be.baur.sds.common.Content;

/**
 * A <code>BinaryType</code> is a string type representing an SDA node with
 * binary content, in MIME base64 encoding. It has a minimum and/or maximum
 * length counted in number of bytes (rather than characters).
 */
public class BinaryType extends StringType {


	/** Create a binary type. */
	public BinaryType(String name) {
		super(name);
	}
	
	@Override
	public String getType() { 
		return Content.BINARY.type; 
	}
}

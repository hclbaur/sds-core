package be.baur.sds.content;

import be.baur.sds.common.Content;

/**
 * A <code>BinaryType</code> is a string type representing an SDA node with
 * binary content, in MIME base64 encoding. Like the {@link StringType} it
 * extends, it has a minimum and/or maximum length, but counted in number of
 * bytes rather than characters.
 */
public class BinaryType extends StringType {


	/** Create a binary type. */
	public BinaryType(String name) {
		super(name);
	}
	
	public Content getContentType() { 
		return Content.BINARY; 
	}
}

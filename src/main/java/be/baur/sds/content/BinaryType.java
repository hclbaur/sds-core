package be.baur.sds.content;

import be.baur.sds.common.Content;

/**
 * A <code>BinaryType</code> is a string type representing an SDA node with
 * binary content (in MIME base64 encoding). Unlike the {@link StringType}, its
 * minimum and maximum length is counted in number of bytes rather than
 * characters.
 */
public final class BinaryType extends AbstractStringType {


	/** Creates a binary type with the supplied <code>name</code>. */
	public BinaryType(String name) {
		super(name);
	}
	
	
	public Content getContentType() { 
		return Content.BINARY; 
	}
}

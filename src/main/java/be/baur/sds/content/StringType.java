package be.baur.sds.content;

import be.baur.sds.common.Content;

/**
 * A <code>StringType</code> is a simple type representing an SDA node with
 * string content. It may have a minimum and/or maximum length counted in number
 * of characters.
 */
public final class StringType extends AbstractStringType {


	/** Creates a string type with the supplied <code>name</code>. */
	public StringType(String name) {
		super(name);
	}
	
	public Content getContentType() { 
		return Content.STRING; 
	}
}

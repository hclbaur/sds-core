package be.baur.sds.content;

import be.baur.sds.common.Content;

/**
 * A <code>StringType</code> represents an SDA node with string content. When
 * setting an allowed length interval, note that length is counted in number of
 * characters.<br>
 * See also {@link BinaryType}.
 */
public final class StringType extends AbstractStringType {


	/**
	 * Creates the type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public StringType(String name) {
		super(name);
	}
	
	
	@Override
	public Content getContentType() { 
		return Content.STRING; 
	}
}

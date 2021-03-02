package be.baur.sds.content;

import be.baur.sds.SimpleType;
import be.baur.sds.common.Content;
import be.baur.sds.common.NaturalInterval;

/**
 * A <code>StringType</code> is a simple type representing an SDA node with
 * string content. It may have a minimum and/or maximum length in number of
 * characters. Unlike other types, it is nullable by default.
 */
public class StringType extends SimpleType {

	private NaturalInterval length = null; // Length null means: any length is allowed.


	/** Create a (nullable) string type. */
	public StringType(String name) {
		super(name); setNullable(true);
	}
	
	public Content getContentType() { 
		return Content.STRING; 
	}

	/** Get the length interval. Default value is <code>null</code>. */
	public NaturalInterval getLength() {
		return length;
	}

	/** Set the length interval. */
	public void setLength(NaturalInterval length) {
		this.length = length;
	}

	/** The minimum content length. */
	public int minLength() {
		return length != null ? length.lower : 0;
	}

	/** The maximum content length. */
	public int maxLength() {
		return length != null ? length.upper : Integer.MAX_VALUE;
	}
}

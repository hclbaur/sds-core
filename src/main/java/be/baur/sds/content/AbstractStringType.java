package be.baur.sds.content;

import be.baur.sds.SimpleType;
import be.baur.sds.common.NaturalInterval;

/**
 * This is an abstract type representing an SDA node with string content and a
 * minimum/maximum length. Unlike other types, it is null-able by default.
 */
public abstract class AbstractStringType extends SimpleType {

	private NaturalInterval length = null; // Length null means: any length is allowed.


	/** Creates the type with the supplied <code>name</code>. */
	public AbstractStringType(String name) {
		super(name); setNullable(true);
	}


	/**
	 * Returns the allowed length interval. Default value is <code>null</code> which
	 * means any length is allowed.
	 */
	public NaturalInterval getLength() {
		return length;
	}


	/** Sets the allowed <code>length</code> interval. */
	public void setLength(NaturalInterval length) {
		this.length = length;
	}


	/** Returns the minimum content length. */
	public int minLength() {
		return length != null ? length.min : 0;
	}


	/** Returns the maximum content length. */
	public int maxLength() {
		return length != null ? length.max : Integer.MAX_VALUE;
	}
}

package be.baur.sds.content;

import be.baur.sds.NodeType;
import be.baur.sds.common.NaturalInterval;

/**
 * An {@code AbstractStringType} represents an SDA node with string content and
 * a minimum/maximum length. Unlike other types, it is null-able by default.
 * <br>
 * See also {@link StringType} and {@link BinaryType}.
 */
public abstract class AbstractStringType extends NodeType {

	private NaturalInterval length = null; // null means any length is allowed.


	/**
	 * Creates the type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public AbstractStringType(String name) {
		super(name); setNullable(true);
	}


	/**
	 * Returns the allowed length interval. This method returns null if any length
	 * is allowed.
	 * 
	 * @return a natural interval, may be null
	 */
	public NaturalInterval getLength() {
		return length;
	}


	/**
	 * Sets the allowed length interval. A length of null means any length is
	 * allowed.
	 * 
	 * @param length a natural interval, may be null
	 */
	public void setLength(NaturalInterval length) {
		this.length = length;
	}


	/**
	 * Returns the minimum content length.
	 * 
	 * @return a minimum length
	 */
	public int minLength() { // do we really need this method?
		return length != null ? length.min : 0;
	}


	/**
	 * Returns the maximum content length.
	 * 
	 * @return a maximum length
	 */
	public int maxLength() { // do we really need this method?
		return length != null ? length.max : Integer.MAX_VALUE;
	}
}

package be.baur.sds.content;

import be.baur.sds.MixedType;
import be.baur.sds.SDS;
import be.baur.sds.common.NaturalInterval;

/**
 * An {@code AbstractStringType} represents an SDA node with string content and
 * a minimum and maximum length. Unlike other types, it is null-able by default.
 * <br>
 * See also {@link StringType} and {@link BinaryType}.
 */
public abstract class AbstractStringType extends MixedType {

	private NaturalInterval length = NaturalInterval.INFINITE; // default is to allow any length


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
	 * Returns the allowed length interval. This method never returns null.
	 * 
	 * @return a natural interval, not null
	 */
	public NaturalInterval getLength() {
		return length;
	}


	/**
	 * Sets the allowed length interval. This method does not accept null.
	 * 
	 * @param length a natural interval, not null
	 * @throws IllegalArgumentException is length is null
	 */
	public void setLength(NaturalInterval length) {
		this.length = SDS.requireNonNull(length, "length must not be null");
	}
}

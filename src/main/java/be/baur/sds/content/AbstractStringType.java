package be.baur.sds.content;

import java.util.Objects;

import be.baur.sds.DataType;
import be.baur.sds.common.NaturalInterval;

/**
 * An {@code AbstractStringType} represents an SDA node with string content and
 * a minimum and maximum length. Unlike other types, it is null-able by default.
 * <br>
 * See also {@link StringType} and {@link BinaryType}.
 */
public abstract class AbstractStringType extends DataType {

	private NaturalInterval length = NaturalInterval.ZERO_TO_MAX; // default allows any length


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
	 * Returns the allowed length interval. The default value is {@code 0..*}, which
	 * means any length is allowed. This method never returns null.
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
	 */
	public void setLength(NaturalInterval length) {
		this.length = Objects.requireNonNull(length, "length must not be null");
	}
}

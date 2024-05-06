package be.baur.sds.content;

import java.util.Objects;
import java.util.function.Function;

import be.baur.sds.DataType;
import be.baur.sds.common.NaturalInterval;

/**
 * An {@code AbstractStringType} defines an SDA node with string content and a
 * minimum and maximum length. Unlike other types, it is null-able by default.
 */
public abstract class AbstractStringType <T> extends DataType {

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


	/**
	 * Returns a constructor function that accepts a string and returns an instance
	 * of a value appropriate for this data type.
	 * <p>
	 * Note: when applied, the function may throw an exception if the argument is
	 * not within the lexical space for this type (that is, when the supplied string
	 * cannot be converted to a valid value).
	 * 
	 * @return a Function
	 * @throws RuntimeException
	 */
	public abstract Function<String, T> valueConstructor();


	/**
	 * Returns the length of a value appropriate for this data type. Unless
	 * specified otherwise, this is the number of Unicode code units in the
	 * specified value.
	 * 
	 * @return a non-negative integer
	 */
	public abstract int valueLength(T value);
}

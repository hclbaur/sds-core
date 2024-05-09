package be.baur.sds.content;

import java.util.Objects;
import java.util.function.Function;

import be.baur.sds.DataType;
import be.baur.sds.common.NaturalInterval;

/**
 * A {@code CharacterType} is an abstract type that defines an SDA node with
 * character data and a minimum and maximum length. It is used to implement the
 * native string and binary types, and can be used to add specialized types that
 * are not easily validated with a regular expression, such as an {@code IBAN}.
 */
public abstract class CharacterType <T> extends DataType {

	private NaturalInterval length = NaturalInterval.ZERO_TO_MAX; // default allows any length


	/**
	 * Creates the type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public CharacterType(String name) {
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
	 * supplied value.
	 * 
	 * @return a non-negative integer
	 */
	public abstract int valueLength(T value);
}

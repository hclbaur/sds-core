package be.baur.sds.types;

import java.util.Objects;

import be.baur.sds.ValueNodeType;
import be.baur.sds.common.NaturalInterval;


/**
 * A {@code CharacterNodeType} defines an SDA node with a value that consists of
 * characters, and has a minimum and maximum length. It is used to implement the
 * native string and binary node types, and can be used to add specialized types
 * that are not easily validated with a regular expression, like an {@code IBAN}.
 * @param <T>
 */
public abstract class CharacterNodeType <T> extends ValueNodeType <T> {

	private NaturalInterval length = NaturalInterval.ZERO_TO_MAX; // default allows any length


	/**
	 * Creates a character node type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public CharacterNodeType(String name) {
		super(name);
	}


	/**
	 * Returns the allowed length interval. The default is {@code 0..*}, which means
	 * any length is allowed. This method never returns null.
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
	 * Returns the length of a value of this type. Unless specified otherwise, this
	 * is the number of Unicode code units.
	 * 
	 * @param value a value
	 * @return a non-negative integer
	 */
	public abstract int valueLength(T value);
}

package samples.types;

import java.util.function.Function;

import be.baur.sds.content.AbstractStringType;

/**
 * A <code>BinaryType</code> defines an SDA node with a valid IBAN value.
 * 
 * @see IBAN
 */
public final class IBANType extends AbstractStringType<IBAN> {

	/** The SDS name of this data type. */
	public static final String NAME = "IBAN";
	
	/** A function that constructs an binary value from a string. */
	public static final Function<String, IBAN> VALUE_CONSTRUCTOR = s -> {
		return IBAN.parse(s);
	};


	/**
	 * Creates the type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public IBANType(String name) {
		super(name);
	}
	

	@Override
	public String getType() {
		return NAME;
	}
	
	
	@Override
	public Function<String, IBAN> valueConstructor() {
		return VALUE_CONSTRUCTOR;
	}
	
	
	/**
	 * Returns the length of the supplied IBAN (whitespace excluded).
	 */
	@Override
	public int valueLength(IBAN value) {
		return value.length();
	}
}

package samples.types;

import java.util.function.Function;

import be.baur.sds.types.CharacterNodeType;


/**
 * A <code>IBANType</code> defines an SDA node with an international bank
 * account as a value.
 * 
 * @see IBAN
 */
public final class IBANNodeType extends CharacterNodeType <IBAN> {

	/** The SDS name of this data type. */
	public static final String NAME = "IBAN";
	
	/** A function that constructs an IBAN from a string. */
	public static final Function<String, IBAN> VALUE_CONSTRUCTOR = s -> {
		return IBAN.parse(s);
	};


	/**
	 * Creates an IBAN node type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public IBANNodeType(String name) {
		super(name);
	}
	

	@Override
	public String getValueType() {
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

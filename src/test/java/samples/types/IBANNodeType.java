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
	public String getDataType() {
		return IBAN.TYPE;
	}
	
	
	@Override
	public Function<String, IBAN> getDataTypeConstructor() {
		return IBAN.CONSTRUCTOR;
	}
	
	
	/**
	 * Returns the length of the supplied IBAN (whitespace excluded).
	 */
	@Override
	public int valueLength(IBAN value) {
		return value.length();
	}
}

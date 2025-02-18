package be.baur.sds.types;

import java.util.Base64;
import java.util.function.Function;

/**
 * A <code>BinaryNodeType</code> defines an SDA node with a binary value in MIME
 * base64 encoding. Note that its length is counted in number of bytes rather
 * than characters.
 */
public final class BinaryNodeType extends CharacterNodeType <byte[]> {

	/** The SDS name of this data type. */
	public static final String NAME = "binary";
	
	/** A function that constructs an binary value from a string. */
	public static final Function<String, byte[]> VALUE_CONSTRUCTOR = s -> {
		return Base64.getDecoder().decode(s);
	};


	/**
	 * Creates a binary node type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public BinaryNodeType(String name) {
		super(name);
	}
	

	@Override
	public String getValueType() {
		return NAME;
	}
	
	
	@Override
	public Function<String, byte[]> valueConstructor() {
		return VALUE_CONSTRUCTOR;
	}
	
	
	/**
	 * Returns the length of the supplied binary in bytes.
	 */
	@Override
	public int valueLength(byte[] value) {
		return value.length;
	}
}

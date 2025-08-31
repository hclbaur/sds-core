package be.baur.sds.types;

import java.util.Base64;
import java.util.function.Function;

/**
 * A <code>BinaryNodeType</code> defines an SDA node with a binary value in MIME
 * base64 encoding. Note that its length is counted in number of bytes rather
 * than characters.
 */
public final class BinaryNodeType extends CharacterNodeType <byte[]> {


	/** Name of the SDS binary type. */
	public static final String TYPE_NAME = "binary";

	/**
	 * Function to construct an SDS binary value from a string.
	 * @throws IllegalArgumentException if the string is not in valid Base64 scheme.
	 */
	public static final Function<String, byte[]> TYPE_CONSTRUCTOR = s -> {
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
	public String getDataType() {
		return TYPE_NAME;
	}
	
	
	@Override
	public Function<String, byte[]> valueConstructor() {
		return TYPE_CONSTRUCTOR;
	}
	
	
	/**
	 * Returns the length of the supplied binary in bytes.
	 */
	@Override
	public int valueLength(byte[] value) {
		return value.length;
	}
}

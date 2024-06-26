package be.baur.sds.types;

import java.util.Base64;
import java.util.function.Function;

/**
 * A <code>BinaryType</code> defines an SDA node with binary data (in MIME
 * base64 encoding). When setting an allowed length interval, note that length
 * is counted in number of bytes rather than characters.
 */
public final class BinaryType extends CharacterType<byte[]> {

	/** The SDS name of this data type. */
	public static final String NAME = "binary";
	
	/** A function that constructs an binary value from a string. */
	public static final Function<String, byte[]> VALUE_CONSTRUCTOR = s -> {
		return Base64.getDecoder().decode(s);
	};


	/**
	 * Creates the type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public BinaryType(String name) {
		super(name);
	}
	

	@Override
	public String getType() {
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

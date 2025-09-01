package be.baur.sds.types;

import java.util.function.Function;

import be.baur.sds.DataType;

/**
 * A <code>BinaryNodeType</code> defines an SDA node with a binary value in MIME
 * base64 encoding. Note that its length is counted in number of bytes rather
 * than characters.
 */
public final class BinaryNodeType extends CharacterNodeType <byte[]> {


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
		return DataType.BINARY;
	}
	
	
	@Override
	public Function<String, byte[]> valueConstructor() {
		return DataType.BINARY_CONSTRUCTOR;
	}
	
	
	/**
	 * Returns the length of the supplied binary in bytes.
	 */
	@Override
	public int valueLength(byte[] value) {
		return value.length;
	}
}

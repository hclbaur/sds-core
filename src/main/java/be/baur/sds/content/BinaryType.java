package be.baur.sds.content;

/**
 * A <code>BinaryType</code> defines an SDA node with binary content (in MIME
 * base64 encoding). When setting an allowed length interval, note that length
 * is counted in number of bytes rather than characters.
 */
public final class BinaryType extends AbstractStringType {

	public static final String NAME = "binary";
	
	
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
}

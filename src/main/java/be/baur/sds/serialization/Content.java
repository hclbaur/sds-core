package be.baur.sds.serialization;

/**
 * Content types allowed by the SDS syntax. The lower-case name of a content
 * type ("binary", "integer", etc) can be accessed using the {@code toString()}
 * method or the {@code type} field.
 */
public enum Content {

	STRING("string"), BINARY("binary"), INTEGER("integer"), DECIMAL("decimal"), 
	DATE("date"), DATETIME("datetime"), BOOLEAN("boolean"), ANY("any");

	/** The (lower-case) type name. */
	public final String type;
	
	Content(String type) {
		this.type = type;
	}
	
	
	/**
	 * Returns the (lower-case) name of this content type.
	 * 
	 * @return the type name
	 */
	public String toString() { 
		return type; 
	}


	/**
	 * Returns a content type by its name. This method returns a null reference if no
	 * content type with the specified name is known.
	 * 
	 * @param type a type name
	 * @return a content type, may be null
	 */
	public static Content get(String type) {
		for (Content c : values()) 
			if (c.type.equals(type)) return c;
		return null;
	}
}

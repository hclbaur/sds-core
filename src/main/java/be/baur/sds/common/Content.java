package be.baur.sds.common;

/**
 * This enumeration defines all SDS content types.
 */
public enum Content {

	STRING("string"), BINARY("binary"), INTEGER("integer"), DECIMAL("decimal"), 
	DATE("date"), DATETIME("datetime"), BOOLEAN("boolean"), ANY("any");

	public final String type;
	
	Content(String type) {
		this.type = type;
	}
	
	public String toString() { 
		return type; 
	}
	
	/** Return an instance by its type or <code>null</code> if not found. */
	public static Content get(String type) {
		for (Content c : values()) 
			if (c.type.equals(type)) return c;
		return null;
	}

}

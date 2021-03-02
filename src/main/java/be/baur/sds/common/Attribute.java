package be.baur.sds.common;

/**
 * This enumeration defines the tags of all SDS attributes.
 */
public enum Attribute {

	NAME("name"), TYPE("type"), MULTIPLICITY("multiplicity"), 
	LENGTH("length"), VALUE("value"), PATTERN("pattern"),
	NULLABLE("nullable");

	public final String tag;

	Attribute(String tag) {
		this.tag = tag;
	}

	public String toString() {
		return tag;
	}

	/** Return an instance by its tag or <code>null</code> if not found. */
	public static Attribute get(String tag) {
		for (Attribute a : values())
			if (a.tag.equals(tag)) return a;
		return null;
	}

}

package be.baur.sds.common;

/**
 * This enumeration defines the tags of all SDS components.
 */
public enum Component {

	NODE("node"), CHOICE("choice"), GROUP("group"), UNORDERED("unordered");

	public final String tag;
	
	Component(String tag) {
		this.tag = tag;
	}
	
	
	/** Returns the tag of this component. */
	public String toString() { 
		return tag; 
	}


	/** Returns an instance by its tag or <code>null</code> if not found. */
	public static Component get(String tag) {
		for (Component m : values()) 
			if (m.tag.equals(tag)) return m;
		return null;
	}
}

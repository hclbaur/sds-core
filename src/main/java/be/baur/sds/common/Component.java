package be.baur.sds.common;

/**
 * Components allowed by the SDS syntax. The lower-case name of a component
 * ("node", "choice", etc) can be accessed using the {@code toString()} method
 * or the {@code tag} field.
 */
public enum Component {

	NODE("node"), CHOICE("choice"), GROUP("group"), UNORDERED("unordered");

	/** The (lower-case) name tag. */
	public final String tag;
	
	Component(String tag) {
		this.tag = tag;
	}
	
	
	/**
	 * Returns the (lower-case) tag of this component.
	 * 
	 * @return the name tag
	 */
	public String toString() { 
		return tag; 
	}


	/**
	 * Returns a component by its tag. This method returns a null reference if no
	 * component with the specified tag is known.
	 * 
	 * @param tag a name tag
	 * @return a component, may be null
	 */
	public static Component get(String tag) {
		for (Component m : values()) 
			if (m.tag.equals(tag)) return m;
		return null;
	}
}

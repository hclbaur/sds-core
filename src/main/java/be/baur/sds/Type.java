package be.baur.sds;

/**
 * This is the abstract superclass of all types.
 * 
 * @see NodeType
 * @see AnyType
 */
public abstract class Type extends Component {

	private String typeName; // the name of this type


	/**
	 * Returns the name of this type.
	 * 
	 * @return a name
	 */
	public String getTypeName() {
		return typeName;
	}


	/**
	 * Sets the name of this type.
	 * 
	 * @param name a name
	 */
	protected void setTypeName(String name) {
		typeName = name;
	}
}

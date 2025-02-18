package be.baur.sds;

/**
 * This is the abstract superclass of all node types.
 * 
 * @see AnyNodeType
 * @see NodeType
 */
public abstract class AbstractNodeType extends Component {

	private String typeName; // the name of this type


	/**
	 * Returns the name of the node defined by this type.
	 * 
	 * @return a node name
	 */
	public String getTypeName() {
		return typeName;
	}


	/**
	 * Sets the name of the node defined by this type.
	 * 
	 * @param name a node name
	 */
	protected void setTypeName(String name) {
		typeName = name;
	}
}

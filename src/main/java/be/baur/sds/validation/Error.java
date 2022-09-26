package be.baur.sds.validation;

import be.baur.sda.Node;

/**
 * An <code>Error</code> occurs if an SDA document violates the constraints
 * imposed by a schema. The node that caused the error can be accessed from
 * {@link Error#getErrorNode()}.
 */
public final class Error {

	private Node errorNode;
	private String msg;

	
	/**
	 * Creates an Error for the specified <code>node</code> using the specified
	 * <code>format</code> and arguments.
	 */
	public Error(Node node, String format, Object... args) {
		this.msg = String.format(format, args);
		this.errorNode = node;
	}


	/** Returns the node that caused the error. */
	public Node getErrorNode() {
		return errorNode;
	}


	/** Returns an error message referencing the offending node. */
	public String toString() {
		return errorNode.path() + ": " + msg;
	}
}

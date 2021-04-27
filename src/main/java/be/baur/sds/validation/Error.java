package be.baur.sds.validation;

import be.baur.sda.Node;

/**
 * An <code>Error</code> is returned if an SDA document violates the constraints
 * imposed by an SDS definition. The node that caused the error can be accessed
 * through {@link Error#getErrorNode()}.
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

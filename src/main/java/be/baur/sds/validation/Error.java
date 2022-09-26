package be.baur.sds.validation;

import be.baur.sda.Node;

/**
 * An <code>Error</code> is registered if - during the validation process - an
 * SDA document is found to violate a constraint imposed by a schema. The node
 * that caused the error can be accessed from {@link Error#getErrorNode()}.
 */
public final class Error {

	private Node errorNode;
	private String message;

	
	/**
	 * Creates an {@code Error} for the specified node from a format string and
	 * arguments.
	 * 
	 * @param node   the node where the error was found
	 * @param format a format string
	 * @param args   arguments used in the format
	 */
	public Error(Node node, String format, Object... args) {
		this.message = String.format(format, args);
		this.errorNode = node;
	}


	/**
	 * Returns the node that caused the error.
	 * 
	 * @return the error node
	 */
	public Node getErrorNode() {
		return errorNode;
	}


	/**
	 * Returns an error message indicating which node caused the error.
	 * 
	 * @return an error prefixed with a path to a node
	 */
	public String toString() {
		return errorNode.path() + ": " + message;
	}
}

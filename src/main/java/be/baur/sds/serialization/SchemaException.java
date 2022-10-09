package be.baur.sds.serialization;

import be.baur.sda.Node;

/**
 * A {@code SchemaException} is thrown by the <code>SDSParser</code> if the SDS
 * syntax is violated. The node where the error occurred during parsing is
 * available from {@link #getErrorNode()}.
 */
@SuppressWarnings("serial")
public final class SchemaException extends Exception {

	private Node errorNode; // node that caused the exception to be thrown
	
	/**
	 * Creates an SDS schema exception.
	 * 
	 * @param node    the node where the error was found
	 * @param message an error message
	 */
	public SchemaException(Node node, String message) {
		super("SDS syntax violation at " + node.path() + ": " + message);
		this.errorNode = node;
	}

	
	/**
	 * Returns the node that caused the exception to be thrown.
	 * 
	 * @return the error node
	 */
	public Node getErrorNode() {
		return errorNode;
	}
}

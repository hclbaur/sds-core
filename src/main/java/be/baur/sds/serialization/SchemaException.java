package be.baur.sds.serialization;

import be.baur.sda.Node;

/**
 * A <code>SchemaException</code> is thrown if the SDS syntax is violated. The
 * node causing the exception can be accessed from {@link SchemaException#getNode}.
 */
@SuppressWarnings("serial")
public final class SchemaException extends Exception {

	private Node node; // node that caused the exception to be thrown

	
	/** Creates an SDS syntax error. */
	public SchemaException(Node node, String message) {
		super("SDS syntax violation at " + node.path() + ": " + message);
		this.node = node;
	}

	
	/** Returns the node that caused the exception to be thrown. */
	public Node getNode() {
		return node;
	}

}

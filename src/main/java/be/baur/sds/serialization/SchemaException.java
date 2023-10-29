package be.baur.sds.serialization;

import be.baur.sda.Node;
import be.baur.sda.serialization.NodeProcessingException;

/**
 * A {@code SchemaException} may be thrown by a {@code SchemaParser} if a
 * {@code Schema} cannot be created, mostly due - but not limited to - some
 * syntactical or semantical violation.
 */
@SuppressWarnings("serial")
public abstract class SchemaException extends NodeProcessingException {
	
	/**
	 * Creates a schema exception with an error message and node.
	 * 
	 * @param node    the node where the error was found
	 * @param message an error message
	 */
	public SchemaException(Node node, String message) {
		super(node, message);
	}

}

package be.baur.sds.serialization;

import be.baur.sda.Node;
import be.baur.sda.NodeException;

/**
 * An {@code SDSParseException} is thrown by an {@code SDSParser} if the SDS
 * syntax or semantics are violated.
 * 
 * @see SDSParser
 */
@SuppressWarnings("serial")
public final class SDSParseException extends NodeException {
	
	/**
	 * Creates an SDS parse exception with an error node and message.
	 * 
	 * @param node    the node where the error was found
	 * @param message an error message
	 */
	public SDSParseException(Node node, String message) {
		super(node, message);
	}


	/**
	 * Creates an SDS parse exception with an error node and a cause.
	 * 
	 * @param node  the node where the exception occurred
	 * @param cause the exception causing this exception
	 */
	public SDSParseException(Node node, Throwable cause) {
		super(node, cause.getMessage()); initCause(cause);
	} 
}

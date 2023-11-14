package be.baur.sds.serialization;

import be.baur.sda.DataNode;
import be.baur.sda.Node;
import be.baur.sda.ProcessingException;

/**
 * An {@code SDSParseException} is thrown by an {@code SDSParser} if the SDS
 * syntax or semantics are violated.
 * 
 * @see SDSParser
 */
@SuppressWarnings("serial")
public final class SDSParseException extends ProcessingException {
	
	/**
	 * Creates an SDS parse exception with an error message and node.
	 * 
	 * @param node    the node where the error was found
	 * @param message an error message
	 */
	public SDSParseException(Node node, String message) {
		super(node, "SDS syntax violation at " + node.path() + ": " + message);
	}

//	public SDSParseException(Node node, String message, Throwable cause) {
//		this(node, message); initCause(cause);
//	} 
//	
//	public SDSParseException(DataNode node, Throwable cause) {
//		super(node, cause.getMessage()); initCause(cause);
//	} 
}

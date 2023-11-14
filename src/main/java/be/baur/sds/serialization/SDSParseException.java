package be.baur.sds.serialization;

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
	 * Creates an SDS parse exception with an error node and message.
	 * 
	 * @param node    the node where the error was found
	 * @param message an error message
	 */
	public SDSParseException(Node node, String message) {
		super(node, (node != null ? ("error at " + node.path() + ": ") : "") + message);
	}


	/**
	 * Returns an SDS parse exception with an error node and formatted message.
	 * 
	 * @param node   the node where the error was found
	 * @param format a format message, and
	 * @param args arguments, as in {@link String#format}
	 */
	public static SDSParseException of(Node node, String format, Object... args) {
		return new SDSParseException(node, String.format(format, args));
	}

//	public SDSParseException(Node node, String message, Throwable cause) {
//		this(node, message); initCause(cause);
//	} 

//	public SDSParseException(DataNode node, Throwable cause) {
//		super(node, cause.getMessage()); initCause(cause);
//	} 
}

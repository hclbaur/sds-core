package be.baur.sds;

import be.baur.sda.Node;

/**
 * A <code>SchemaException</code> if the SDS syntax is violated. The SDA node
 * that caused the exception can be accessed from <code>getErrorNode()</code>.
 */
@SuppressWarnings("serial")
public final class SchemaException extends Exception {

	private Node errorNode;

	public SchemaException(Node node, String format, Object... args) {
		super("SDS syntax violation at " + node.path() + ": " + String.format(format, args));
		errorNode = node;
	}

	/** Returns the node that caused the exception. */
	public Node getErrorNode() {
		return errorNode;
	}

	// format strings
	public static final String ComponentNotAllowed = "component '%s' is not allowed here";
	public static final String ComponentIncomplete = "component '%s' is incomplete";
	public static final String ComponentExpected = "component '%s' is expected";
	public static final String ComponentUnknown = "component '%s' is unknown";
	public static final String ComponentEmpty = "component '%s' is empty";

	public static final String AttributeNotSingular = "attribute '%s' can occur only once";
	public static final String AttributeNotAllowed = "attribute '%s' is not allowed here";
	public static final String AttributeUnknown = "attribute '%s' is unknown";
	public static final String AttributeMissing = "attribute '%s' is missing";
	public static final String AttributeEmpty = "attribute '%s' is empty";
	public static final String AttributeInvalid = "%s '%s' is invalid; %s";
	
	public static final String ContentTypeUnknown = "content type '%s' is unknown";
}

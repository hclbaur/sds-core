package be.baur.sds.validation;

import be.baur.sda.Node;

/**
 * An <code>Error</code> is returned if an SDA document violates the constraints
 * imposed by an SDS definition. The node that caused the error can be accessed
 * through <code>getNode()</code>.
 */
public class Error {

	private Node node;
	private String msg;

	public Error(Node node, String format, Object... args) {
		this.node = node;
		msg = String.format(format, args);
	}

	/** Returns the node that caused the error. */
	public Node getNode() {
		return node;
	}

	/** Returns an error message with reference to the offending node. */
	public String toString() {
		return node.path() + ": " + msg;
	}

	// format strings
	public static final String GLOBAL_TYPE_NOT_FOUND = "global type '%s' not found";
	public static final String EXPECTING_NODE_TYPE = "expecting node '%s' to be a %s type";
	public static final String MANDATORY_NODE_EXPECTED = "mandatory node '%s' expected in '%s'";
	public static final String CONTENT_MISSING_AT_END = "mandatory content missing at end of '%s'";
	public static final String CONTENT_MISSING_BEFORE = "mandatory content missing before '%s'";
	public static final String EXPECTING_NODE_BUT_GOT = "expecting node '%s', but got '%s' instead";
	public static final String NODE_NOT_EXPECTED_IN = "node '%s' not expected in '%s'";
	
	public static final String INVALID_VALUE_FOR_TYPE = "value '%s' is invalid for type %s: %s";
	public static final String EMPTY_VALUE_NOT_ALLOWED = "empty value not allowed; node '%s' is not nullable";
	public static final String VALUE_DOES_NOT_MATCH= "value '%s' does not match pattern '%s'";
	public static final String INVALID_BINARY_VALUE = "node '%s' has an invalid binary value: %s";
	public static final String INVALID_BOOLEAN_VALUE = "value '%s' is not a valid boolean";
	public static final String LENGTH_SUBCEEDS_MIN = "value '%s' has length %d but %d is the minimum";
	public static final String LENGTH_EXCEEDS_MAX = "value '%s' has length %d but %d is the maximum";
	public static final String VALUE_SUBCEEDS_MIN = "value '%s' subceeds the minimum of %s";
	public static final String VALUE_EXCEEDS_MAX = "value '%s' exceeds the maximum of %s";
}

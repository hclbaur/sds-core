package be.baur.sds;

import java.io.IOException;
import java.io.Reader;

import be.baur.sds.serialization.SDSParseException;
import be.baur.sds.serialization.SDSParser;

/**
 * This class defines static constants and utility methods.
 */
public final class SDS {
	
	private SDS() {} // cannot construct this

	private static SDSParser PARSER = new SDSParser(); // singleton parser

	/**
	 * Creates a schema from a character input stream in SDS format, using the
	 * default SDS parser.
	 * 
	 * @param input an input stream
	 * @return a schema
	 * @throws IOException       if an I/O operation failed
	 * @throws SDSParseException if an SDS parse exception occurs
	 * @see SDSParser
	 */
	public static Schema parse(Reader input) throws IOException, SDSParseException {
		return PARSER.parse(input);
	}
	
	
//	/** Convenience method that throws an exception if the argument is null.
//	 * 
//	 * @param obj the argument to check
//	 * @param msg the exception message
//	 * @return the original argument
//	 */
//	public static <T> T requireNonNull(T obj, String msg) {
//	    if (obj == null)
//	    	throw new IllegalArgumentException(msg);
//	    return obj;
//	}
}

package be.baur.sds;

import java.io.IOException;
import java.io.Reader;

import be.baur.sda.serialization.SDAParseException;
import be.baur.sds.serialization.SDSParseException;
import be.baur.sds.serialization.SDSParser;
import be.baur.sds.validation.SDAValidator;
import be.baur.sds.validation.Validator;

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
	 * @return a schema
	 * @throws IOException       if an I/O operation failed
	 * @throws SDAParseException if an SDA parse exception occurs
	 * @throws SDSParseException if an SDS parse exception occurs
	 * @see SDSParser
	 */
	public static Schema parse(Reader input) throws IOException, SDAParseException, SDSParseException {
		return PARSER.parse(input);
	}

	
	/**
	 * Returns a new instance of the default SDS validator.
	 * 
	 * @return an {@link SDAValidator}
	 */
	public static Validator validator() {
		return new SDAValidator();
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

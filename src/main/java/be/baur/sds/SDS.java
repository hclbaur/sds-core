package be.baur.sds;

import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.function.Function;

import be.baur.sds.serialization.SDSParseException;
import be.baur.sds.serialization.SDSParser;

/**
 * This class defines static constants and utility methods.
 */
public final class SDS {
	
	private SDS() {} // cannot construct this


	/*
	 * Native SDS data types and constructor functions.
	 */
	
	/** Name of the SDS string type. */
	public static final String STRING_TYPE = "string";
	
	/** Function to construct an SDS string value from a string. */
	public static final Function<String, String> STRING_CONSTRUCTOR = s -> {
		return s; // strings are immutable so just return the original
	};


	/** Name of the SDS binary type. */
	public static final String BINARY_TYPE = "binary";

	/**
	 * Function to construct an SDS binary value from a string.
	 * @throws IllegalArgumentException if the string is not in valid Base64 scheme.
	 */
	public static final Function<String, byte[]> BINARY_CONSTRUCTOR = s -> {
		return Base64.getDecoder().decode(s);
	};


	/** Name of the SDS integer type. */
	public static final String INTEGER_TYPE = "integer";
	
	/**
	 * Function to construct an SDS integer value from a string.
	 * @throws NumberFormatException if the string cannot be converted to an integer.
	 */
	public static final Function<String, Integer> INTEGER_CONSTRUCTOR = Integer::new;
	

	/** Name of the SDS decimal type. */
	public static final String DECIMAL_TYPE = "decimal";
	
	/**
	 * Function to construct an SDS decimal value from a string.
	 * @throws NumberFormatException if the string cannot be converted to a number.
	 */
	public static final Function<String, Double> DECIMAL_CONSTRUCTOR = Double::new;


	/** Name of the SDS date type. */
	public static final String DATE_TYPE = "date";

	/**
	 * Function to construct an SDS date value from a string.
	 * @throws DateTimeParseException if the string cannot be converted to a date.
	 */
	public static final Function<String, LocalDate> DATE_CONSTRUCTOR = s -> {
		return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
	};


	/** Name of the SDS datetime type. */
	public static final String DATETIME_TYPE = "datetime";

	/**
	 * Function to construct an SDS datetime value from a string.
	 * @throws DateTimeParseException if the string cannot be converted to a datetime.
	 */
	public static final Function<String, ZonedDateTime> DATETIME_CONSTRUCTOR = s -> {
		return ZonedDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	};


	/** Name of the SDS boolean type. */
	public static final String BOOLEAN_TYPE = "boolean";

	/**
	 * Function to construct an SDS boolean value from a string.
	 * @throws IllegalArgumentException if the string equals neither "true" nor "false".
	 */
	public static final Function<String, Boolean> BOOLEAN_CONSTRUCTOR = s -> {
		if (s != null && (s.equals("true") || s.equals("false"))) return Boolean.valueOf(s);
		throw new IllegalArgumentException("either true or false is expected");
	};


	/*
	 * Parser related fields and methods
	 */
	
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

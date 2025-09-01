package be.baur.sds;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.function.Function;

/**
 * This class defines static constants for the names and constructors of the
 * built-in SDS data types, and methods to (pre-)register and retrieve those
 * constructors.
 */
public final class DataType {
	
	private DataType() {} // cannot construct this


	/*
	 * Native SDS data types and constructor functions.
	 */
	
	/** Name of the SDS string data type. */
	public static final String STRING = "string";
	
	/** Function to construct an SDS string value from a string. */
	public static final Function<String, String> STRING_CONSTRUCTOR = s -> {
		return s; // strings are immutable so just return the original
	};


	/** Name of the SDS binary data type. */
	public static final String BINARY = "binary";

	/**
	 * Function to construct an SDS binary value from a string.
	 * @throws IllegalArgumentException if the string is not in valid Base64 scheme.
	 */
	public static final Function<String, byte[]> BINARY_CONSTRUCTOR = s -> {
		return Base64.getDecoder().decode(s);
	};


	/** Name of the SDS integer data type. */
	public static final String INTEGER = "integer";
	
	/**
	 * Function to construct an SDS integer value from a string.
	 * @throws NumberFormatException if the string cannot be converted to an integer.
	 */
	public static final Function<String, Integer> INTEGER_CONSTRUCTOR = Integer::new;
	

	/** Name of the SDS decimal data type. */
	public static final String DECIMAL = "decimal";
	
	/**
	 * Function to construct an SDS decimal value from a string.
	 * @throws NumberFormatException if the string cannot be converted to a number.
	 */
	public static final Function<String, Double> DECIMAL_CONSTRUCTOR = Double::new;


	/** Name of the SDS date data type. */
	public static final String DATE = "date";

	/**
	 * Function to construct an SDS date value from a string.
	 * @throws DateTimeParseException if the string cannot be converted to a date.
	 */
	public static final Function<String, LocalDate> DATE_CONSTRUCTOR = s -> {
		return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
	};


	/** Name of the SDS datetime data type. */
	public static final String DATETIME = "datetime";

	/**
	 * Function to construct an SDS datetime value from a string.
	 * @throws DateTimeParseException if the string cannot be converted to a datetime.
	 */
	public static final Function<String, ZonedDateTime> DATETIME_CONSTRUCTOR = s -> {
		return ZonedDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	};


	/** Name of the SDS boolean data type. */
	public static final String BOOLEAN = "boolean";

	/**
	 * Function to construct an SDS boolean value from a string.
	 * @throws IllegalArgumentException if the string equals neither "true" nor "false".
	 */
	public static final Function<String, Boolean> BOOLEAN_CONSTRUCTOR = s -> {
		if (s != null && (s.equals("true") || s.equals("false"))) return Boolean.valueOf(s);
		throw new IllegalArgumentException("either true or false is expected");
	};

}

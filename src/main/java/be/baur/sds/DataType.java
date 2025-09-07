package be.baur.sds;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * This class defines static constants for the names and constructors of the
 * built-in SDS data types, and methods to (pre-)register and retrieve those
 * constructors. This allows us to keep code generic and SDS extensible with
 * new or custom data types.
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


	/*
	 * Register native SDS data types.
	 */
	
	// Map that holds constructor functions to produce SDS data types
	private static final Map<String, Function<String, ?>> conmap = new HashMap<String, Function<String, ?>>();
	
	static {
		register(DataType.STRING, DataType.STRING_CONSTRUCTOR);
		register(DataType.BINARY, DataType.BINARY_CONSTRUCTOR);
		register(DataType.INTEGER, DataType.INTEGER_CONSTRUCTOR);
		register(DataType.DECIMAL, DataType.DECIMAL_CONSTRUCTOR);
		register(DataType.DATE, DataType.DATE_CONSTRUCTOR);
		register(DataType.DATETIME, DataType.DATETIME_CONSTRUCTOR);
		register(DataType.BOOLEAN, DataType.BOOLEAN_CONSTRUCTOR);
	}


	/**
	 * Registers a constructor function for the specified data type. This method
	 * returns true if a constructor was registered and false if a constructor was
	 * already registered for this type. The type and function must not be null.
	 * 
	 * @param type the data type to register, not null or empty
	 * @param func a data type constructor function, not null
	 * @return true or false
	 * @throws IllegalArgumentException if type is null or empty
	 */
	public static boolean register(String type, Function<String, ?> func) {
		
		Objects.requireNonNull(func, "constructor function must not be null");
		if (type == null || type.isEmpty())
			throw new IllegalArgumentException("type must not be null or empty");
		
		if (! conmap.containsKey(type)) {
			conmap.put(type, func);
			return true;
		}
		return false;
	}


	/**
	 * Returns a constructor function for the specified data type, or throws an
	 * exception if the type is unknown (e.g. if it has not been registered).
	 * 
	 * @param type a data type
	 * @return a constructor function
	 * @throws IllegalArgumentException if the type is not known
	 */
	public static Function<String, ?> getConstructor(String type) {
		if (! isRegistered(type))
			throw new IllegalArgumentException("type '" + type + "' is unknown");
		return conmap.get(type);
	}


	/**
	 * Returns true if the argument is a registered data type, and false otherwise.
	 * 
	 * @param type a data type
	 * @return true or false
	 */
	public static boolean isRegistered(String type) {
		return type == null ? false : conmap.containsKey(type);
	}

}

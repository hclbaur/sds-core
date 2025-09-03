package be.baur.sds;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

import be.baur.sds.types.BinaryNodeType;
import be.baur.sds.types.BooleanNodeType;
import be.baur.sds.types.DateNodeType;
import be.baur.sds.types.DateTimeNodeType;
import be.baur.sds.types.DecimalNodeType;
import be.baur.sds.types.IntegerNodeType;
import be.baur.sds.types.StringNodeType;


/**
 * This abstract class defines a generic SDA node type with a value. It is
 * extended by several node types to implement various data types:
 * {@code StringNodeType}, {@code IntegerNodeType}, {@code BooleanNodeType},
 * etc. In addition, it has static methods for registration of these types.
 */
public abstract class DataNodeType <T> extends NodeType {

	private Pattern pattern = null;		// pre-compiled pattern.
	private boolean nullable = false; 	// null-ability (if that is a word).	

	
	/**
	 * Creates a type that defines a node with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public DataNodeType(String name) {
		super(name);
	}


	/**
	 * Returns the name of the data type, e.g. "string", "integer", "boolean",
	 * etc.
	 * 
	 * @return a data type, not null or empty
	 */
	public abstract String getDataType();


	/**
	 * Returns a constructor function that accepts a string and returns a value
	 * appropriate for a node of this type.
	 * <p>
	 * Note: when applied, the function may throw an exception if the argument is
	 * not within the lexical space for this type (e.g. when the supplied string
	 * cannot be converted to a valid value).
	 * 
	 * @return a constructor function
	 */
	public abstract Function<String, T> valueConstructor();


	/**
	 * Returns the lexical space restriction pattern for the value. This method will
	 * return a null reference if no pattern has been set.
	 * 
	 * @return a (pre-compiled) pattern, may be null
	 */
	public Pattern getPattern() {
		return pattern;
	}

	
	/**
	 * Sets the lexical space restriction pattern for the value.
	 * 
	 * @param pattern a (pre-compiled) pattern, may be null
	 */
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	
	/**
	 * Returns whether an empty value is allowed (nullable).
	 * 
	 * @return true or false
	 */
	public boolean isNullable() {
		return nullable;
	}


	/**
	 * Sets whether an empty value is allowed (nullable).
	 * 
	 * @param nullable true or false
	 */
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	
	
	/*
	 * Register native SDS data node types.
	 */
	
	// Map that holds constructor functions to produce SDS data node types
	@SuppressWarnings("rawtypes")
	private static Map<String, Function<String, DataNodeType>> conmap = new HashMap<String, Function<String, DataNodeType>>();
	
	static {
		register(DataType.STRING, StringNodeType::new );
		register(DataType.BINARY, BinaryNodeType::new );
		register(DataType.INTEGER, IntegerNodeType::new );
		register(DataType.DECIMAL, DecimalNodeType::new );
		register(DataType.DATE, DateNodeType::new );
		register(DataType.DATETIME, DateTimeNodeType::new );
		register(DataType.BOOLEAN, BooleanNodeType::new );
	}


	/**
	 * Registers a constructor function for the specified data node type. This
	 * method returns true if a constructor was registered and false if a
	 * constructor was already registered for this type. The type and function must
	 * not be null.
	 * 
	 * @param type the data type, not null or empty
	 * @param func a data node type constructor function, not null
	 * @return true or false
	 * @throws IllegalArgumentException if type is null or empty
	 */
	@SuppressWarnings("rawtypes")
	public static boolean register(String type, Function<String, DataNodeType> func) {
		
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
	 * Returns a constructor function for the specified data node type, or throws an
	 * exception if the type is unknown (e.g. if it has not been registered).
	 * 
	 * @param type a data type
	 * @return a constructor function
	 * @throws IllegalArgumentException if the type is not known
	 */
	@SuppressWarnings("rawtypes")
	public static Function<String, DataNodeType> getConstructor(String type) {
		if (! isRegistered(type))
			throw new IllegalArgumentException("type '" + type + "' is unknown");
		return conmap.get(type);
	}


	/**
	 * Returns true if a data node type was registered for this specific data type,
	 * and false otherwise.
	 * 
	 * @param type a data type
	 * @return true or false
	 */
	public static boolean isRegistered(String type) {
		return type == null ? false : conmap.containsKey(type);
	}

}

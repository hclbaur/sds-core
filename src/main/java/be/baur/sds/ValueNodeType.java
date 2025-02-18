package be.baur.sds;

import java.util.function.Function;
import java.util.regex.Pattern;


/**
 * This abstract class defines a generic SDA node type with a value. It is
 * extended by several node types to implement specific value data types:
 * {@code StringNodeType}, {@code IntegerNodeType}, {@code BooleanNodeType},
 * etc.
 */
public abstract class ValueNodeType <T> extends NodeType {

	private Pattern pattern = null;		// pre-compiled pattern.
	private boolean nullable = false; 	// null-ability (if that is a word).	

	
	/**
	 * Creates a type that defines a node with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public ValueNodeType(String name) {
		super(name);
	}


	/**
	 * Returns the name of the value data type, e.g. "string", "integer", "boolean",
	 * etc.
	 * 
	 * @return a value data type name, not null
	 */
	public abstract String getValueType();


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

}

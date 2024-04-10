package be.baur.sds;

import java.util.regex.Pattern;

import be.baur.sda.SDA;


/**
 * A {@code DataType} represents an SDA node definition with complex and/or
 * simple content. This abstract type is sub-classed for appropriate content
 * types: {@code StringType}, {@code IntegerType}, {@code BooleanType}, etc.
 */
public abstract class DataType extends NodeType {

	private Pattern pattern = null;		// pre-compiled pattern.
	private boolean nullable = false; 	// null-ability (if that is a word).	

	
	/**
	 * Creates a node type with the specified name.
	 * 
	 * @param name a valid node name, see {@link SDA#isName}
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public DataType(String name) {
		super(name);
	}
	
	
//	/**
//	 * Returns a value of an appropriate type if the supplied string is within the
//	 * lexical space of this SDS data type. Otherwise this method returns null.
//	 * 
//	 * @param str a String representing a value
//	 * @return an Object or null
//	 */
//	public abstract Object valueOf(String str) ;


	/**
	 * Returns the name of this data type, e.g. "string", "integer", "boolean", etc.
	 * 
	 * @return a type, not null
	 */
	public abstract String getType();
	
	
	/**
	 * Returns the pattern that simple content must match. This method will
	 * return a null reference if no pattern has been set.
	 * 
	 * @return a (pre-compiled) pattern, may be null
	 */
	public Pattern getPattern() {
		return pattern;
	}

	
	/**
	 * Sets the simple content pattern for this type.
	 * 
	 * @param pattern a (pre-compiled) pattern, may be null
	 */
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	
	/**
	 * Returns whether empty simple content is allowed (nullable).
	 * 
	 * @return true or false
	 */
	public boolean isNullable() {
		return nullable;
	}


	/**
	 * Sets whether empty simple content is allowed (nullable).
	 * 
	 * @param nullable true or false
	 */
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

}

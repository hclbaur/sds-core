package be.baur.sds;

import java.util.regex.Pattern;

import be.baur.sda.Node;
import be.baur.sds.common.Content;


/**
 * A {@code MixedType} represents an SDA node definition with complex and/or
 * simple content. This abstract type is sub-classed for appropriate content
 * types: {@code StringType}, {@code IntegerType}, {@code BooleanType}, etc.
 */
public abstract class MixedType extends NodeType {

	private Pattern pattern = null;		// the pre-compiled (from pattexp) pattern.
	private boolean nullable = false; 	// default null-ability (if that is a word).	

	
	/**
	 * Creates a node type with the specified name.
	 * 
	 * @param name a valid node name, see also {@link Node}
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public MixedType(String name) {
		super(name);
	}
	
	
	/**
	 * Returns the simple content type.
	 * 
	 * @return a content type, not null
	 */
	public abstract Content getContentType();
	
	
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

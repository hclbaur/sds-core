package be.baur.sds;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import be.baur.sda.Node;
import be.baur.sds.common.Content;


/**
 * A {@code MixedType} represents an SDA node definition with complex and/or
 * simple content. This abstract type is sub-classed for appropriate content
 * types: {@code StringType}, {@code IntegerType}, {@code BooleanType}, etc.
 */
public abstract class MixedType extends NodeType {

	private String pattexp = null; 		// the regular expression defining the pattern.
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
	 * Returns the pattern that valid simple content must match. This method will
	 * return a null reference if no pattern expression has been set.
	 * 
	 * @return the (pre-compiled) pattern, may be null
	 */
	public Pattern getPattern() {
		return pattern;
	}

	
	/**
	 * Returns the regular expression that valid simple content must match. This
	 * method will return a null reference if no pattern expression has been set.
	 * 
	 * @return a regular expression, may be null
	 */
	@Deprecated  // can be obtained from the toString method of the pattern
	public String getPatternExpr() {
		return pattexp;
	}


	/**
	 * Sets the simple content pattern for this type from a regular expression.
	 * 
	 * @param regexp a regular expression
	 * @throws PatternSyntaxException if the regular expression is invalid.
	 */
	public void setPatternExpr(String regexp) {
		if (regexp == null || regexp.isEmpty()) {
			pattexp = null; pattern = null;
		}
		else {
			pattern = Pattern.compile(regexp);
			pattexp = regexp; // set after successful compile!
		}
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

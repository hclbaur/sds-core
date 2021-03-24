package be.baur.sds;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import be.baur.sda.ComplexNode;
import be.baur.sda.SimpleNode;
import be.baur.sds.common.Attribute;
import be.baur.sds.common.Component;
import be.baur.sds.common.Content;
import be.baur.sds.common.NaturalInterval;
import be.baur.sds.content.AnyType;
import be.baur.sds.content.RangedType;
import be.baur.sds.content.StringType;

/**
 * A <code>SimpleType</code> defines a simple SDA node, with a content type like
 * a string, integer, date, etc. As it does not need to contain other components
 * it extends {@link SimpleNode}.
 */
public abstract class SimpleType extends SimpleNode implements ComponentType {

	/** Construct a simple type with default multiplicity (mandatory and singular). */
	public SimpleType(String name) {
		super(name, null); // the value field is currently not used
	}
	
	// Set when constructed from a reference.
	private String globaltype = null;
	
	public String getGlobalType() {
		return globaltype;
	}

	public void setGlobalType(String type) {
		this.globaltype = type;
	}
	
	/** Returns the content type. */
	public abstract Content getContentType();
	
	private NaturalInterval multiplicity = null;	// Multiplicity null means: exactly once.
	private String pattexp = null;  				// Regular expression defining the pattern.
	private Pattern pattern = null;					// Pre-compiled pattern (from expression).
	private boolean nullable = false;				// Default null-ability is false.
	
	public NaturalInterval getMultiplicity() {
		return multiplicity;
	}

	public void setMultiplicity(NaturalInterval multiplicity) {
		this.multiplicity = multiplicity;
	}
	
	/** Returns the (pre-compiled) pattern for this type. */
	public Pattern getPattern() {
		return pattern;
	}

	/** Returns the pattern as a regular expression. */
	public String getPatternExpr() {
		return pattexp;
	}

	/**
	 * Sets the pattern for this type from a regular expression.
	 * @throws PatternSyntaxException if the expression is invalid.
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

	/** Returns the null-ability (if that is even a word). */
	public boolean isNullable() {
		return nullable;
	}

	/** Sets the null-ability (if not equal to <code>null</null>). */
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	
	public ComplexNode toNode() {
		
		ComplexNode node = new ComplexNode(Component.NODE.tag);
		
		// name attribute is omitted for an unnamed any type, and for a type
		// reference that has the same name as the global type it refers to
		if (! (( getGlobalType() != null && getName().equals(getGlobalType()) )
			|| ( this instanceof AnyType && !((AnyType) this).isNamed() )) ) {
			node.nodes.add(new SimpleNode(Attribute.NAME.tag, getName()));
		}
		
		// set the content type - or in case of a reference - the global type
		node.nodes.add(new SimpleNode(Attribute.TYPE.tag,
			getGlobalType() == null ? getContentType().type : getGlobalType()));
		
		if (minOccurs() != 1 || maxOccurs() != 1)
			node.nodes.add(new SimpleNode(Attribute.MULTIPLICITY.tag, multiplicity.toString()));
		
		boolean stringType = (this instanceof StringType);
		if (stringType) {
			StringType t = (StringType) this;
			if (t.minLength() != 0 || t.maxLength() != Integer.MAX_VALUE)
				node.nodes.add(new SimpleNode(Attribute.LENGTH.tag, t.getLength().toString()));
		}

		if (this instanceof RangedType) {
			RangedType<?> t = (RangedType<?>) this;
			if (t.getRange() != null)
				node.nodes.add(new SimpleNode(Attribute.VALUE.tag, t.getRange().toString()));
		}
		
		if (pattern != null)
			node.nodes.add(new SimpleNode(Attribute.PATTERN.tag, pattexp));
		
		if (stringType == !nullable)
			node.nodes.add(new SimpleNode(Attribute.NULLABLE.tag, String.valueOf(nullable)));
		
		return node;
	}

	
	@Override
	public String toString() {
		return toNode().toString();
	}
}

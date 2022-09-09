package be.baur.sds;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import be.baur.sda.Node;
import be.baur.sds.common.Attribute;
import be.baur.sds.common.Component;
import be.baur.sds.common.Content;
import be.baur.sds.common.NaturalInterval;
import be.baur.sds.content.AbstractStringType;
import be.baur.sds.content.AnyType;
import be.baur.sds.content.RangedType;

/**
 * A <code>SimpleType</code> represents an SDS definition of a simple SDA node,
 * with a simple content type like a string, integer, date, etc.
 */
public abstract class SimpleType extends Node implements ComponentType {

	private String globaltype = null; // the global type this component refers to.
	private NaturalInterval multiplicity = null; // the default multiplicity: mandatory and singular.
	private String pattexp = null; // the regular expression defining the pattern.
	private Pattern pattern = null;	// the pre-compiled pattern (from expression).
	private boolean nullable = false; // the default null-ability (if that is a word).	
	
	/** Creates a simple type with the specified <code>name</code>.*/
	public SimpleType(String name) {
		super(name, null); // the value field is currently not used
	}

	
	public String getGlobalType() {
		return globaltype;
	}

	
	public void setGlobalType(String type) {
		this.globaltype = type;
	}
	
	
	/** Returns the content type. */
	public abstract Content getContentType();

	
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

	
	/** Returns whether this type is null-able. */
	public boolean isNullable() {
		return nullable;
	}

	
	/** Sets whether this type is null-able. */
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	
	public final Node toNode() {
		
		Node node = new Node(Component.NODE.tag);
		
		// name attribute is omitted for an unnamed any type, and for a type
		// reference that has the same name as the global type it refers to
		if (! (( getGlobalType() != null && getName().equals(getGlobalType()) )
			|| ( this instanceof AnyType && !((AnyType) this).isNamed() )) ) {
			node.addNode(new Node(Attribute.NAME.tag, getName()));
		}
		
		// set the content type - or in case of a reference - the global type
		node.addNode(new Node(Attribute.TYPE.tag,
			getGlobalType() == null ? getContentType().type : getGlobalType()));
		
		// Render the multiplicity if not default.
		if (multiplicity != null && (multiplicity.min != 1 || multiplicity.max != 1)) 
			node.getNodes().add(new Node(Attribute.OCCURS.tag, multiplicity.toString()));
		
		boolean stringType = (this instanceof AbstractStringType);
		if (stringType) {
			AbstractStringType t = (AbstractStringType) this;
			if (t.minLength() != 0 || t.maxLength() != Integer.MAX_VALUE)
				node.getNodes().add(new Node(Attribute.LENGTH.tag, t.getLength().toString()));
		}

		if (this instanceof RangedType) {
			RangedType<?> t = (RangedType<?>) this;
			if (t.getRange() != null)
				node.getNodes().add(new Node(Attribute.VALUE.tag, t.getRange().toString()));
		}
		
		if (pattern != null)
			node.getNodes().add(new Node(Attribute.PATTERN.tag, pattexp));
		
		if (stringType == !nullable)
			node.getNodes().add(new Node(Attribute.NULLABLE.tag, String.valueOf(nullable)));
		
		return node;
	}

	
	@Override
	public final String toString() {
		return toNode().toString();
	}
}

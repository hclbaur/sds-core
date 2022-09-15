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
 * A <code>NodeType</code> represents an SDS definition of an SDA node, with
 * simple and/or complex content; the basic building block of a {@link Schema}.
 */
public class NodeType extends ComponentType {

	private String globaltype = null; 				// name of the global type this component refers to.
	private NaturalInterval multiplicity = null; 	// default multiplicity (mandatory and singular).
	private String pattexp = null; 					// the regular expression defining the pattern.
	private Pattern pattern = null;					// the pre-compiled (from pattexp) pattern.
	private boolean nullable = false; 				// default null-ability (if that is a word).	
	
	// should overwrite addNode to accept only NodeType?
	
	/** Creates a type with the specified <code>name</code>.*/
	public NodeType(String name) {
		super(name); // the value field is currently not used in a type definition
	}

	
	/** Returns the name of the referenced global type. */
	public String getGlobalType() {
		return globaltype;
	}

	
	/** Sets the name of the referenced global type. */
	public void setGlobalType(String type) {
		this.globaltype = type;
	}
	
	
	/** Returns the (simple) content type. */
	public Content getContentType() {
		return null;
	}

	
	/**
	 * Returns the formal multiplicity of this component. The default is
	 * <code>null</code>, which means the component must occur exactly once.
	 */
	public NaturalInterval getMultiplicity() {
		return multiplicity;
	}

	
	/**
	 * Sets the multiplicity of this component. The default is <code>null</code>,
	 * which means the component must occur exactly once.
	 */
	public void setMultiplicity(NaturalInterval multiplicity) {
		this.multiplicity = multiplicity;
	}
	
	
	/** Returns the (pre-compiled) pattern that valid simple content must match. */
	public Pattern getPattern() {
		return pattern;
	}

	/** Returns the simple content pattern as a regular expression. */
	public String getPatternExpr() {
		return pattexp;
	}


	/**
	 * Sets and compiles the simple content pattern for this type from a regular expression.
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

	
	/** Returns whether this type is null-able. */
	public boolean isNullable() {
		return nullable;
	}

	
	/** Sets whether this type is null-able. */
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	
	/**
	 * Returns an SDA node structure that represents this component. In other words,
	 * what an SDA parser would return upon processing an input stream defining the
	 * component in SDS syntax.
	 */
	public final Node toNode() {
		
		Node node = new Node(Component.NODE.tag);
/*		
		// Omit the name for an unnamed any type, and for a type
		// reference with the same name as the referenced type
		if (! (( getGlobalType() != null && getName().equals(getGlobalType()) )
				|| ( this instanceof AnyType && !((AnyType) this).isNamed() )) ) {
			node.setValue(getName());
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
*/		
		return node;
	}

	
	@Override
	public final String toString() {
		return toNode().toString();
	}
}

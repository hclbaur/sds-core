package be.baur.sds;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import be.baur.sda.Node;
import be.baur.sda.NodeSet;
import be.baur.sds.common.Attribute;
import be.baur.sds.common.Component;
import be.baur.sds.common.Content;
import be.baur.sds.content.AbstractStringType;
import be.baur.sds.content.AnyType;
import be.baur.sds.content.BooleanType;
import be.baur.sds.content.IntegerType;
import be.baur.sds.content.RangedType;
import be.baur.sds.content.StringType;


/**
 * A {@code NodeType} represents an SDS type definition of an SDA node, with
 * simple and/or complex content. It is the basic building block (a component)
 * of a {@link Schema}.
 * 
 * Note that an instance of this class is a <i>complex type</i>; it cannot have
 * simple content. For a <i>simple type</i>, instantiate one of its subclasses,
 * like {@link StringType}, {@link IntegerType}, {@link BooleanType}, etc.
 * Subsequently, you may create a <i>mixed type</i> by adding child components.
 */
public class NodeType extends ComponentType {

	private String pattexp = null; 		// the regular expression defining the pattern.
	private Pattern pattern = null;		// the pre-compiled (from pattexp) pattern.
	private boolean nullable = false; 	// default null-ability (if that is a word).	

	private NodeType globaltypenode = null; // the global type this component refers to.
	
	/** Creates a type with the specified <code>name</code>.*/
	public NodeType(String name) {
		super(name); // the value field is currently not used in a type definition
	}

	// should I overwrite add() to accept only ComponentType?
	
	
	/** Returns the (simple) content type. */
	public Content getContentType() {
		return null; // by default no simple content, subclasses override this method
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

	
	/*
	 * The following three methods overrides the super type method to handle type
	 * references. For a regular node type, we just return the child nodes. But a
	 * type reference has no children; it is just a reference to a global type in
	 * the schema root. So we find that type and treat its children as if they were
	 * our own. Obviously this does not constitute an actual parent-child relation,
	 * and may cause unexpected behavior at some point in the future, but we shall
	 * cross that bridge when we get there.
	 */
	@Override
	public final NodeSet getNodes() {
		
		if (getGlobalType() == null) return super.getNodes();
		if (globaltypenode == null) // not bound yet, so get it from the schema root
			globaltypenode = (NodeType) this.root().getNodes().get(getGlobalType()).get(1);
		return globaltypenode.getNodes(); // should never cause NPE
	}

	@Override /* handle type reference */
	public final boolean isComplex() {
		
		if (getGlobalType() == null) return super.isComplex();
		if (globaltypenode == null) // not bound yet, so get it from the schema root
			globaltypenode = (NodeType) this.root().getNodes().get(getGlobalType()).get(1);
		return globaltypenode.isComplex(); // should never cause NPE
	}

	@Override /* handle type reference */
	public final boolean isParent() {
		
		if (getGlobalType() == null) return super.isParent();
		if (globaltypenode == null) // not bound yet, so get it from the schema root
			globaltypenode = (NodeType) this.root().getNodes().get(getGlobalType()).get(1);
		return globaltypenode.isParent(); // should never cause NPE
	}
	
	
	public Node toNode() {
		
		Node node = new Node(Component.NODE.tag);
		
		// Omit the name for an unnamed any type, and for a type
		// reference with the same name as the referenced type.
		if (! (( getGlobalType() != null && getName().equals(getGlobalType()) )
				|| ( this instanceof AnyType && !((AnyType) this).isNamed() )) ) {
			node.setValue(getName());
		}
	
		// Render the type attribute for a global type reference,
		// or for the simple content type, if we have that.
		if (getGlobalType() != null)
			node.add(new Node(Attribute.TYPE.tag, getGlobalType()));
		else if (getContentType() != null)
			node.add(new Node(Attribute.TYPE.tag, getContentType().type));
		
		// Render the multiplicity if not default.
		if (getMultiplicity() != null && (getMultiplicity().min != 1 || getMultiplicity().max != 1)) 
			node.add(new Node(Attribute.OCCURS.tag, getMultiplicity().toString()));
		
		boolean stringType = (this instanceof AbstractStringType);
		if (stringType) {
			AbstractStringType t = (AbstractStringType) this;
			if (t.minLength() != 0 || t.maxLength() != Integer.MAX_VALUE)
				node.add(new Node(Attribute.LENGTH.tag, t.getLength().toString()));
		}

		if (this instanceof RangedType) {
			RangedType<?> t = (RangedType<?>) this;
			if (t.getRange() != null)
				node.add(new Node(Attribute.VALUE.tag, t.getRange().toString()));
		}
		
		if (pattern != null)
			node.add(new Node(Attribute.PATTERN.tag, pattexp));
		
		if (stringType == !nullable)
			node.add(new Node(Attribute.NULLABLE.tag, String.valueOf(nullable)));
		
		// Finally, render any children, unless we are a type reference.
		if (isParent() && getGlobalType() == null)
			for (Node child : getNodes()) node.add(((ComponentType) child).toNode());
		
		return node;
	}


	public String toString() {
		return toNode().toString();
	}
}

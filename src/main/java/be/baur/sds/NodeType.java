package be.baur.sds;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import be.baur.sda.Node;
import be.baur.sda.NodeSet;
import be.baur.sds.content.AbstractStringType;
import be.baur.sds.content.AnyType;
import be.baur.sds.content.RangedType;
import be.baur.sds.serialization.Attribute;
import be.baur.sds.serialization.Components;
import be.baur.sds.serialization.Content;


/**
 * A {@code NodeType} represents an SDA node definition, with simple and/or
 * complex content. It is one of the building blocks of a {@code Schema}.
 * 
 * Note that an instance of this class is a <i>complex type</i>; it cannot have
 * simple content. For a <i>simple type</i>, instantiate one of its subclasses,
 * like {@code StringType}, {@code IntegerType}, {@code BooleanType}, etc.
 * Subsequently, you can create a <i>mixed type</i> by adding child components.
 */
public class NodeType extends Component {

	private String pattexp = null; 		// the regular expression defining the pattern.
	private Pattern pattern = null;		// the pre-compiled (from pattexp) pattern.
	private boolean nullable = false; 	// default null-ability (if that is a word).	

	//private String globalTypeName = null; 	// the name of the type this component refers to.
	private NodeType globalTypeNode = null; // the global node type this component refers to.
	
	/**
	 * Creates a node type with the specified name.
	 * 
	 * @param name a valid node name, see also {@link Node}
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public NodeType(String name) {
		super(name); // the value field is currently not used in a type definition
	}
	
	
	/**
	 * Returns the (simple) content type. This method will return a null reference
	 * if this type does not allow simple content.
	 * 
	 * @return a content type, may be null
	 */
	public Content getContentType() {
		return null; // by default no simple content, subclasses override this method
	}
	
	
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
	 * Returns whether this type is null-able.
	 * 
	 * @return true or false
	 */
	public boolean isNullable() {
		return nullable;
	}


	/**
	 * Returns whether this type is null-able.
	 * 
	 * @param nullable true or false
	 */
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	
//	/**
//	 * Returns the name of the referenced global type. A component may re-use a type
//	 * defined in the root section of the schema. This method returns null if this
//	 * component is not referencing a type.
//	 * 
//	 * @return the name of the referenced type, may be null
//	 */
//	public String getGlobalType() {
//		return globalTypeName;
//	}
//
//	
//	/**
//	 * Sets the name of the referenced global type. A component may re-use a type
//	 * defined in the root section of the schema. This method cannot be used to
//	 * re(set) an existing reference as this is likely to cause a problem.
//	 * 
//	 * @param type the name of the referenced type
//	 */
//	public void setGlobalType(String type) {
//		if (type != null) this.globalTypeName = type;
//	}
	
	/*
	 * The following three methods overrides the super type method to handle type
	 * references. For a regular node type, we just access the super type. But a
	 * type reference has no child nodes of its own; it is just a reference to a
	 * type in the schema. So we find that and treat its children as if they were
	 * our own. Obviously this does not constitute an actual parent-child relation,
	 * and may cause unexpected behavior at some point in the future, but we shall
	 * cross that bridge when we get there.
	 */
	@Override
	public final NodeSet getNodes() {
		
		if (getGlobalType() == null) return super.getNodes();
		if (globalTypeNode == null) // not bound yet, so get it from the schema root
			globalTypeNode = (NodeType) this.root().getNodes().get(getGlobalType());
		return globalTypeNode.getNodes(); // should not cause NPE
	}

	@Override /* handle type reference */
	public final boolean isComplex() {
		
		if (getGlobalType() == null) return super.isComplex();
		if (globalTypeNode == null) // not bound yet, so get it from the schema root
			globalTypeNode = (NodeType) this.root().getNodes().get(getGlobalType());
		return globalTypeNode.isComplex(); // should not cause NPE
	}

	@Override /* handle type reference */
	public final boolean isParent() {
		
		if (getGlobalType() == null) return super.isParent();
		if (globalTypeNode == null) // not bound yet, so get it from the schema root
			globalTypeNode = (NodeType) this.root().getNodes().get(getGlobalType());
		return globalTypeNode.isParent(); // should not cause NPE
	}
	
	
	@Override
	public Node toNode() {
		
		Node node = new Node(Components.NODE.tag);
		
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
			for (Node child : getNodes()) node.add(((Component) child).toNode());
		
		return node;
	}


	@Override
	public String toString() {
		return toNode().toString();
	}
}

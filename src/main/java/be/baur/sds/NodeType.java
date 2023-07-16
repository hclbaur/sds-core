package be.baur.sds;

import be.baur.sda.Node;
import be.baur.sda.NodeSet;
import be.baur.sds.content.AbstractStringType;
import be.baur.sds.content.AnyType;
import be.baur.sds.content.RangedType;
import be.baur.sds.serialization.Attribute;
import be.baur.sds.serialization.Components;


/**
 * A {@code NodeType} represents an SDA node type definition. It is one of the
 * building blocks of a {@code Schema}.
 * 
 * Note that an instance of this class is a <i>complex type</i>. For a <i>simple
 * type</i>, instantiate a {@code MixedType} subclass, like {@code StringType},
 * {@code IntegerType}, {@code BooleanType}, etc.
 */
public class NodeType extends Component {

	//private String globalTypeName = null; 	// the name of the type this component refers to.
	private NodeType globalTypeNode = null; // the global node type this component refers to.
	
	/**
	 * Creates a node type with the specified name.
	 * 
	 * @param name a valid node name, see also {@link Node}
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public NodeType(String name) {
		super(name);
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
	public final boolean isLeaf() {
		
		if (getGlobalType() == null) return super.isLeaf();
		if (globalTypeNode == null) // not bound yet, so get it from the schema root
			globalTypeNode = (NodeType) this.root().getNodes().get(getGlobalType());
		return globalTypeNode.isLeaf(); // should not cause NPE
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
		// or for a simple (mixed) content type.
		if (getGlobalType() != null)
			node.add(new Node(Attribute.TYPE.tag, getGlobalType()));
		else if (this instanceof MixedType)
			node.add(new Node(Attribute.TYPE.tag, ((MixedType) this).getContentType().type));
		
		// Render the multiplicity if not default.
		if (getMultiplicity().min != 1 || getMultiplicity().max != 1) 
			node.add(new Node(Attribute.OCCURS.tag, getMultiplicity().toString()));
		
		// facets are rendered ONLY if we are not a type reference!
		if (getGlobalType() == null) {

			boolean stringType = (this instanceof AbstractStringType);
			if (stringType) {
				AbstractStringType t = (AbstractStringType) this;
				if (t.getLength().min != 0 || t.getLength().max != Integer.MAX_VALUE)
					node.add(new Node(Attribute.LENGTH.tag, t.getLength().toString()));
			}
	
			if (this instanceof RangedType) {
				RangedType<?> t = (RangedType<?>) this;
				if (t.getRange().min != null || t.getRange().max != null)
					node.add(new Node(Attribute.VALUE.tag, t.getRange().toString()));
			}
			
			if (this instanceof MixedType) {
				MixedType m = (MixedType) this;
				
				if (m.getPattern() != null)
					node.add(new Node(Attribute.PATTERN.tag, m.getPattern().toString()));
				
				if (stringType == !m.isNullable())
					node.add(new Node(Attribute.NULLABLE.tag, String.valueOf(m.isNullable())));
			}
		}
		
		// Finally, render any children, unless we are a type reference
		if (isParent() && getGlobalType() == null)
			for (Node child : getNodes()) node.add(((Component) child).toNode());
		
		return node;
	}

}

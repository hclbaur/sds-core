package be.baur.sds;

import java.util.List;

import be.baur.sda.Node;
import be.baur.sda.SDA;
import be.baur.sda.DataNode;
import be.baur.sds.content.AbstractStringType;
import be.baur.sds.content.AnyType;
import be.baur.sds.content.RangedType;
import be.baur.sds.serialization.Attribute;
import be.baur.sds.serialization.Components;


/**
 * A {@code NodeType} is an SDA node type definition. It is one of the building
 * blocks of a {@code Schema} that defines content, the other being a
 * {@code ModelGroup}.
 * 
 * Note that an instance of this class is a <i>complex type</i>. For a <i>simple
 * type</i>, instantiate a {@code MixedType} subclass, like {@code StringType},
 * {@code IntegerType}, {@code BooleanType}, etc.
 * 
 * @see ModelGroup
 **/
public class NodeType extends Component {

	private NodeType globalType = null; // the global type this type refers to, if any
	private String typeName; // the name of this type


	/**
	 * Creates a node type with the specified name.
	 * 
	 * @param type a valid node name, see {@link SDA#isName}
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public NodeType(String name) {
		setTypeName(name);
	}
	

	/**
	 * Returns the name of this type.
	 * 
	 * @return a valid node name, not null or empty
	 */
	public String getTypeName() {
		return typeName;
	}


	/**
	 * Sets the name of this type. Ultimately, a type represents an instance of a
	 * data node, so the name of the type is restricted to valid node names.
	 * 
	 * @param type a valid node name, see {@link SDA#isName}
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public final void setTypeName(String name) {
		if (! SDA.isName(name)) 
			throw new IllegalArgumentException("invalid node name (" + name + ")");
		typeName = name;
	}


	/*
	 * The following three methods overrides the super type method to handle type
	 * references. For a regular node type, we just access the super type. But a
	 * type reference has no child nodes of its own; it is just a reference to a
	 * type in the schema. So we find that and treat its children as if they were
	 * our own. Obviously this does not constitute an actual parent-child relation,
	 * and may cause unexpected behavior at some point in the future, but we shall
	 * cross that bridge when we get there.
	 */

	@Override /* handle type reference */
	public final List<Node> nodes() {
		
		if (getGlobalType() == null) return super.nodes();
		if (globalType == null) // not bound yet, so get it from the schema root
			globalType = (NodeType) root().get(t -> ((NodeType) t).getTypeName().equals(getGlobalType()));
		return globalType.nodes(); // should not cause NPE
	}

	@Override /* handle type reference */
	public final boolean isLeaf() {
		
		if (getGlobalType() == null) return super.isLeaf();
		if (globalType == null) // not bound yet, so get it from the schema root
			globalType = (NodeType) root().get(t -> ((NodeType) t).getTypeName().equals(getGlobalType()));
		return globalType.isLeaf(); // should not cause NPE
	}

	@Override /* handle type reference */
	public final boolean isParent() {
		
		if (getGlobalType() == null) return super.isParent();
		if (globalType == null) // not bound yet, so get it from the schema root
			globalType = (NodeType) root().get(t -> ((NodeType) t).getTypeName().equals(getGlobalType()));
		return globalType.isParent(); // should not cause NPE
	}
	
	
	@Override
	public DataNode toSDA() {
		
		final DataNode node = new DataNode(Components.NODE.tag);
		
		// Omit the name for an unnamed any type, and for a type
		// reference with the same name as the referenced type.
		if (! (( getGlobalType() != null && typeName.equals(getGlobalType()) )
				|| ( this instanceof AnyType && !((AnyType) this).isNamed() )) ) {
			node.setValue(typeName);
		}
	
		// Render the type attribute for a global type reference,
		// or for a simple (mixed) content type.
		if (getGlobalType() != null)
			node.add(new DataNode(Attribute.TYPE.tag, getGlobalType()));
		else if (this instanceof MixedType)
			node.add(new DataNode(Attribute.TYPE.tag, ((MixedType) this).getContentType().type));
		
		// Render the multiplicity if not default.
		if (getMultiplicity().min != 1 || getMultiplicity().max != 1) 
			node.add(new DataNode(Attribute.OCCURS.tag, getMultiplicity().toString()));
		
		// facets are rendered ONLY if we are not a type reference!
		if (getGlobalType() == null) {

			final boolean stringType = (this instanceof AbstractStringType);

			if (stringType) {
				AbstractStringType t = (AbstractStringType) this;
				if (t.getLength().min != 0 || t.getLength().max != Integer.MAX_VALUE)
					node.add(new DataNode(Attribute.LENGTH.tag, t.getLength().toString()));
			}
	
			if (this instanceof RangedType) {
				RangedType<?> t = (RangedType<?>) this;
				if (t.getRange().min != null || t.getRange().max != null)
					node.add(new DataNode(Attribute.VALUE.tag, t.getRange().toString()));
			}
			
			if (this instanceof MixedType) {
				MixedType m = (MixedType) this;
				
				if (m.getPattern() != null)
					node.add(new DataNode(Attribute.PATTERN.tag, m.getPattern().toString()));
				
				if (stringType == !m.isNullable())
					node.add(new DataNode(Attribute.NULLABLE.tag, String.valueOf(m.isNullable())));
			}
		}
		
		// Finally, render any children, unless we are a type reference
		if (isParent() && getGlobalType() == null)
			for (Node child : nodes()) node.add(((Component) child).toSDA());
		
		return node;
	}

}

package be.baur.sds;

import java.util.List;

import be.baur.sda.DataNode;
import be.baur.sda.Node;
import be.baur.sda.SDA;
import be.baur.sds.parsing.Attribute;
import be.baur.sds.parsing.Components;
import be.baur.sds.types.CharacterNodeType;
import be.baur.sds.types.ComparableNodeType;
import be.baur.sds.types.StringNodeType;


/**
 * A {@code NodeType} defines an SDA node without a value. For a node that
 * allows simple content, instantiate a subclass of a {@code DataNodeType}.
 * 
 * @see DataNodeType
 **/
public class NodeType extends AbstractNodeType {

	private NodeType globalType = null; // the global type this type refers to, if any


	/**
	 * Creates a type that defines a node with the specified name.
	 * 
	 * @param name a valid node name, not null or empty
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public NodeType(String name) {
		setTypeName(name);
	}
	

	/**
	 * Returns the name of the node defined by this type. This method always returns
	 * a valid node name.
	 * 
	 * @return a valid node name, never null or empty
	 */
	@Override
	public final String getTypeName() {
		return super.getTypeName();
	}

	
	/**
	 * Sets the name of the node defined by this type. The name is restricted to
	 * valid node names.
	 * 
	 * @param name a valid node name, not null or empty
	 * @throws IllegalArgumentException if the name is invalid
	 */
	@Override
	public final void setTypeName(String name) {
		if (! SDA.isName(name)) 
			throw new IllegalArgumentException("invalid node name (" + name + ")");
		super.setTypeName(name);
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
		
		// Omit the name for a reference with the same name as the referenced type
		if (! ( getGlobalType() != null && getTypeName().equals(getGlobalType()) )) {
			node.setValue(getTypeName());
		}
	
		// Render the type attribute for a global type reference, or a data type
		if (getGlobalType() != null)
			node.add(new DataNode(Attribute.TYPE.tag, getGlobalType()));
		else if (this instanceof DataNodeType)
			node.add(new DataNode(Attribute.TYPE.tag, ((DataNodeType<?>) this).getDataType()));
		
		// Render the multiplicity if not default
		if (getMultiplicity().min != 1 || getMultiplicity().max != 1) 
			node.add(new DataNode(Attribute.OCCURS.tag, getMultiplicity().toString()));
		
		// facets are rendered ONLY if we are not a type reference!
		if (getGlobalType() == null) {

			final boolean isCharType = (this instanceof CharacterNodeType);

			if (isCharType) {
				CharacterNodeType<?> t = (CharacterNodeType<?>) this;
				if (t.getLength().min != 0 || t.getLength().max != Integer.MAX_VALUE)
					node.add(new DataNode(Attribute.LENGTH.tag, t.getLength().toString()));
			}
	
			if (this instanceof ComparableNodeType) {
				ComparableNodeType<?> t = (ComparableNodeType<?>) this;
				if (t.getInterval().min != null || t.getInterval().max != null)
					node.add(new DataNode(Attribute.VALUE.tag, t.getInterval().toString()));
			}
			
			if (this instanceof DataNodeType) {
				DataNodeType<?> t = (DataNodeType<?>) this;
				
				if (t.getPattern() != null)
					node.add(new DataNode(Attribute.PATTERN.tag, t.getPattern().toString()));
				
				// Add nullable only for a non-nullable string or a nullable other type
				if ((this instanceof StringNodeType) == !t.isNullable())
					node.add(new DataNode(Attribute.NULLABLE.tag, String.valueOf(t.isNullable())));
			}
		}
		
		// Finally, render any children, unless we are a type reference
		if (isParent() && getGlobalType() == null)
			for (Node child : nodes()) node.add(((Component) child).toSDA());
		
		return node;
	}

}

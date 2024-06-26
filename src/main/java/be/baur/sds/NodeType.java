package be.baur.sds;

import java.util.List;

import be.baur.sda.Node;
import be.baur.sda.SDA;
import be.baur.sda.DataNode;
import be.baur.sds.model.ModelGroup;
import be.baur.sds.serialization.Attribute;
import be.baur.sds.serialization.Components;
import be.baur.sds.types.CharacterType;
import be.baur.sds.types.ComparableType;
import be.baur.sds.types.StringType;


/**
 * A {@code NodeType} is an SDA node type definition. It is one of the building
 * blocks of a {@code Schema} that defines SDA node content, the other being a
 * {@code ModelGroup}.
 * 
 * Note that an instance of this class is a complex type. For a type with simple
 * content, instantiate a {@code DataType} subclass, like {@code StringType},
 * {@code IntegerType}, {@code BooleanType}, etc.
 * 
 * @see ModelGroup
 * @see DataType
 **/
public class NodeType extends Type {

	private NodeType globalType = null; // the global type this type refers to, if any


	/**
	 * Creates a type with the specified name, which must be a valid node name.
	 * 
	 * @see SDA#isName
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public NodeType(String name) {
		setTypeName(name);
	}
	

	/**
	 * Returns the name of this type. A type defines an instance of a data node, so
	 * this method always returns a valid node name.
	 * 
	 * @return a valid node name, not null or empty
	 */
	@Override
	public final String getTypeName() {
		return super.getTypeName();
	}

	
	/**
	 * Sets the name of this type. A type defines an instance of a data node, so the
	 * name of this type is restricted to valid node names.
	 * 
	 * @see SDA#isName
	 * 
	 * @param name a valid node name, may be null or empty
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
		else if (this instanceof DataType)
			node.add(new DataNode(Attribute.TYPE.tag, ((DataType) this).getType()));
		
		// Render the multiplicity if not default
		if (getMultiplicity().min != 1 || getMultiplicity().max != 1) 
			node.add(new DataNode(Attribute.OCCURS.tag, getMultiplicity().toString()));
		
		// facets are rendered ONLY if we are not a type reference!
		if (getGlobalType() == null) {

			final boolean isCharType = (this instanceof CharacterType);

			if (isCharType) {
				CharacterType<?> t = (CharacterType<?>) this;
				if (t.getLength().min != 0 || t.getLength().max != Integer.MAX_VALUE)
					node.add(new DataNode(Attribute.LENGTH.tag, t.getLength().toString()));
			}
	
			if (this instanceof ComparableType) {
				ComparableType<?> t = (ComparableType<?>) this;
				if (t.getInterval().min != null || t.getInterval().max != null)
					node.add(new DataNode(Attribute.VALUE.tag, t.getInterval().toString()));
			}
			
			if (this instanceof DataType) {
				DataType t = (DataType) this;
				
				if (t.getPattern() != null)
					node.add(new DataNode(Attribute.PATTERN.tag, t.getPattern().toString()));
				
				// Add nullable only for a non-nullable string or a nullable other type
				if ((this instanceof StringType) == !t.isNullable())
					node.add(new DataNode(Attribute.NULLABLE.tag, String.valueOf(t.isNullable())));
			}
		}
		
		// Finally, render any children, unless we are a type reference
		if (isParent() && getGlobalType() == null)
			for (Node child : nodes()) node.add(((Component) child).toSDA());
		
		return node;
	}

}

package be.baur.sds;

import be.baur.sda.Node;
import be.baur.sds.common.Attribute;
import be.baur.sds.common.Component;

/**
 * A <code>ComplexType</code> represents an SDS definition of a complex SDA
 * node. It is a container for other components and/or model groups.
 */
//@Deprecated
public class ComplexType extends NodeType {

	//private ComplexType globalcomplextype = null; // the global complex type this component refers to.
	
	/** Creates a complex type with the specified <code>name</code>.*/
	public ComplexType(String name) {
		super(name); add(null); // by definition, a complex type has child nodes.
	}
	
		
	/*
	 * This method overrides the super type method to handle type references. For a
	 * normal complex type, we just return the child nodes. But a type reference has
	 * no children; it is just a reference to a global type in the schema root. So
	 * we find that type and return its children as if they were our own. Note that
	 * this does not constitute an actual parent-child relation, and may cause
	 * unexpected behavior at some point in the future, but we shall cross that
	 * bridge when we get there.
	 */
//	@Override
//	public NodeSet getNodes() {
//		
//		if (getGlobalType() == null) return super.getNodes();
//		
//		if (globalcomplextype == null) // not bound yet, so get it from the schema root
//			globalcomplextype = (ComplexType) this.root().getNodes().get(getGlobalType()).get(1);
//		
//		return globalcomplextype != null ? globalcomplextype.getNodes() : new NodeSet();
//	}
	
	
	public Node toNode() {
		
		Node node = new Node(Component.NODE.tag);
		
		// Omit the name for type references with the same name as the referenced global type.

		if (getGlobalType() == null || ! getName().equals(getGlobalType()))
			node.setValue(getName());
		
		if (getGlobalType() != null) // Render the type attribute if we have one.
			node.add(new Node(Attribute.TYPE.tag, getGlobalType()));
		
		// Render the multiplicity if not default.
		if (getMultiplicity() != null && (getMultiplicity().min != 1 || getMultiplicity().max != 1)) 
			node.add(new Node(Attribute.OCCURS.tag, getMultiplicity().toString()));
		
		if (getGlobalType() == null) // Render children, unless we are a type reference.
			for (Node child : getNodes()) node.add(((ComponentType) child).toNode());

		return node;
	}
	
	
//	@Override
//	public final String toString() {
//		return toNode().toString();
//	}
}

package be.baur.sds;

import be.baur.sda.ComplexNode;
import be.baur.sda.Node;
import be.baur.sda.NodeSet;
import be.baur.sda.SimpleNode;
import be.baur.sds.common.Attribute;
import be.baur.sds.common.Component;
import be.baur.sds.common.NaturalInterval;
import be.baur.sds.model.AbstractGroup;

/**
 * A <code>ComplexType</code> represents an SDS definition of a complex SDA
 * node. It is a container for other components and/or model groups.
 */
public class ComplexType extends ComplexNode implements ComponentType {

	private String globaltype = null; // the (name of the) global type this component refers to.
	private ComplexType globalcomplextype = null; // the global complex type this component refers to.
	private NaturalInterval multiplicity = null; // the default multiplicity: mandatory and singular.
	
	
	/** Creates a complex type with the specified <code>name</code>.*/
	public ComplexType(String name) {
		super(name);
	}
	

	public String getGlobalType() {
		return globaltype;
	}


	public void setGlobalType(String type) {
		this.globaltype = type;
	}


	public NaturalInterval getMultiplicity() {
		return multiplicity;
	}


	public void setMultiplicity(NaturalInterval multiplicity) {
		this.multiplicity = multiplicity;
	}
	
		
	/*
	 * This method overrides the one in its super type (ComplexNode) to handle type
	 * references. For a normal complex type, we just return the child nodes. But a
	 * type reference has no children, it is just a reference to a global type in
	 * the schema root. So we find that type and return its children as if they were
	 * our own. Note that this does not constitute an actual parent-child relation,
	 * and may cause unexpected behavior at some point in the future, but we shall
	 * cross that bridge when we get there.
	 */
	@Override
	public NodeSet getNodes() {
		
		if (globaltype == null) return super.getNodes();
		
		if (globalcomplextype == null) // not bound yet, so get it from the schema root
			globalcomplextype = (ComplexType) ((ComplexNode) this.root()).getNodes().get(globaltype).get(1);
		
		return globalcomplextype != null ? globalcomplextype.getNodes() : new NodeSet();
	}
	
	
	public final ComplexNode toNode() {
		
		ComplexNode node; // resulting node, returned at the end of this method
		
		// Omit the name attribute for model groups or 
		// type references with the same name as the referenced global type.
		if (! (this instanceof AbstractGroup) ) {
			node = new ComplexNode(Component.NODE.tag);
			if (getGlobalType() == null || ! getName().equals(getGlobalType()))
				node.getNodes().add(new SimpleNode(Attribute.NAME.tag, getName()));
		}
		else node = new ComplexNode(getName());
		
		if (getGlobalType() != null) // Render the type attribute if we have one.
			node.getNodes().add(new SimpleNode(Attribute.TYPE.tag, getGlobalType()));
		
		// Render the multiplicity if not default.
		if (multiplicity != null && (multiplicity.min != 1 || multiplicity.max != 1)) 
			node.getNodes().add(new SimpleNode(Attribute.OCCURS.tag, multiplicity.toString()));
		
		if (getGlobalType() == null) // Render children, unless we are a type reference.
			for (Node child : getNodes()) node.getNodes().add(((ComponentType) child).toNode());

		return node;
	}
	
	
	@Override
	public final String toString() {
		return toNode().toString();
	}
}

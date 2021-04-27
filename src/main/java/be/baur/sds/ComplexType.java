package be.baur.sds;

import be.baur.sda.ComplexNode;
import be.baur.sda.Node;
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

	private String globaltype = null; // the global type this component refers to.
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
	
	
	public final ComplexNode toNode() {
		
		ComplexNode node; // resulting node, returned at the end of this method
		
		// Omit the name attribute for model groups or 
		// type references with the same name as the referenced global type.
		if (! (this instanceof AbstractGroup) ) {
			node = new ComplexNode(Component.NODE.tag);
			if (getGlobalType() == null || ! getName().equals(getGlobalType()))
				node.nodes.add(new SimpleNode(Attribute.NAME.tag, getName()));
		}
		else node = new ComplexNode(getName());
		
		if (getGlobalType() != null) // Render the type attribute if we have one.
			node.nodes.add(new SimpleNode(Attribute.TYPE.tag, getGlobalType()));
		
		// Render the multiplicity if not default.
		if (multiplicity != null && (multiplicity.min != 1 || multiplicity.max != 1)) 
			node.nodes.add(new SimpleNode(Attribute.OCCURS.tag, multiplicity.toString()));
		
		if (getGlobalType() == null) // Render children, unless we are a type reference.
			for (Node child : nodes) node.nodes.add(((ComponentType) child).toNode());

		return node;
	}
	
	
	@Override
	public final String toString() {
		return toNode().toString();
	}
}

package be.baur.sds;

import be.baur.sda.ComplexNode;
import be.baur.sda.Node;
import be.baur.sda.SimpleNode;
import be.baur.sds.common.Attribute;
import be.baur.sds.common.Component;
import be.baur.sds.common.NaturalInterval;

/**
 * A <code>ComplexType</code> is a class defining a complex SDA node. It may
 * contain other components and/or model groups; which is why it extends a
 * {@link ComplexNode}.
 */
public class ComplexType extends ComplexNode implements ComponentType {

	/** Create a type with default multiplicity (mandatory and singular). */
	public ComplexType(String name) {
		super(name);
	}
	
	// Set when constructed from a reference.
	private String globaltype = null;
	
	public String getGlobalType() {
		return globaltype;
	}

	public void setGlobalType(String type) {
		this.globaltype = type;
	}
	
	// The default multiplicity is null; the component must occur exactly once.
	private NaturalInterval multiplicity = null;
	
	public NaturalInterval getMultiplicity() {
		return multiplicity;
	}

	public void setMultiplicity(NaturalInterval multiplicity) {
		this.multiplicity = multiplicity;
	}
	
	
	public ComplexNode toNode() {
		
		ComplexNode node; // resulting node, returned at the end of this method
		
		// the name attribute is omitted for a model group, and for a type
		// reference with the same name as the global type it refers to
		if (! (this instanceof ModelGroup) ) {
			node = new ComplexNode(Component.NODE.tag);
			if (getGlobalType() == null || ! getName().equals(getGlobalType()))
				node.nodes.add(new SimpleNode(Attribute.NAME.tag, getName()));
		}
		else node = new ComplexNode(getName());
		
		// Render the type attribute if we have one (Schema or type reference)
		if (getGlobalType() != null) 
			node.nodes.add(new SimpleNode(Attribute.TYPE.tag, getGlobalType()));
		
		if (minOccurs() != 1 || maxOccurs() != 1)
			node.nodes.add(new SimpleNode(Attribute.MULTIPLICITY.tag, multiplicity.toString()));
		
		// Render children, unless we are a global type reference (but not a Schema)
		if (this instanceof Schema || getGlobalType() == null)
			for (Node child : nodes) node.nodes.add(((ComponentType) child).toNode());

		return node;
	}
	
	
	@Override
	public String toString() {
		return toNode().toString();
	}
}

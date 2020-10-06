package be.baur.sds;

import java.util.Optional;

import be.baur.sda.ComplexNode;
import be.baur.sda.Node;
import be.baur.sda.SimpleNode;
import be.baur.sds.common.Attribute;
import be.baur.sds.common.Component;
import be.baur.sds.common.NaturalInterval;
import be.baur.sds.model.ChoiceGroup;
import be.baur.sds.model.Group;
import be.baur.sds.model.ModelGroup;
import be.baur.sds.model.UnorderedGroup;

/**
 * A <code>ComplexType</code> is a class defining a complex SDA node. It
 * contains other types and/or model groups; which is why it extends a
 * {@link ComplexNode}.
 */
public class ComplexType extends ComplexNode implements Type {

	
	/** Create a type with default multiplicity (mandatory and singular) */
	public ComplexType(String name) {
		super(name);
	}


	/**
	 * This is a factory method to construct a <code>ComplexType</code> from a node
	 * representing SDS content. For example: <br>
	 * <code>node{ name "contact" node{ name "firstname" type "string" }}</code>
	 * defines a (complex) SDA node with name "contact" containing a (simple)
	 * string-type node with name "firstname".<br>
	 * <br>
	 * This method should only be called by {@link Type}<code>.from()</code> after
	 * some sanity checks. Do not make it public.
	 */
	static ComplexType from(ComplexNode sds) throws SchemaException {
		/*
		 * Sanity checks: the caller (Type.from()) has already verified that we have a
		 * valid tag, have one or more complex child nodes, and that all of our simple
		 * nodes (attributes) have valid tags.
		 */

		// Complex types should not have attributes other than name and multiplicity.
		Optional<Node> attribute = sds.get().get(SimpleNode.class).stream()
			.filter(n -> ! (n.name.equals(Attribute.NAME.tag) 
					|| n.name.equals(Attribute.MULTIPLICITY.tag)) )
			.findFirst();
		if (attribute.isPresent())
			throw new SchemaException(sds, SchemaException.AttributeNotAllowed, attribute.get().name);

		// The name attribute is required unless we are a model group, in which case it is forbidden
		SimpleNode name = Attribute.getNode(sds, Attribute.NAME, sds.name.equals(Component.NODE.tag) ? true : null);

		ComplexType cType;	// the complex type that will be returned at the end of this method.

		switch (Component.get(sds.name)) {
			case NODE		: cType = new ComplexType(name.value); break;
			case GROUP		: cType = new Group(); break;
			case CHOICE		: cType = new ChoiceGroup(); break;
			case UNORDERED	: cType = new UnorderedGroup(); break;
			default: // will never get here, unless we forgot to implement something...
				throw new RuntimeException("SDS component '" + sds.name + "' not implemented!");
		}
		
		// in a model group, we must have at least two other components
		if (cType instanceof ModelGroup && sds.get().get(ComplexNode.class).size() < 2)
			throw new SchemaException(sds, SchemaException.ComponentIncomplete, cType.name);
		
		// Set the multiplicity from the corresponding attribute (if present)
		SimpleNode multiplicity = Attribute.getNode(sds, Attribute.MULTIPLICITY, false);
		try {
			if (multiplicity != null) {
				NaturalInterval interval = NaturalInterval.from(multiplicity.value);
				cType.setMultiplicity(interval);
			}
		} catch (Exception e) {
			throw new SchemaException(multiplicity, 
				SchemaException.AttributeInvalid, multiplicity.name, multiplicity.value, e.getMessage());
		}
		
		return cType;
	}

	
	// The default multiplicity is null, which means the component must occur exactly once.
	private NaturalInterval multiplicity = null;
	
	public NaturalInterval getMultiplicity() {
		return multiplicity;
	}

	public void setMultiplicity(NaturalInterval multiplicity) {
		this.multiplicity = multiplicity;
	}

	
	public ComplexNode toNode() {
		
		ComplexNode node;
		
		if (! (this instanceof ModelGroup) ) {
			node = new ComplexNode(Component.NODE.tag);
			node.add(new SimpleNode(Attribute.NAME.tag, name));
		}
		else node = new ComplexNode(this.name);
		
		if (minOccurs() != 1 || maxOccurs() != 1)
			node.add(new SimpleNode(Attribute.MULTIPLICITY.tag, multiplicity.toString()));
		
		for (Node child : this.get())
			node.add(((Type) child).toNode());

		return node;
	}
	
	
	public String toString() {
		return toNode().toString();
	}
}

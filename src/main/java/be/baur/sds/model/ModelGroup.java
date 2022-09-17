package be.baur.sds.model;

import java.util.Optional;

import be.baur.sda.Node;
import be.baur.sds.ComplexType;
import be.baur.sds.ComponentType;
import be.baur.sds.NodeType;
import be.baur.sds.common.Attribute;

/**
 * The abstract superclass of all model groups. 
 * 
 * @see {@link SequenceGroup}, {@link ChoiceGroup} and {@link UnorderedGroup}
 */
public abstract class ModelGroup extends ComplexType {

	public ModelGroup(String name) {
		super(name);
	}

	
	/**
	 * This method overrides the super method, to return the effective minimum
	 * number of times that a group must occur within its context. Even when the
	 * formal multiplicity states that it is mandatory, empty content is valid if
	 * all of the alternatives are optional. For example, given a schema like
	 * 
	 * <pre>
	 * node "person" {
	 *     group { 
	 *         node "firstname" { type "string" occurs "0..1" } 
	 *         node "lastname" { type "string" occurs "0..1" } 
	 *     }
	 * }
	 * </pre>
	 * 
	 * the instance below is valid since both nodes may be omitted.
	 * 
	 * <pre>
	 * person { }
	 * </pre>
	 */
	@Override
	public int minOccurs() {
		
		Optional<Node> man = this.getNodes().stream()
			.filter(n -> (n instanceof ComponentType) && ((ComponentType) n).minOccurs() > 0)
			.findFirst(); // if a group contains no mandatory components, it is optional.
		if (man.isPresent()) return super.minOccurs();
		return 0;
	}
	

	@Override
	public final Node toNode() {
		
		Node node = new Node(getName()); // group, choice or unordered
		
		// maybe someday we will support named groups, but not today
		
//		if (getGlobalType() == null || ! getName().equals(getGlobalType()))
//			node.setValue(getName());
//	
//		if (getGlobalType() != null) // Render the type attribute if we have one.
//			node.add(new Node(Attribute.TYPE.tag, getGlobalType()));
	
		// Render the multiplicity if not default.
		if (getMultiplicity() != null && (getMultiplicity().min != 1 || getMultiplicity().max != 1)) 
			node.add(new Node(Attribute.OCCURS.tag, getMultiplicity().toString()));
		
		//if (getGlobalType() == null) // Render children, unless we are a type reference.
		for (Node child : getNodes()) node.add(((ComponentType) child).toNode());

		return node;
	}

	public final String toString() {
		return toNode().toString();
	}
}

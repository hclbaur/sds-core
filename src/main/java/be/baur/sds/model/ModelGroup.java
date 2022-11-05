package be.baur.sds.model;

import java.util.Optional;

import be.baur.sda.Node;
import be.baur.sds.Component;
import be.baur.sds.serialization.Attribute;

/**
 * The abstract superclass of all model groups. <br>
 * See also {@link SequenceGroup}, {@link ChoiceGroup} and
 * {@link UnorderedGroup}.
 */
public abstract class ModelGroup extends Component {

	/**
	 * Creates a model group with the specified name.
	 * 
	 * @param name a valid node name, see also {@link Node}
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public ModelGroup(String name) {
		super(name); add(null); // all groups must have child nodes
	}

	
	/**
	 * Returns the effective minimum number of times that a group must occur within
	 * its context. Even when the formal multiplicity states that it is mandatory,
	 * empty content is valid if all of the alternatives are optional. For example,
	 * given a schema like
	 * 
	 * <pre>
	 * <code>
	 * node "person" {
	 *     group { 
	 *         node "firstname" { type "string" occurs "0..1" } 
	 *         node "lastname" { type "string" occurs "0..1" } 
	 *     }
	 * }
	 * </code>
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
			.filter(n -> (n instanceof Component) && ((Component) n).minOccurs() > 0)
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
		if (getMultiplicity().min != 1 || getMultiplicity().max != 1) 
			node.add(new Node(Attribute.OCCURS.tag, getMultiplicity().toString()));
		
		//if (getGlobalType() == null) // Render children, unless we are a type reference.
		for (Node child : getNodes()) node.add(((Component) child).toNode());

		return node;
	}


	@Override
	public final String toString() {
		return toNode().toString();
	}
}

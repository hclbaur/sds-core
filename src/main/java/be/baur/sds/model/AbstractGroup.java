package be.baur.sds.model;

import java.util.Optional;

import be.baur.sda.Node;
import be.baur.sds.ComplexType;
import be.baur.sds.ComponentType;

/**
 * An abstract class that all model groups extend: {@link Group},
 * {@link ChoiceGroup}, and {@link UnorderedGroup}.
 */
public abstract class AbstractGroup extends ComplexType {

	public AbstractGroup(String name) {
		super(name);
	}

	
	/**
	 * This method overrides the default, to return the effective minimum number of
	 * times that a group must occur within its context. Even if the formal
	 * multiplicity states that it is mandatory, empty content is valid if all of
	 * the alternatives are optional. For example, given a schema like
	 * 
	 * <pre>
	 * node {
	 *     name "person"
	 *     group { 
	 *         node { name "firstname" type "string" occurs "0..1" } 
	 *         node { name "lastname" type "string" occurs "0..1" } 
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
		
		Optional<Node> man = this.nodes.stream()
			.filter(n -> (n instanceof ComponentType) && ((ComponentType) n).minOccurs() > 0)
			.findFirst(); // if a group contains no mandatory components, it is optional.
		if (man.isPresent()) return super.minOccurs();
		return 0;
	}
}

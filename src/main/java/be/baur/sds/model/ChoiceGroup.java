/**
 * 
 */
package be.baur.sds.model;

import java.util.Optional;

import be.baur.sda.Node;
import be.baur.sds.Component;
import be.baur.sds.serialization.Components;

/**
 * A choice group defines two (or more) mutually exclusive nodes or
 * content models, for example:
 * 
 * <pre>
 * <code>
 * choice {
 *     node "firstname" { type "string" }
 *     node "lastname" { type "string" }
 * }
 * </code>
 * </pre>
 * 
 * Which means that either a first name is expected, or a last name, but not
 * both.
 */
public final class ChoiceGroup extends ModelGroup {

	@Override
	public final String getName() {
		return Components.CHOICE.tag;
	}


	/**
	 * Returns the effective minimum number of times that a choice group must occur
	 * within its context. Even when the formal multiplicity states that it is
	 * mandatory, empty content is valid if at least one of the alternatives is
	 * optional. For example, given a schema like
	 * 
	 * <pre>
	 * node {
	 *     name "person"
	 *     choice { 
	 *         node { name "firstname" type "string" occurs "0..1" } 
	 *         node { name "lastname" type "string" } 
	 *     }
	 * }
	 * </pre>
	 * 
	 * the instance below is valid since one can "choose" a firstname and - seeing
	 * that it is optional - omit it.
	 * 
	 * <pre>
	 * person { }
	 * </pre>
	 */
	@Override
	public int minOccurs() {
		
		Optional<Node> opt = this.nodes().stream()
			.filter(n -> (n instanceof Component) && ((Component) n).minOccurs() == 0)
			.findFirst(); // if a choice contains at least one optional component, it is optional.
		if (opt.isPresent()) return 0;
		return super.minOccurs();
	}
}

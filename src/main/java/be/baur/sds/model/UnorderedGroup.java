/**
 * 
 */
package be.baur.sds.model;

import be.baur.sds.common.Component;

/**
 * An unordered group defines two or more nodes or content models that may occur in any order:
 * <pre><code> group {
 *  node { name "firstname" type "string" }
 *  node { name "lastname" type "string" }
 * }</code></pre>
 * Which means that a first and last name is expected, or the other way around.
 */
public class UnorderedGroup extends ModelGroup {

	public UnorderedGroup() {
		super(Component.UNORDERED.tag);
	}

}

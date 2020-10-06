/**
 * 
 */
package be.baur.sds.model;

import be.baur.sds.common.Component;

/**
 * A group defines two or more interdependent nodes or content models, for example:
 * <pre><code> group {
 *  node { name "middlename" type "string" multiplicity "0..1"}
 *  node { name "lastname" type "string" }
 * }</code></pre>
 * Which means that a last name is excepted, optionally preceded by a middle name.
 */
public class Group extends ModelGroup {

	public Group() {
		super(Component.GROUP.tag);
	}

}

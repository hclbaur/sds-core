/**
 * 
 */
package be.baur.sds.model;

import be.baur.sds.common.Component;

/**
 * A (sequence) group defines two or more interdependent nodes or content models, for example:
 * <pre>
 * group {
 *     node { name "middlename" type "string" occurs "0..1"}
 *     node { name "lastname" type "string" }
 * }
 * </pre>
 * Which means that a last name is expected, optionally preceded by a middle name.
 */
public final class SequenceGroup extends ModelGroup {

	/** Creates a group. */
	public SequenceGroup() {
		super(Component.GROUP.tag);
	}
}

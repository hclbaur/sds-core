/**
 * 
 */
package be.baur.sds.model;

import be.baur.sds.common.Component;

/**
 * A sequence group defines two or more interdependent nodes or content
 * models, for example:
 * 
 * <pre>
 * <code>
 * group {
 *     node "middlename" { type "string" occurs "0..1"}
 *     node "lastname" { type "string" }
 * }
 * </code>
 * </pre>
 * 
 * Which means that a last name is expected, optionally preceded by a middle
 * name.
 */
public final class SequenceGroup extends ModelGroup {

	/** Creates a sequence group. */
	public SequenceGroup() {
		super(Component.GROUP.tag); // extends Node so must have a tag, even if not really used
	}
}

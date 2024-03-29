/**
 * 
 */
package be.baur.sds.model;

import be.baur.sds.serialization.Components;

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

	@Override
	public final String getName() {
		return Components.GROUP.tag;
	}

}

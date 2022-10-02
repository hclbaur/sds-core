/**
 * 
 */
package be.baur.sds.model;

import be.baur.sds.serialization.Components;

/**
 * An unordered group defines two or more nodes or content models that may occur
 * in any order:
 * 
 * <pre>
 * <code>
 * unordered {
 *     node "firstname" { type "string" }
 *     node "lastname" { type "string" }
 * }
 * </code>
 * </pre>
 * 
 * Which means that a first and last name is expected, or the other way around.
 */
public final class UnorderedGroup extends ModelGroup {

	/** Creates an unordered group. */
	public UnorderedGroup() {
		super(Components.UNORDERED.tag); // extends Node so must have a tag, even if not really used
	}
}

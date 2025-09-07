/**
 * 
 */
package be.baur.sds.model;

import be.baur.sds.parsing.Components;

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

	@Override
	public final String getName() {
		return Components.UNORDERED.tag;
	}
	
}

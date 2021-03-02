/**
 * 
 */
package be.baur.sds.model;

import be.baur.sds.ModelGroup;
import be.baur.sds.common.Component;

/**
 * A choice group defines two (or more) mutually exclusive nodes or content models, for example:
 * <pre><code> choice {
 *  node { name "firstname" type "string" }
 *  node { name "lastname" type "string" }
 * }</code></pre>
 * Which means that either a first name is expected, or a last name, but not both.
 */
public class ChoiceGroup extends ModelGroup {

	public ChoiceGroup() {
		super(Component.CHOICE.tag);
	}

}

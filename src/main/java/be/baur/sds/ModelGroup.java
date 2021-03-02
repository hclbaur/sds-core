/**
 * 
 */
package be.baur.sds;

import be.baur.sds.model.UnorderedGroup;

/**
 * An abstract class that all model groups extend: {@link Schema},
 * {@link Group}, {@link ChoiceGroup}, and {@link UnorderedGroup}.
 */
public abstract class ModelGroup extends ComplexType {

	public ModelGroup(String name) {
		super(name);
	}

//	String[] getNames() {
//		return this.get().stream().map(n -> n.getName()).collect(null);
//	}
}

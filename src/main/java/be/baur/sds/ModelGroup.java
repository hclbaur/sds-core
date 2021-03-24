package be.baur.sds;

/**
 * An abstract class that all model groups extend: {@link Schema},
 * {@link Group}, {@link ChoiceGroup}, and {@link UnorderedGroup}.
 */
public abstract class ModelGroup extends ComplexType {

	public ModelGroup(String name) {
		super(name);
	}

}

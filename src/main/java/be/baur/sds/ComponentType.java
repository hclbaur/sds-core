package be.baur.sds;

import be.baur.sda.Node;
import be.baur.sds.common.NaturalInterval;
import be.baur.sds.model.ChoiceGroup;
import be.baur.sds.model.ModelGroup;
import be.baur.sds.model.SequenceGroup;
import be.baur.sds.model.UnorderedGroup;

/**
 * The abstract superclass of schema components. <br>
 * See also {@link NodeType} and {@link ModelGroup}
 */
public abstract class ComponentType extends Node {

	private String globalTypeName = null; 			// name of the global type this component refers to.
	private NaturalInterval multiplicity = null; 	// default multiplicity (mandatory and singular).

	/** Creates a component. */
	public ComponentType(String name) {
		super(name); // extends Node so it must have a tag, even if we do not really need or use it
	}

	
	/**
	 * Returns the name of the referenced global type. A schema component may re-use
	 * a type defined in the main section of the schema.
	 */
	public String getGlobalType() {
		return globalTypeName;
	}

	
	/** Sets the name of the referenced global type. */
	public void setGlobalType(String type) {
		this.globalTypeName = type;
	}

	
	/**
	 * Returns the formal multiplicity of this component. The default is
	 * <code>null</code>, which means the component must occur exactly once.
	 */
	public NaturalInterval getMultiplicity() {
		return multiplicity;
	}

	
	/** Sets the multiplicity of this component. */
	public void setMultiplicity(NaturalInterval multiplicity) {
		this.multiplicity = multiplicity;
	}


	
	/**
	 * Returns the effective minimum number of times this component must occur
	 * within its context.
	 */
	public int minOccurs() {
		return multiplicity != null ? multiplicity.min : 1;
	}

	
	/**
	 * Returns the effective maximum number of times this component may occur 
	 * within its context.
	 */
	public int maxOccurs() {
		return multiplicity != null ? multiplicity.max : 1;
	}


	/**
	 * Returns an SDA node structure that represents this component in SDS syntax.
	 * In other words, what an SDA parser would return upon processing the schema
	 * for this component.
	 */
	public abstract Node toNode();

	
	/** Returns the string representation of this component in SDS syntax. */
	@Override 
	public abstract String toString();
}

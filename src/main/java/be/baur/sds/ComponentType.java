package be.baur.sds;

import be.baur.sda.Node;
import be.baur.sds.common.NaturalInterval;

/**
 * An abstract class representing a schema component, such as {@link SimpleType}
 * and {@link ComplexType}.
 */
public abstract class ComponentType extends Node {

	private String globaltype = null; 				// name of the global type this component refers to.
	private NaturalInterval multiplicity = null; 	// default multiplicity (mandatory and singular).

	/** Creates a component with the specified <code>name</code>.*/
	public ComponentType(String name) {
		super(name);
	}

	
	/** Returns the name of the referenced global type. */
	public String getGlobalType() {
		return globaltype;
	}

	
	/** Sets the name of the referenced global type. */
	public void setGlobalType(String type) {
		this.globaltype = type;
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
	 * Returns an SDA node structure that represents this component. In other words,
	 * what an SDA parser would return upon processing an input stream defining the
	 * component in SDS syntax.
	 */
	public abstract Node toNode();

	
	/** Returns the string representation of this component in SDS syntax. */
	@Override 
	public abstract String toString();
}

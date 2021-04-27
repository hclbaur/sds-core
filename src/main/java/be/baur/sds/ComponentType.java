package be.baur.sds;

import be.baur.sda.ComplexNode;
import be.baur.sds.common.NaturalInterval;

/**
 * An interface implemented by classes representing a schema component, such as
 * {@link SimpleType} and {@link ComplexType}.
 */
public interface ComponentType {

	
	/** Returns the name of this component. */
	String getName();
	
	
	/** Sets the name of this component. */
	void setName(String name);
	
	
	/** Returns the name of the referenced global type. */
	public String getGlobalType();

	
	/** Sets the name of the referenced global type. */
	public void setGlobalType(String type);

	
	/**
	 * Returns the formal multiplicity of this component. The default is
	 * <code>null</code>, which means the component must occur exactly once.
	 */
	NaturalInterval getMultiplicity();

	
	/** Sets the multiplicity of this component. */
	void setMultiplicity(NaturalInterval multiplicity);

	
	/**
	 * Returns the effective minimum number of times this component must occur
	 * within its context.
	 */
	default int minOccurs() {
		return getMultiplicity() != null ? getMultiplicity().min : 1;
	}

	
	/**
	 * Returns the effective maximum number of times this component may occur within
	 * its context.
	 */
	default int maxOccurs() {
		return getMultiplicity() != null ? getMultiplicity().max : 1;
	}


	/**
	 * Returns an SDA node structure that represents this component. In other words,
	 * what an SDA parser would return upon processing an input stream describing
	 * the component in SDS notation.
	 */
	ComplexNode toNode();

	
	/** Returns the string representation of this component in SDS syntax. */
	String toString();
}

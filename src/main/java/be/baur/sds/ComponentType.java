package be.baur.sds;

import be.baur.sda.ComplexNode;
import be.baur.sds.common.NaturalInterval;

/**
 * This is the interface that should be implemented by classes representing a
 * schema component, such as {@link SimpleType} and {@link ComplexType}. All
 * types must have a multiplicity field indicating how often the component may
 * occur within its context (default is mandatory and singular).
 */
public interface ComponentType {

	/** Sets the name of this component. */
	void setName(String name);
	
	/** Returns the name of this component. */
	String getName();
	
	/** Get the name of the referenced global type. */
	public String getGlobalType();

	/** Set the name of the referenced global type. */
	public void setGlobalType(String type);
	
	/**
	 * Returns the multiplicity field of this component. The default value is
	 * <code>null</code>, which means the component must occur exactly once.
	 * Convenience methods <code>minOccurs()</code> and <code>maxOccurs()</code>
	 * return the lower and upper multiplicity limits.
	 */
	NaturalInterval getMultiplicity();

	/** Sets the multiplicity of this component. */
	void setMultiplicity(NaturalInterval multiplicity);

	/** The minimum number of times this component must occur within its context. */
	default int minOccurs() {
		return getMultiplicity() != null ? getMultiplicity().lower : 1;
	}

	/** The maximum number of times this component may occur within its context. */
	default int maxOccurs() {
		return getMultiplicity() != null ? getMultiplicity().upper : 1;
	}

	/** Represent this component as an SDA node. */
	ComplexNode toNode();

	/** Format this component as a string. */
	String toString();
}

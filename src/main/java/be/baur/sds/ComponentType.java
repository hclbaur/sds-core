package be.baur.sds;

import be.baur.sda.Node;
import be.baur.sda.serialization.SDAFormatter;
import be.baur.sds.common.NaturalInterval;
import be.baur.sds.model.ModelGroup;

/**
 * The abstract superclass of schema components. <br>
 * See also {@link NodeType} and {@link ModelGroup}
 */
public abstract class ComponentType extends Node {

	private String globalTypeName = null; 			// name of the global type this component refers to.
	private NaturalInterval multiplicity = null; 	// default multiplicity (mandatory and singular).

	/** Creates a component with the specified name. */
	public ComponentType(String name) {
		super(name);
	}

	
	/**
	 * Returns the name of the referenced global type. A component may re-use a type
	 * defined in the root section of the schema. This method returns null if this
	 * component is not referencing a type.
	 * 
	 * @return the name of the referenced type, may be null
	 */
	public String getGlobalType() {
		return globalTypeName;
	}

	
	/**
	 * Sets the name of the referenced global type. A component may re-use a type
	 * defined in the root section of the schema. This method cannot be used to
	 * re(set) an existing reference as this is likely to cause a problem.
	 * 
	 * @param type the name of the referenced type, null is ignored
	 */
	public void setGlobalType(String type) {
		if (type != null) this.globalTypeName = type;
	}

	
	/**
	 * Returns the multiplicity of this component. The default value is null, which
	 * means the component must occur exactly once (and which is equivalent to
	 * {@code [1,1]}).
	 * 
	 * @return a natural interval, may be null
	 */
	public NaturalInterval getMultiplicity() {
		return multiplicity;
	}

	
	/**
	 * Sets the multiplicity of this component. This method accepts a null
	 * reference, which means the component must occur exactly once (and which is
	 * equivalent to {@code [1,1]}).
	 * 
	 * @param a natural interval, may be null
	 */
	public void setMultiplicity(NaturalInterval multiplicity) {
		this.multiplicity = multiplicity;
	}

	
	/**
	 * Returns the minimum number of times this component must occur within its
	 * context.
	 * 
	 * @return a non-negative integer
	 */
	public int minOccurs() {
		return multiplicity != null ? multiplicity.min : 1;
	}

	
	/**
	 * Returns the maximum number of times this component may occur within its
	 * context.
	 * 
	 * @return a non-negative integer
	 */
	public int maxOccurs() {
		return multiplicity != null ? multiplicity.max : 1;
	}


	/**
	 * Returns an SDA node representing this component. In other words, what an SDA
	 * parser would return upon processing an input stream describing the component
	 * in SDS notation.
	 * 
	 * @returns an SDA node
	 */
	public abstract Node toNode();

	
	/**
	 * Returns the string representing this component in SDS notation. For
	 * example:
	 * 
	 * <pre>
	 * <code>node "greeting" { node "message" { type "string" } }</code>
	 * </pre>
	 * 
	 * Note that the returned string is formatted as a single line of text. For a
	 * more readable output, use the {@link #toNode} method and render the output
	 * node using an {@link SDAFormatter}.
	 * 
	 * @return an SDS representation of this component
	 */
	@Override 
	public abstract String toString();
}

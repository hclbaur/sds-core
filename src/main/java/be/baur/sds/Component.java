package be.baur.sds;

import java.util.Objects;

import be.baur.sda.Node;
import be.baur.sda.serialization.SDAFormatter;
import be.baur.sds.common.NaturalInterval;
import be.baur.sds.model.ModelGroup;

/**
 * The abstract superclass of schema components. <br>
 * See also {@link NodeType} and {@link ModelGroup}
 */
public abstract class Component extends Node {

	private String globalTypeName = null; 	// name of the global type that this component refers to.
	private NaturalInterval multiplicity = NaturalInterval.ONE_TO_ONE; 	// default is mandatory and singular.

	/**
	 * Creates a component with the specified name.
	 * 
	 * @param name a valid node name, see also {@link Node}
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public Component(String name) {
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
	 * Returns the multiplicity interval of this component. The default value is
	 * {@code [1,1]}, which means the component must occur exactly once. This method
	 * never returns null.
	 * 
	 * @return a natural interval, may be null
	 */
	public NaturalInterval getMultiplicity() {
		return multiplicity;
	}

	
	/**
	 * Sets the multiplicity of this component. This method does not accept null.
	 * 
	 * @param length a natural interval, not null
	 */
	public void setMultiplicity(NaturalInterval multiplicity) {
		this.multiplicity = Objects.requireNonNull(multiplicity, "multiplicity must not be null");
	}

	
	/**
	 * Returns the minimum number of times this component must occur within its
	 * context.
	 * 
	 * @return a non-negative integer
	 */
	public int minOccurs() {
		return multiplicity.min;
	}

	
	/**
	 * Returns the maximum number of times this component may occur within its
	 * context.
	 * 
	 * @return a non-negative integer
	 */
	public int maxOccurs() {
		return multiplicity.max;
	}


	/**
	 * Returns an SDA node representing this component. In other words, what an SDA
	 * parser would return upon processing an input stream describing the component
	 * in SDS notation.
	 * 
	 * @return an SDA node
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

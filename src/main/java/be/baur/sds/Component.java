package be.baur.sds;

import java.util.Objects;

import be.baur.sda.AbstractNode;
import be.baur.sda.DataNode;
import be.baur.sda.io.SDAFormatter;
import be.baur.sds.common.NaturalInterval;
import be.baur.sds.model.ModelGroup;

/**
 * This is the abstract superclass of all schema components (node types and
 * model groups).
 * 
 * @see AbstractNodeType
 * @see ModelGroup
 */
public abstract class Component extends AbstractNode {

	private String globalTypeName = null; // name of the global type that this component refers to.
	private NaturalInterval multiplicity = NaturalInterval.EXACTLY_ONE; // default is mandatory and singular.

	
	/**
	 * Returns the name of the referenced global type. Components may re-use a type
	 * defined in the root section of the schema. This method returns null if this
	 * component is not referencing a global type.
	 * 
	 * @return the referenced type name, may be null
	 */
	public String getGlobalType() {
		return globalTypeName;
	}

	
	/**
	 * Sets the name of the referenced global type. Components may re-use a type
	 * defined in the root section of the schema. This method cannot be used to
	 * clear or reset an existing reference as this is likely to cause a problem.
	 * 
	 * @param globaltype the referenced global type name, not null
	 */
	public void setGlobalType(String globaltype) {
		this.globalTypeName = Objects.requireNonNull(globaltype, "globaltype must not be null");
	}

	
	/**
	 * Returns the multiplicity interval of this component. The default value is
	 * {@code [1,1]}, which means "exactly once". This method never returns null.
	 * 
	 * @return a natural interval, never null
	 */
	public NaturalInterval getMultiplicity() {
		return multiplicity;
	}

	
	/**
	 * Sets the multiplicity of this component. This method does not accept null.
	 * 
	 * @param multiplicity a natural interval, not null
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
	public abstract DataNode toSDA();

	
	/**
	 * Returns the string representing this component in SDS notation. For
	 * example:
	 * 
	 * <pre>
	 * <code>node "greeting" { node "message" { type "string" } }</code>
	 * </pre>
	 * 
	 * Note that the returned string is formatted as a single line of text. For a
	 * more readable output, use the {@link #toSDA} method and render the output
	 * node using an {@link SDAFormatter}.
	 * 
	 * @return an SDS representation of this component
	 */
	@Override
	public final String toString() {
		return toSDA().toString();
	}

}

package be.baur.sds.model;

import java.util.Set;

import be.baur.sda.DataNode;
import be.baur.sds.Component;
import be.baur.sds.parser.Attribute;
import be.baur.sds.parser.Components;

/**
 * This is the abstract superclass of all model groups.
 * 
 * @see SequenceGroup
 * @see ChoiceGroup
 * @see UnorderedGroup
 */
public abstract class ModelGroup extends Component {

	public static final Set<String> TAGS // all known model group tags to date
		= Set.of(Components.GROUP.tag, Components.CHOICE.tag, Components.UNORDERED.tag);

	
	private String groupName = ""; // the name of this group (default empty string for anonymous group)

	/**
	 * Returns the name of this group, or an empty string if it is an anonymous group.
	 * 
	 * @return the group name, never null, maybe empty
	 */
	final public String getGroupName() {
		return groupName;
	}


	/**
	 * Sets the name of the name of this group.
	 * 
	 * @param name the group name
	 */
	final public void setGroupName(String name) {
		groupName = name;
	}


	/**
	 * Returns the effective minimum number of times that a group must occur within
	 * its context. Even when the formal multiplicity states that it is mandatory,
	 * empty content is valid if all of the alternatives are optional. For example,
	 * given a schema like
	 * 
	 * <pre>
	 * <code>
	 * node "person" {
	 *     group { 
	 *         node "firstname" { type "string" occurs "0..1" } 
	 *         node "lastname" { type "string" occurs "0..1" } 
	 *     }
	 * }
	 * </code>
	 * </pre>
	 * 
	 * the instance below is valid since both nodes may be omitted.
	 * 
	 * <pre>
	 * person { }
	 * </pre>
	 */
	@Override
	public int minOccurs() {
		
		if (this.get(n -> n.minOccurs() > 0) == null)
			return 0; // if a group has no mandatory components, it is optional
		return super.minOccurs(); // otherwise return the formal multiplicity
	}
	

	@Override
	public final DataNode toSDA() {
		
		final var node = new DataNode(getName()); // group, choice or unordered
		
		// omit the name for a reference with the same name as the referenced group
		if (! ( getReference() != null && getGroupName().equals(getReference()) )) {
			node.setValue(getGroupName());
		}
		
		// render the type attribute for a group reference
		String reference = getReference();
		if (reference != null)
			node.add(new DataNode(Attribute.TYPE.tag, reference));
	
		// render the multiplicity if not default
		if (getMultiplicity().min != 1 || getMultiplicity().max != 1) 
			node.add(new DataNode(Attribute.OCCURS.tag, getMultiplicity().toString()));
		
		// render children, unless we are a group reference
		if (reference == null)
			for (var component : nodes())
				node.add(component.toSDA());

		return node;
	}

}

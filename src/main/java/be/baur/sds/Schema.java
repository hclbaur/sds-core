package be.baur.sds;

import java.io.IOException;
import java.io.StringReader;

import be.baur.sda.AbstractNode;
import be.baur.sda.DataNode;
import be.baur.sda.Node;
import be.baur.sda.serialization.ParseException;
import be.baur.sda.serialization.SDAFormatter;
import be.baur.sds.serialization.Attribute;
import be.baur.sds.serialization.SDSParser;
import be.baur.sds.validation.Validator;

/**
 * A {@code Schema} node represents an SDA document definition that can be used
 * to validate SDA content (amongst others). It is a container for components
 * that define the content model, like node types and model groups. Schema is
 * usually not created "manually" but read and parsed from a definition in SDS
 * notation.
 * 
 * @see Component
 * @see SDSParser
 */
public final class Schema extends AbstractNode {

	public static final String TAG = "schema";	
	
	private String defaultType = null; // the designated root node


	/**
	 * Returns the default type for this schema. This method returns null if no
	 * default type has been set.
	 * 
	 * @return the default type name, may be null
	 */
	public String getDefaultType() {
		return defaultType;
	}

	
	/**
	 * Sets the default type for this schema. The argument must refer to an existing
	 * global type, or an exception will be thrown. A null value is allowed, and
	 * will effectively clear the default type.
	 * 
	 * @param type the default type name, may be null
	 * @throws IllegalArgumentException if the referenced type is unknown
	 */
	public void setDefaultType(String type) {
 
		if (type != null && get(t -> ((NodeType) t).getTypeName().equals(type)) == null)
			throw new IllegalArgumentException("no such global type (" + type + ")");
		this.defaultType = type; 
	}

	
	/**
	 * Returns an SDA node representing this schema. In other words, what an SDA
	 * parser would return upon processing an input stream describing the schema in
	 * SDS notation.
	 * 
	 * @return an SDA node
	 */
	public DataNode toSDA() {
		
		final DataNode node = new DataNode(TAG); 
		node.add(null); // just in case we have no child nodes
		
		if (defaultType != null) // render type attribute if we have one
			node.add(new DataNode(Attribute.TYPE.tag, defaultType));

		for (Node component : nodes()) // render all components
			node.add(((Component) component).toSDA());

		return node;
	}
	
	
	/**
	 * Returns the string representing this schema in SDS notation. For example:
	 * 
	 * <pre>
	 * <code>schema { node "greeting" { node "message" { type "string" } } }</code>
	 * </pre>
	 * 
	 * Note that the returned string is formatted as a single line of text. For a
	 * more readable output, use the {@link #toSDA} method and render the output
	 * node using an {@link SDAFormatter}.
	 * 
	 * @return an SDS representation of this node
	 */
	@Override
	public String toString() {
		return toSDA().toString();
	}


	/**
	 * Verifies this schema. This method can be used to to validate a schema that
	 * was not created by the {@code SDTParser}.
	 * 
	 * @throws IOException    if an input exception occurs
	 * @throws ParseException if a parse exception occurs
	 */
	public void verify() throws IOException, ParseException {
		// Serialize the schema and parse it using the SDSParser to reveal issues
		SDS.parse(new StringReader(this.toString()));
	}
	
	
	/**
	 * Returns a {@code Validator} associated with this schema.
	 * 
	 * @return a validator, not null
	 * @see Validator
	 */
	public Validator newValidator() {
		return new Validator() {
			protected Schema getSchema() {
				return Schema.this;
			}
		};
	}
}

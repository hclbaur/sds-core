package be.baur.sds;

import java.io.StringReader;
import java.util.Objects;

import be.baur.sda.AbstractNode;
import be.baur.sda.DataNode;
import be.baur.sda.Node;
import be.baur.sda.io.SDAFormatter;
import be.baur.sds.parsing.SDSParseException;
import be.baur.sds.parsing.SDSParser;
import be.baur.sds.validation.Validator;

/**
 * A {@code Schema} represents an SDA document definition that can be used
 * to validate SDA content. It is a container for components that define the
 * content model, like node types and model groups. Schema is usually not
 * created "manually" but read and parsed from a definition in SDS notation.
 * 
 * @see Component
 * @see SDSParser
 */
public final class Schema extends AbstractNode {

	public static final String TAG = "schema";


	/**
	 * Returns a global type specified by name, or null if no appropriate type could
	 * be found in the schema.
	 * 
	 * @param name the name of the type to get, not null
	 * @return a global type, may be null
	 */
	public NodeType getGlobalType(String name) {
		Objects.requireNonNull(name, "name must not be null");
		return get(t -> t instanceof NodeType && ((NodeType) t).getTypeName().equals(name));
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
	 * was not created by the {@code SDSParser}. This method returns nothing but
	 * throws an {@code SDSParseException} if the schema is not valid.
	 * 
	 * @throws SDSParseException if an SDS parse exception occurs
	 */
	public void verify() throws SDSParseException {
		// Serialize the schema and parse it back to reveal issues
		try {
			SDS.parse(new StringReader(this.toString()));
		} catch (Exception e) { // should never happen
			e.printStackTrace(); 
		}
	}
	
	
	/**
	 * Returns a {@code Validator} associated with this schema, prepared to validate
	 * SDA content against any global type declared in this schema.
	 * 
	 * @return a validator, not null
	 * @see Validator
	 */
	public Validator newValidator() {
		Validator val = new Validator() {
			@Override
			protected Schema getSchema() {
				return Schema.this;
			}
		};
		return val;
	}
}

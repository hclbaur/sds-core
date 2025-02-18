package be.baur.sds;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import be.baur.sda.AbstractNode;
import be.baur.sda.DataNode;
import be.baur.sda.Node;
import be.baur.sda.io.SDAFormatter;
import be.baur.sds.serialization.SDSParseException;
import be.baur.sds.serialization.SDSParser;
import be.baur.sds.types.BinaryNodeType;
import be.baur.sds.types.BooleanNodeType;
import be.baur.sds.types.DateTimeNodeType;
import be.baur.sds.types.DateNodeType;
import be.baur.sds.types.DecimalNodeType;
import be.baur.sds.types.IntegerNodeType;
import be.baur.sds.types.StringNodeType;
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

	
	/*
	 * A map that holds constructor functions to produce SDS node types. This allows
	 * us to keep the factory code generic and SDS extensible with new node types.
	 */
	@SuppressWarnings("rawtypes")
	private static Map<String, Function<String, ValueNodeType>> constructors = new HashMap<String, Function<String,ValueNodeType>>();


	/**
	 * Registers a function that returns an instance of a specific value node type.
	 * The type and function must not be null, and the type can be registered only
	 * once.
	 * 
	 * @param type     the data type to register
	 * @param function a function that returns a {@code ValueNodeType}
	 * @throws IllegalStateException if the type is already registered
	 */
	@SuppressWarnings("rawtypes")
	public static void registerType(String type, Function<String, ValueNodeType> function) {
		Objects.requireNonNull(type, "type must not be null");
		Objects.requireNonNull(function, "function must not be null");
		if (constructors.containsKey(type))
			throw new IllegalStateException("type " + type + " has already been registered");
		constructors.put(type, function);
	}


	/**
	 * Returns an instance of a value node type for the specified data type, or null
	 * if the requested type is unknown (e.g. has not been registered).
	 * 
	 * @param type a data type
	 * @param type a valid node name
	 * @return a {@code ValueNodeType}, or null
	 * @throws IllegalArgumentException if name is not a valid node name
	 */
	public static ValueNodeType<?> getRegisteredType(String type, String name) {
		if (type != null && constructors.containsKey(type))
			return constructors.get(type).apply(name);
		return null;
	}


	/**
	 * Returns true if the specified data type is registered.
	 * 
	 * @param type a data type
	 * @return true or false
	 */
	public static boolean isRegisteredType(String type) {
		return type == null ? false : constructors.containsKey(type);
	}


	// Register native SDS data types.
	static {
		registerType(StringNodeType.NAME, StringNodeType::new );
		registerType(BinaryNodeType.NAME, BinaryNodeType::new );
		registerType(IntegerNodeType.NAME, IntegerNodeType::new );
		registerType(DecimalNodeType.NAME, DecimalNodeType::new );
		registerType(DateNodeType.NAME, DateNodeType::new );
		registerType(DateTimeNodeType.NAME, DateTimeNodeType::new );
		registerType(BooleanNodeType.NAME, BooleanNodeType::new );
	}


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

package be.baur.sds;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import be.baur.sda.AbstractNode;
import be.baur.sda.DataNode;
import be.baur.sda.Node;
import be.baur.sda.serialization.SDAFormatter;
import be.baur.sds.serialization.SDSParseException;
import be.baur.sds.serialization.SDSParser;
import be.baur.sds.types.BinaryType;
import be.baur.sds.types.BooleanType;
import be.baur.sds.types.DateTimeType;
import be.baur.sds.types.DateType;
import be.baur.sds.types.DecimalType;
import be.baur.sds.types.IntegerType;
import be.baur.sds.types.StringType;
import be.baur.sds.validation.Validator;

/**
 * A {@code Schema} node represents an SDA document definition that can be used
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
	 * A map that holds functions to produce all native SDS data types. This allows
	 * us to keep the factory code generic and extensible with new types.
	 */
	private static Map<String, Function<String,DataType>> dataTypeFunctions = new HashMap<String, Function<String,DataType>>();

	/**
	 * Registers a function that creates an instance of a specific SDS data type.
	 * The type and function must not be null, and a type can be registered only
	 * once, otherwise an exception will be thrown.
	 * 
	 * @param type     the name of the type to register
	 * @param function a Function that returns a new DataType
	 * @throws IllegalStateException if {@code type} was already registered
	 */
	public static void registerDataType(String type, Function<String,DataType> function) {
		Objects.requireNonNull(type, "type must not be null");
		Objects.requireNonNull(function, "function must not be null");
		if (dataTypeFunctions.containsKey(type))
			throw new IllegalStateException("type " + type + " has already been registered");
		dataTypeFunctions.put(type, function);
	}

	/**
	 * Returns a DataType instance of the specified type and name, or null if the
	 * requested data type is unknown (has not been registered).
	 * 
	 * @param type a valid data type, not null
	 * @param name a valid node name, not null
	 * @return a DataType, or null
	 */
	public static DataType getDataType(String type, String name) {
		Objects.requireNonNull(type, "type must not be null");
		Objects.requireNonNull(name, "name must not be null");
		if (dataTypeFunctions.containsKey(type))
			return dataTypeFunctions.get(type).apply(name);
		return null;
	}
	
	/**
	 * Returns true if the specified type is a registered data type.
	 * 
	 * @param type a data type, not null
	 * @return true or false
	 */
	public static boolean isDataType(String type) {
		Objects.requireNonNull(type, "type must not be null");
		return dataTypeFunctions.containsKey(type);
	}

	// Register native SDS data types.
	static {
		registerDataType(StringType.NAME, StringType::new );
		registerDataType(BinaryType.NAME, BinaryType::new );
		registerDataType(IntegerType.NAME, IntegerType::new );
		registerDataType(DecimalType.NAME, DecimalType::new );
		registerDataType(DateType.NAME, DateType::new );
		registerDataType(DateTimeType.NAME, DateTimeType::new );
		registerDataType(BooleanType.NAME, BooleanType::new );
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

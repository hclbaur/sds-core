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
import be.baur.sds.parsing.SDSParseException;
import be.baur.sds.parsing.SDSParser;
import be.baur.sds.types.BinaryNodeType;
import be.baur.sds.types.BooleanNodeType;
import be.baur.sds.types.DateNodeType;
import be.baur.sds.types.DateTimeNodeType;
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
	 * Maps that hold constructor functions to produce SDS data (node) types. This
	 * allows us to keep the factory code generic and SDS extensible with new or
	 * custom data types.
	 */
	private static Map<String, Function<String, ?>> dtcmap = new HashMap<String, Function<String, ?>>();
	@SuppressWarnings("rawtypes")
	private static Map<String, Function<String, DataNodeType>> ntcmap = new HashMap<String, Function<String, DataNodeType>>();


	/**
	 * Registers constructor functions for the specified data (node) types. The
	 * type and functions must not be null, and a type can be registered only once.
	 * 
	 * @param type the data type to register, not null or empty
	 * @param dtcfun a data type constructor function, not null
	 * @param ntcfun a {@code DataNodeType} constructor function, not null
	 * @throws IllegalArgumentException if the type is already registered
	 */
	@SuppressWarnings("rawtypes")
	public static void registerDataType(String type, Function<String, ?> dtcfun, Function<String, DataNodeType> ntcfun) {
		Objects.requireNonNull(dtcfun, "data type constructor function must not be null");
		Objects.requireNonNull(ntcfun, "node type constructor function must not be null");
		if (type == null || type.isEmpty())
			throw new IllegalArgumentException("type must not be null or empty");
		if (dtcmap.containsKey(type) || ntcmap.containsKey(type))
			throw new IllegalArgumentException("type '" + type + "' has already been registered");
		dtcmap.put(type, dtcfun);
		ntcmap.put(type, ntcfun);
	}


	/**
	 * Returns a constructor function for the specified data type, or throws an
	 * exception if the type is unknown (e.g. has not been registered).
	 * 
	 * @param type a data type
	 * @return a constructor function
	 * @throws IllegalArgumentException if the type is not known
	 */
	public static Function<String, ?> dataTypeConstructor(String type) {
		if (! isDataType(type))
			throw new IllegalArgumentException("type '" + type + "' is unknown");
		return dtcmap.get(type);
	}


	/**
	 * Returns a constructor function for the specified node type, or throws an
	 * exception if the type is unknown (e.g. has not been registered).
	 * 
	 * @param type a data type
	 * @return a constructor function
	 * @throws IllegalArgumentException if the type is not known
	 */
	@SuppressWarnings("rawtypes")
	public static Function<String, DataNodeType> nodeTypeConstructor(String type) {
		if (! isDataType(type))
			throw new IllegalArgumentException("type '" + type + "' is unknown");
		return ntcmap.get(type);
	}


	/**
	 * Returns true if the argument is a registered data type, and false otherwise.
	 * 
	 * @param type a data type
	 * @return true or false
	 */
	public static boolean isDataType(String type) {
		return type == null ? false : ntcmap.containsKey(type);
	}


	/*
	 * Register native SDS data types.
	 */
	static {
		registerDataType(DataType.STRING, DataType.STRING_CONSTRUCTOR, StringNodeType::new );
		registerDataType(DataType.BINARY, DataType.BINARY_CONSTRUCTOR, BinaryNodeType::new );
		registerDataType(DataType.INTEGER, DataType.INTEGER_CONSTRUCTOR, IntegerNodeType::new );
		registerDataType(DataType.DECIMAL, DataType.DECIMAL_CONSTRUCTOR, DecimalNodeType::new );
		registerDataType(DataType.DATE, DataType.DATE_CONSTRUCTOR, DateNodeType::new );
		registerDataType(DataType.DATETIME, DataType.DATETIME_CONSTRUCTOR, DateTimeNodeType::new );
		registerDataType(DataType.BOOLEAN, DataType.BOOLEAN_CONSTRUCTOR, BooleanNodeType::new );
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

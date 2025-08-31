package be.baur.sds.types;

import java.util.function.Function;

/**
 * A <code>IntegerNodeType</code> defines an SDA node with an integer value.
 */
public final class IntegerNodeType extends ComparableNodeType <Integer> {


	/** Name of the SDS integer type. */
	public static final String TYPE_NAME = "integer";
	
	/**
	 * Function to construct an SDS integer value from a string.
	 * @throws NumberFormatException if the string cannot be converted to an integer.
	 */
	public static final Function<String, Integer> TYPE_CONSTRUCTOR = Integer::new;


    /**
     * Creates an integer node type with the specified name.
     *
     * @param name a valid node name
     * @throws IllegalArgumentException if the name is invalid
     */
    public IntegerNodeType(String name) {
        super(name);
    }

    @Override
    public String getDataType() {
        return TYPE_NAME;
    }

    @Override
    public Function<String, Integer> valueConstructor() {
        return TYPE_CONSTRUCTOR;
    }

}
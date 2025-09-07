package be.baur.sds.types;

import java.util.function.Function;

import be.baur.sds.DataType;

/**
 * A <code>IntegerNodeType</code> defines an SDA node with an integer value.
 */
public final class IntegerNodeType extends ComparableNodeType <Integer> {


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
		return DataType.INTEGER;
	}


	@Override
	public Function<String, Integer> getDataTypeConstructor() {
		return DataType.INTEGER_CONSTRUCTOR;
	}

}

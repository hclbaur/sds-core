package be.baur.sds.types;

import java.util.function.Function;

import be.baur.sds.DataType;

/**
 * A <code>DecimalNodeType</code> defines an SDA node with a decimal value.
 */
public final class DecimalNodeType extends ComparableNodeType <Double> {


	/**
	 * Creates a decimal node type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public DecimalNodeType(String name) {
		super(name);
	}
	

	@Override
	public String getDataType() {
		return DataType.DECIMAL;
	}
	

	@Override
	public Function<String, Double> valueConstructor() {
		return DataType.DECIMAL_CONSTRUCTOR;
	}

}

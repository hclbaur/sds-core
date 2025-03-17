package samples.types;

import java.util.function.Function;

import be.baur.sds.types.ComparableNodeType;

/**
 * A <code>GMonthDayNodeType</code> defines an SDA node with temporal data (a local
 * recurring calendar date without a year or time zone).
 * 
 * @see GMonthDay
 */
public final class GMonthDayNodeType extends ComparableNodeType <GMonthDay> {

	
	/**
	 * Creates an gMonthDay node type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public GMonthDayNodeType(String name) {
		super(name);
	}

	
	@Override
	public String getDataType() {
		return GMonthDay.TYPE;
	}

	
	@Override
	public Function<String, GMonthDay> valueConstructor() {
		return GMonthDay.CONSTRUCTOR;
	}

}

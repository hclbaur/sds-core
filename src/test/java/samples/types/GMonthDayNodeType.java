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

	/** The SDS name of this data type. */
	public static final String NAME = "gMonthDay";

	/** A function that constructs a gMonthDay from a string. */
	public static final Function<String, GMonthDay> VALUE_CONSTRUCTOR = s -> {
		return GMonthDay.parse(s);
	};

	
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
	public String getValueType() {
		return NAME;
	}

	
	@Override
	public Function<String, GMonthDay> valueConstructor() {
		return VALUE_CONSTRUCTOR;
	}

	
	/**
	 * Returns a GMonthDay if the supplied string is within the lexical space of
	 * this type (which follows the ISO 8601 syntax "--MM-DD").
	 * 
	 * @param s the string to be converted
	 * @return a GMonthDay
	 * @throws IllegalArgumentException if conversion is not possible
	 */
	public static GMonthDay valueOf(String s) {
		return VALUE_CONSTRUCTOR.apply(s);
	}
}

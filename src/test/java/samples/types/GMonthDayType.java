package samples.types;

import java.util.function.Function;

import be.baur.sds.content.RangedType;

/**
 * A <code>GMonthDayType</code> defines an SDA node with temporal content (a
 * local recurring calendar date without a year or time zone).
 * 
 * @see GMonthDay
 */
public final class GMonthDayType extends RangedType<GMonthDay> {

	/** The SDS name of this data type. */
	public static final String NAME = "gMonthDay";

	/** A function that constructs a gMonthDay value from a string. */
	public static final Function<String, GMonthDay> VALUE_CONSTRUCTOR = s -> {
		return GMonthDay.parse(s);
	};

	
	/**
	 * Creates the type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public GMonthDayType(String name) {
		super(name);
	}

	
	@Override
	public String getType() {
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

package be.baur.sds.content;

import java.time.DateTimeException;
import java.util.function.Function;

import be.baur.sds.common.DateTime;

/**
 * A <code>DateTimeType</code> represents an SDA node with temporal content
 * (date, time and time zone).
 */
public final class DateTimeType extends RangedType<DateTime> {

	/** The SDS name of this data type. */
	public static final String NAME = "datetime";

	/** A function that constructs a date-time value from a string. */
	public static final Function<String, DateTime> VALUE_CONSTRUCTOR = DateTime::new;

	/**
	 * Creates the type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public DateTimeType(String name) {
		super(name);
	}


	@Override
	public String getType() {
		return NAME;
	}


//	@Override
//	public Class<DateTime> valueClass() {
//		return DateTime.class;
//	}
	

	@Override
	public Function<String, DateTime> valueConstructor() {
		return VALUE_CONSTRUCTOR;
	}
	
	
	/**
	 * Returns a DateTime if the supplied string is within the lexical space of this type.
	 * 
	 * @param s the string to be converted
	 * @return a DateTime
	 * @throws DateTimeException if conversion is not possible
	 */
	public static DateTime valueOf(String s) {
		return VALUE_CONSTRUCTOR.apply(s);
	}
}

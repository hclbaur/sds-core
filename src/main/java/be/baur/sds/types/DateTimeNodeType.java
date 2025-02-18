package be.baur.sds.types;

import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

/**
 * A <code>DateTimeNodeType</code> defines an SDA node with a temporal value (a
 * date and time including the time zone).
 */
public final class DateTimeNodeType extends ComparableNodeType <ZonedDateTime> {

	/** The SDS name of this data type. */
	public static final String NAME = "datetime";

	/** A function that constructs a date-time value from a string. */
	public static final Function<String, ZonedDateTime> VALUE_CONSTRUCTOR = s -> {
		return ZonedDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	};


	/**
	 * Creates a date-time node type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public DateTimeNodeType(String name) {
		super(name);
	}


	@Override
	public String getValueType() {
		return NAME;
	}


	@Override
	public Function<String, ZonedDateTime> valueConstructor() {
		return VALUE_CONSTRUCTOR;
	}
	
	
	/**
	 * Returns a ZonedDateTime if the supplied string is within the lexical space of this type.
	 * 
	 * @param s the string to be converted
	 * @return a ZonedDateTime
	 * @throws DateTimeException if conversion is not possible
	 */
	public static ZonedDateTime valueOf(String s) {
		return VALUE_CONSTRUCTOR.apply(s);
	}
}

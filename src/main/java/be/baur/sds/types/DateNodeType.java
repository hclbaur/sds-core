package be.baur.sds.types;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

/**
 * A <code>DateNodeType</code> defines an SDA node with a temporal value (a calendar
 * date without time zone).
 */
public final class DateNodeType extends ComparableNodeType <LocalDate> {


	/** Name of the SDS date type. */
	public static final String TYPE_NAME = "date";

	/**
	 * Function to construct an SDS date value from a string.
	 * @throws DateTimeParseException if the string cannot be converted to a date.
	 */
	public static final Function<String, LocalDate> TYPE_CONSTRUCTOR = s -> {
		return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
	};


	/**
	 * Creates a date node type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public DateNodeType(String name) {
		super(name);
	}
	

	@Override
	public String getDataType() {
		return TYPE_NAME;
	}
	

	@Override
	public Function<String, LocalDate> valueConstructor() {
		return TYPE_CONSTRUCTOR;
	}

}

package be.baur.sds.types;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

/**
 * A <code>DateNodeType</code> defines an SDA node with a temporal value (a calendar
 * date without time zone).
 */
public final class DateNodeType extends ComparableNodeType <LocalDate> {

	/** The SDS name of this data type. */
	public static final String NAME = "date";
	
	/** A function that constructs a date value from a string. */
	public static final Function<String, LocalDate> VALUE_CONSTRUCTOR = s -> {
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
	public String getValueType() {
		return NAME;
	}
	

	@Override
	public Function<String, LocalDate> valueConstructor() {
		return VALUE_CONSTRUCTOR;
	}
	
	
	/**
	 * Returns a LocalDate if the supplied string is within the lexical space of this type.
	 * 
	 * @param s the string to be converted
	 * @return a LocalDate
	 * @throws DateTimeException if conversion is not possible
	 */
	public static LocalDate valueOf(String s) {
		return VALUE_CONSTRUCTOR.apply(s);
	}
}

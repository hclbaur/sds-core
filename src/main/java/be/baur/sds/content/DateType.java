package be.baur.sds.content;

import java.util.function.Function;

import be.baur.sds.common.Date;

/**
 * A <code>DateType</code> represents an SDA node with temporal content (a
 * calendar date without time zone).
 */
public final class DateType extends RangedType<Date> {

	/** The SDS name of this data type. */
	public static final String NAME = "date";
	
	/** A function that constructs a date value from a string. */
	public static final Function<String, Date> VALUE_CONSTRUCTOR = Date::new;	


	/**
	 * Creates the type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public DateType(String name) {
		super(name);
	}
	

	@Override
	public String getType() {
		return NAME;
	}


//	@Override
//	public Class<Date> valueClass() {
//		return Date.class;
//	}
	

	@Override
	public Function<String, Date> valueConstructor() {
		return VALUE_CONSTRUCTOR;
	}
	
	
	/**
	 * Returns a Date if the supplied string is within the lexical space of this type.
	 * 
	 * @param s the string to be converted
	 * @return a Date
	 * @throws DateTimeException if conversion is not possible
	 */
	public static Date valueOf(String s) {
		return VALUE_CONSTRUCTOR.apply(s);
	}
}

package be.baur.sds.content;

import java.util.function.Function;

import be.baur.sds.common.DateTime;

/**
 * A <code>DateTimeType</code> represents an SDA node with temporal content
 * (date, time and time zone).
 */
public final class DateTimeType extends RangedType<DateTime> {

	public static final String TYPE = "datetime";


	/**
	 * Creates the type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public DateTimeType(String name) {
		super(name);
	}


	public String getType() {
		return TYPE;
	}


	public Class<DateTime> valueClass() {
		return DateTime.class;
	}
	

	public Function<String, DateTime> valueConstructor() {
		return DateTime::new;
	}
}

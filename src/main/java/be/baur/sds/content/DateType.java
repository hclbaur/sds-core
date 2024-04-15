package be.baur.sds.content;

import java.util.function.Function;

import be.baur.sds.common.Date;

/**
 * A <code>DateType</code> represents an SDA node with temporal content (a
 * calendar date without time zone).
 */
public final class DateType extends RangedType<Date> {

	public static final String NAME = "date";


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


	@Override
	public Class<Date> valueClass() {
		return Date.class;
	}
	

	@Override
	public Function<String, Date> valueConstructor() {
		return Date::new;
	}
}

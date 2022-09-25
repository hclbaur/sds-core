package be.baur.sds.content;

import be.baur.sds.common.Content;
import be.baur.sds.common.DateTime;

/**
 * A <code>DateTimeType</code> represents an SDA node with temporal content
 * (date, time and time zone).<br>
 * See also {@link DateType}.
 */
public final class DateTimeType extends RangedType<DateTime> {


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
	public Content getContentType() { 
		return Content.DATETIME; 
	}
}

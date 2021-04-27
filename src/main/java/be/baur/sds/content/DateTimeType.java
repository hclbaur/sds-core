package be.baur.sds.content;

import be.baur.sds.common.Content;
import be.baur.sds.common.DateTime;

/**
 * A <code>DateTimeType</code> is a {@link RangedType} representing an SDA node with
 * temporal content (date, time and time zone).
 */
public final class DateTimeType extends RangedType<DateTime> {


	/** Creates a date-time type with the supplied <code>name</code>. */
	public DateTimeType(String name) {
		super(name);
	}
	
	
	public Content getContentType() { 
		return Content.DATETIME; 
	}
}

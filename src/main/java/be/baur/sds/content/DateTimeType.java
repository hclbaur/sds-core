package be.baur.sds.content;

import be.baur.sds.common.Content;
import be.baur.sds.common.DateTime;

/**
 * A <code>DateTimeType</code> is a {@link RangedType} representing a SDA node with
 * temporal content (date, time and time zone).
 */
public class DateTimeType extends RangedType<DateTime> {


	/** Create a datetime type. */
	public DateTimeType(String name) {
		super(name);
	}
	
	@Override
	public String getType() { 
		return Content.DATETIME.type; 
	}
}

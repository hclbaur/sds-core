package be.baur.sds.content;

import be.baur.sds.common.Content;
import be.baur.sds.common.Date;

/**
 * A <code>DateType</code> is a {@link RangedType} representing a SDA node with
 * temporal content (a date without time or time zone).
 */
public class DateType extends RangedType<Date> {


	/** Create a date type. */
	public DateType(String name) {
		super(name);
	}
	
	public Content getContentType() { 
		return Content.DATE; 
	}
}

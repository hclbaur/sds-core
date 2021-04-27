package be.baur.sds.content;

import be.baur.sds.common.Content;
import be.baur.sds.common.Date;

/**
 * A <code>DateType</code> is a {@link RangedType} representing an SDA node with
 * temporal content (a date without time or time zone).
 */
public final class DateType extends RangedType<Date> {


	/** Creates a date type with the supplied <code>name</code>. */
	public DateType(String name) {
		super(name);
	}
	
	
	public Content getContentType() { 
		return Content.DATE; 
	}
}

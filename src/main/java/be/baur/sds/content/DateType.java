package be.baur.sds.content;

import be.baur.sds.common.Content;
import be.baur.sds.common.Date;

/**
 * A <code>DateType</code> represents an SDA node with temporal content (a
 * calendar date without time zone).<br>
 * See also {@link DateTimeType}.
 */
public final class DateType extends RangedType<Date> {


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
	public Content getContentType() { 
		return Content.DATE; 
	}
}

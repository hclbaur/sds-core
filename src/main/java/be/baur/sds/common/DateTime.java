package be.baur.sds.common;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A <code>DateTime</code> is a wrapper for a {@link ZonedDateTime} class, for
 * use in an {@link Interval}. The wrapper provides a constructor accepting a
 * string in extended ISO 8601 format.
 */
public final class DateTime implements Comparable<Object> {

	public final ZonedDateTime value;
	private static DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

	
	/** Creates a DateTime from a string in extended ISO 8601 format. */
	public DateTime(String date) {
		value = ZonedDateTime.parse(date, formatter);
	}

	
	/** Returns this DateTime as a string in extended ISO 8601 format. */
	public String toString() {
		return value.format(formatter);
	}

	
	@Override
	public int compareTo(Object datetime) {
		return value.compareTo(((DateTime) datetime).value);
	}
}

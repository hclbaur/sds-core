package be.baur.sds.common;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A <code>DateTime</code> is a wrapper for a {@link ZonedDateTime} class, for use
 * in {@link Interval}. The wrapper is needed because a <code>ZonedDateTime</code> 
 * (or <code>Date</code>) has no constructor accepting a string in ISO 8601 format.
 */
@SuppressWarnings("rawtypes")
public class DateTime implements Comparable {

	public final ZonedDateTime value;
	private static DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

	
	public DateTime(String s) {
		value = ZonedDateTime.parse(s, formatter);
	}

	public String toString() {
		return value.format(formatter);
	}

	@Override
	public int compareTo(Object datetime) {
		return value.compareTo(((DateTime) datetime).value);
	}
}

package be.baur.sds.common;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A <code>DateTime</code> is a wrapper class providing a constructor that
 * accepts a string in ISO 8601 format. This is used in constructing an
 * {@link Interval}.
 */
public final class DateTime implements Comparable<Object> {

	public final ZonedDateTime value; // the class that is wrapped
	private static DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

	
	/**
	 * Creates a date and time from a string in extended ISO 8601 format.
	 * 
	 * @param datetime a formatted date and time
	 */
	public DateTime(String datetime) {
		value = ZonedDateTime.parse(datetime, formatter);
	}

	
	/**
	 * Returns this date and time as a string in extended ISO 8601 format.
	 * 
	 * @return a formatted date and time
	 */
	public String toString() {
		return value.format(formatter);
	}

	
	@Override
	public int compareTo(Object datetime) {
		return value.compareTo(((DateTime) datetime).value);
	}
}

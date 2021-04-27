package be.baur.sds.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A <code>Date</code> is a wrapper for a {@link LocalDate} class, for use in an
 * {@link Interval}. The wrapper provides a constructor that accepts a string in
 * ISO 8601 format.
 */
public final class Date implements Comparable<Object> {

	public final LocalDate value;
	private static DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
	
	
	/** Creates a Date from a string in YYYY-MM-DD format. */
	public Date(String date) {
		value = LocalDate.parse(date, formatter);
	}

	
	/** Returns this date as a string in YYYY-MM-DD format. */
	public String toString() {
		return value.format(formatter);
	}


	@Override
	public int compareTo(Object date) {
		return value.compareTo(((Date) date).value);
	}
}

package be.baur.sds.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A <code>Date</code> is a wrapper class providing a constructor that accepts a
 * string in ISO 8601 format. This is used in constructing an {@link Interval}.
 */
public final class Date implements Comparable<Object> {

	public final LocalDate value; // the class that is wrapped
	private static DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
	
	
	/**
	 * Creates a date from a string in YYYY-MM-DD format.
	 * 
	 * @param date a formatted date
	 */
	public Date(String date) {
		value = LocalDate.parse(date, formatter);
	}

	
	/**
	 * Returns this date as a string in YYYY-MM-DD format.
	 * 
	 * @return a formatted date
	 */
	public String toString() {
		return value.format(formatter);
	}


	@Override
	public int compareTo(Object date) {
		return value.compareTo(((Date) date).value);
	}
}

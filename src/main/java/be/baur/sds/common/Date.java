package be.baur.sds.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A <code>Date</code> is a wrapper for a {@link LocalDate} class, for use in
 * {@link Interval}. The wrapper is needed because a <code>LocalDate</code> has
 * no constructor that accepts a string in ISO 8601 format.
 */
@SuppressWarnings("rawtypes")
public class Date implements Comparable {

	public final LocalDate value;
	private static DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

	
	public Date(String s) {
		value = LocalDate.parse(s, formatter);
	}

	public String toString() {
		return value.format(formatter);
	}

	@Override
	public int compareTo(Object date) {
		return value.compareTo(((Date) date).value);
	}
}

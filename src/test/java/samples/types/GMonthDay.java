package samples.types;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A {@code GMonthDay} is a local recurring calendar day without year or time
 * zone. The lexical representation of such a date is given by the ISO 8601
 * syntax"--MM-DD" (the leading hyphens are mandatory).
 */
public final class GMonthDay implements Comparable<Object> {
	
	private static final int[] maxday = {31,29,31,30,31,30,31,31,30,31,30,31};
	
	public static final GMonthDay MIN_VALUE = new GMonthDay(1,1);
	public static final GMonthDay MAX_VALUE = new GMonthDay(12,31);

	private final int month, day;

	
	/**
	 * Creates a new GMonthDay from a month and a day.
	 * 
	 * @param month a month (1-12)
	 * @param day a day (1-31)
	 * @throws IllegalArgumentException if either month and/or day is invalid
	 */
	public GMonthDay(int month, int day) {
		
		if (month < 1 || month > 12) 
			throw new IllegalArgumentException("month " + month + " is invalid");

		if (day < 1 || day > maxday[month-1]) 
			throw new IllegalArgumentException("day " + day + " is invalid");
		
		this.month = month;	this.day = day;
	}



	private static final Pattern pattern = Pattern.compile("--\\d{2}-\\d{2}");
	
	/**
	 * Returns a GMonthDay constructed obtained from a string.
	 *
	 * @param date a string in "--MM-DD" format
	 * @return a GMonthDay
	 * @throws IllegalArgumentException if the date is invalid
	 */
	public static GMonthDay parse(String date) {
		
		Objects.requireNonNull(date, "date must not be null");
		
		if (! pattern.matcher(date).matches()) 
			throw new IllegalArgumentException("date '" + date + "' is invalid");
		
		return new GMonthDay(
			Integer.parseInt(date.substring(2,4)), 
			Integer.parseInt(date.substring(5,7))
		);
	}

	
	/**
	 * Returns this GMonthDay in "--MM-DD" format.
	 */
	@Override
	public String toString() {
		return String.format("--%02d-%02d", month, day);
	}

	
	@Override
	public int compareTo(Object gmonthday) {

		GMonthDay other = (GMonthDay) gmonthday;
		if (this.month == other.month) {
			return (this.day - other.day);
		} else
			return (this.month - other.month);
	}
}

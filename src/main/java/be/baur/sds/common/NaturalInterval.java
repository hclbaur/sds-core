package be.baur.sds.common;

/**
 * This class models an interval, with limits that are natural numbers (e.g.
 * non-negative integers). It is used for multiplicity and length. The limits
 * are inclusive, so the interval is closed by definition. The notation is as
 * follows:
 * 
 * <pre>
 * a..b	: where both a and b are non-negative integers and a <= b
 * a..*	: "unbounded", but equivalent to a..MAX_INT in practice
 * a	: fixed value, equivalent to "a..a"	(degenerate interval)
 * </pre>
 */
public final class NaturalInterval {

	/** Lower limit */
	public final int min;
	/** Upper limit */
	public final int max;

	
	/**
	 * Creates the interval from a lower and upper limit.
	 * 
	 * @throws IllegalArgumentException
	 */
	public NaturalInterval(int min, int max) {

		if (min < 0 || max < 0)
			throw new IllegalArgumentException("negative values are not allowed");
		else if (min > max)
			throw new IllegalArgumentException("lower limit exceeds upper limit");

		this.min = min;
		this.max = max;
	}
	
	
	/**
	 * Creates an interval from a string in interval notation.
	 * 
	 * @throws IllegalArgumentException
	 */
	public static NaturalInterval from(String interval)  {
	
		interval = (interval == null) ? "" : interval.trim();
		if (interval.isEmpty())
			throw new IllegalArgumentException("no interval specified");
		
		int min, max, dots = interval.indexOf("..");

		try {
			if (dots < 0) { // no dots; parse a single integer
				min = Integer.parseInt(interval);
				max = min;
			} else { // otherwise, look for lower limit before the dots
				min = Integer.parseInt(interval.substring(0, dots).trim());

				// and for an upper limit or an * after the dots
				String s = interval.substring(dots + 2).trim();
				max = s.equals("*") ? Integer.MAX_VALUE : Integer.parseInt(s);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("missing or non-integer value(s)", e);
		}

		return new NaturalInterval(min, max);	

	}
	

	/**
	 * Returns 0 if the supplied value lies within the interval, -1 if it subceeds
	 * the lower interval limit, and 1 if it exceeds the upper limit.
	 */
	public int contains(int value) {

		return (value < min) ? -1 : ((value > max) ? 1 : 0);
	}

	
	/** Returns the interval as a string in interval notation. */
	public String toString() {
		return (min == max) ? "" + min
			: min + ".." + ((max == Integer.MAX_VALUE) ? "*" : "" + max);
	}
}

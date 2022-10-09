package be.baur.sds.common;

/**
 * A {@code NaturalInterval} represents an interval with limits that are natural
 * numbers (that is non-negative integers). Lower and upper limiting values are
 * considered inclusive. In natural interval notation, no brackets are used:
 * 
 * <pre>
 * <code>
 * a..b	Closed
 * a..*	Right unbounded, but a..Integer.MAX_VALUE in practice
 *   a	Degenerate, equivalent to a..a
 * </code>
 * </pre>
 * 
 * See also {@link Interval}.
 */
public final class NaturalInterval {

	/** Lower interval limit */
	public final int min;
	/** Upper interval limit */
	public final int max;


	/**
	 * Creates an interval from two integer limit points. The minimum should never
	 * exceed the maximum value, and neither can be negative.
	 * 
	 * @param min the lower limit
	 * @param max the upper limit
	 * @throws IllegalArgumentException if the lower exceeds the upper limit or
	 *                                  either limit is invalid
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
	 * Creates an interval from a string in natural interval notation.
	 * 
	 * @param interval a valid interval notation, not null or empty
	 * @return a natural interval
	 * @throws IllegalArgumentException in case of an invalid interval
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
	 * Checks whether a value lies within this interval. This method returns 0 if
	 * the specified value is contained within the interval limits, -1 if it
	 * subceeds the lower limit, and 1 if it exceeds the upper limit.
	 * 
	 * @param value the value to be evaluated
	 * @return -1, 0 or 1
	 */
	public int contains(int value) {

		return (value < min) ? -1 : ((value > max) ? 1 : 0);
	}

	
	/**
	 * Returns this interval as a string in natural interval notation.
	 * 
	 * @return natural interval notation
	 */
	public String toString() {
		return (min == max) ? "" + min
			: min + ".." + ((max == Integer.MAX_VALUE) ? "*" : "" + max);
	}
}

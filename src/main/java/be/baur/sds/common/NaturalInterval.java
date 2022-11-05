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
 *   
 * where a >= 0 and b >= a
 * </code>
 * </pre>
 * 
 * See also {@link Interval}.
 */
public final class NaturalInterval {

	/** All non-negative integers from 0 to "infinity", e.g. {@code 0..Integer.MAX_VALUE}. */
	public static final NaturalInterval ZERO_TO_MAX = new NaturalInterval(0, Integer.MAX_VALUE);
	
	/** All positive integers from 1 to "infinity", e.g. {@code 1..Integer.MAX_VALUE}. */
	public static final NaturalInterval ONE_TO_MAX = new NaturalInterval(1, Integer.MAX_VALUE);
	
	/** The unit interval from 0 to 1, e.g. {@code 0..1}. */
	public static final NaturalInterval ZERO_TO_ONE = new NaturalInterval(0, 1);
	
	/** The degenerate interval {@code 1..1}, so just 1. */
	public static final NaturalInterval ONE_TO_ONE = new NaturalInterval(1, 1);
	
	
	/** The lower interval limit */
	public final int min;
	/** The upper interval limit */
	public final int max;


	/*
	 * Creates an interval from two integer limits. The minimum limit should never
	 * exceed the maximum limit, and neither can be negative.
	 * 
	 * @param min the lower limit
	 * @param max the upper limit
	 * @throws IllegalArgumentException if the lower exceeds the upper limit or
	 *                                  either limit is invalid
	 */
	 private NaturalInterval(int min, int max) {

		if (min < 0 || max < 0)
			throw new IllegalArgumentException("negative values are not allowed");
		else if (min > max)
			throw new IllegalArgumentException("lower limit exceeds upper limit");

		this.min = min;
		this.max = max;
	}

	 
	/**
	 * Creates an interval from two integer limits.
	 * 
	 * @param min the lower limit
	 * @param max the upper limit
	 * @return a natural interval
	 * @throws IllegalArgumentException for an invalid interval
	 */
	 public static NaturalInterval from(int min, int max) {
		 
		 /* return static values for common intervals */
		 if (min == 0) {
			 if (max == 1) return ZERO_TO_ONE;
			 if (max == Integer.MAX_VALUE) return ZERO_TO_MAX;
		 }
		 if (min == 1) {
			 if (max == 1) return ONE_TO_ONE;
			 if (max == Integer.MAX_VALUE) return ONE_TO_MAX;
		 } 
		 return new NaturalInterval(min, max);
	}


	/**
	 * Creates an interval from a string in natural interval notation.
	 * 
	 * @param interval a valid interval notation, not null or empty
	 * @return a natural interval
	 * @throws IllegalArgumentException for an invalid interval
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

		return NaturalInterval.from(min, max);	

	}

	
	/**
	 * Checks whether a value lies within this interval. This method returns 0 if
	 * the specified integer value is contained within the interval limits, -1 if 
	 * it subceeds the lower limit, and 1 if it exceeds the upper limit.
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

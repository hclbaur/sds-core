package be.baur.sds.common;

/**
 * This class models an interval, with a lower and an upper limit point that is
 * a natural number (e.g. non-negative integer). It is used for attributes like
 * multiplicity and length. The limits are inclusive, so the interval is closed
 * by definition. The notation is as follows:
 * 
 * <pre>
 * a..b	: where both a and b are non-negative integers and a <= b
 * a..*	: "unbounded", but equivalent to a..MAX_INT in practice
 * a	: fixed value, equivalent to "a..a"	(degenerate interval)
 * </pre>
 */

public class NaturalInterval {

	/** Lower limit */
	public final int lower;
	/** Upper limit */
	public final int upper;

	
	/**
	 * Construct the interval from a lower and upper limit.
	 * 
	 * @throws IllegalArgumentException
	 */
	public NaturalInterval(int lower, int upper) {

		if (lower < 0 || upper < 0)
			throw new IllegalArgumentException("negative values are not allowed");
		else if (lower > upper)
			throw new IllegalArgumentException("lower limit exceeds upper limit");

		this.lower = lower;
		this.upper = upper;
	}
	
	
	/**
	 * Factory method to create an interval from a string in interval notation.
	 * 
	 * @throws IllegalArgumentException
	 */
	public static NaturalInterval from(String interval)  {
	
		interval = (interval == null) ? "" : interval.trim();
		if (interval.isEmpty())
			throw new IllegalArgumentException("no interval specified");
		
		int lower, upper, dots = interval.indexOf("..");

		try {
			if (dots < 0) { // no dots; parse a single integer
				lower = Integer.parseInt(interval);
				upper = lower;
			} else { // otherwise, look for lower before the dots
				lower = Integer.parseInt(interval.substring(0, dots).trim());

				// and for upper or an * after the dots
				String s = interval.substring(dots + 2).trim();
				upper = s.equals("*") ? Integer.MAX_VALUE : Integer.parseInt(s);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("missing or non-integer value(s)", e);
		}

		return new NaturalInterval(lower, upper);	

	}
	

	/**
	 * This method returns 0 if the supplied value lies within the interval, and -1
	 * (value below interval limit) or 1 (value beyond interval limit) otherwise.
	 */
	public int contains(int value) {

		return (value < lower) ? -1 : ((value > upper) ? 1 : 0);
	}

	
	/** Returns this instance in interval notation. */
	public String toString() {
		return (lower == upper) ? "" + lower
			: lower + ".." + ((upper == Integer.MAX_VALUE) ? "*" : "" + upper);
	}

//	public static void main (String[] args) {
//		
//		int i = 100000000;
//		long start = new Date().getTime();
//		while (i>0) {
//			NaturalInterval.from("0..*");
//			--i;
//		}
//		System.out.println(new Date().getTime() - start);
//	}
	
}

package be.baur.sds.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class models a generic interval, with a lower and an upper limit point.
 * If a limit point is included (or excluded), the interval is called closed (or
 * open) on that particular side. Or, in interval notation:
 * 
 * <pre>
 * (a..b)	Open
 * [a..b]	Closed
 * (a..b]	Left open
 * [a..b)	Right open
 * (a..*)	Left open, right unbounded
 * [a..*)	Right unbounded
 * (*..b)	Right open, left unbounded
 * (*..b]	Left unbounded
 * (*..*)	Unbounded
 * </pre>
 */
@SuppressWarnings("rawtypes")
public final class Interval <T extends Comparable> {

	/** Lower limit */
	public final T min;
	/** Upper limit */
	public final T max;
	/** Interval type */
	public final int type;

	/** Interval types */
	public final static int CLOSED 		= 	0b00;
	public final static int LEFT_OPEN 	= 	0b10;
	public final static int RIGHT_OPEN 	= 	0b01;
	public final static int OPEN 		= 	0b11;
	
	// Pre-compiled pattern that matches an interval notation
	private static final String LB="\\[\\(";
	private static final String RB="\\)\\]";
	private static final Pattern PATTERN = 
			Pattern.compile("["+LB+"]([^"+LB+"]+)\\.\\.([^"+RB+"]+)["+RB+"]");

	
	/**
	 * Creates an <code>Interval</code> from its limit points and a type. The
	 * minimum value can never exceed the maximum value. A <code>null</code> value
	 * for a limit point means that the interval is unbounded (and by definition
	 * open) on that side.
	 * 
	 * @throws IllegalArgumentException
	 */
	@SuppressWarnings("unchecked")
	public Interval(T min, T max, int type) {

		this.min = min; this.max = max;

		if (min == null) type |= LEFT_OPEN;
		if (max == null) type |= RIGHT_OPEN;
		this.type = type;

		if (min == null || max == null) return;

		if (min.compareTo(max) > 0)
			throw new IllegalArgumentException("lower limit exceeds upper limit");
	}

	
	/**
	 * Creates a an <code>Interval</code> from a string in interval notation, or a
	 * fixed value.
	 * 
	 * @throws IllegalArgumentException
	 */
	public static <T extends Comparable> Interval<T> from(String interval, Class<T> cls) {
		
		T min = null, max = null; // null means unbounded, or * in interval notation
		
		interval = (interval == null) ? "" : interval.trim();
		if (interval.isEmpty())
			throw new IllegalArgumentException("no interval specified");
		
		Matcher matcher = PATTERN.matcher(interval);

		if (! matcher.matches()) { // no match; could be a fixed value
			
			if (interval.contains(".."))
				throw new IllegalArgumentException("invalid interval notation");
			try {
				min = cls.getConstructor(String.class).newInstance(interval);
			} catch (Exception e) {
				throw new IllegalArgumentException("invalid limiting value", e);
			}
			return new Interval<T>(min, min, CLOSED);
		}

		int type = CLOSED;
		if (interval.startsWith("(")) type |= LEFT_OPEN;
		if (interval.endsWith(")")) type |= RIGHT_OPEN;

		String lowerlimit = matcher.group(1).trim();
		try {
			if (! lowerlimit.equals("*")) 
				min = cls.getConstructor(String.class).newInstance(lowerlimit);
		} catch (Exception e) {
			throw new IllegalArgumentException("invalid lower limit", e);
		}
		
		String upperlimit = matcher.group(2).trim();
		try {
			if (! upperlimit.equals("*")) 
				max = cls.getConstructor(String.class).newInstance(upperlimit);
		} catch (Exception e) {
			throw new IllegalArgumentException("invalid upper limit", e);
		}
			
		return new Interval<T>(min, max, type);
	}

	
	/**
	 * Returns 0 if the supplied value lies within the interval, -1 if it subceeds
	 * the lower interval limit, and 1 if it exceeds the upper limit. The value must
	 * never be <code>null</code>.
	 */
	@SuppressWarnings({ "unchecked", "hiding" })
	public <T extends Comparable> int contains(T value) {
		
		int comp;
		if (min != null) {
			comp = value.compareTo(min);
			if (comp < 0 || comp == 0 && (type & LEFT_OPEN) > 0) return -1;
		}
		if (max != null) {
			comp = value.compareTo(max);
			if (comp > 0 || comp == 0 && (type & RIGHT_OPEN) > 0) return 1;
		}
		return 0;
	}
	
	
	/** Returns the interval as a string in interval notation. */
	public String toString() {
		
		if (min == max && min != null) return min.toString(); // fixed value
		
		return ((type & LEFT_OPEN) > 0 ? "(" : "[") + (min == null ? "*" : min)
			+ ".." + (max == null ? "*" : max) + ((type & RIGHT_OPEN) > 0 ? ")" : "]");
	}
}

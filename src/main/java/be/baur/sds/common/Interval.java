package be.baur.sds.common;

import java.lang.reflect.InvocationTargetException;
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
public class Interval <T extends Comparable> {

	/** Lower limit */
	public final T lower;
	/** Upper limit */
	public final T upper;
	/** Interval type */
	public final int type;

	/** Interval types */
	public final static int CLOSED 		= 	0b00;
	public final static int LEFT_OPEN 	= 	0b10;
	public final static int RIGHT_OPEN 	= 	0b01;
	public final static int OPEN 		= 	0b11;
	
	// Precompiled pattern that matches an interval notation
	private static final String LB="\\[\\(";
	private static final String RB="\\)\\]";
	private static final Pattern PATTERN = 
			Pattern.compile("["+LB+"]([^"+LB+"]+)\\.\\.([^"+RB+"]+)["+RB+"]");

	
	/**
	 * Construct an <code>Interval</code> from lower and upper limit points and a
	 * type. The <code>lower</code> limit can never exceed the <code>upper</code>
	 * upper. A <code>null</code> value for a limit point means that the interval
	 * is unbounded (and by definition open) on that side.
	 * 
	 * @throws IllegalArgumentException
	 */
	@SuppressWarnings("unchecked")
	public Interval(T lower, T upper, int type) {

		this.lower = lower; this.upper = upper;

		if (lower == null) type |= LEFT_OPEN;
		if (upper == null) type |= RIGHT_OPEN;
		this.type = type;

		if (lower == null || upper == null) return;

		if (lower.compareTo(upper) > 0)
			throw new IllegalArgumentException("lower limit exceeds upper limit");
	}

	
	/**
	 * Factory method to create a an <code>Interval</code> from a string in interval
	 * notation, or a fixed value.
	 * 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IllegalArgumentException
	 */
	public static <T extends Comparable> Interval<T> from(String interval, Class<T> cls) throws Exception {
		
		T lower = null, upper = null;
		
		interval = (interval == null) ? "" : interval.trim();
		if (interval.isEmpty())
			throw new IllegalArgumentException("no interval specified");
		
		Matcher matcher = PATTERN.matcher(interval);

		if (! matcher.matches()) { // no match; could be a fixed value
			
			if (interval.contains(".."))
				throw new IllegalArgumentException("invalid interval notation");
			try {
				lower = cls.getConstructor(String.class).newInstance(interval);
			} catch (Exception e) {
				throw new IllegalArgumentException("invalid limiting value", e);
			}
			return new Interval<T>(lower, lower, CLOSED);
		}

		int type = CLOSED;
		if (interval.startsWith("(")) type |= LEFT_OPEN;
		if (interval.endsWith(")")) type |= RIGHT_OPEN;

		String lowerlimit = matcher.group(1).trim();
		try {
			if (! lowerlimit.equals("*")) 
				lower = cls.getConstructor(String.class).newInstance(lowerlimit);
		} catch (Exception e) {
			throw new IllegalArgumentException("invalid lower limit", e);
		}
		
		String upperlimit = matcher.group(2).trim();
		try {
			if (! upperlimit.equals("*")) 
				upper = cls.getConstructor(String.class).newInstance(upperlimit);
		} catch (Exception e) {
			throw new IllegalArgumentException("invalid upper limit", e);
		}
			
		return new Interval<T>(lower, upper, type);
	}

	
	/** Returns the interval as a string in interval notation. */
	public String toString() {
		
		if (lower == upper && lower != null) return lower.toString(); // fixed value
		
		return ((type & LEFT_OPEN) > 0 ? "(" : "[") + (lower == null ? "*" : lower)
			+ ".." + (upper == null ? "*" : upper) + ((type & RIGHT_OPEN) > 0 ? ")" : "]");
	}

}

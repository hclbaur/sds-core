package be.baur.sds.common;

import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An {@code Interval} represents a generic interval, with a lower and an upper
 * limiting value. If a limit is inclusive, the interval is called <i>closed</i>
 * on that particular side, and <i>open</i> when it is exclusive. In interval
 * notation, parentheses are used for open intervals, and square brackets for
 * closed ones.
 * 
 * <pre>
 * <code>
 * (a..b)	Open
 * [a..b]	Closed
 * (a..b]	Left open
 * [a..b)	Right open
 * (a..*)	Left open, right unbounded
 * [a..*)	Right unbounded
 * (*..b)	Right open, left unbounded
 * (*..b]	Left unbounded
 * (*..*)	Unbounded
 *   a  	Degenerate, equivalent to [a..a]
 * </code>
 * </pre>
 * 
 * See also {@link NaturalInterval}.
 */
@SuppressWarnings("rawtypes")
public final class Interval <T extends Comparable> {

	/** Interval types */
	public final static int CLOSED 		= 	0b00;
	public final static int LEFT_OPEN 	= 	0b10;
	public final static int RIGHT_OPEN 	= 	0b01;
	public final static int OPEN 		= 	0b11;
	
	/** All values in the range minus infinity to infinity, e.g. {@code (*..*)}. */
	public static final Interval<Comparable> MIN_TO_MAX = new Interval<>(null, null, OPEN);
	
	// Private pre-compiled pattern to match an interval notation
	private static final String LB="\\[\\(";
	private static final String RB="\\)\\]";
	private static final Pattern PATTERN = 
		Pattern.compile("["+LB+"]([^"+LB+"]+)\\.\\.([^"+RB+"]+)["+RB+"]");

	
	/** Lower interval limit */
	public final T min;
	/** Upper interval limit */
	public final T max;
	/** The type of interval */
	public final int type;


	/*
	 * Creates an interval from two limit points and a type. The minimum should
	 * never exceed the maximum value. A null value for a limit means that the
	 * interval is unbounded (and open) on that side.
	 * 
	 * @param min  the lower limit, may be null
	 * @param max  the upper limit, may be null
	 * @param type the interval type
	 * @throws IllegalArgumentException if the lower exceeds the upper limit
	 */
	@SuppressWarnings("unchecked")
	private Interval(T min, T max, int type) {

		if (type < CLOSED || type > OPEN)
			throw new IllegalArgumentException("invalid interval type " + type);

		if (min == null) type |= LEFT_OPEN;
		if (max == null) type |= RIGHT_OPEN;
		this.type = type;

		this.min = min; this.max = max;
		if (min == null || max == null) return;

		if (min.compareTo(max) > 0)
			throw new IllegalArgumentException("lower limit exceeds upper limit");
	}


	/**
	 * Creates an interval from two limit points and a type.
	 * 
	 * @param <T>  a value type
	 * @param min  the lower limit, null means unbounded
	 * @param max  the upper limit, null means unbounded
	 * @param type one of OPEN, LEFT_OPEN, RIGHT_OPEN or CLOSED
	 * @return an Interval
	 * @throws IllegalArgumentException for an invalid interval
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Comparable> Interval<T> from(T min, T max, int type) {
		
		if (min == null && max == null) return (Interval<T>) MIN_TO_MAX;
		return new Interval<T>(min, max, type);
	}

	
	/**
	 * Creates an interval from a string in interval notation. This method requires
	 * a constructor Function that accepts a string and returns a value of the type
	 * appropriate for this interval. For example:
	 * <pre>
	 * Interval.from("[0..42]", IntegerType.VALUE_CONSTRUCTOR);
	 * </pre>
	 * @param <T>      a value type
	 * @param interval an interval string, not null or empty
	 * @param func     a constructor Function accepting a string
	 * @return an Interval
	 * @throws IllegalArgumentException if the specified interval is invalid
	 */
	public static <T extends Comparable> Interval<T> from(String interval, Function<String, T> func) {
		/*
		 * This function used to accept a class and use reflection to create a value from a string:
		 * value = class.getConstructor(String.class).newInstance(string);
		 * Now it uses a functional interface, so it no longer depends on a string constructor.
		 */
		T min = null, max = null; // null means unbounded, or * in interval notation
		
		interval = (interval == null) ? "" : interval.trim();
		if (interval.isEmpty())
			throw new IllegalArgumentException("no interval specified");
		
		Matcher matcher = PATTERN.matcher(interval);

		if (! matcher.matches()) { // no match; could be a degenerate interval
			
			if (interval.contains("..")) // but has dots, so must be invalid
				throw new IllegalArgumentException("invalid interval notation");
			try {
				min = func.apply(interval);
			} catch (Exception e) {
				throw new IllegalArgumentException("invalid limiting value", e);
			}
			return from(min, min, CLOSED);
		}

		int type = CLOSED;
		if (interval.startsWith("(")) type |= LEFT_OPEN;
		if (interval.endsWith(")")) type |= RIGHT_OPEN;

		String lowerlimit = matcher.group(1).trim();
		try {
			if (! lowerlimit.equals("*")) 
				min = func.apply(lowerlimit);
		} catch (Exception e) {
			throw new IllegalArgumentException("invalid lower limit", e);
		}
		
		String upperlimit = matcher.group(2).trim();
		try {
			if (! upperlimit.equals("*")) 
				max = func.apply(upperlimit);
		} catch (Exception e) {
			throw new IllegalArgumentException("invalid upper limit", e);
		}
			
		return from(min, max, type);
	}

	
	/**
	 * Checks if a value lies within this interval. This method returns 0 if the
	 * specified value is contained within the interval limits, -1 if it subceeds
	 * the lower limit, and 1 if it exceeds the upper limit. The value must not be
	 * null.
	 * 
	 * @param <T>   a value type
	 * @param value the value to be evaluated, not null
	 * @return -1, 0 or 1
	 */
	@SuppressWarnings({ "unchecked", "hiding" })
	public <T extends Comparable> int contains(T value) {
		
		Objects.requireNonNull(value, "value must not be null");
		
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
	
	
	/**
	 * Returns this interval as a string in interval notation.
	 * 
	 * @return an interval string
	 */
	@Override
	public String toString() {
		
		if (min == max && min != null) return min.toString(); // fixed value
		
		return ((type & LEFT_OPEN) > 0 ? "(" : "[") + (min == null ? "*" : min)
			+ ".." + (max == null ? "*" : max) + ((type & RIGHT_OPEN) > 0 ? ")" : "]");
	}
}

package be.baur.sds.types;

import java.util.Objects;
import java.util.function.Function;

import be.baur.sds.DataType;
import be.baur.sds.common.Interval;

/**
 * A {@code ComparableType} defines an SDA node with a value that lies within an
 * interval and can be numerically compared to other values of the same type. It
 * is used to implement the native integer, decimal and date(time) data types,
 * and can be used to add more exotic ones, such as a {@code GMonthDay}.
 * 
 * @see Interval
 */
public abstract class ComparableType <T extends Comparable<? super T>> extends DataType {

	private Interval<?> range = Interval.MIN_TO_MAX; // default is to allow any value


	/**
	 * Creates the type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public ComparableType(String name) {
		super(name);
	}
	
	
	/**
	 * Returns the interval of allowed values. The default value is {@code (*..*)},
	 * which means any value is allowed. This method never returns null.
	 * 
	 * @return an interval, never null
	 */
	public Interval<?> getInterval() {
		return range;
	}


	/**
	 * Sets the the interval of allowed values. This method does not accept null.
	 * 
	 * @param range an interval, not null
	 */
	public void setInterval(Interval<T> range) {
		this.range = Objects.requireNonNull(range, "range must not be null");
	}
	
	
	/**
	 * Returns a constructor function that accepts a string and returns an instance
	 * of a value appropriate for this data type.
	 * <p>
	 * Note: when applied, the function may throw an exception if the argument is
	 * not within the lexical space for this type (that is, when the supplied string
	 * cannot be converted to a valid value).
	 * 
	 * @return a Function
	 */
	public abstract Function<String, T> valueConstructor();
}

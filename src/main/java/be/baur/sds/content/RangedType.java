package be.baur.sds.content;

import java.util.Objects;
import java.util.function.Function;

import be.baur.sds.DataType;
import be.baur.sds.common.Interval;

/**
 * A {@code RangedType} defines an SDA node with a value that lies within an
 * interval, like an integer, a decimal or a date(time).
 * 
 * @see Interval
 */
public abstract class RangedType <T extends Comparable<? super T>> extends DataType {

	private Interval<?> range = Interval.MIN_TO_MAX; // default is to allow any value
	
	
	/**
	 * Creates the type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public RangedType(String name) {
		super(name);
	}
	
	
	/**
	 * Returns the interval of allowed values. The default value is {@code (*..*)},
	 * which means any value is allowed. This method never returns null.
	 * 
	 * @return an interval, never null
	 */
	public Interval<?> getRange() {
		return range;
	}


	/**
	 * Sets the the interval of allowed values. This method does not accept null.
	 * 
	 * @param range an interval, not null
	 */
	public void setRange(Interval<T> range) {
		this.range = Objects.requireNonNull(range, "range must not be null");
	}
	

	/**
	 * Returns the Class of a value for this type.
	 * 
	 * @return a Class
	 */
	public abstract Class<T> valueClass();
	
	
	/**
	 * Returns a constructor function that accepts a string and returns an instance
	 * of a value appropriate for this data type.
	 * <p>
	 * Note: when applied, the function may throw an exception if the argument is
	 * not within the lexical space for this type (that is, when the supplied string
	 * cannot be converted to a valid value).
	 * 
	 * @return a Function
	 * @throws RuntimeException
	 */
	public abstract Function<String, T> valueConstructor();
}

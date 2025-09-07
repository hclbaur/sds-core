package be.baur.sds.types;

import java.util.Objects;

import be.baur.sds.DataNodeType;
import be.baur.sds.common.Interval;

/**
 * A {@code ComparableNodeType} defines an SDA node with a value that lies
 * within an interval and can be (numerically) compared to other values of the
 * same type. It is used to implement the native integer, decimal and date(time)
 * node types, and can be used to add more exotic ones, like {@code GMonthDay}.
 */
public abstract class ComparableNodeType <T extends Comparable<? super T>> extends DataNodeType <T> {

	private Interval<?> range = Interval.MIN_TO_MAX; // default is to allow any value


	/**
	 * Creates a comparable node type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public ComparableNodeType(String name) {
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

}

package be.baur.sds.content;

import java.util.Objects;

import be.baur.sds.DataType;
import be.baur.sds.common.Interval;

/**
 * A <code>RangedType</code> represents an SDA node with a value that lies
 * within an interval, like an integer, a decimal or a date(time).<br>
 * See also {@link Interval}.
 */
public abstract class RangedType <T extends Comparable<?>> extends DataType {

	private Interval<?> range = Interval.MIN_TO_MAX; // default any value is allowed.
	
	
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
	public void setRange(Interval<?> range) {
		this.range = Objects.requireNonNull(range, "range must not be null");
	}
}

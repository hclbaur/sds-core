package be.baur.sds.content;

import be.baur.sds.MixedType;
import be.baur.sds.common.Interval;

/**
 * A <code>RangedType</code> represents an SDA node with a value that lies
 * within an interval, like an integer, a decimal or a date(time).<br>
 * See also {@link Interval}.
 */
public abstract class RangedType <T extends Comparable<?>> extends MixedType {

	private Interval<?> range = null; // Range null means: any value is allowed.
	
	
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
	 * Returns the interval of allowed values. This method returns null if any value
	 * is allowed.
	 * 
	 * @return an interval, may be null
	 */
	public Interval<?> getRange() {
		return range;
	}


	/**
	 * Sets the the interval of allowed values. An interval of null means any length
	 * is allowed.
	 * 
	 * @param range an interval, may be null
	 */
	public void setRange(Interval<?> range) {
		this.range = range;
	}
}

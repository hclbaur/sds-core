package be.baur.sds.content;

import be.baur.sds.SimpleType;
import be.baur.sds.common.Interval;

/**
 * A <code>RangedType</code> is an abstract simple type representing a SDA node
 * with a value that lies within a particular {@link Interval}, like an integer,
 * a double, a date, or basically any {@link Comparable}.
 */
@SuppressWarnings("rawtypes")
public abstract class RangedType <T extends Comparable> extends SimpleType {

	private Interval<T> range = null; // Range null means: any value of <T> is allowed.
	
	
	/** Create a ranged type. */
	public RangedType(String name) {
		super(name);
	}
	
	/** Get the range interval. Default value is <code>null</code>. */
	public Interval<T> getRange() {
		return range;
	}

	/** Set the range interval. */
	public void setRange(Interval<T> range) {
		this.range = range;
	}
}

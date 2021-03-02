package be.baur.sds.content;

import be.baur.sds.SimpleType;
import be.baur.sds.common.Interval;

/**
 * A <code>RangedType</code> is an abstract simple type representing a SDA node
 * with a value that lies within a particular {@link Interval}, like an integer,
 * a double, a date, or in theory any {@link Comparable}.
 */
public abstract class RangedType <T extends Comparable<?>> extends SimpleType {

	private Interval<?> range = null; // Range null means: any value is allowed.
	
	/** Create a ranged type. */
	public RangedType(String name) {
		super(name);
	}
	
	/** Get the range interval. Default value is <code>null</code>. */
	public Interval<?> getRange() {
		return range;
	}

	/** Set the range interval. */
	public void setRange(Interval<?> range) {
		this.range = range;
	}
}

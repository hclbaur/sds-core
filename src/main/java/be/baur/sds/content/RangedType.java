package be.baur.sds.content;

import be.baur.sds.NodeType;
import be.baur.sds.common.Interval;

/**
 * A <code>RangedType</code> is an abstract simple type representing an SDA node
 * with a value that lies within a particular {@link Interval}, like an integer,
 * a decimal, a date, etc..
 */
public abstract class RangedType <T extends Comparable<?>> extends NodeType {

	private Interval<?> range = null; // Range null means: any value is allowed.
	
	
	/** Creates the ranged type with the supplied <code>name</code>. */
	public RangedType(String name) {
		super(name);
	}
	
	
	/** Returns the range interval. Default value is <code>null</code>. */
	public Interval<?> getRange() {
		return range;
	}

	
	/** Sets the range interval. */
	public void setRange(Interval<?> range) {
		this.range = range;
	}
}

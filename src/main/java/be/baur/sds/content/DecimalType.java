package be.baur.sds.content;

import be.baur.sds.common.Content;

/**
 * A <code>DecimalType</code> is a {@link RangedType} representing an SDA node with decimal content.
 */
public final class DecimalType extends RangedType<Double> {


	/** Creates a decimal type with the supplied <code>name</code>. */
	public DecimalType(String name) {
		super(name);
	}
	
	public Content getContentType() { 
		return Content.DECIMAL; 
	}
}

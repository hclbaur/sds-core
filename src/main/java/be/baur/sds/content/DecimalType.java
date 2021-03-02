package be.baur.sds.content;

import be.baur.sds.common.Content;

/**
 * A <code>DecimalType</code> is a {@link RangedType} representing an SDA node with decimal content.
 */
public class DecimalType extends RangedType<Double> {


	/** Create a decimal type. */
	public DecimalType(String name) {
		super(name);
	}
	
	public Content getContentType() { 
		return Content.DECIMAL; 
	}
}

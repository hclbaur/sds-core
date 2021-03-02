package be.baur.sds.content;

import be.baur.sds.common.Content;

/**
 * A <code>IntegerType</code> is a {@link RangedType} representing an SDA node with integer content.
 */
public class IntegerType extends RangedType<Integer> {


	/** Create an integer type. */
	public IntegerType(String name) {
		super(name);
	}
	
	public Content getContentType() { 
		return Content.INTEGER; 
	}
}

package be.baur.sds.content;

import be.baur.sds.common.Content;

/**
 * A <code>IntegerType</code> is a {@link RangedType} representing an SDA node with integer content.
 */
public final class IntegerType extends RangedType<Integer> {


	/** Creates an integer type with the supplied <code>name</code>. */
	public IntegerType(String name) {
		super(name);
	}
	
	
	public Content getContentType() { 
		return Content.INTEGER; 
	}
}

package be.baur.sds;

import be.baur.sds.serialization.SchemaParser;
import be.baur.sds.serialization.SDSParser;
import be.baur.sds.validation.SDAValidator;
import be.baur.sds.validation.Validator;

/**
 * This class defines static constants and utility methods.
 */
public final class SDS {
	
	private SDS() {} // cannot construct this

	
	/**
	 * Returns a new instance of the default SDS parser.
	 * 
	 * @return an {@link SDSParser}
	 */
	public static SchemaParser parser() {
		return new SDSParser();
	}
	
	
	/**
	 * Returns a new instance of the default SDS validator.
	 * 
	 * @return an {@link SDAValidator}
	 */
	public static Validator validator() {
		return new SDAValidator();
	}
	
	
//	/** Convenience method that throws an exception if the argument is null.
//	 * 
//	 * @param obj the argument to check
//	 * @param msg the exception message
//	 * @return the original argument
//	 */
//	public static <T> T requireNonNull(T obj, String msg) {
//	    if (obj == null)
//	    	throw new IllegalArgumentException(msg);
//	    return obj;
//	}
}

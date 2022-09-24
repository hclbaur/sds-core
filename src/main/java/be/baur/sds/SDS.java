package be.baur.sds;

import be.baur.sda.serialization.SDAParser;
import be.baur.sds.serialization.Parser;
import be.baur.sds.serialization.SDSParser;
import be.baur.sds.validation.SDAValidator;
import be.baur.sds.validation.Validator;

/** This is a general class to define some constants and static convenience methods. */
public final class SDS {
	
	private SDS() {} // cannot construct this

	
	/**
	 * Returns a new instance of the default SDS parser.
	 * 
	 * @return an {@link SDSParser}
	 */
	public static Parser parser() {
		return new SDSParser();
	}
	
	
	/**
	 * Returns a new instance of the default SDS validator.
	 * 
	 * @return an {@link SDSValidator}
	 */
	public static Validator validator() {
		return new SDAValidator();
	}
}

package be.baur.sds;

import be.baur.sds.serialization.Parser;
import be.baur.sds.serialization.SDSParser;
import be.baur.sds.validation.SDAValidator;
import be.baur.sds.validation.Validator;

/** This is a general class to define some constants and static convenience methods. */
public final class SDS {
	

	/** Returns a new instance of the default SDS parser. */
	public static Parser parser() {
		return new SDSParser();
	}
	
	
	/** Returns a new instance of the default SDA validator. */
	public static Validator validator() {
		return new SDAValidator();
	}
}

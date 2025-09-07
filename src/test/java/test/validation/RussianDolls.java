package test.validation;

import be.baur.sda.DataNode;
import be.baur.sda.SDA;
import be.baur.sds.SDS;
import be.baur.sds.validation.Validator;
import be.baur.sds.validation.Validator.Errors;
import test.Test;

public final class RussianDolls {

	/* 
	 * Parsing and validation of self- and cross- referencing types.
	 */
	public static void main(String[] args) throws Exception {

		Test t = new Test(s -> {
			return s;
		});
		

		DataNode doc = SDA.parse(Test.getResourceFile("/russiandolls.sda"));
		Validator validator = SDS.parse(Test.getResourceFile("/russiandolls.sds")).newValidator();

		Errors errors = validator.validate(doc);
		//for (be.baur.sds.validation.Error error : errors) System.out.println(error.toString());

		t.ts1("S01", errors.isEmpty() ? "" : errors.get(0).toString(), "");
		
		doc = SDA.parse(Test.getResourceFile("/russiandolls2.sda"));
		validator = SDS.parse(Test.getResourceFile("/russiandolls2.sds")).newValidator();

		errors = validator.validate(doc);
		//for (be.baur.sds.validation.Error error : errors) System.out.println(error.toString());

		t.ts1("S02", errors.isEmpty() ? "" : errors.get(0).toString(), "");
		
		doc = SDA.parse(Test.getResourceFile("/russiandolls3.sda"));
		validator = SDS.parse(Test.getResourceFile("/russiandolls3.sds")).newValidator();

		errors = validator.validate(doc);
		//for (be.baur.sds.validation.Error error : errors) System.out.println(error.toString());

		t.ts1("S03", errors.isEmpty() ? "" : errors.get(0).toString(), "");
	}
}

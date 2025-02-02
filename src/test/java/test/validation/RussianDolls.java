package test.validation;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

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
		

		DataNode doc = SDA.parse(new File(RussianDolls.class.getResource("/russiandolls.sda").getFile()));

		InputStream sds = RussianDolls.class.getResourceAsStream("/russiandolls.sds");
		Validator validator = SDS.parse(new InputStreamReader(sds, "UTF-8")).newValidator();

		Errors errors = validator.validate(doc);
		//for (be.baur.sds.validation.Error error : errors) System.out.println(error.toString());

		t.ts1("S01", errors.isEmpty() ? "" : errors.get(0).toString(), "");
		
		doc = SDA.parse(new File(RussianDolls.class.getResource("/russiandolls2.sda").getFile()));

		sds = RussianDolls.class.getResourceAsStream("/russiandolls2.sds");
		validator = SDS.parse(new InputStreamReader(sds, "UTF-8")).newValidator();

		errors = validator.validate(doc);
		//for (be.baur.sds.validation.Error error : errors) System.out.println(error.toString());

		t.ts1("S02", errors.isEmpty() ? "" : errors.get(0).toString(), "");
		
		doc = SDA.parse(new File(RussianDolls.class.getResource("/russiandolls3.sda").getFile()));

		sds = RussianDolls.class.getResourceAsStream("/russiandolls3.sds");
		validator = SDS.parse(new InputStreamReader(sds, "UTF-8")).newValidator();

		errors = validator.validate(doc);
		//for (be.baur.sds.validation.Error error : errors) System.out.println(error.toString());

		t.ts1("S03", errors.isEmpty() ? "" : errors.get(0).toString(), "");
	}
}

package test.validation;

import java.io.InputStream;
import java.io.InputStreamReader;

import be.baur.sda.DataNode;
import be.baur.sda.SDA;
import be.baur.sds.SDS;
import be.baur.sds.validation.ErrorList;
import be.baur.sds.validation.Validator;
import test.Test;

public final class RussianDolls {

	/* 
	 * Parsing and validation of self- and cross- referencing types.
	 */
	public static void main(String[] args) throws Exception {

		Test t = new Test(s -> {
			return s;
		});
		
		InputStream sda = RussianDolls.class.getResourceAsStream("/russiandolls.sda");
		DataNode document = SDA.parse(new InputStreamReader(sda, "UTF-8"));

		InputStream sds = RussianDolls.class.getResourceAsStream("/russiandolls.sds");
		Validator validator = SDS.parse(new InputStreamReader(sds, "UTF-8")).newValidator();

		ErrorList errors = validator.validate(document);
		//for (be.baur.sds.validation.Error error : errors) System.out.println(error.toString());

		t.ts1("S01", errors.isEmpty() ? "" : errors.get(0).toString(), "");
		
		sda = RussianDolls.class.getResourceAsStream("/russiandolls2.sda");
		document = SDA.parse(new InputStreamReader(sda, "UTF-8"));

		sds = RussianDolls.class.getResourceAsStream("/russiandolls2.sds");
		validator = SDS.parse(new InputStreamReader(sds, "UTF-8")).newValidator();

		errors = validator.validate(document);
		//for (be.baur.sds.validation.Error error : errors) System.out.println(error.toString());

		t.ts1("S02", errors.isEmpty() ? "" : errors.get(0).toString(), "");
		
		sda = RussianDolls.class.getResourceAsStream("/russiandolls3.sda");
		document = SDA.parse(new InputStreamReader(sda, "UTF-8"));

		sds = RussianDolls.class.getResourceAsStream("/russiandolls3.sds");
		validator = SDS.parse(new InputStreamReader(sds, "UTF-8")).newValidator();

		errors = validator.validate(document);
		//for (be.baur.sds.validation.Error error : errors) System.out.println(error.toString());

		t.ts1("S03", errors.isEmpty() ? "" : errors.get(0).toString(), "");
	}
}

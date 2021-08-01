package test.validation;

import java.io.InputStream;
import java.io.InputStreamReader;

import be.baur.sda.Node;
import be.baur.sda.SDA;
import be.baur.sds.SDS;
import be.baur.sds.Schema;
import be.baur.sds.validation.ErrorList;
import test.Test;

public final class RussianDolls {
	
	private static be.baur.sda.serialization.Parser parser = SDA.parser();

	/* 
	 * Parsing and validation of self- and cross- referencing types.
	 */
	public static void main(String[] args) throws Exception {

		Test t = new Test(s -> {
			return s;
		});
		
		InputStream sda = RussianDolls.class.getResourceAsStream("/russiandolls.sda");
		Node document = parser.parse(new InputStreamReader(sda, "UTF-8"));

		InputStream sds = RussianDolls.class.getResourceAsStream("/russiandolls.sds");
		Schema schema = SDS.parser().parse(new InputStreamReader(sds, "UTF-8"));

		ErrorList errors = SDS.validator().validate(document, schema, null);
		//for (be.baur.sds.validation.Error error : errors) System.out.println(error.toString());

		t.ts1("S01", errors.isEmpty() ? "" : errors.get(0).toString(), "");
		
		sda = RussianDolls.class.getResourceAsStream("/russiandolls2.sda");
		document = parser.parse(new InputStreamReader(sda, "UTF-8"));

		sds = RussianDolls.class.getResourceAsStream("/russiandolls2.sds");
		schema = SDS.parser().parse(new InputStreamReader(sds, "UTF-8"));

		errors = SDS.validator().validate(document, schema, null);
		//for (be.baur.sds.validation.Error error : errors) System.out.println(error.toString());

		t.ts1("S02", errors.isEmpty() ? "" : errors.get(0).toString(), "");
		
		sda = RussianDolls.class.getResourceAsStream("/russiandolls3.sda");
		document = parser.parse(new InputStreamReader(sda, "UTF-8"));

		sds = RussianDolls.class.getResourceAsStream("/russiandolls3.sds");
		schema = SDS.parser().parse(new InputStreamReader(sds, "UTF-8"));

		errors = SDS.validator().validate(document, schema, null);
		//for (be.baur.sds.validation.Error error : errors) System.out.println(error.toString());

		t.ts1("S03", errors.isEmpty() ? "" : errors.get(0).toString(), "");
	}
}

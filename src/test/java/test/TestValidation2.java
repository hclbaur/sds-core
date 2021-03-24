package test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import be.baur.sda.Node;
import be.baur.sda.SDA;
import be.baur.sds.Schema;
import be.baur.sds.serialization.Parser;
import be.baur.sds.validation.Error;
import be.baur.sds.validation.Validator;

public final class TestValidation2 {
	
	private static be.baur.sda.serialization.Parser parser = SDA.parser();

	public static void main(String[] args) throws Exception {

		Test t = new Test(s -> {
			return s;
		});
		
		InputStream sda = TestValidation2.class.getResourceAsStream("/addressbook.sda");
		Node document = parser.parse(new InputStreamReader(sda, "UTF-8"));

		InputStream sds = TestValidation2.class.getResourceAsStream("/addressbook.sds");
		Schema schema = Parser.parse(new InputStreamReader(sds, "UTF-8"));

		ArrayList<Error> errors = Validator.validate(document, schema);
		//for (Error error : errors) System.out.println(error.toString());

		t.t1("F01", errors.get(0).toString(), "/addressbook/contact[1]/person/about: expecting 'about' to be a complex type");
		t.t1("F02", errors.get(1).toString(), "/addressbook/contact[1]/address/housenumber: got 'housenumber', but expected 'streetname' or 'postalcode' instead");
		t.t1("F03", errors.get(2).toString(), "/addressbook/contact[1]/email[3]: 'email' not expected in 'contact'");
		t.t1("F04", errors.get(3).toString(), "/addressbook/contact[2]/person/about: content missing at end of 'about'; expecting any node");
		t.t1("F05", errors.get(4).toString(), "/addressbook/contact[2]/address/altitude: 'altitude' not expected in 'address'");
		t.t1("F06", errors.get(5).toString(), "/addressbook/contact[2]/twitter: got 'twitter', but expected 'phone' or 'email' instead");
		t.t1("F07", errors.get(6).toString(), "/addressbook/contact[3]/address/longitude: 'longitude' not expected in 'address'");
		t.t1("F08", errors.get(7).toString(), "/addressbook/contact[3]/address/latitude: 'latitude' not expected in 'address'");
		t.t1("F09", errors.get(8).toString(), "/addressbook/contact[3]/twitter: 'twitter' not expected in 'contact'");
		t.t1("F10", errors.get(9).toString(), "/addressbook/contact[4]/address/longitude: 'longitude' not expected in 'address'");
		t.t1("F11", errors.get(10).toString(), "/addressbook/contact[4]: content missing at end of 'contact'; expecting 'phone' or 'email'");
		t.t1("F12", errors.get(11).toString(), "/addressbook/contact[5]/address: content missing at end of 'address'; expecting 'longitude'");
		t.t1("F13", errors.get(12).toString(), "/addressbook/contact[5]: content missing at end of 'contact'; expecting 'phone' or 'email'");
		t.t1("F14", errors.get(13).toString(), "/addressbook/contact[6]: content missing at end of 'contact'; expecting 'phone' or 'email'");
	}
}

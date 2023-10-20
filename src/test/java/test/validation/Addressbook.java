package test.validation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import be.baur.sda.SDA;
import be.baur.sda.DataNode;
import be.baur.sda.serialization.Parser;
import be.baur.sds.SDS;
import be.baur.sds.Schema;
import be.baur.sds.validation.Error;
import be.baur.sds.validation.ErrorList;
import test.Test;

public final class Addressbook {
	
	private static Parser<DataNode> parser = SDA.parser();

	/* 
	 * Parsing and validation of complex types and model groups.
	 */
	public static void main(String[] args) throws Exception {

		Test t = new Test(s -> {
			return s;
		});
		
		InputStream sda = Addressbook.class.getResourceAsStream("/addressbook.sda");
		DataNode document = parser.parse(new InputStreamReader(sda, "UTF-8"));

		InputStream sds = Addressbook.class.getResourceAsStream("/addressbook.sds");
		Schema schema = SDS.parser().parse(new InputStreamReader(sds, "UTF-8"));

		ErrorList errors = SDS.validator().validate(document, schema, "contact");
		t.ts1("F01", errors.get(0) + "", "/addressbook: got 'addressbook', but 'contact' was expected");
		errors = SDS.validator().validate(document, schema, null);
		//for (Error error : errors) System.out.println(error.toString());
		Iterator<Error> e = errors.iterator();
		
		t.ts1("F02", e.next() + "", "/addressbook/contact[1]/person/about: only complex content is expected for node 'about'");
		t.ts1("F03", e.next() + "", "/addressbook/contact[1]/address/housenumber: got 'housenumber', but 'streetname' or 'postalcode' was expected");
		t.ts1("F04", e.next() + "", "/addressbook/contact[1]/email[2]: 'email' was not expected in 'contact'");
		t.ts1("F05", e.next() + "", "/addressbook/contact[2]/person/about: content missing at end of 'about'; expected any node");
		t.ts1("F06", e.next() + "", "/addressbook/contact[2]/address/height: 'height' was not expected in 'address'");
		t.ts1("F07", e.next() + "", "/addressbook/contact[2]/twitter: got 'twitter', but 'phone' or 'email' was expected");
		t.ts1("F08", e.next() + "", "/addressbook/contact[3]/address/longitude: 'longitude' was not expected in 'address'");
		// ABUNDANT t.ts1("F08", e.next() + "", "/addressbook/contact[3]/address/latitude: 'latitude' was not expected in 'address'");
		t.ts1("F09", e.next() + "", "/addressbook/contact[3]/twitter: got 'twitter', but 'phone' was expected");
		t.ts1("F10", e.next() + "", "/addressbook/contact[4]/address/longitude: 'longitude' was not expected in 'address'");
		t.ts1("F11", e.next() + "", "/addressbook/contact[4]: content missing at end of 'contact'; expected 'phone'");
		t.ts1("F12", e.next() + "", "/addressbook/contact[5]/address: content missing at end of 'address'; expected 'longitude'");
		t.ts1("F13", e.next() + "", "/addressbook/contact[5]/phone[3]: 'phone' was not expected in 'contact'");
		t.ts1("F14", e.next() + "", "/addressbook/contact[6]/address/postalcode: got 'postalcode', but 'housenumber' was expected");
		t.ts1("F15", e.next() + "", "/addressbook/contact[6]: content missing at end of 'contact'; expected 'phone' or 'email'");
		t.ts1("F16", e.next() + "", "/addressbook/contact[7]/person: empty value not allowed; 'person' is not nullable");
		t.ts1("F16", e.next() + "", "/addressbook/contact[7]/address: value 'nowhere' does not match pattern 'home|work'");
		t.ts1("F99", e.hasNext() + "", "false");
	}
}

package test.validation;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import be.baur.sda.DataNode;
import be.baur.sda.SDA;
import be.baur.sds.SDS;
import be.baur.sds.Schema;
import be.baur.sds.validation.Validator;
import be.baur.sds.validation.Validator.Errors;
import test.Test;

public final class Addressbook {

	/* 
	 * Parsing and validation of complex types and model groups.
	 */
	public static void main(String[] args) throws Exception {

		Test t = new Test(s -> {
			return s;
		});
		

		DataNode doc = SDA.parse(new File(Addressbook.class.getResource("/addressbook.sda").getFile()));

		InputStream sds = Addressbook.class.getResourceAsStream("/addressbook.sds");
		Schema schema = SDS.parse(new InputStreamReader(sds, "UTF-8"));
		Validator validator = schema.newValidator();

		// validate against existing type but not what we expect
		Errors errors = validator.validate(doc, "contact");
		t.ts1("F01", errors.get(0) + "", "/addressbook: got 'addressbook', but 'contact' was expected");
		
		// try again with any matching type
		errors = validator.validate(doc);
		//for (Error error : errors) System.out.println(error.toString());
		Iterator<?> e = errors.iterator();
		
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
		t.ts1("F17", e.next() + "", "/addressbook/contact[7]/address: value 'nowhere' does not match pattern 'home|work'");
		t.ts1("F18", e.next() + "", "/addressbook/contact[7]/bank: value 'NL64 ABNC 0417 1643 01' is invalid for type IBAN: invalid checksum");
		t.ts1("F19", e.hasNext() + "", "false");
		
		// now validate an actual contact (the first one)
		errors = validator.validate(doc.get("contact"));
		e = errors.iterator();

		t.ts1("F20", e.next() + "", "/addressbook/contact[1]/person/about: only complex content is expected for node 'about'");
		t.ts1("F21", e.next() + "", "/addressbook/contact[1]/address/housenumber: got 'housenumber', but 'streetname' or 'postalcode' was expected");
		t.ts1("F22", e.next() + "", "/addressbook/contact[1]/email[2]: 'email' was not expected in 'contact'");
		t.ts1("F23", e.hasNext() + "", "false");
		
		// and finally try to validate the owner node (type only)
		errors = validator.validate(doc.get("owner")); // error, no such global type
		errors.addAll(validator.validate(doc.get("owner"), "contact")); // error, expecting contact node
		errors.addAll(validator.validateType(doc.get("owner"), "contact")); /// OK, owner is of type contact
		e = errors.iterator();

		t.ts1("F24", e.next() + "", "/addressbook/owner: no declaration for 'owner' found");
		t.ts1("F25", e.next() + "", "/addressbook/owner: got 'owner', but 'contact' was expected");
		t.ts1("F26", e.hasNext() + "", "false");
		
	}
}

package test.validation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import be.baur.sda.Node;
import be.baur.sda.SDA;
import be.baur.sds.SDS;
import be.baur.sds.Schema;
import be.baur.sds.validation.Error;
import be.baur.sds.validation.ErrorList;
import test.Test;

public final class Contacts {
	
	private static be.baur.sda.serialization.Parser parser = SDA.parser();

	/* 
	 * Parsing and validation of simple types with facets.
	 */
	public static void main(String[] args) throws Exception {

		Test t = new Test(s -> {
			return s;
		});
		
		InputStream sda = Contacts.class.getResourceAsStream("/contacts.sda");
		Node document = parser.parse(new InputStreamReader(sda, "UTF-8"));

		InputStream sds = Contacts.class.getResourceAsStream("/contacts.sds");
		Schema schema = SDS.parser().parse(new InputStreamReader(sds, "UTF-8"));

		SDS.validator().validate(document, schema, "contacts");
		ErrorList errors = SDS.validator().validate(document, schema, null);
		//for (Error error : errors) System.out.println(error.toString());
		Iterator<Error> e = errors.iterator();
		
		t.ts1("F01", e.next() + "", "/contacts/contact[6]: expecting 'contact' to be a complex type");
		t.ts1("F02", e.next() + "", "/contacts/contact[7]: content missing at end of 'contact'; expected 'id'");
		t.ts1("F03", e.next() + "", "/contacts/contact[8]/name: empty value not allowed; 'name' is not nullable");
		t.ts1("F04", e.next() + "", "/contacts/contact[9]/name[2]: 'name' was not expected in 'contact'");
		t.ts1("F05", e.next() + "", "/contacts/contact[10]/name[2]: 'name' was not expected in 'contact'");
		t.ts1("F06", e.next() + "", "/contacts/contact[11]/firstname: got 'firstname', but 'name' was expected");
		t.ts1("F07", e.next() + "", "/contacts/contact[12]/phone: got 'phone', but 'name' was expected");
		t.ts1("F08", e.next() + "", "/contacts/contact[13]/phonenumber: 'phonenumber' was not expected in 'contact'");
		// ABUNDANT t.ts1("F09", e.next() + "", "/contacts/contact[13]/birthdate: 'birthdate' was not expected in 'contact'");
		t.ts1("F10", e.next() + "", "/contacts/contact[14]/phone: 'phone' was not expected in 'contact'");
		t.ts1("F11", e.next() + "", "/contacts/contact[15]/birthdate: expecting 'birthdate' to be a simple type");
		t.ts1("F12", e.next() + "", "/contacts/contact[16]/name: expecting 'name' to be a simple type");
		t.ts1("F13", e.next() + "", "/contacts/contact[17]/phone: value '06-123456' has length 9 but 11 is the minimum");
		t.ts1("F14", e.next() + "", "/contacts/contact[18]/phone: value '0-012345678' does not match pattern '\\d{2,4}-\\d{6,8}'");
		t.ts1("F15", e.next() + "", "/contacts/contact[19]/phone: value '066-01234567' has length 12 but 11 is the maximum");
		t.ts1("F16", e.next() + "", "/contacts/contact[20]/birthdate: value '1970-02-29' is invalid for type date: Text '1970-02-29' could not be parsed: Invalid date 'February 29' as '1970' is not a leap year");
		t.ts1("F17", e.next() + "", "/contacts/contact[21]/birthdate: value '1870-02-28' subceeds the minimum of 1900-01-01");
		t.ts1("F18", e.next() + "", "/contacts/contact[22]/star: value 'yes' is not a valid boolean");
		t.ts1("F19", e.next() + "", "/contacts/contact[23]/icon: 'icon' has an invalid binary value: Input byte[] should at least have 2 bytes for base64 bytes");
		t.ts1("F20", e.next() + "", "/contacts/contact[24]/id: value '0' is not inclusive");
		t.ts1("F21", e.next() + "", "/contacts/contact[25]/id: value '24' is not inclusive");
		t.ts1("F22", e.next() + "", "/contacts/compact: 'compact' was not expected in 'contacts'");
		t.ts1("F99", e.hasNext() + "", "false");
	}
}

package test;

import java.io.InputStream;
import java.io.InputStreamReader;

import be.baur.sda.Node;
import be.baur.sda.SDA;
import be.baur.sds.SDS;
import be.baur.sds.Schema;
import be.baur.sds.validation.ErrorList;

public final class TestValContacts {
	
	private static be.baur.sda.serialization.Parser parser = SDA.parser();

	public static void main(String[] args) throws Exception {

		Test t = new Test(s -> {
			return s;
		});
		
		InputStream sda = TestValContacts.class.getResourceAsStream("/contacts.sda");
		Node document = parser.parse(new InputStreamReader(sda, "UTF-8"));

		InputStream sds = TestValContacts.class.getResourceAsStream("/contacts.sds");
		Schema schema = SDS.parser().parse(new InputStreamReader(sds, "UTF-8"));

		SDS.validator().validate(document, schema, "contacts");
		ErrorList errors = SDS.validator().validate(document, schema, null);
		//for (Error error : errors) System.out.println(error.toString());
		
		t.ts1("F01", errors.get(0).toString(), "/contacts/contact[6]: expecting 'contact' to be a complex type");
		t.ts1("F02", errors.get(1).toString(), "/contacts/contact[7]: content missing at end of 'contact'; expecting 'id'");
		t.ts1("F03", errors.get(2).toString(), "/contacts/contact[8]/name: empty value not allowed; 'name' is not nullable");
		t.ts1("F04", errors.get(3).toString(), "/contacts/contact[9]/name[2]: 'name' not expected in 'contact'");
		t.ts1("F05", errors.get(4).toString(), "/contacts/contact[10]/name[2]: 'name' not expected in 'contact'");
		t.ts1("F06", errors.get(5).toString(), "/contacts/contact[11]/firstname: got 'firstname', but expected 'name' instead");
		t.ts1("F07", errors.get(6).toString(), "/contacts/contact[12]/phone: got 'phone', but expected 'name' instead");
		t.ts1("F08", errors.get(7).toString(), "/contacts/contact[13]/phonenumber: 'phonenumber' not expected in 'contact'");
		t.ts1("F09", errors.get(8).toString(), "/contacts/contact[13]/birthdate: 'birthdate' not expected in 'contact'");
		t.ts1("F10", errors.get(9).toString(), "/contacts/contact[14]/phone: 'phone' not expected in 'contact'");
		t.ts1("F11", errors.get(10).toString(), "/contacts/contact[15]/birthdate: expecting 'birthdate' to be a simple type");
		t.ts1("F12", errors.get(11).toString(), "/contacts/contact[16]/name: expecting 'name' to be a simple type");
		t.ts1("F13", errors.get(12).toString(), "/contacts/contact[17]/phone: value '06-123456' has length 9 but 11 is the minimum");
		t.ts1("F14", errors.get(13).toString(), "/contacts/contact[18]/phone: value '0-012345678' does not match pattern '\\d{2,4}-\\d{6,8}'");
		t.ts1("F15", errors.get(14).toString(), "/contacts/contact[19]/phone: value '066-01234567' has length 12 but 11 is the maximum");
		t.ts1("F16", errors.get(15).toString(), "/contacts/contact[20]/birthdate: value '1970-02-29' is invalid for type date: Text '1970-02-29' could not be parsed: Invalid date 'February 29' as '1970' is not a leap year");
		t.ts1("F17", errors.get(16).toString(), "/contacts/contact[21]/birthdate: value '1870-02-28' subceeds the minimum of 1900-01-01");
		t.ts1("F18", errors.get(17).toString(), "/contacts/contact[22]/star: value 'yes' is not a valid boolean");
		t.ts1("F19", errors.get(18).toString(), "/contacts/contact[23]/icon: 'icon' has an invalid binary value: Input byte[] should at least have 2 bytes for base64 bytes");
		t.ts1("F20", errors.get(19).toString(), "/contacts/contact[24]/id: value '0' is not inclusive");
		t.ts1("F21", errors.get(20).toString(), "/contacts/contact[25]/id: value '24' is not inclusive");
		t.ts1("F22", errors.get(21).toString(), "/contacts/compact: 'compact' not expected in 'contacts'");
	}
}

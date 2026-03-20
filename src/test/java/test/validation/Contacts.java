package test.validation;

import java.util.Iterator;

import be.baur.sda.DataNode;
import be.baur.sda.SDA;
import be.baur.sds.SDS;
import be.baur.sds.validation.Validator;
import be.baur.sds.validation.Validator.Errors;
import test.Test;

public final class Contacts {

	/* 
	 * Parsing and validation of simple types with facets.
	 */
	public static void main(String[] args) throws Exception {

		Test t = new Test(s -> {
			return s;
		});
		

		DataNode doc = SDA.parse(Test.getResourceFile("/contacts.sda"));
		Validator validator = SDS.parse(Test.getResourceFile("/contacts.sds")).newValidator();
		
		Errors errors = validator.validate(doc);
		//for (Error error : errors) System.out.println(error.toString());
		Iterator<?> e = errors.iterator();
		
		t.s("F01", e.next() + "", "/contacts/contact[6]: complex content is expected for node 'contact'");
		t.s("F02", e.next() + "", "/contacts/contact[7]: content missing at end of 'contact'; expected 'name'");
		t.s("F03", e.next() + "", "/contacts/contact[8]/name: empty value not allowed; 'name' is not nullable");
		t.s("F04", e.next() + "", "/contacts/contact[9]/name[2]: 'name' was not expected in 'contact'");
		t.s("F05", e.next() + "", "/contacts/contact[10]/name[2]: 'name' was not expected in 'contact'");
		t.s("F06", e.next() + "", "/contacts/contact[11]/firstname: got 'firstname', but 'name' was expected");
		t.s("F07", e.next() + "", "/contacts/contact[12]/phone: got 'phone', but 'name' was expected");
		t.s("F08", e.next() + "", "/contacts/contact[13]/phonenumber: 'phonenumber' was not expected in 'contact'");
		// ABUNDANT t.ts1("F09", e.next() + "", "/contacts/contact[13]/birthdate: 'birthdate' was not expected in 'contact'");
		t.s("F09", e.next() + "", "/contacts/contact[14]/phone: 'phone' was not expected in 'contact'");
		t.s("F10", e.next() + "", "/contacts/contact[15]/birthdate: no complex content is expected for node 'birthdate'");
		t.s("F11", e.next() + "", "/contacts/contact[15]/birthdate: empty value not allowed; 'birthdate' is not nullable");
		t.s("F12", e.next() + "", "/contacts/contact[16]/name: no complex content is expected for node 'name'");
		t.s("F13", e.next() + "", "/contacts/contact[16]/name: empty value not allowed; 'name' is not nullable");		
		t.s("F14", e.next() + "", "/contacts/contact[17]/phone: value '06-123456' has length 9 but 11 is the minimum");
		t.s("F15", e.next() + "", "/contacts/contact[18]/phone: value '0-012345678' does not match pattern '\\d{2,4}-\\d{6,8}'");
		t.s("F16", e.next() + "", "/contacts/contact[19]/phone: value '066-01234567' has length 12 but 11 is the maximum");
		t.s("F17", e.next() + "", "/contacts/contact[20]/birthdate: value '1970-02-29' is invalid for type date: Text '1970-02-29' could not be parsed: Invalid date 'February 29' as '1970' is not a leap year");
		t.s("F18", e.next() + "", "/contacts/contact[21]/birthdate: value '1870-02-28' subceeds the minimum of 1900-01-01");
		t.s("F19", e.next() + "", "/contacts/contact[22]/star: value 'yes' is invalid for type boolean: either true or false is expected");
		t.s("F20", e.next() + "", "/contacts/contact[23]/icon: value '*' is invalid for type binary: Input byte[] should at least have 2 bytes for base64 bytes");
		t.s("F21", e.next() + "", "/contacts/contact[24]: value '24' is not inclusive");
		t.s("F22", e.next() + "", "/contacts/contact[25]: value '0' is not inclusive");
		t.s("F23", e.next() + "", "/contacts/contact[26]: value 'x' is invalid for type integer: For input string: \"x\"");
		t.s("F25", e.next() + "", "/contacts/contact[27]: complex content is expected for node 'contact'");
		t.s("F24", e.next() + "", "/contacts/contact[27]: empty value not allowed; 'contact' is not nullable");
		t.s("F26", e.next() + "", "/contacts/contact[28]: content missing at end of 'contact'; expected 'name'");
		t.s("F27", e.next() + "", "/contacts/contact[28]: empty value not allowed; 'contact' is not nullable");
		t.s("F28", e.next() + "", "/contacts/contact[29]: content missing at end of 'contact'; expected 'name'");
		t.s("F29", e.next() + "", "/contacts/contact[29]: empty value not allowed; 'contact' is not nullable");
		t.s("F30", e.next() + "", "/contacts/compact: 'compact' was not expected in 'contacts'");
		t.s("F31", e.hasNext() + "", "false");
	}
}

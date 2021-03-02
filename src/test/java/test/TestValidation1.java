package test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import be.baur.sda.Node;
import be.baur.sds.Schema;
import be.baur.sds.serialization.Parser;
import be.baur.sds.validation.Error;
import be.baur.sds.validation.Validator;

public final class TestValidation1 {
	
	private static be.baur.sda.parse.Parser parser = new be.baur.sda.parse.Parser();

	public static void main(String[] args) throws Exception {

		Test t = new Test(s -> {
			return s;
		});
		
		InputStream sda = TestValidation1.class.getResourceAsStream("/contacts.sda");
		Node document = parser.parse(new InputStreamReader(sda, "UTF-8"));

		InputStream sds = TestValidation1.class.getResourceAsStream("/contacts.sds");
		Schema schema = Parser.parse(new InputStreamReader(sds, "UTF-8"));

		ArrayList<Error> errors = Validator.validate(document, schema);
		//for (Error error : errors) System.out.println(error.toString());
		
		t.t1("F01", errors.get(0).toString(), "/contacts/contact[6]: expecting node 'contact' to be a complex type");
		t.t1("F02", errors.get(1).toString(), "/contacts/contact[7]: mandatory node 'id' expected in 'contact'");
		t.t1("F03", errors.get(2).toString(), "/contacts/contact[8]/name: empty value not allowed; node 'name' is not nullable");
		t.t1("F04", errors.get(3).toString(), "/contacts/contact[9]/name[2]: node 'name' not expected in 'contact'");
		t.t1("F05", errors.get(4).toString(), "/contacts/contact[10]/name[2]: node 'name' not expected in 'contact'");
		t.t1("F06", errors.get(5).toString(), "/contacts/contact[11]/firstname: expecting node 'name', but got 'firstname' instead");
		t.t1("F07", errors.get(6).toString(), "/contacts/contact[12]/phone: expecting node 'name', but got 'phone' instead");
		t.t1("F08", errors.get(7).toString(), "/contacts/contact[13]/phonenumber: node 'phonenumber' not expected in 'contact'");
		t.t1("F09", errors.get(8).toString(), "/contacts/contact[13]/birthdate: node 'birthdate' not expected in 'contact'");
		t.t1("F10", errors.get(9).toString(), "/contacts/contact[14]/phone: node 'phone' not expected in 'contact'");
		t.t1("F11", errors.get(10).toString(), "/contacts/contact[15]/birthdate: expecting node 'birthdate' to be a simple type");
		t.t1("F12", errors.get(11).toString(), "/contacts/contact[16]/name: expecting node 'name' to be a simple type");
		t.t1("F13", errors.get(12).toString(), "/contacts/contact[17]/phone: value '06-123456' has length 9 but 11 is the minimum");
		t.t1("F14", errors.get(13).toString(), "/contacts/contact[18]/phone: value '0-012345678' does not match pattern '\\d{2,4}-\\d{6,8}'");
		t.t1("F15", errors.get(14).toString(), "/contacts/contact[19]/phone: value '066-01234567' has length 12 but 11 is the maximum");
		t.t1("F16", errors.get(15).toString(), "/contacts/contact[20]/birthdate: value '1970-02-29' is invalid for type date: Text '1970-02-29' could not be parsed: Invalid date 'February 29' as '1970' is not a leap year");
		t.t1("F17", errors.get(16).toString(), "/contacts/contact[21]/birthdate: value '1870-02-28' subceeds the minimum of 1900-01-01");
		t.t1("F18", errors.get(17).toString(), "/contacts/contact[22]/star: value 'yes' is not a valid boolean");
		t.t1("F19", errors.get(18).toString(), "/contacts/contact[23]/icon: node 'icon' has an invalid binary value: Input byte[] should at least have 2 bytes for base64 bytes");
		t.t1("F20", errors.get(19).toString(), "/contacts/compact: node 'compact' not expected in 'contacts'");
	}
}

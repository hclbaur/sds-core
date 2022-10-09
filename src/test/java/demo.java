import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

import be.baur.sda.Node;
import be.baur.sda.NodeSet;
import be.baur.sda.SDA;
import be.baur.sds.SDS;
import be.baur.sds.Schema;
import be.baur.sds.serialization.SchemaException;
import be.baur.sds.validation.Error;
import be.baur.sds.validation.ErrorList;

public class demo {

	public static void main(String[] args) throws IOException, ParseException, SchemaException   {
		
		FileReader sds = new FileReader(args[0]);
		Schema schema = SDS.parser().parse(sds);
		
		FileReader sda = new FileReader(args[1]);
		Node root = SDA.parser().parse(sda);
		
		ErrorList errors = SDS.validator().validate(root, schema, null);
		if (! errors.isEmpty()) {
			for (Error error : errors) System.out.println(error.toString()); return;
		}
		
		for (Node contact : root.getNodes().find("contact")) {
			
			Node name = contact.getNodes().get("firstname");
			NodeSet numbers = contact.getNodes().find("phonenumber");
			
			System.out.println(name.getValue() + " has " + numbers.size() + " phone number(s).");
			
			int i = 0; 	for (Node number : numbers) {
				System.out.println("  Number " + ++i + ": " + number.getValue());
			}
		}
	}
}

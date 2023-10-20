import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import be.baur.sda.Node;
import be.baur.sda.SDA;
import be.baur.sda.DataNode;
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
		DataNode root = SDA.parser().parse(sda);
		
		ErrorList errors = SDS.validator().validate(root, schema, null);
		if (! errors.isEmpty()) {
			for (Error error : errors) System.out.println(error.toString()); return;
		}
		
		for (Node contact : root.find("contact")) {
			
			DataNode name = contact.get("firstname");
			List<DataNode> numbers = contact.find("phonenumber");
			
			System.out.println(name.getValue() + " has " + numbers.size() + " phone number(s).");
			
			int i = 0; 	for (DataNode number : numbers) {
				System.out.println("  Number " + ++i + ": " + number.getValue());
			}
		}
	}
}

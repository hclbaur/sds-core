import java.io.File;
import java.util.List;

import be.baur.sda.DataNode;
import be.baur.sda.Node;
import be.baur.sda.SDA;
import be.baur.sds.SDS;
import be.baur.sds.Schema;
import be.baur.sds.validation.Validator;
import be.baur.sds.validation.Validator.Errors;

public class demo {

	public static void main(String[] args) throws Exception {
		
		Schema schema = SDS.parse(new File(args[0]));
		DataNode root = SDA.parse(new File(args[1]));
		
		Validator validator = schema.newValidator();
		Errors errors = validator.validate(root);
		if (! errors.isEmpty()) {
			errors.forEach(error -> System.out.println(error.toString()));
			return;
		}

		for (Node contact : root.getAll("contact")) {
			
			DataNode name = contact.get("firstname");
			List<DataNode> numbers = contact.getAll("phonenumber");
			
			System.out.println(name.getValue() + " has " + numbers.size() + " phone number(s).");
			
			int i = 0; 	for (DataNode number : numbers) {
				System.out.println("  Number " + ++i + ": " + number.getValue());
			}
		}
	}
}

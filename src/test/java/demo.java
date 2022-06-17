import java.io.FileReader;

import be.baur.sda.ComplexNode;
import be.baur.sda.Node;
import be.baur.sda.NodeSet;
import be.baur.sda.SDA;
import be.baur.sda.SimpleNode;
import be.baur.sds.SDS;
import be.baur.sds.Schema;
import be.baur.sds.validation.Error;
import be.baur.sds.validation.ErrorList;

public class demo {

	public static void main(String[] args) throws Exception {
		
		FileReader sds = new FileReader(args[0]);
		Schema schema = SDS.parser().parse(sds);
		
		FileReader sda = new FileReader(args[1]);
		ComplexNode root = (ComplexNode) SDA.parser().parse(sda);
		
		ErrorList errors = SDS.validator().validate(root, schema, null);
		if (! errors.isEmpty()) {
			for (Error error : errors) System.out.println(error.toString()); return;
		}
		
		for (Node c : root.getNodes().get("contact")) {
			
			ComplexNode contact = (ComplexNode) c;
			SimpleNode name = (SimpleNode) contact.getNodes().get("firstname").get(1);
			NodeSet numbers = contact.getNodes().get("phonenumber");
			
			System.out.println(name.getValue() + " has " + numbers.size() + " phone number(s).");
			
			int i = 0; 	for (Node n : numbers) {
				System.out.println("  Number " + ++i + ": " + ((SimpleNode) n).getValue());
			}
		}
	}
}

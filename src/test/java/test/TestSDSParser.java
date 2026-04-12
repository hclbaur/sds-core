package test;

import java.io.File;
import java.util.function.Function;

import be.baur.sda.DataNode;
import be.baur.sda.SDA;
import be.baur.sds.DataNodeType;
import be.baur.sds.DataType;
import be.baur.sds.Schema;
import be.baur.sds.parser.SDSParser;
import samples.types.GMonthDay;
import samples.types.GMonthDayNodeType;
import samples.types.IBAN;
import samples.types.IBANNodeType;

public final class TestSDSParser {

	public static void main(String[] args) throws Exception {
		
		/* register custom types */
		DataType.register(IBAN.TYPE_NAME, IBAN.CONSTRUCTOR); 
		DataNodeType.register(IBAN.TYPE_NAME, IBANNodeType::new); 
		DataType.register(GMonthDay.TYPE_NAME, GMonthDay.CONSTRUCTOR);
		DataNodeType.register(GMonthDay.TYPE_NAME, GMonthDayNodeType::new);
		
		/* test parsing SDS from files and formatting back to SDS */
		System.out.print("contacts ");
		DataNode sds = SDA.parse(Test.getResourceFile("/contacts.sds"));
		Schema schema = SDSParser.parse(sds);
		if (! sds.toString().equals(schema.toString())) {
			System.out.println("\nEXPECTED: " + sds);
			System.out.println("RETURNED: " + schema);
		}
		
		System.out.print("addressbook ");
		sds = SDA.parse(Test.getResourceFile("/addressbook.sds"));
		schema = SDSParser.parse(sds);
		if (! sds.toString().equals(schema.toString())) {
			System.out.println("\nEXPECTED: " + sds);
			System.out.println("RETURNED: " + schema);
		}
		
		/* test writing a schema to an output file */
		SDA.format(new File("/tmp/test.sds"), sds);
		
		/* verify a schema */
		schema.verify();
		
		
		Function<String, String> strfun = str -> {
			try {
				return SDSParser.parse( SDA.parse(str) ).toString();
			} catch (Exception e) {
				return e.getLocalizedMessage();
			}
		};
		
		Test t = new Test(strfun);
		
		/* test valid SDS */
		t.s("S01", "schema { node \"name\" { type \"string\" } }", null);
		t.s("S02", "schema { node \"contact\" { node \"name\" { type \"string\" } } }", null);
		t.s("S03", "schema { node \"contact\" { node \"name\" { type \"string\" occurs \"0..1\" } } }", null);
		t.s("S04", "schema { node \"book\" { node \"contact\" { occurs \"1..*\" node \"name\" { type \"string\" } } } }", null);
		t.s("S05", "schema { node \"phone\" { type \"string\" } node { type \"phone\" } }", null);
		t.s("S06", "schema { node \"phone\" { type \"string\" } node \"mobile\" { type \"phone\" } }", null);

		t.s("S07", "schema { node \"g\" { group { node \"x\" { type \"string\" } node \"y\" { type \"string\" } } } }", null);
		t.s("S08", "schema { node \"c\" { choice { node \"x\" { type \"string\" } node \"y\" { type \"string\" } } } }", null);
		t.s("S10", "schema { node \"u\" { unordered { node \"x\" { type \"string\" } node \"y\" { type \"string\" } } } }", null);
		t.s("S11", "schema { node \"ean13\" { type \"string\" length \"13\" } }", null);
		t.s("S12", "schema { node \"image\" { type \"binary\" length \"0..1024\" } }", null);
		t.s("S13", "schema { node \"bool\" { type \"boolean\" nullable \"true\" } }", null);
		t.s("S14", "schema { node \"id\" { type \"string\" nullable \"false\" } }", null);
		t.s("S15", "schema { node \"id\" { type \"string\" pattern \"[^\\\\s]\" } }", null);
		t.s("S16", "schema { node \"bit\" { type \"integer\" value \"[0..1]\" } }", null);
		t.s("S17", "schema { node \"one\" { type \"integer\" value \"1\" } }", null);
		t.s("S18", "schema { node \"pi\" { type \"decimal\" value \"3.14\" } }", null);
		t.s("S19", "schema { node \"kelvin\" { type \"decimal\" value \"[-273.15..*)\" } }", null);
		t.s("S20", "schema { node \"now\" { type \"datetime\" value \"2020-08-11T17:55:00+02:00\" } }", "schema { node \"now\" { type \"datetime\" value \"2020-08-11T17:55+02:00\" } }");
		t.s("S23", "schema { node \"today\" { type \"datetime\" value \"[2020-08-11T00:00:00+02:00..2020-08-12T00:00:00+02:00)\" } }", "schema { node \"today\" { type \"datetime\" value \"[2020-08-11T00:00+02:00..2020-08-12T00:00+02:00)\" } }");
		t.s("S24", "schema { node \"today\" { type \"date\" value \"2020-08-11\" } }", null);
		t.s("S25", "schema { node \"august\" { type \"date\" value \"[2020-08-01..2020-09-01)\" } }", null);
		t.s("S26", "schema { node { type \"any\" } }", null);
		t.s("S27", "schema { node \"anything\" { type \"any\" } }", null);
		t.s("S28", "schema { node \"anything\" { node { type \"any\" } } }", null);
		t.s("S29", "schema { node \"x\" { type \"string\" node \"y\" { type \"string\" } } }", null);
		
		// test custom types
		t.s("S30", "schema { node \"bank\" { type \"IBAN\" length \"18\" } }", null);
		t.s("S31", "schema { node \"today\" { type \"gMonthDay\" value \"--08-11\" } }", null);
		t.s("S32", "schema { node \"august\" { type \"gMonthDay\" value \"[--08-01..--09-01)\" } }", null);
		
		t.checkFailures();
		
		t = new Test(strfun, "error at ");
		
		/* test invalid SDS */
		System.out.print("\n              ");
		t.s("F01", "node { }", "/node: a 'schema' node is expected");
		t.s("F02", "node \"\"", "/node: a 'schema' node is expected");
		t.s("F03", "schema { }", "/schema: a 'schema' node must have content");
		t.s("F04", "schema \"\"", "/schema: a 'schema' node must have content");
		t.s("F05", "schema{ type \"\" }", "/schema/type: attribute 'type' is not allowed here");

		t.s("F09", "schema{ occurs \"1\" }", "/schema/occurs: attribute 'occurs' is not allowed here");
		t.s("F10", "schema{ node{ name \"x\" type \"string\" occurs \"1\" } }", "/schema/node: attribute 'occurs' is not allowed here");
		t.s("F11", "schema{ node{ name \"x\" occurs \"1\" node{ name \"x\" type \"string\" } } }", "/schema/node: attribute 'occurs' is not allowed here");
		t.s("F12", "schema{ node \"x\" { choice{ } } }", "/schema/node/choice: component 'choice' is incomplete");
		t.s("F13", "schema{ choice{ } }", "/schema/choice: component 'choice' is not allowed here");
		t.s("F14", "schema{ name \"mobile\" }", "/schema/name: attribute 'name' is unknown");
		t.s("F15", "schema{ node{ type \"phone\" } }", "/schema/node/type: type 'phone' is unknown");
		t.s("F16", "schema{ node \"mobile\" { type \"phone\" } }", "/schema/node/type: type 'phone' is unknown");
		t.s("F17", "schema{ node \"phone\" { type \"string\" } node{ type \"phone\" nullable \"false\"} }", "/schema/node[2]: attribute 'nullable' is not allowed here");
		t.s("F18", "schema{ note{ } }", "/schema/note: component 'note' is unknown");
		t.s("F19", "schema{ node{ } }", "/schema/node: component 'node' is incomplete");
		t.s("F20", "schema{ node \"x\" { note{} } }", "/schema/node/note: component 'note' is unknown");
		t.s("F21", "schema{ node \"x\" { node{} } }", "/schema/node/node: component 'node' is incomplete");
		t.s("F22", "schema{ node \"x\" { schema{} } }", "/schema/node/schema: component 'schema' is unknown");
		t.s("F23", "schema{ node{ test \"\" } }", "/schema/node/test: attribute 'test' is unknown");
		System.out.print("\n              ");
		t.s("F24", "schema{ node{ type \"\" } }", "/schema/node/type: attribute 'type' is empty");
		t.s("F25", "schema{ node{ type \"string\" } }", "/schema/node: a name is expected");
		t.s("F26", "schema{ node{ type \"string\" node{} } }", "/schema/node: a name is expected");
		t.s("F27", "schema{ node{ type \"string\" type \"\" } }", "/schema/node/type[1]: attribute 'type' can occur only once");
		t.s("F28", "schema{ node \"m\" { node \"x\" { type \"string\" occurs \"\" } } }", "/schema/node/node/occurs: attribute 'occurs' is empty");
		t.s("F29", "schema{ node \"m\" { node \"x\" { type \"string\" occurs \"-1\" } } }", "/schema/node/node/occurs: occurs '-1' is invalid; negative values are not allowed");
		t.s("F30", "schema{ node \"m\" { node \"x\" { type \"string\" occurs \"a\" } } }", "/schema/node/node/occurs: occurs 'a' is invalid; missing or non-integer value(s)");
		t.s("F31", "schema{ node \"c\" { choice{ node \"x\" { type \"string\" } } } }", "/schema/node/choice: component 'choice' is incomplete");
		t.s("F32", "schema{ node \"c\" { choice{ name \"x\" } } }", "/schema/node/choice/name: attribute 'name' is unknown");
		t.s("F33", "schema{ node \"c\" { choice \"123\" { node{} } } }", "/schema/node/choice: name '123' is not expected");		
		t.s("F34", "schema{ node \"c\" { choice{ nullable \"true\" } } }", "/schema/node/choice: attribute 'nullable' is not allowed here");
		t.s("F35", "schema{ node \"x\" { type \"string\" length \"\" } }", "/schema/node/length: attribute 'length' is empty");
		t.s("F36", "schema{ node \"x\"{ type \"binary\" length \"-1\" } }", "/schema/node/length: length '-1' is invalid; negative values are not allowed");
		t.s("F37", "schema{ node \"x\" { type \"boolean\" length \"5\" } }", "/schema/node: attribute 'length' is not allowed here");
		t.s("F38", "schema{ node \"x\" { type \"boolean\" nullable \"maybe\" } }", "/schema/node/nullable: nullable 'maybe' is invalid; must be 'true' or 'false'");
		t.s("F39", "schema{ node \"x\" { type \"integer\" value \"\" } }", "/schema/node/value: attribute 'value' is empty");
		t.s("F40", "schema{ node \"x\" { type \"decimal\" value \"[1..-1]\" } }", "/schema/node/value: value '[1..-1]' is invalid; lower limit exceeds upper limit");
		t.s("F41", "schema{ node \"x\" { type \"boolean\" value \"5\" } }", "/schema/node: attribute 'value' is not allowed here");
		t.s("F42", "schema{ node { type \"any\" nullable \"true\" } }", "/schema/node: attribute 'nullable' is not allowed here");
		t.s("F43", "schema{ node \"123\" { type \"string\" } }", "/schema/node: '123' is not a valid node name");
		t.s("F44", "schema{ node \"phone\" { type \"string\" } node \"123\" { type \"phone\" } }", "/schema/node[2]: '123' is not a valid node name");
		t.s("F45", "schema { node \"x\" { type \"any\" node \"y\" { type \"string\" } } }", "/schema/node: type 'any' is invalid; node defines content");
	
		t.checkFailures();
		
		// test performance
		var p = new TestPerf<DataNode>(node -> {
			try {
				SDSParser.parse(node);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	
		p.run("\nPerformance : P01", sds, 2000, 40);
	
	}

}

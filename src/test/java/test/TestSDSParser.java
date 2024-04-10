package test;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.function.Function;

import be.baur.sda.DataNode;
import be.baur.sda.SDA;
import be.baur.sds.Schema;
import be.baur.sds.serialization.SDSParser;

public final class TestSDSParser {

	public static void main(String[] args) throws Exception {
		
		/* test parsing SDS from files and formatting back to SDS */
		System.out.print("contacts ");
		InputStream input = TestSDSParser.class.getResourceAsStream("/contacts.sds");
		DataNode sds = SDA.parse(new InputStreamReader(input,"UTF-8"));
		Schema schema = SDSParser.parse(sds);
		if (! sds.toString().equals(schema.toString())) {
			System.out.println("\nEXPECTED: " + sds);
			System.out.println("RETURNED: " + schema);
		}
		
		System.out.print("addressbook ");
		input = TestSDSParser.class.getResourceAsStream("/addressbook.sds");
		sds = SDA.parse(new InputStreamReader(input,"UTF-8"));
		schema = SDSParser.parse(sds);
		if (! sds.toString().equals(schema.toString())) {
			System.out.println("\nEXPECTED: " + sds);
			System.out.println("RETURNED: " + schema);
		}
		
		/* test writing a schema to an output file */
		OutputStreamWriter output = 
			new OutputStreamWriter(new FileOutputStream("c:/temp/test.sds"), "UTF-8");
		SDA.format(output, schema.toSDA()); output.close();
		
		/* verify a schema */
		schema.verify();
		
		
		Function<String, String> strfun = str -> {
			try {
				return SDSParser.parse( SDA.parse(new StringReader(str)) ).toString();
			} catch (Exception e) {
				return e.getLocalizedMessage();
			}
		};
		
		Test s = new Test(strfun);
		Test f = new Test(strfun, "error at ");
		
		/* test valid SDS */
		s.ts1("S01", "schema { node \"name\" { type \"string\" } }", null);
		s.ts1("S02", "schema { node \"contact\" { node \"name\" { type \"string\" } } }", null);
		s.ts1("S03", "schema { node \"contact\" { node \"name\" { type \"string\" occurs \"0..1\" } } }", null);
		s.ts1("S04", "schema { node \"book\" { node \"contact\" { occurs \"1..*\" node \"name\" { type \"string\" } } } }", null);
		s.ts1("S05", "schema { node \"phone\" { type \"string\" } node { type \"phone\" } }", null);
		s.ts1("S06", "schema { node \"phone\" { type \"string\" } node \"mobile\" { type \"phone\" } }", null);

		s.ts1("S09", "schema { node \"g\" { group { node \"x\" { type \"string\" } node \"y\" { type \"string\" } } } }", null);
		s.ts1("S10", "schema { node \"c\" { choice { node \"x\" { type \"string\" } node \"y\" { type \"string\" } } } }", null);
		s.ts1("S11", "schema { node \"u\" { unordered { node \"x\" { type \"string\" } node \"y\" { type \"string\" } } } }", null);
		s.ts1("S12", "schema { node \"ean13\" { type \"string\" length \"13\" } }", null);
		s.ts1("S13", "schema { node \"image\" { type \"binary\" length \"0..1024\" } }", null);
		s.ts1("S14", "schema { node \"bool\" { type \"boolean\" nullable \"true\" } }", null);
		s.ts1("S15", "schema { node \"id\" { type \"string\" nullable \"false\" } }", null);
		s.ts1("S16", "schema { node \"id\" { type \"string\" pattern \"[^\\\\s]\" } }", null);
		s.ts1("S17", "schema { node \"bit\" { type \"integer\" value \"[0..1]\" } }", null);
		s.ts1("S18", "schema { node \"one\" { type \"integer\" value \"1\" } }", null);
		s.ts1("S19", "schema { node \"pi\" { type \"decimal\" value \"3.14\" } }", null);
		s.ts1("S20", "schema { node \"kelvin\" { type \"decimal\" value \"[-273.15..*)\" } }", null);
		s.ts1("S21", "schema { node \"now\" { type \"datetime\" value \"2020-08-11T17:55:00+02:00\" } }", null);
		s.ts1("S22", "schema { node \"today\" { type \"datetime\" value \"[2020-08-11T00:00:00+02:00..2020-08-12T00:00:00+02:00)\" } }", null);
		s.ts1("S23", "schema { node \"today\" { type \"date\" value \"2020-08-11\" } }", null);
		s.ts1("S24", "schema { node \"august\" { type \"date\" value \"[2020-08-01..2020-09-01)\" } }", null);
		s.ts1("S25", "schema { node { type \"any\" } }", null);
		s.ts1("S26", "schema { node \"anything\" { type \"any\" } }", null);
		s.ts1("S27", "schema { node \"anything\" { node { type \"any\" } } }", null);
		s.ts1("S28", "schema { node \"x\" { type \"string\" node \"y\" { type \"string\" } } }", null);
		
		/* test invalid SDS */
		System.out.print("\n              ");
		f.ts1("F01", "node { }", "/node: a 'schema' node is expected");
		f.ts1("F02", "node \"\"", "/node: a 'schema' node is expected");
		f.ts1("F03", "schema { }", "/schema: a 'schema' node must have content");
		f.ts1("F04", "schema \"\"", "/schema: a 'schema' node must have content");
		f.ts1("F05", "schema{ type \"\" }", "/schema/type: attribute 'type' is not allowed here");

		f.ts1("F09", "schema{ occurs \"1\" }", "/schema/occurs: attribute 'occurs' is not allowed here");
		f.ts1("F10", "schema{ node{ name \"x\" type \"string\" occurs \"1\" } }", "/schema/node: attribute 'occurs' is not allowed here");
		f.ts1("F11", "schema{ node{ name \"x\" occurs \"1\" node{ name \"x\" type \"string\" } } }", "/schema/node: attribute 'occurs' is not allowed here");
		f.ts1("F12", "schema{ node \"x\" { choice{ } } }", "/schema/node/choice: component 'choice' is incomplete");
		f.ts1("F13", "schema{ choice{ } }", "/schema/choice: component 'choice' is not allowed here");
		f.ts1("F14", "schema{ name \"mobile\" }", "/schema/name: attribute 'name' is unknown");
		f.ts1("F15", "schema{ node{ type \"phone\" } }", "/schema/node/type: type 'phone' is unknown");
		f.ts1("F16", "schema{ node \"mobile\" { type \"phone\" } }", "/schema/node/type: type 'phone' is unknown");
		f.ts1("F17", "schema{ node \"phone\" { type \"string\" } node{ type \"phone\" nullable \"false\"} }", "/schema/node[2]: attribute 'nullable' is not allowed here");
		f.ts1("F18", "schema{ note{ } }", "/schema/note: component 'note' is unknown");
		f.ts1("F19", "schema{ node{ } }", "/schema/node: component 'node' is incomplete");
		f.ts1("F20", "schema{ node \"x\" { note{} } }", "/schema/node/note: component 'note' is unknown");
		f.ts1("F21", "schema{ node \"x\" { node{} } }", "/schema/node/node: component 'node' is incomplete");
		f.ts1("F22", "schema{ node \"x\" { schema{} } }", "/schema/node/schema: component 'schema' is unknown");
		f.ts1("F23", "schema{ node{ test \"\" } }", "/schema/node/test: attribute 'test' is unknown");
		System.out.print("\n              ");
		f.ts1("F24", "schema{ node{ type \"\" } }", "/schema/node/type: attribute 'type' is empty");
		f.ts1("F25", "schema{ node{ type \"string\" } }", "/schema/node: a name is expected");
		f.ts1("F26", "schema{ node{ type \"string\" node{} } }", "/schema/node: a name is expected");
		f.ts1("F27", "schema{ node{ type \"string\" type \"\" } }", "/schema/node/type[1]: attribute 'type' can occur only once");
		f.ts1("F28", "schema{ node \"m\" { node \"x\" { type \"string\" occurs \"\" } } }", "/schema/node/node/occurs: attribute 'occurs' is empty");
		f.ts1("F29", "schema{ node \"m\" { node \"x\" { type \"string\" occurs \"-1\" } } }", "/schema/node/node/occurs: occurs '-1' is invalid; negative values are not allowed");
		f.ts1("F30", "schema{ node \"m\" { node \"x\" { type \"string\" occurs \"a\" } } }", "/schema/node/node/occurs: occurs 'a' is invalid; missing or non-integer value(s)");
		f.ts1("F31", "schema{ node \"c\" { choice{ node \"x\" { type \"string\" } } } }", "/schema/node/choice: component 'choice' is incomplete");
		f.ts1("F32", "schema{ node \"c\" { choice{ name \"x\" } } }", "/schema/node/choice/name: attribute 'name' is unknown");
		f.ts1("F33", "schema{ node \"c\" { choice \"123\" { node{} } } }", "/schema/node/choice: name '123' is not expected");		
		f.ts1("F34", "schema{ node \"c\" { choice{ nullable \"true\" } } }", "/schema/node/choice: attribute 'nullable' is not allowed here");
		f.ts1("F35", "schema{ node \"x\" { type \"string\" length \"\" } }", "/schema/node/length: attribute 'length' is empty");
		f.ts1("F36", "schema{ node \"x\"{ type \"binary\" length \"-1\" } }", "/schema/node/length: length '-1' is invalid; negative values are not allowed");
		f.ts1("F37", "schema{ node \"x\" { type \"boolean\" length \"5\" } }", "/schema/node: attribute 'length' is not allowed here");
		f.ts1("F38", "schema{ node \"x\" { type \"boolean\" nullable \"maybe\" } }", "/schema/node/nullable: nullable 'maybe' is invalid; must be 'true' or 'false'");
		f.ts1("F39", "schema{ node \"x\" { type \"integer\" value \"\" } }", "/schema/node/value: attribute 'value' is empty");
		f.ts1("F40", "schema{ node \"x\" { type \"decimal\" value \"[1..-1]\" } }", "/schema/node/value: value '[1..-1]' is invalid; lower limit exceeds upper limit");
		f.ts1("F41", "schema{ node \"x\" { type \"boolean\" value \"5\" } }", "/schema/node: attribute 'value' is not allowed here");
		f.ts1("F42", "schema{ node { type \"any\" nullable \"true\" } }", "/schema/node: attribute 'nullable' is not allowed here");
		f.ts1("F43", "schema{ node \"123\" { type \"string\" } }", "/schema/node: '123' is not a valid node name");
		f.ts1("F44", "schema{ node \"phone\" { type \"string\" } node \"123\" { type \"phone\" } }", "/schema/node[2]: '123' is not a valid node name");
		f.ts1("F45", "schema { node \"x\" { type \"any\" node \"y\" { type \"string\" } } }", "/schema/node: type 'any' is invalid; node defines content");
	
		// test performance
		UnitTestPerformance<DataNode> perf = new UnitTestPerformance<DataNode>(node -> {
			try {
				SDSParser.parse(node);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	
		perf.run("\nPerformance : P01", sds, 2000, 40);
	
	}

}

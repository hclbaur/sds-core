package test;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import be.baur.sda.Node;
import be.baur.sda.SDA;
import be.baur.sds.Schema;
import be.baur.sds.serialization.SDSParser;
import be.baur.sds.serialization.SchemaException;

public final class TestSDSParser 
{
	private static be.baur.sda.serialization.Parser sdaparser = SDA.parser();
	
	public static void main(String[] args) throws Exception {
		
		Test t = new Test(s -> {
			try {
				return SDSParser.parse( sdaparser.parse(new StringReader(s)) ).toString();
			} catch (Exception e) {
				return e.getMessage();
			}
		});
		
		/* test parsing SDS from files and formatting back to SDS */
		InputStream input = TestSDSParser.class.getResourceAsStream("/contacts.sds");
		Node sds = sdaparser.parse(new InputStreamReader(input,"UTF-8"));
		Schema schema = SDSParser.parse(sds);
		if (! sds.toString().equals(schema.toString())) {
			System.out.println("\nEXPECTED: " + sds);
			System.out.println("RETURNED: " + schema);
		}
		
		input = TestSDSParser.class.getResourceAsStream("/addressbook.sds");
		sds = sdaparser.parse(new InputStreamReader(input,"UTF-8"));
		schema = SDSParser.parse(sds);
		if (! sds.toString().equals(schema.toString())) {
			System.out.println("\nEXPECTED: " + sds);
			System.out.println("RETURNED: " + schema);
		}
		
		/* test writing a schema to an output file */
		OutputStreamWriter output = 
			new OutputStreamWriter(new FileOutputStream("c:/temp/addressbook.sds"), "UTF-8");
		SDA.formatter().format(output, schema.toNode()); output.close();
		
		/* verify a schema */
		SDSParser sdsparser = new SDSParser(); 
		sdsparser.verify(schema);
		
		/* test valid SDS */
		t.ts1("S01", "schema { node { name \"name\" type \"string\" } }", null);
		t.ts1("S02", "schema { node { name \"contact\" node { name \"name\" type \"string\" } } }", null);
		t.ts1("S03", "schema { node { name \"contact\" node { name \"name\" type \"string\" occurs \"0..1\" } } }", null);
		t.ts1("S04", "schema { node { name \"book\" node { name \"contact\" occurs \"1..*\" node { name \"name\" type \"string\" } } } }", null);
		t.ts1("S05", "schema { node { name \"phone\" type \"string\" } node { type \"phone\" } }", null);
		t.ts1("S06", "schema { node { name \"phone\" type \"string\" } node { name \"mobile\" type \"phone\" } }", null);
		t.ts1("S07", "schema { type \"phone\" node { name \"phone\" type \"string\" } node { type \"phone\" } }", null);
		t.ts1("S08", "schema { type \"mobile\" node { name \"phone\" type \"string\" } node { name \"mobile\" type \"phone\" } }", null);
		t.ts1("S09", "schema { node { name \"g\" group { node { name \"x\" type \"string\" } node { name \"y\" type \"string\" } } } }", null);
		t.ts1("S10", "schema { node { name \"c\" choice { node { name \"x\" type \"string\" } node { name \"y\" type \"string\" } } } }", null);
		t.ts1("S11", "schema { node { name \"u\" unordered { node { name \"x\" type \"string\" } node { name \"y\" type \"string\" } } } }", null);
		t.ts1("S12", "schema { node { name \"ean13\" type \"string\" length \"13\" } }", null);
		t.ts1("S13", "schema { node { name \"image\" type \"binary\" length \"0..1024\" } }", null);
		t.ts1("S14", "schema { node { name \"bool\" type \"boolean\" nullable \"true\" } }", null);
		t.ts1("S15", "schema { node { name \"id\" type \"string\" nullable \"false\" } }", null);
		t.ts1("S16", "schema { node { name \"id\" type \"string\" pattern \"[^\\\\s]\" } }", null);
		t.ts1("S17", "schema { node { name \"bit\" type \"integer\" value \"[0..1]\" } }", null);
		t.ts1("S18", "schema { node { name \"one\" type \"integer\" value \"1\" } }", null);
		t.ts1("S19", "schema { node { name \"pi\" type \"decimal\" value \"3.14\" } }", null);
		t.ts1("S20", "schema { node { name \"kelvin\" type \"decimal\" value \"[-273.15..*)\" } }", null);
		t.ts1("S21", "schema { node { name \"now\" type \"datetime\" value \"2020-08-11T17:55:00+02:00\" } }", null);
		t.ts1("S22", "schema { node { name \"today\" type \"datetime\" value \"[2020-08-11T00:00:00+02:00..2020-08-12T00:00:00+02:00)\" } }", null);
		t.ts1("S23", "schema { node { name \"today\" type \"date\" value \"2020-08-11\" } }", null);
		t.ts1("S24", "schema { node { name \"august\" type \"date\" value \"[2020-08-01..2020-09-01)\" } }", null);
		t.ts1("S25", "schema { node { name \"anything\" type \"any\" } }", null);
		t.ts1("S26", "schema { node { type \"any\" } }", null);
		
		/* test invalid SDS */
		try { 
			sdsparser.parse(new StringReader("name \"name\""));
			throw new Exception("SchemaException expected here!");
		}
		catch (SchemaException e) { /* System.out.println(e.getMessage()); */ }
		
		System.out.print("\n              ");
		String s = "SDS syntax violation at ";
		t.ts1("F01", "node{ name \"x\" type \"string\" }", s + "/node: a complex 'schema' node is expected");
		t.ts1("F02", "schema{ }", s + "/schema: a 'schema' node cannot be empty");
		t.ts1("F03", "schema{ test \"\" }", s + "/schema/test: attribute 'test' is unknown");
		t.ts1("F04", "schema{ type \"\" }", s + "/schema/type: attribute 'type' is empty");
		t.ts1("F05", "schema{ type \"string\" }", s + "/schema: type 'string' is invalid; no such global type (string)");
		t.ts1("F06", "schema{ occurs \"1\" }", s + "/schema/occurs: attribute 'occurs' is not allowed here");
		t.ts1("F07", "schema{ node{ name \"x\" type \"string\" occurs \"1\" } }", s + "/schema/node: attribute 'occurs' is not allowed here");
		t.ts1("F08", "schema{ node{ name \"x\" occurs \"1\" node{ name \"x\" type \"string\" } } }", s + "/schema/node: attribute 'occurs' is not allowed here");
		t.ts1("F09", "schema{ node{ name \"x\" choice{ } } }", s + "/schema/node/choice: component 'choice' is empty");
		t.ts1("F10", "schema{ choice{ } }", s + "/schema/choice: component 'choice' is not allowed here");
		t.ts1("F11", "schema{ name \"mobile\" }", s + "/schema/name: attribute 'name' is not allowed here");
		t.ts1("F12", "schema{ node{ type \"phone\" } }", s + "/schema/node/type: content type 'phone' is unknown");
		t.ts1("F13", "schema{ node{ name \"mobile\" type \"phone\" } }", s + "/schema/node/type: content type 'phone' is unknown");
		t.ts1("F14", "schema{ node{ name \"phone\" type \"string\" } node{ type \"phone\" nullable \"false\"} }", s + "/schema/node[2]: attribute 'nullable' is not allowed here");
		t.ts1("F15", "schema{ note{ } }", s + "/schema/note: component 'note' is unknown");
		t.ts1("F16", "schema{ node{ } }", s + "/schema/node: component 'node' is empty");
		t.ts1("F17", "schema{ node{ name \"x\" note{} } }", s + "/schema/node/note: component 'note' is unknown");
		t.ts1("F18", "schema{ node{ name \"x\" node{} } }", s + "/schema/node/node: component 'node' is empty");
		t.ts1("F19", "schema{ node{ name \"x\" schema{} } }", s + "/schema/node/schema: component 'schema' is unknown");
		t.ts1("F20", "schema{ node{ test \"\" } }", s + "/schema/node/test: attribute 'test' is unknown");
		System.out.print("\n              ");
		t.ts1("F21", "schema{ node{ type \"\" } }", s + "/schema/node/type: attribute 'type' is empty");
		t.ts1("F22", "schema{ node{ type \"string\" } }", s + "/schema/node: attribute 'name' is missing");
		t.ts1("F23", "schema{ node{ type \"string\" node{} } }", s + "/schema/node: attribute 'type' is not allowed here");
		t.ts1("F24", "schema{ node{ type \"string\" type \"\" } }", s + "/schema/node/type[1]: attribute 'type' can occur only once");
		t.ts1("F25", "schema{ node{ name \"m\" node{ name \"x\" type \"string\" occurs \"\" } } }", s + "/schema/node/node/occurs: attribute 'occurs' is empty");
		t.ts1("F26", "schema{ node{ name \"m\" node{ name \"x\" type \"string\" occurs \"-1\" } } }", s + "/schema/node/node/occurs: occurs '-1' is invalid; negative values are not allowed");
		t.ts1("F27", "schema{ node{ name \"m\" node{ name \"x\" type \"string\" occurs \"a\" } } }", s + "/schema/node/node/occurs: occurs 'a' is invalid; missing or non-integer value(s)");
		t.ts1("F28", "schema{ node{ name \"c\" choice{ node{ name \"x\" type \"string\" } } } }", s + "/schema/node/choice: component 'choice' is incomplete");
		t.ts1("F29", "schema{ node{ name \"c\" choice{ name \"x\" } } }", s + "/schema/node/choice: attribute 'name' is not allowed here");
		t.ts1("F30", "schema{ node{ name \"c\" choice{ nullable \"true\" } } }", s + "/schema/node/choice: attribute 'nullable' is not allowed here");
		t.ts1("F31", "schema{ node{ name \"x\" type \"string\" length \"\" } }", s + "/schema/node/length: attribute 'length' is empty");
		t.ts1("F32", "schema{ node{ name \"x\" type \"binary\" length \"-1\" } }", s + "/schema/node/length: length '-1' is invalid; negative values are not allowed");
		t.ts1("F33", "schema{ node{ name \"x\" type \"boolean\" length \"5\" } }", s + "/schema/node: attribute 'length' is not allowed here");
		t.ts1("F34", "schema{ node{ name \"x\" type \"boolean\" nullable \"maybe\" } }", s + "/schema/node/nullable: nullable 'maybe' is invalid; must be 'true' or 'false'");
		t.ts1("F35", "schema{ node{ name \"x\" type \"integer\" value \"\" } }", s + "/schema/node/value: attribute 'value' is empty");
		t.ts1("F36", "schema{ node{ name \"x\" type \"decimal\" value \"[1..-1]\" } }", s + "/schema/node/value: value '[1..-1]' is invalid; lower limit exceeds upper limit");
		t.ts1("F37", "schema{ node{ name \"x\" type \"boolean\" value \"5\" } }", s + "/schema/node: attribute 'value' is not allowed here");
		t.ts1("F38", "schema{ node{ type \"any\" nullable \"true\" } }", s + "/schema/node: attribute 'nullable' is not allowed here");
		t.ts1("F39", "schema{ node{ name \"123\" type \"string\" } }", s + "/schema/node/name: '123' is not a valid node name");
		t.ts1("F40", "schema{ node{ name \"123\" node{ name \"name\" type \"string\" } } }", s + "/schema/node/name: '123' is not a valid node name");
		t.ts1("F41", "schema{ node{ name \"phone\" type \"string\" } node{ name \"123\" type \"phone\" } }", s + "/schema/node[2]/name: '123' is not a valid node name");
	}

}

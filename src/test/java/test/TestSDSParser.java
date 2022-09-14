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
		t.ts1("S01", "schema { node \"name\" { type \"string\" } }", null);
		t.ts1("S02", "schema { node \"contact\" { node \"name\" { type \"string\" } } }", null);
		t.ts1("S03", "schema { node \"contact\" { node \"name\" { type \"string\" occurs \"0..1\" } } }", null);
		t.ts1("S04", "schema { node \"book\" { node \"contact\" { occurs \"1..*\" node \"name\" { type \"string\" } } } }", null);
		t.ts1("S05", "schema { node \"phone\" { type \"string\" } node { type \"phone\" } }", null);
		t.ts1("S06", "schema { node \"phone\" { type \"string\" } node \"mobile\" { type \"phone\" } }", null);
		t.ts1("S07", "schema { type \"phone\" node \"phone\" { type \"string\" } node { type \"phone\" } }", null);
		t.ts1("S08", "schema { type \"mobile\" node \"phone\" { type \"string\" } node \"mobile\" { type \"phone\" } }", null);
		t.ts1("S09", "schema { node \"g\" { group { node \"x\" { type \"string\" } node \"y\" { type \"string\" } } } }", null);
		t.ts1("S10", "schema { node \"c\" { choice { node \"x\" { type \"string\" } node \"y\" { type \"string\" } } } }", null);
		t.ts1("S11", "schema { node \"u\" { unordered { node \"x\" { type \"string\" } node \"y\" { type \"string\" } } } }", null);
		t.ts1("S12", "schema { node \"ean13\" { type \"string\" length \"13\" } }", null);
		t.ts1("S13", "schema { node \"image\" { type \"binary\" length \"0..1024\" } }", null);
		t.ts1("S14", "schema { node \"bool\" { type \"boolean\" nullable \"true\" } }", null);
		t.ts1("S15", "schema { node \"id\" { type \"string\" nullable \"false\" } }", null);
		t.ts1("S16", "schema { node \"id\" { type \"string\" pattern \"[^\\\\s]\" } }", null);
		t.ts1("S17", "schema { node \"bit\" { type \"integer\" value \"[0..1]\" } }", null);
		t.ts1("S18", "schema { node \"one\" { type \"integer\" value \"1\" } }", null);
		t.ts1("S19", "schema { node \"pi\" { type \"decimal\" value \"3.14\" } }", null);
		t.ts1("S20", "schema { node \"kelvin\" { type \"decimal\" value \"[-273.15..*)\" } }", null);
		t.ts1("S21", "schema { node \"now\" { type \"datetime\" value \"2020-08-11T17:55:00+02:00\" } }", null);
		t.ts1("S22", "schema { node \"today\" { type \"datetime\" value \"[2020-08-11T00:00:00+02:00..2020-08-12T00:00:00+02:00)\" } }", null);
		t.ts1("S23", "schema { node \"today\" { type \"date\" value \"2020-08-11\" } }", null);
		t.ts1("S24", "schema { node \"august\" { type \"date\" value \"[2020-08-01..2020-09-01)\" } }", null);
		t.ts1("S25", "schema { node \"anything\" { type \"any\" } }", null);
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
		t.ts1("F09", "schema{ node \"x\" { choice{ } } }", s + "/schema/node/choice: component 'choice' is empty");
		t.ts1("F10", "schema{ choice{ } }", s + "/schema/choice: component 'choice' is not allowed here");
		t.ts1("F11", "schema{ name \"mobile\" }", s + "/schema/name: attribute 'name' is unknown");
		t.ts1("F12", "schema{ node{ type \"phone\" } }", s + "/schema/node/type: content type 'phone' is unknown");
		t.ts1("F13", "schema{ node \"mobile\" { type \"phone\" } }", s + "/schema/node/type: content type 'phone' is unknown");
		t.ts1("F14", "schema{ node \"phone\" { type \"string\" } node{ type \"phone\" nullable \"false\"} }", s + "/schema/node[2]: attribute 'nullable' is not allowed here");
		t.ts1("F15", "schema{ note{ } }", s + "/schema/note: component 'note' is unknown");
		t.ts1("F16", "schema{ node{ } }", s + "/schema/node: component 'node' is empty");
		t.ts1("F17", "schema{ node \"x\" { note{} } }", s + "/schema/node/note: component 'note' is unknown");
		t.ts1("F18", "schema{ node \"x\" { node{} } }", s + "/schema/node/node: component 'node' is empty");
		t.ts1("F19", "schema{ node \"x\" { schema{} } }", s + "/schema/node/schema: component 'schema' is unknown");
		t.ts1("F20", "schema{ node{ test \"\" } }", s + "/schema/node/test: attribute 'test' is unknown");
		System.out.print("\n              ");
		t.ts1("F21", "schema{ node{ type \"\" } }", s + "/schema/node/type: attribute 'type' is empty");
		t.ts1("F22", "schema{ node{ type \"string\" } }", s + "/schema/node: '' is not a valid node name");
		t.ts1("F23", "schema{ node{ type \"string\" node{} } }", s + "/schema/node: '' is not a valid node name");
		t.ts1("F24", "schema{ node{ type \"string\" type \"\" } }", s + "/schema/node/type[1]: attribute 'type' can occur only once");
		t.ts1("F25", "schema{ node \"m\" { node \"x\" { type \"string\" occurs \"\" } } }", s + "/schema/node/node/occurs: attribute 'occurs' is empty");
		t.ts1("F26", "schema{ node \"m\" { node \"x\" { type \"string\" occurs \"-1\" } } }", s + "/schema/node/node/occurs: occurs '-1' is invalid; negative values are not allowed");
		t.ts1("F27", "schema{ node \"m\" { node \"x\" { type \"string\" occurs \"a\" } } }", s + "/schema/node/node/occurs: occurs 'a' is invalid; missing or non-integer value(s)");
		t.ts1("F28", "schema{ node \"c\" { choice{ node{ name \"x\" type \"string\" } } } }", s + "/schema/node/choice: component 'choice' is incomplete");
		t.ts1("F29", "schema{ node \"c\" { choice{ name \"x\" } } }", s + "/schema/node/choice/name: attribute 'name' is unknown");
		t.ts1("F30", "schema{ node \"c\" { choice{ nullable \"true\" } } }", s + "/schema/node/choice: attribute 'nullable' is not allowed here");
		t.ts1("F31", "schema{ node \"x\" { type \"string\" length \"\" } }", s + "/schema/node/length: attribute 'length' is empty");
		t.ts1("F32", "schema{ node \"x\"{ type \"binary\" length \"-1\" } }", s + "/schema/node/length: length '-1' is invalid; negative values are not allowed");
		t.ts1("F33", "schema{ node \"x\" { type \"boolean\" length \"5\" } }", s + "/schema/node: attribute 'length' is not allowed here");
		t.ts1("F34", "schema{ node \"x\" { type \"boolean\" nullable \"maybe\" } }", s + "/schema/node/nullable: nullable 'maybe' is invalid; must be 'true' or 'false'");
		t.ts1("F35", "schema{ node \"x\" { type \"integer\" value \"\" } }", s + "/schema/node/value: attribute 'value' is empty");
		t.ts1("F36", "schema{ node \"x\" { type \"decimal\" value \"[1..-1]\" } }", s + "/schema/node/value: value '[1..-1]' is invalid; lower limit exceeds upper limit");
		t.ts1("F37", "schema{ node \"x\" { type \"boolean\" value \"5\" } }", s + "/schema/node: attribute 'value' is not allowed here");
		t.ts1("F38", "schema{ node { type \"any\" nullable \"true\" } }", s + "/schema/node: attribute 'nullable' is not allowed here");
		t.ts1("F39", "schema{ node \"123\" { type \"string\" } }", s + "/schema/node: '123' is not a valid node name");
		t.ts1("F41", "schema{ node \"phone\" { type \"string\" } node \"123\" { type \"phone\" } }", s + "/schema/node[2]: '123' is not a valid node name");
	}

}

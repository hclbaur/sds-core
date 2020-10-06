package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import be.baur.sda.ComplexNode;
import be.baur.sda.parse.Parser;
import be.baur.sda.parse.SyntaxException;
import be.baur.sds.Schema;
import be.baur.sds.SchemaException;

public final class TestSchema 
{
	public static void main(String[] args) throws Exception {
		
		Test t = new Test(s -> {
			try {
				return Schema.from( (ComplexNode) parser.parse(new StringReader(s)) ).toString();
			} catch (SyntaxException | SchemaException | IOException e) {
				return e.getMessage();
			}
		});
		
		/* test schema from file */
		InputStream input = Schema.class.getResourceAsStream("/addressbook.sds");
		//Schema schema = 
		Schema.parse(new InputStreamReader(input,"UTF-8"));
		//(new Renderer()).render(new PrintWriter(System.out), schema.toNode(), 4);
		
		/* test valid SDS */
		t.test("S01", "schema{ node{ name \"name\" type \"string\" } }", null);
		t.test("S02", "schema{ node{ name \"contact\" node{ name \"name\" type \"string\" } } }", null);
		t.test("S03", "schema{ node{ name \"contact\" node{ name \"name\" type \"string\" multiplicity \"0..1\" } } }", null);
		t.test("S04", "schema{ node{ name \"book\" node{ name \"contact\" multiplicity \"1..*\" node{ name \"name\" type \"string\" } } } }", null);
		t.test("S05", "schema{ node{ name \"phone\" type \"string\" } node{ type \"phone\" } }", null);
		t.test("S06", "schema{ node{ name \"phone\" type \"string\" } node{ name \"mobile\" type \"phone\" } }", null);
		t.test("S07", "schema{ type \"phone\" node{ name \"phone\" type \"string\" } node{ type \"phone\" } }", null);
		t.test("S08", "schema{ type \"mobile\" node{ name \"phone\" type \"string\" } node{ name \"mobile\" type \"phone\" } }", null);
		
		/* test invalid SDS */
		try { 
			Schema.parse(new StringReader("name \"name\""));
			throw new Exception("SchemaException expected here!");
		}
		catch (SchemaException e) {};
		
		String s = "SDS syntax violation at ";
		t.test("F01", "node{ name \"name\" type \"string\" }", s + "/node: component 'schema' is expected");
		t.test("F02", "schema{ }", s + "/schema: component 'schema' is empty");
		t.test("F03", "schema{ test \"\" }", s + "/schema/test: attribute 'test' is unknown");
		t.test("F04", "schema{ type \"\" }", s + "/schema/type: attribute 'type' is empty");
		t.test("F05", "schema{ type \"string\" }", s + "/schema/type: type 'string' is invalid; no such root node");
		t.test("F06", "schema{ note{} }", s + "/schema/note: component 'note' is unknown");
		t.test("F07", "schema{ node{} }", s + "/schema/node: component 'node' is empty");
		t.test("F08", "schema{ choice{} }", s + "/schema/choice: component 'choice' is not allowed here");
		t.test("F10", "schema{ name \"mobile\" }", s + "/schema/name: attribute 'name' is not allowed here");
		t.test("F11", "schema{ node{ type \"phone\" } }", s + "/schema/node/type: content type 'phone' is unknown");
		t.test("F12", "schema{ node{ name \"mobile\" type \"phone\" } }", s + "/schema/node/type: content type 'phone' is unknown");
		t.test("F13", "schema{ node{ name \"name\" type \"string\" multiplicity \"1\" } }", s + "/schema/node: attribute 'multiplicity' is not allowed here");
		t.test("F14", "schema{ node{ name \"phone\" type \"string\" } node{ type \"phone\" nullable \"false\"} }", s + "/schema/node: attribute 'nullable' is not allowed here");
	}

	private static Parser parser = new Parser();
}

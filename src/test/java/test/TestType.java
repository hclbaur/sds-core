package test;

import java.io.IOException;
import java.io.StringReader;

import be.baur.sda.ComplexNode;
import be.baur.sda.parse.Parser;
import be.baur.sda.parse.SyntaxException;
import be.baur.sds.SchemaException;
import be.baur.sds.Type;

public final class TestType {

	public static void main(String[] args) {

		Test t = new Test(s -> {
			try {
				return Type.from( (ComplexNode) parser.parse(new StringReader(s)) ).toString();
			} catch (SyntaxException | SchemaException | IOException e) {
				return e.getMessage();
			}
		});
		
		/* test valid SDS */
		t.test("S01", "node{ name \"name\" type \"string\" multiplicity \"2\" }", null);
		t.test("S02", "node{ name \"contact\" multiplicity \"0..1\" node{ name \"name\" type \"string\" } }", null);
		t.test("S03", "group{ node{ name \"x\" type \"string\" } node{ name \"y\" type \"string\" } }", null);
		t.test("S04", "choice{ node{ name \"x\" type \"string\" } node{ name \"y\" type \"string\" } }", null);
		t.test("S05", "unordered{ node{ name \"x\" type \"string\" } node{ name \"y\" type \"string\" } }", null);
		
		/* test invalid SDS */
		String s = "SDS syntax violation at ";
		t.test("F01", "note{ }", s + "/note: component 'note' is unknown");
		t.test("F02", "node{ }", s + "/node: component 'node' is empty");
		t.test("F03", "node{ name \"x\" name{} }", s + "/node/name: component 'name' is unknown");
		t.test("F04", "node{ name \"x\" node{} }", s + "/node/node: component 'node' is empty");
		t.test("F05", "node{ name \"x\" schema{} }", s + "/node/schema: component 'schema' is not allowed here");
		t.test("F06", "node{ test \"\" }", s + "/node/test: attribute 'test' is unknown");
		t.test("F07", "node{ type \"\" }", s + "/node/type: attribute 'type' is empty");
		t.test("F08", "node{ type \"phone\" }", s + "/node/type: content type 'phone' is unknown");
		t.test("F09", "node{ name \"mobile\" type \"phone\" }", s + "/node/type: content type 'phone' is unknown");
		t.test("F10", "node{ type \"string\" }", s + "/node: attribute 'name' is missing");
		t.test("F11", "node{ type \"string\" node{} }", s + "/node: attribute 'type' is not allowed here");
		t.test("F12", "node{ type \"string\" type \"\" }", s + "/node/type: attribute 'type' can occur only once");
		t.test("F13", "node{ name \"x\" type \"string\" multiplicity \"\" }", s + "/node/multiplicity: attribute 'multiplicity' is empty");
		t.test("F14", "node{ name \"x\" type \"string\" multiplicity \"-1\" }", s + "/node/multiplicity: multiplicity '-1' is invalid; negative values are not allowed");
		t.test("F15", "node{ name \"x\" type \"string\" multiplicity \"a\" }", s + "/node/multiplicity: multiplicity 'a' is invalid; missing or non-integer value(s)");
		t.test("F16", "group{ }", s + "/group: component 'group' is empty");
		t.test("F17", "choice{ node{ name \"x\" type \"string\" } }", s + "/choice: component 'choice' is incomplete");
		t.test("F18", "unordered{ name \"x\" }", s + "/unordered: attribute 'name' is not allowed here");
		t.test("F19", "group{ nullable \"true\" }", s + "/group: attribute 'nullable' is not allowed here");
	}
	
	private static Parser parser = new Parser();
}

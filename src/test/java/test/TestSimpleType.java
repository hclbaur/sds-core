package test;

import java.io.IOException;
import java.io.StringReader;

import be.baur.sda.ComplexNode;
import be.baur.sda.parse.Parser;
import be.baur.sda.parse.SyntaxException;
import be.baur.sds.SchemaException;
import be.baur.sds.Type;

public final class TestSimpleType {

	public static void main(String[] args) {

		Test t = new Test(s -> {
			try {
				return Type.from( (ComplexNode) parser.parse(new StringReader(s)) ).toString();
			} catch (SyntaxException | SchemaException | IOException e) {
				return e.getMessage();
			}
		});
		
		/* test valid SDS */
		t.test("S01", "node{ name \"ean13\" type \"string\" length \"13\" }", null);
		t.test("S02", "node{ name \"image\" type \"binary\" length \"0..1024\" }", null);
		t.test("S03", "node{ name \"bool\" type \"boolean\" nullable \"true\" }", null);
		t.test("S04", "node{ name \"id\" type \"string\" nullable \"false\" }", null);
		t.test("S05", "node{ name \"id\" type \"string\" pattern \"[^\\\\s]\" }", null);
		t.test("S06", "node{ name \"bit\" type \"integer\" value \"[0..1]\" }", null);
		t.test("S07", "node{ name \"one\" type \"integer\" value \"1\" }", null);
		t.test("S08", "node{ name \"pi\" type \"decimal\" value \"3.14\" }", null);
		t.test("S09", "node{ name \"kelvin\" type \"decimal\" value \"[-273.15..*)\" }", null);
		t.test("S10", "node{ name \"now\" type \"datetime\" value \"2020-08-11T17:55:00+02:00\" }", null);
		t.test("S11", "node{ name \"today\" type \"datetime\" value \"[2020-08-11T00:00:00+02:00..2020-08-12T00:00:00+02:00)\" }", null);
		t.test("S12", "node{ name \"today\" type \"date\" value \"2020-08-11\" }", null);
		t.test("S13", "node{ name \"august\" type \"date\" value \"[2020-08-01..2020-09-01)\" }", null);
		t.test("S14", "node{ name \"anything\" type \"any\" }", null);
		t.test("S15", "node{ type \"any\" multiplicity \"0..*\" }", null);
		
		/* test invalid SDS */
		String s = "SDS syntax violation at ";
		t.test("F01", "node{ name \"name\" type \"string\" length \"\" }", s + "/node/length: attribute 'length' is empty");
		t.test("F02", "node{ name \"name\" type \"binary\" length \"-1\" }", s + "/node/length: length '-1' is invalid; negative values are not allowed");
		t.test("F03", "node { name \"bool\" type \"boolean\" length \"5\" }", s + "/node: attribute 'length' is not allowed here");
		t.test("F04", "node { name \"bool\" type \"boolean\" nullable \"maybe\" }", s + "/node/nullable: nullable 'maybe' is invalid; must be 'true' or 'false'");
		t.test("F05", "node{ name \"int\" type \"integer\" value \"\" }", s + "/node/value: attribute 'value' is empty");
		t.test("F06", "node{ name \"dec\" type \"decimal\" value \"[1..-1]\" }", s + "/node/value: value '[1..-1]' is invalid; lower limit exceeds upper limit");
		t.test("F07", "node { name \"bool\" type \"boolean\" value \"5\" }", s + "/node: attribute 'value' is not allowed here");
		t.test("F08", "node{ type \"any\" nullable \"true\" }", s + "/node: attribute 'nullable' is not allowed here");
	}
	
	private static Parser parser = new Parser();
}

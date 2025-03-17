package test;

import java.util.function.BiFunction;

import be.baur.sds.Schema;

public final class TestSDSTypes {

	public static void main(String[] args) throws Exception {
		
		BiFunction<String, String, String> bifun = (value,type) -> {
			try {
				return Schema.dataTypeConstructor(type).apply(value).toString();
			} catch (Exception e) {
				return e.getLocalizedMessage();
			}
		};
		
		Test t = new Test(bifun);
		
		t.ts2("S01", "true", "boolean", "true");
		t.ts2("S02", "false", "boolean", "false");
		t.ts2("S03", "", "string", "");
		t.ts2("S04", "abc", "string", "abc");
		t.ts2("S05", "", "binary", "[B@446cdf90");
		t.ts2("S06", "abc=", "binary", "[B@799f7e29");
		t.ts2("S07", "-00", "integer", "0");
		t.ts2("S08", "+00", "integer", "0");
		t.ts2("S09", "-00.", "decimal", "-0.0");
		t.ts2("S10", "+.00", "decimal", "0.0");
		t.ts2("S11", "1e-1", "decimal", "0.1");
		t.ts2("S12", "1968-02-29", "date", "1968-02-29");
		t.ts2("S13", "1968-02-29T12:00:00+01:00", "datetime", "1968-02-29T12:00+01:00");
		t.ts2("S14", "--02-29", "gMonthDay", "--02-29");
		t.ts2("S15", "NL38 INGB 0005111236", "IBAN", "NL38 INGB 0005 1112 36");
		
		t.ts2("F01", "maybe", "boolean", "either true or false is expected");
		t.ts2("F02", "a=", "binary", "Last unit does not have enough valid bits");
		t.ts2("F03", "a", "integer", "For input string: \"a\"");
		t.ts2("F04", "a", "decimal", "For input string: \"a\"");
		t.ts2("F05", "1968-02-30", "date", "Text '1968-02-30' could not be parsed: Invalid date 'FEBRUARY 30'");
		t.ts2("F06", "1968-02-30T12:00", "datetime", "Text '1968-02-30T12:00' could not be parsed at index 16");
		t.ts2("F07", "--02-30", "gMonthDay", "day 30 is invalid");
		t.ts2("F08", "NL38 INGB 0005111237", "IBAN", "invalid checksum");
	
	}

}

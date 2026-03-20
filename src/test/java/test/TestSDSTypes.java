package test;

import java.util.function.BiFunction;

import be.baur.sds.DataType;

public final class TestSDSTypes {

	public static void main(String[] args) throws Exception {
		
		BiFunction<String, String, String> bifun = (value,type) -> {
			try {
				Object o = DataType.getConstructor(type).apply(value);
				return type.equals("binary") ? new String((byte[])o) : o.toString();
			} catch (Exception e) {
				return e.getLocalizedMessage();
			}
		};
		
		Test t = new Test(bifun);
		
		t.ss("S01", "true", "boolean", "true");
		t.ss("S02", "false", "boolean", "false");
		t.ss("S03", "", "string", "");
		t.ss("S04", "abc", "string", "abc");
		t.ss("S05", "", "binary", "");
		t.ss("S06", "U0RT", "binary", "SDS");
		t.ss("S07", "-00", "integer", "0");
		t.ss("S08", "+00", "integer", "0");
		t.ss("S09", "-00.", "decimal", "-0.0");
		t.ss("S10", "+.00", "decimal", "0.0");
		t.ss("S11", "1e-1", "decimal", "0.1");
		t.ss("S12", "1968-02-29", "date", "1968-02-29");
		t.ss("S13", "1968-02-29T12:00:00+01:00", "datetime", "1968-02-29T12:00+01:00");
		t.ss("S14", "--02-29", "gMonthDay", "--02-29");
		t.ss("S15", "NL38 INGB 0005111236", "IBAN", "NL38 INGB 0005 1112 36");
		
		t.ss("F01", "maybe", "boolean", "either true or false is expected");
		t.ss("F02", "a=", "binary", "Last unit does not have enough valid bits");
		t.ss("F03", "a", "integer", "For input string: \"a\"");
		t.ss("F04", "a", "decimal", "For input string: \"a\"");
		t.ss("F05", "1968-02-30", "date", "Text '1968-02-30' could not be parsed: Invalid date 'FEBRUARY 30'");
		t.ss("F06", "1968-02-30T12:00", "datetime", "Text '1968-02-30T12:00' could not be parsed at index 16");
		t.ss("F07", "--02-30", "gMonthDay", "day 30 is invalid");
		t.ss("F08", "NL38 INGB 0005111237", "IBAN", "invalid checksum");
	
		t.checkFailures();
	}

}

package test;

import be.baur.sds.common.NaturalInterval;

public final class TestNaturalInterval {

	public static void main(String[] args) throws Exception {

		Test t = new Test(s -> NaturalInterval.from(s).toString());
		
		/* test valid cases */
		t.test("S01", "0", "0");
		t.test("S02", "1", "1");
		t.test("S03", "11", "11");
		t.test("S04", "0..0", "0");
		t.test("S05", "0..1", "0..1");
		t.test("S06", "0..11", "0..11");
		t.test("S07", "0..*", "0..*");
		t.test("S08", "1..1", "1");
		t.test("S09", "1..11", "1..11");
		t.test("S10", "1..*", "1..*");
		t.test("S11", "-0..*", "0..*"); 	/* odd? */
		
		/* test invalid cases */
		t.testError("F01", "", "no interval specified");
		t.testError("F02", " ", "no interval specified");
		t.testError("F03", "a", "missing or non-integer value(s)");
		t.testError("F04", "-1", "negative values are not allowed");
		t.testError("F05", "..", "missing or non-integer value(s)");
		t.testError("F06", "a..", "missing or non-integer value(s)");
		t.testError("F07", "1..a", "missing or non-integer value(s)");
		t.testError("F08", "*..*", "missing or non-integer value(s)");	/* odd */
		t.testError("F09", "-1..1", "negative values are not allowed");
		t.testError("F10", "2..1", "lower limit exceeds upper limit");
		t.testError("F11", "2..-1", "negative values are not allowed");
	}

}

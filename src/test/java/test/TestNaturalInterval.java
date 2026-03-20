package test;

import be.baur.sds.common.NaturalInterval;

public final class TestNaturalInterval {

	public static void main(String[] args) throws Exception {

		Test t1 = new Test(s -> {
			try {
				return NaturalInterval.from(s).toString();
			} catch (Exception e) {	return e.getMessage(); }
		});
		
		Test t2 = new Test( (s1,s2) -> NaturalInterval.from(s2).contains(Integer.parseInt(s1)) + "");
		
		// test valid intervals
		t1.s("S01", "0", "0");
		t1.s("S02", "1", "1");
		t1.s("S03", "11", "11");
		t1.s("S04", "0..0", "0");
		t1.s("S05", "0..1", "0..1");
		t1.s("S06", "0..11", "0..11");
		t1.s("S07", "0..*", "0..*");
		t1.s("S08", "1..1", "1");
		t1.s("S09", "1..11", "1..11");
		t1.s("S10", "1..*", "1..*");
		t1.s("S11", "-0..*", "0..*"); 	/* odd? */
		
		// test contains()
		t2.ss("S12", "0", "1", "-1");
		t2.ss("S13", "1", "1", "0");
		t2.ss("S14", "2", "1", "1");
		t2.ss("S15", "0", "1..2", "-1");
		t2.ss("S16", "1", "1..2", "0");
		t2.ss("S17", "2", "1..2", "0");
		t2.ss("S18", "3", "1..2", "1");
		t2.ss("S19", "-1", "0..*", "-1");
		t2.ss("S20", Integer.MAX_VALUE + "", "0..*", "0");
		
		// test invalid intervals
		t1.s("F01", "", "no interval specified");
		t1.s("F02", " ", "no interval specified");
		t1.s("F03", "a", "missing or non-integer value(s)");
		t1.s("F04", "-1", "negative values are not allowed");
		t1.s("F05", "..", "missing or non-integer value(s)");
		t1.s("F06", "a..", "missing or non-integer value(s)");
		t1.s("F07", "1..a", "missing or non-integer value(s)");
		t1.s("F08", "*..*", "missing or non-integer value(s)");	/* odd */
		t1.s("F09", "-1..1", "negative values are not allowed");
		t1.s("F10", "2..1", "lower limit exceeds upper limit");
		t1.s("F11", "2..-1", "negative values are not allowed");

		t1.checkFailures();
		t2.checkFailures();
	}

}

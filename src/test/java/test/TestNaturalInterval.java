package test;

import be.baur.sds.common.NaturalInterval;

public final class TestNaturalInterval {

	public static void main(String[] args) throws Exception {

		Test t1 = new Test( s -> NaturalInterval.from(s).toString() );
		Test t2 = new Test( (s1,s2) -> NaturalInterval.from(s2).contains(Integer.parseInt(s1)) + "");
		
		// test valid intervals
		t1.ts1("S01", "0", "0");
		t1.ts1("S02", "1", "1");
		t1.ts1("S03", "11", "11");
		t1.ts1("S04", "0..0", "0");
		t1.ts1("S05", "0..1", "0..1");
		t1.ts1("S06", "0..11", "0..11");
		t1.ts1("S07", "0..*", "0..*");
		t1.ts1("S08", "1..1", "1");
		t1.ts1("S09", "1..11", "1..11");
		t1.ts1("S10", "1..*", "1..*");
		t1.ts1("S11", "-0..*", "0..*"); 	/* odd? */
		
		// test contains()
		t2.ts2("S12", "0", "1", "-1");
		t2.ts2("S13", "1", "1", "0");
		t2.ts2("S14", "2", "1", "1");
		t2.ts2("S15", "0", "1..2", "-1");
		t2.ts2("S16", "1", "1..2", "0");
		t2.ts2("S17", "2", "1..2", "0");
		t2.ts2("S18", "3", "1..2", "1");
		t2.ts2("S19", "-1", "0..*", "-1");
		t2.ts2("S20", Integer.MAX_VALUE + "", "0..*", "0");
		
		// test invalid intervals
		t1.ts1Error("F01", "", "no interval specified");
		t1.ts1Error("F02", " ", "no interval specified");
		t1.ts1Error("F03", "a", "missing or non-integer value(s)");
		t1.ts1Error("F04", "-1", "negative values are not allowed");
		t1.ts1Error("F05", "..", "missing or non-integer value(s)");
		t1.ts1Error("F06", "a..", "missing or non-integer value(s)");
		t1.ts1Error("F07", "1..a", "missing or non-integer value(s)");
		t1.ts1Error("F08", "*..*", "missing or non-integer value(s)");	/* odd */
		t1.ts1Error("F09", "-1..1", "negative values are not allowed");
		t1.ts1Error("F10", "2..1", "lower limit exceeds upper limit");
		t1.ts1Error("F11", "2..-1", "negative values are not allowed");

	}

}

package test;

import be.baur.sds.common.Interval;

public final class TestIntervalInteger {

	public static void main(String[] args) throws Exception {

		Test t1 = new Test(s -> {
			try {
				return Interval.from(s, Integer.class).toString();
			} catch (Exception e) {	return e.getMessage(); }
		});
		
		Test t2 = new Test( (s1,s2) -> {
			try {
				return Interval.from(s2, Integer.class).contains(new Integer(s1)) + "";
			} catch (Exception e) { return e.getMessage(); }
		});
		
		/* test valid cases */
		t1.t1("S01", "00", "0");
		t1.t1("S02", "-9", "-9");
		t1.t1("S03", "99", "99");
		t1.t1("S04", "[-1..1]", "[-1..1]");
		t1.t1("S05", "(-2..2)", "(-2..2)");
		t1.t1("S06", "[3..*)", "[3..*)");
		t1.t1("S07", "(*..-3]", "(*..-3]");
		t1.t1("S08", "[*..*]", "(*..*)");
		
		// test contains()
		t2.t2("S09", "0", "1", "-1");
		t2.t2("S10", "1", "1", "0");
		t2.t2("S11", "2", "1", "1");
		t2.t2("S12", "0", "[1..2]", "-1");
		t2.t2("S13", "1", "[1..2]", "0");
		t2.t2("S14", "2", "[1..2]", "0");
		t2.t2("S15", "3", "[1..2]", "1");
		t2.t2("S16", "1", "(1..3)", "-1");
		t2.t2("S17", "2", "(1..3)", "0");
		t2.t2("S18", "3", "(1..3)", "1");
		t2.t2("S19", Integer.MIN_VALUE + "", "[*..*]", "0");
		t2.t2("S20", Integer.MAX_VALUE + "", "[*..*]", "0");
		
		/* test invalid cases */
		t1.t1("F01", "", "no interval specified");
		t1.t1("F02", "..", "invalid interval notation");
		t1.t1("F03", "1..", "invalid interval notation");
		t1.t1("F04", "..1", "invalid interval notation");
		t1.t1("F05", "1..1", "invalid interval notation");
		
		t1.t1("F06", "[1]", "invalid limiting value");
		t1.t1("F07", "[a]", "invalid limiting value");
		t1.t1("F08", "[a..]", "invalid interval notation");
		t1.t1("F09", "[..a]", "invalid interval notation");
		t1.t1("F10", "[a..1]", "invalid lower limit");
		t1.t1("F11", "[1..a]", "invalid upper limit");		
		t1.t1("F12", "[1..0]", "lower limit exceeds upper limit");
	}

}

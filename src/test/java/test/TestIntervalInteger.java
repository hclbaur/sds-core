package test;

import be.baur.sds.DataType;
import be.baur.sds.common.Interval;

public final class TestIntervalInteger {

	public static void main(String[] args) throws Exception {

		Test t1 = new Test(s -> {
			try {
				return Interval.from(s, DataType.INTEGER_CONSTRUCTOR).toString();
			} catch (Exception e) {	return e.getMessage(); }
		});
		
		Test t2 = new Test( (s1,s2) -> {
			try {
				return Interval.from(s2, DataType.INTEGER_CONSTRUCTOR).contains(DataType.INTEGER_CONSTRUCTOR.apply(s1))+"";
			} catch (Exception e) { return e.getMessage(); }
		});
		
		/* test valid cases */
		t1.s("S01", "00", "0");
		t1.s("S02", "-9", "-9");
		t1.s("S03", "99", "99");
		t1.s("S04", "[-1 ..1]", "[-1..1]");
		t1.s("S05", "(-2.. 2)", "(-2..2)");
		t1.s("S06", "[3..*)", "[3..*)");
		t1.s("S07", "(*..-3]", "(*..-3]");
		t1.s("S08", "[*..*]", "(*..*)");
		
		// test contains()
		t2.ss("S09", "0", "1", "-1");
		t2.ss("S10", "1", "1", "0");
		t2.ss("S11", "2", "1", "1");
		t2.ss("S12", "0", "[1..2]", "-1");
		t2.ss("S13", "1", "[1..2]", "0");
		t2.ss("S14", "2", "[1..2]", "0");
		t2.ss("S15", "3", "[1..2]", "1");
		t2.ss("S16", "1", "(1..3)", "-1");
		t2.ss("S17", "2", "(1..3)", "0");
		t2.ss("S18", "3", "(1..3)", "1");
		t2.ss("S19", Integer.MIN_VALUE + "", "[*..*]", "0");
		t2.ss("S20", Integer.MAX_VALUE + "", "[*..*]", "0");
		
		/* test invalid cases */
		t1.s("F01", "", "no interval specified");
		t1.s("F02", "..", "invalid interval notation");
		t1.s("F03", "1..", "invalid interval notation");
		t1.s("F04", "..1", "invalid interval notation");
		t1.s("F05", "1..1", "invalid interval notation");
		
		t1.s("F06", "[1]", "invalid limiting value");
		t1.s("F07", "[a]", "invalid limiting value");
		t1.s("F08", "[a..]", "invalid interval notation");
		t1.s("F09", "[..a]", "invalid interval notation");
		t1.s("F10", "[a..1]", "invalid lower limit");
		t1.s("F11", "[1..a]", "invalid upper limit");		
		t1.s("F12", "[1..0]", "lower limit exceeds upper limit");
		
		t1.checkFailures();
		t2.checkFailures();
	}

}

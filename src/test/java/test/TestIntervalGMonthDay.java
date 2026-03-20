package test;

import be.baur.sds.common.Interval;
import samples.types.GMonthDay;

public final class TestIntervalGMonthDay {

	public static void main(String[] args) throws Exception {

		Test t1 = new Test(s -> {
			try {
				return Interval.from(s, GMonthDay.CONSTRUCTOR).toString();
			} catch (Exception e) {	return e.getMessage(); }
		});
		
		Test t2 = new Test( (s1,s2) -> {
			try {
				return Interval.from(s2, GMonthDay.CONSTRUCTOR).contains(GMonthDay.CONSTRUCTOR.apply(s1))+"";
			} catch (Exception e) { return e.getMessage(); }
		});
		
		/* test valid cases */
		t1.s("S01", "--01-01", "--01-01");
		t1.s("S02", "--12-31", "--12-31");
		t1.s("S03", "[--02-01 ..--02-29]", "[--02-01..--02-29]");
		t1.s("S04", "(--01-31.. --03-01)", "(--01-31..--03-01)");
		t1.s("S05", "[--01-01..*)", "[--01-01..*)");
		t1.s("S06", "(*..--12-31]", "(*..--12-31]");
		t1.s("S07", "[*..*]", "(*..*)");
		
		// test contains()
		t2.ss("S08", "--01-01", "--01-02", "-1");
		t2.ss("S09", "--01-01", "--01-01", "0");
		t2.ss("S10", "--01-02", "--01-01", "1");
		t2.ss("S11", "--01-01", "[--01-02..--01-03]", "-1");
		t2.ss("S12", "--01-02", "[--01-02..--01-03]", "0");
		t2.ss("S13", "--01-03", "[--01-02..--01-03]", "0");
		t2.ss("S14", "--01-04", "[--01-02..--01-03]", "1");
		t2.ss("S15", "--01-01", "(--01-01..--01-03)", "-1");
		t2.ss("S16", "--01-02", "(--01-01..--01-03)", "0");
		t2.ss("S17", "--01-03", "(--01-01..--01-03)", "1");
		t2.ss("S18", GMonthDay.MIN_VALUE + "", "[*..*]", "0");
		t2.ss("S19", GMonthDay.MAX_VALUE + "", "[*..*]", "0");
		
		/* test invalid cases */
		t1.s("F01", "", "no interval specified");
		t1.s("F02", "..", "invalid interval notation");
		t1.s("F03", "--01-01..", "invalid interval notation");
		t1.s("F04", "..--12-31", "invalid interval notation");
		t1.s("F05", "--01-01..--01-01", "invalid interval notation");
		
		t1.s("F06", "[--01-01]", "invalid limiting value");
		t1.s("F07", "[a]", "invalid limiting value");
		t1.s("F08", "[a..]", "invalid interval notation");
		t1.s("F09", "[..a]", "invalid interval notation");
		t1.s("F10", "[a..--12-31]", "invalid lower limit");
		t1.s("F11", "[--01-01..a]", "invalid upper limit");		
		t1.s("F12", "[--01-02..--01-01]", "lower limit exceeds upper limit");
		
		t1.checkFailures();
		t2.checkFailures();
	}

}

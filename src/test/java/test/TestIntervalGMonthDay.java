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
		t1.ts1("S01", "--01-01", "--01-01");
		t1.ts1("S02", "--12-31", "--12-31");
		t1.ts1("S03", "[--02-01 ..--02-29]", "[--02-01..--02-29]");
		t1.ts1("S04", "(--01-31.. --03-01)", "(--01-31..--03-01)");
		t1.ts1("S05", "[--01-01..*)", "[--01-01..*)");
		t1.ts1("S06", "(*..--12-31]", "(*..--12-31]");
		t1.ts1("S07", "[*..*]", "(*..*)");
		
		// test contains()
		t2.ts2("S08", "--01-01", "--01-02", "-1");
		t2.ts2("S09", "--01-01", "--01-01", "0");
		t2.ts2("S10", "--01-02", "--01-01", "1");
		t2.ts2("S11", "--01-01", "[--01-02..--01-03]", "-1");
		t2.ts2("S12", "--01-02", "[--01-02..--01-03]", "0");
		t2.ts2("S13", "--01-03", "[--01-02..--01-03]", "0");
		t2.ts2("S14", "--01-04", "[--01-02..--01-03]", "1");
		t2.ts2("S15", "--01-01", "(--01-01..--01-03)", "-1");
		t2.ts2("S16", "--01-02", "(--01-01..--01-03)", "0");
		t2.ts2("S17", "--01-03", "(--01-01..--01-03)", "1");
		t2.ts2("S18", GMonthDay.MIN_VALUE + "", "[*..*]", "0");
		t2.ts2("S19", GMonthDay.MAX_VALUE + "", "[*..*]", "0");
		
		/* test invalid cases */
		t1.ts1("F01", "", "no interval specified");
		t1.ts1("F02", "..", "invalid interval notation");
		t1.ts1("F03", "--01-01..", "invalid interval notation");
		t1.ts1("F04", "..--12-31", "invalid interval notation");
		t1.ts1("F05", "--01-01..--01-01", "invalid interval notation");
		
		t1.ts1("F06", "[--01-01]", "invalid limiting value");
		t1.ts1("F07", "[a]", "invalid limiting value");
		t1.ts1("F08", "[a..]", "invalid interval notation");
		t1.ts1("F09", "[..a]", "invalid interval notation");
		t1.ts1("F10", "[a..--12-31]", "invalid lower limit");
		t1.ts1("F11", "[--01-01..a]", "invalid upper limit");		
		t1.ts1("F12", "[--01-02..--01-01]", "lower limit exceeds upper limit");
	}

}

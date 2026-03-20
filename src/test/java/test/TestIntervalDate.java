package test;

import java.time.LocalDate;

import be.baur.sds.DataType;
import be.baur.sds.common.Interval;

public final class TestIntervalDate {

	public static void main(String[] args) throws Exception {

		Test t1 = new Test(s -> {
			try {
				return Interval.from(s, DataType.DATE_CONSTRUCTOR).toString();
			} catch (Exception e) { return e.getMessage(); }
		});
		
		Test t2 = new Test( (s1,s2) -> {
			try {
				return Interval.from(s2, DataType.DATE_CONSTRUCTOR).contains(DataType.DATE_CONSTRUCTOR.apply(s1))+"";
			} catch (Exception e) { return e.getMessage(); }
		});
		
		/* test valid cases */
		t1.s("S01", "1968-02-28", "1968-02-28");
		t1.s("S02", "1968-02-29", "1968-02-29");
		t1.s("S03", "[1968-02-28 ..1968-03-01]", "[1968-02-28..1968-03-01]");
		t1.s("S04", "(1968-02-28.. 1968-03-01)", "(1968-02-28..1968-03-01)");
		t1.s("S05", "[1968-02-28..*)", "[1968-02-28..*)");
		t1.s("S06", "(*..1968-02-28]", "(*..1968-02-28]");
		t1.s("S07", "[*..*]", "(*..*)");
		
		// test contains()
		t2.ss("S08", "1968-02-27", "1968-02-28", "-1");
		t2.ss("S09", "1968-02-28", "1968-02-28", "0");
		t2.ss("S10", "1968-02-29", "1968-02-28", "1");
		t2.ss("S11", "1968-02-27", "[1968-02-28..1968-02-29]", "-1");
		t2.ss("S12", "1968-02-28", "[1968-02-28..1968-02-29]", "0");
		t2.ss("S13", "1968-02-29", "[1968-02-28..1968-02-29]", "0");
		t2.ss("S14", "1969-03-01", "[1968-02-28..1968-02-29]", "1");
		t2.ss("S15", "1968-02-28", "(1968-02-28..1969-03-01)", "-1");
		t2.ss("S16", "1968-02-29", "(1968-02-28..1969-03-01)", "0");
		t2.ss("S17", "1969-03-01", "(1968-02-28..1969-03-01)", "1");
		t2.ss("S18", LocalDate.MIN + "", "[*..*]", "0");
		t2.ss("S19", LocalDate.MAX + "", "[*..*]", "0");
		
		/* test invalid cases */
		t1.s("F01", "", "no interval specified");
		t1.s("F02", "..", "invalid interval notation");
		t1.s("F03", "0000-00-00", "invalid limiting value");
		t1.s("F04", "1968-02-28..", "invalid interval notation");
		t1.s("F05", "..1968-02-28", "invalid interval notation");
		t1.s("F06", "1968-02-28..1968-02-28", "invalid interval notation");
		
		t1.s("F07", "[1968-02-28]", "invalid limiting value");
		t1.s("F08", "[a]", "invalid limiting value");
		t1.s("F09", "[a..]", "invalid interval notation");
		t1.s("F10", "[..a]", "invalid interval notation");
		t1.s("F11", "[a..1968-02-28]", "invalid lower limit");
		t1.s("F12", "[1968-02-28..a]", "invalid upper limit");		
		t1.s("F13", "[1968-02-29..1968-02-28]", "lower limit exceeds upper limit");
		
		t1.checkFailures();
		t2.checkFailures();
	}

}

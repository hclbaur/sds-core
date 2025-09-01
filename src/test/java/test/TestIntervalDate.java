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
		t1.ts1("S01", "1968-02-28", "1968-02-28");
		t1.ts1("S02", "1968-02-29", "1968-02-29");
		t1.ts1("S03", "[1968-02-28 ..1968-03-01]", "[1968-02-28..1968-03-01]");
		t1.ts1("S04", "(1968-02-28.. 1968-03-01)", "(1968-02-28..1968-03-01)");
		t1.ts1("S05", "[1968-02-28..*)", "[1968-02-28..*)");
		t1.ts1("S06", "(*..1968-02-28]", "(*..1968-02-28]");
		t1.ts1("S07", "[*..*]", "(*..*)");
		
		// test contains()
		t2.ts2("S08", "1968-02-27", "1968-02-28", "-1");
		t2.ts2("S09", "1968-02-28", "1968-02-28", "0");
		t2.ts2("S10", "1968-02-29", "1968-02-28", "1");
		t2.ts2("S11", "1968-02-27", "[1968-02-28..1968-02-29]", "-1");
		t2.ts2("S12", "1968-02-28", "[1968-02-28..1968-02-29]", "0");
		t2.ts2("S13", "1968-02-29", "[1968-02-28..1968-02-29]", "0");
		t2.ts2("S14", "1969-03-01", "[1968-02-28..1968-02-29]", "1");
		t2.ts2("S15", "1968-02-28", "(1968-02-28..1969-03-01)", "-1");
		t2.ts2("S16", "1968-02-29", "(1968-02-28..1969-03-01)", "0");
		t2.ts2("S17", "1969-03-01", "(1968-02-28..1969-03-01)", "1");
		t2.ts2("S18", LocalDate.MIN + "", "[*..*]", "0");
		t2.ts2("S19", LocalDate.MAX + "", "[*..*]", "0");
		
		/* test invalid cases */
		t1.ts1("F01", "", "no interval specified");
		t1.ts1("F02", "..", "invalid interval notation");
		t1.ts1("F03", "0000-00-00", "invalid limiting value");
		t1.ts1("F04", "1968-02-28..", "invalid interval notation");
		t1.ts1("F05", "..1968-02-28", "invalid interval notation");
		t1.ts1("F06", "1968-02-28..1968-02-28", "invalid interval notation");
		
		t1.ts1("F07", "[1968-02-28]", "invalid limiting value");
		t1.ts1("F08", "[a]", "invalid limiting value");
		t1.ts1("F09", "[a..]", "invalid interval notation");
		t1.ts1("F10", "[..a]", "invalid interval notation");
		t1.ts1("F11", "[a..1968-02-28]", "invalid lower limit");
		t1.ts1("F12", "[1968-02-28..a]", "invalid upper limit");		
		t1.ts1("F13", "[1968-02-29..1968-02-28]", "lower limit exceeds upper limit");
	}

}

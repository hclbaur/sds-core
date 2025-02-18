package test;

import java.time.Instant;

import be.baur.sds.common.Interval;
import be.baur.sds.types.DateTimeNodeType;

public final class TestIntervalDateTime {

	public static void main(String[] args) throws Exception {

		Test t1 = new Test(s -> {
			try {
				return Interval.from(s, DateTimeNodeType.VALUE_CONSTRUCTOR).toString();
			} catch (Exception e) { return e.getMessage(); }
		});
		
		Test t2 = new Test( (s1,s2) -> {
			try {
				return Interval.from(s2, DateTimeNodeType.VALUE_CONSTRUCTOR).contains(DateTimeNodeType.valueOf(s1))+"";
			} catch (Exception e) { return e.getMessage(); }
		});
		
		/* test valid cases */
		t1.ts1("S01", "1968-02-28T12:00:00+01:00", "1968-02-28T12:00+01:00");
		t1.ts1("S02", "1968-02-29T12:00:00.500+01:00", "1968-02-29T12:00:00.500+01:00");
		t1.ts1("S03", "[1968-02-28T12:00+01:00 ..1968-03-01T12:00+01:00]", "[1968-02-28T12:00+01:00..1968-03-01T12:00+01:00]");
		t1.ts1("S04", "(1968-02-28T12:00:00+01:00.. 1968-03-01T12:00:00+01:00)", "(1968-02-28T12:00+01:00..1968-03-01T12:00+01:00)");
		t1.ts1("S05", "[1968-02-28T12:00:00+01:00..*)", "[1968-02-28T12:00+01:00..*)");
		t1.ts1("S06", "(*..1968-02-28T12:00:00+01:00]", "(*..1968-02-28T12:00+01:00]");
		t1.ts1("S07", "[*..*]", "(*..*)");
		
		/* test invalid cases */
		t1.ts1("F01", "", "no interval specified");
		t1.ts1("F02", "..", "invalid interval notation");
		t1.ts1("F03", "0000-00-00T00:00:00+00:00", "invalid limiting value");
		t1.ts1("F04", "1968-02-28T12:00:00+01:00..", "invalid interval notation");
		t1.ts1("F05", "..1968-02-28T12:00:00+01:00", "invalid interval notation");
		t1.ts1("F06", "1968-02-28T12:00:00+01:00..1968-02-28T12:00:00+01:00", "invalid interval notation");
		
		// test contains()
		t2.ts2("S07", "1968-02-28T11:59:59+01:00", "1968-02-28T12:00:00+01:00", "-1");
		t2.ts2("S08", "1968-02-28T12:00:00+01:00", "1968-02-28T12:00:00+01:00", "0");
		t2.ts2("S09", "1968-02-28T12:00:01+01:00", "1968-02-28T12:00:00+01:00", "1");
		t2.ts2("S10", "1968-02-28T11:59:59+01:00", "[1968-02-28T12:00:00+01:00..1968-02-28T12:00:01+01:00]", "-1");
		t2.ts2("S11", "1968-02-28T12:00:00+01:00", "[1968-02-28T12:00:00+01:00..1968-02-28T12:00:01+01:00]", "0");
		t2.ts2("S12", "1968-02-28T12:00:01+01:00", "[1968-02-28T12:00:00+01:00..1968-02-28T12:00:01+01:00]", "0");
		t2.ts2("S13", "1968-02-28T12:00:02+01:00", "[1968-02-28T12:00:00+01:00..1968-02-28T12:00:01+01:00]", "1");
		t2.ts2("S14", "1968-02-28T12:00:00+01:00", "(1968-02-28T12:00:00+01:00..1968-02-28T12:00:02+01:00)", "-1");
		t2.ts2("S15", "1968-02-28T12:00:01+01:00", "(1968-02-28T12:00:00+01:00..1968-02-28T12:00:02+01:00)", "0");
		t2.ts2("S16", "1968-02-28T12:00:02+01:00", "(1968-02-28T12:00:00+01:00..1968-02-28T12:00:02+01:00)", "1");
		t2.ts2("S17", Instant.MIN.plusSeconds(3600*24*366) + "", "[*..*]", "0");
		t2.ts2("S18", Instant.MAX.minusSeconds(3600*24*366) + "", "[*..*]", "0");
		
		t1.ts1("F07", "[1968-02-28T12:00:00+01:00]", "invalid limiting value");
		t1.ts1("F08", "[a]", "invalid limiting value");
		t1.ts1("F09", "[a..]", "invalid interval notation");
		t1.ts1("F10", "[..a]", "invalid interval notation");
		t1.ts1("F11", "[a..1968-02-28T12:00:00+01:00]", "invalid lower limit");
		t1.ts1("F12", "[1968-02-28T12:00:00+01:00..a]", "invalid upper limit");		
		t1.ts1("F13", "[1968-02-29T12:00:00+01:00..1968-02-28T12:00:00+01:00]", "lower limit exceeds upper limit");
	}

}

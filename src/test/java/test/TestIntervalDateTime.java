package test;

import be.baur.sds.common.DateTime;
import be.baur.sds.common.Interval;

public final class TestIntervalDateTime {

	public static void main(String[] args) throws Exception {

		Test t = new Test(s -> {
			try {
				return Interval.from(s, DateTime.class).toString();
			} catch (Exception e) {
				return e.getMessage();
			}
		});
		
		/* test valid cases */
		t.test("S01", "1968-02-28T12:00:00+01:00", "1968-02-28T12:00:00+01:00");
		t.test("S02", "1968-02-29T12:00:00.500+01:00", "1968-02-29T12:00:00.5+01:00");
		t.test("S03", "[1968-02-28T12:00:00+01:00..1968-03-01T12:00:00+01:00]", "[1968-02-28T12:00:00+01:00..1968-03-01T12:00:00+01:00]");
		t.test("S04", "(1968-02-28T12:00:00+01:00..1968-03-01T12:00:00+01:00)", "(1968-02-28T12:00:00+01:00..1968-03-01T12:00:00+01:00)");
		t.test("S05", "[1968-02-28T12:00:00+01:00..*)", "[1968-02-28T12:00:00+01:00..*)");
		t.test("S06", "(*..1968-02-28T12:00:00+01:00]", "(*..1968-02-28T12:00:00+01:00]");
		t.test("S07", "[*..*]", "(*..*)");
		
		/* test invalid cases */
		t.test("F01", "", "no interval specified");
		t.test("F02", "..", "invalid interval notation");
		t.test("F03", "0000-00-00T00:00:00+00:00", "invalid limiting value");
		t.test("F04", "1968-02-28T12:00:00+01:00..", "invalid interval notation");
		t.test("F05", "..1968-02-28T12:00:00+01:00", "invalid interval notation");
		t.test("F06", "1968-02-28T12:00:00+01:00..1968-02-28T12:00:00+01:00", "invalid interval notation");
		
		t.test("F07", "[1968-02-28T12:00:00+01:00]", "invalid limiting value");
		t.test("F08", "[a]", "invalid limiting value");
		t.test("F09", "[a..]", "invalid interval notation");
		t.test("F10", "[..a]", "invalid interval notation");
		t.test("F11", "[a..1968-02-28T12:00:00+01:00]", "invalid lower limit");
		t.test("F12", "[1968-02-28T12:00:00+01:00..a]", "invalid upper limit");		
		t.test("F13", "[1968-02-29T12:00:00+01:00..1968-02-28T12:00:00+01:00]", "lower limit exceeds upper limit");
	}

}

package test;

import be.baur.sds.common.Interval;

public final class TestIntervalDecimal {

	public static void main(String[] args) throws Exception {

		Test t = new Test(s -> {
			try {
				return Interval.from(s, Double.class).toString();
			} catch (Exception e) {
				return e.getMessage();
			}
		});
		
		/* test valid cases */
		t.test("S00", "0", "0.0");
		t.test("S01", "-.50", "-0.5");
		t.test("S02", "03.141", "3.141");
		t.test("S03", "99.90e-09", "9.99E-8");
		t.test("S04", "[-1.1..1]", "[-1.1..1.0]");
		t.test("S05", "(-2.2..2)", "(-2.2..2.0)");
		t.test("S06", "[3.3..*)", "[3.3..*)");
		t.test("S07", "(*..-3.3]", "(*..-3.3]");
		t.test("S08", "[*..*]", "(*..*)");
		
		/* test invalid cases */
		t.test("F01", "", "no interval specified");
		t.test("F02", "..", "invalid interval notation");
		t.test("F03", "1.1..", "invalid interval notation");
		t.test("F04", "..1.1", "invalid interval notation");
		t.test("F05", "1.1..1.1", "invalid interval notation");
		
		t.test("F06", "[1.1]", "invalid limiting value");
		t.test("F07", "[a]", "invalid limiting value");
		t.test("F08", "[a..]", "invalid interval notation");
		t.test("F09", "[..a]", "invalid interval notation");
		t.test("F10", "[a..1.1]", "invalid lower limit");
		t.test("F11", "[1.1..a]", "invalid upper limit");		
		t.test("F12", "[1.1..0]", "lower limit exceeds upper limit");
	}

}

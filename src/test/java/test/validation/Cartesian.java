package test.validation;

import java.util.Iterator;

import be.baur.sda.DataNode;
import be.baur.sda.SDA;
import be.baur.sds.SDS;
import be.baur.sds.validation.Validator;
import be.baur.sds.validation.Validator.Errors;
import test.Test;

public final class Cartesian {

	/* 
	 * Parsing and validation of unordered groups of Cartesian coordinates.
	 */
	public static void main(String[] args) throws Exception {

		Test t = new Test(s -> {
			return s;
		});
		

		DataNode doc = SDA.parse(Test.getResourceFile("/cartesian.sda"));
		Validator validator = SDS.parse(Test.getResourceFile("/cartesian.sds")).newValidator();

		Errors errors = validator.validate(doc);
		//for (Error error : errors) System.out.println(error.toString());
		Iterator<?> e = errors.iterator();
		
		t.s("F01", e.next() + "", "/cartesian/line[5]/point[1]: content missing at end of 'point'; expected 'y'");
		t.s("F02", e.next() + "", "/cartesian/line[5]/point[2]: content missing at end of 'point'; expected 'y'");
		t.s("F03", e.next() + "", "/cartesian/line[6]/point[1]: content missing at end of 'point'; expected 'x'");
		t.s("F04", e.next() + "", "/cartesian/line[6]/point[2]: content missing at end of 'point'; expected 'x'");
		t.s("F05", e.next() + "", "/cartesian/line[7]/point[1]: content missing at end of 'point'; expected 'y'");
		t.s("F06", e.next() + "", "/cartesian/line[7]/point[2]: content missing at end of 'point'; expected 'x'");
		t.s("F07", e.next() + "", "/cartesian/line[8]/point[1]: content missing at end of 'point'; expected 'x' or 'y'");
		t.s("F08", e.next() + "", "/cartesian/line[8]/point[2]: content missing at end of 'point'; expected 'x','y' or 'z'");
		t.s("F09", e.next() + "", "/cartesian/line[9]/point/tag: got 'tag', but 'x','y' or 'z' was expected");
		t.s("F10", e.next() + "", "/cartesian/line[9]: content missing at end of 'line'; expected 'point'");
		t.s("F11", e.next() + "", "/cartesian/line[10]/point: content missing at end of 'point'; expected 'id'");
		t.s("F12", e.next() + "", "/cartesian/line[10]: content missing at end of 'line'; expected 'point'");
		t.s("F13", e.next() + "", "/cartesian/line[11]/point[1]/U: got 'U', but 'x','y' or 'z' was expected");
		t.s("F14", e.next() + "", "/cartesian/line[11]/point[2]/U: got 'U', but 'y' was expected");
		// ABUNDANT t.ts1("F16", e.next() + "", "/cartesian/line[11]/point[2]/z: 'z' was not expected in 'point'");
		t.s("F17", e.next() + "", "/cartesian/line[12]/point[1]/U: 'U' was not expected in 'point'");
		// ABUNDANT t.ts1("F18", e.next() + "", "/cartesian/line[12]/point[1]/z: 'z' was not expected in 'point'");
		t.s("F19", e.next() + "", "/cartesian/line[12]/point[2]/U: 'U' was not expected in 'point'");
		t.s("F20", e.next() + "", "/cartesian/line[13]/point[1]/U: got 'U', but 'x','y' or 'z' was expected");
		t.s("F21", e.next() + "", "/cartesian/line[13]/point[2]/U: got 'U', but 'y' was expected");
		t.s("F22", e.next() + "", "/cartesian/line[14]/point[1]/U: 'U' was not expected in 'point'");
		t.s("F23", e.next() + "", "/cartesian/line[14]/point[2]/U: got 'U', but 'y' was expected");
		t.s("F24", e.next() + "", "/cartesian/line[14]/point[2]: content missing at end of 'point'; expected 'y'");
		t.s("F25", e.next() + "", "/cartesian/line[15]/point[1]/U: got 'U', but 'x','y' or 'z' was expected");
		t.s("F26", e.next() + "", "/cartesian/line[15]/point[2]/U: got 'U', but 'x','y' or 'z' was expected");
		t.s("F99", e.hasNext() + "", "false");
	}
}

package test.validation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import be.baur.sda.DataNode;
import be.baur.sda.SDA;
import be.baur.sds.SDS;
import be.baur.sds.validation.Error;
import be.baur.sds.validation.ErrorList;
import be.baur.sds.validation.Validator;
import test.Test;

public final class Cartesian {

	/* 
	 * Parsing and validation of unordered groups of Cartesian coordinates.
	 */
	public static void main(String[] args) throws Exception {

		Test t = new Test(s -> {
			return s;
		});
		
		InputStream sda = Cartesian.class.getResourceAsStream("/cartesian.sda");
		DataNode document = SDA.parse(new InputStreamReader(sda, "UTF-8"));

		InputStream sds = Cartesian.class.getResourceAsStream("/cartesian.sds");
		Validator validator = SDS.parse(new InputStreamReader(sds, "UTF-8")).newValidator();

		ErrorList errors = validator.validate(document, null);
		//for (Error error : errors) System.out.println(error.toString());
		Iterator<Error> e = errors.iterator();
		
		t.ts1("F01", e.next() + "", "/cartesian/line[5]/point[1]: content missing at end of 'point'; expected 'y'");
		t.ts1("F02", e.next() + "", "/cartesian/line[5]/point[2]: content missing at end of 'point'; expected 'y'");
		t.ts1("F03", e.next() + "", "/cartesian/line[6]/point[1]: content missing at end of 'point'; expected 'x'");
		t.ts1("F04", e.next() + "", "/cartesian/line[6]/point[2]: content missing at end of 'point'; expected 'x'");
		t.ts1("F05", e.next() + "", "/cartesian/line[7]/point[1]: content missing at end of 'point'; expected 'y'");
		t.ts1("F06", e.next() + "", "/cartesian/line[7]/point[2]: content missing at end of 'point'; expected 'x'");
		t.ts1("F07", e.next() + "", "/cartesian/line[8]/point[1]: content missing at end of 'point'; expected 'x' or 'y'");
		t.ts1("F08", e.next() + "", "/cartesian/line[8]/point[2]: content missing at end of 'point'; expected 'x','y' or 'z'");
		t.ts1("F09", e.next() + "", "/cartesian/line[9]/point/tag: got 'tag', but 'x','y' or 'z' was expected");
		t.ts1("F10", e.next() + "", "/cartesian/line[9]: content missing at end of 'line'; expected 'point'");
		t.ts1("F11", e.next() + "", "/cartesian/line[10]/point: content missing at end of 'point'; expected 'id'");
		t.ts1("F12", e.next() + "", "/cartesian/line[10]: content missing at end of 'line'; expected 'point'");
		t.ts1("F13", e.next() + "", "/cartesian/line[11]/point[1]/U: got 'U', but 'x','y' or 'z' was expected");
		t.ts1("F14", e.next() + "", "/cartesian/line[11]/point[2]/U: got 'U', but 'y' was expected");
		// ABUNDANT t.ts1("F16", e.next() + "", "/cartesian/line[11]/point[2]/z: 'z' was not expected in 'point'");
		t.ts1("F17", e.next() + "", "/cartesian/line[12]/point[1]/U: 'U' was not expected in 'point'");
		// ABUNDANT t.ts1("F18", e.next() + "", "/cartesian/line[12]/point[1]/z: 'z' was not expected in 'point'");
		t.ts1("F19", e.next() + "", "/cartesian/line[12]/point[2]/U: 'U' was not expected in 'point'");
		t.ts1("F20", e.next() + "", "/cartesian/line[13]/point[1]/U: got 'U', but 'x','y' or 'z' was expected");
		t.ts1("F21", e.next() + "", "/cartesian/line[13]/point[2]/U: got 'U', but 'y' was expected");
		t.ts1("F22", e.next() + "", "/cartesian/line[14]/point[1]/U: 'U' was not expected in 'point'");
		t.ts1("F23", e.next() + "", "/cartesian/line[14]/point[2]/U: got 'U', but 'y' was expected");
		t.ts1("F24", e.next() + "", "/cartesian/line[14]/point[2]: content missing at end of 'point'; expected 'y'");
		t.ts1("F25", e.next() + "", "/cartesian/line[15]/point[1]/U: got 'U', but 'x','y' or 'z' was expected");
		t.ts1("F26", e.next() + "", "/cartesian/line[15]/point[2]/U: got 'U', but 'x','y' or 'z' was expected");
		t.ts1("F99", e.hasNext() + "", "false");
	}
}

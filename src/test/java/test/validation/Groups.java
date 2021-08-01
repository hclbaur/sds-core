package test.validation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import be.baur.sda.Node;
import be.baur.sda.SDA;
import be.baur.sds.SDS;
import be.baur.sds.Schema;
import be.baur.sds.validation.Error;
import be.baur.sds.validation.ErrorList;
import test.Test;

public final class Groups {
	
	private static be.baur.sda.serialization.Parser parser = SDA.parser();

	/* 
	 * Parsing and validation of all kinds of sequence groups.
	 */
	public static void main(String[] args) throws Exception {

		Test t = new Test(s -> {
			return s;
		});
		
		InputStream sda = Groups.class.getResourceAsStream("/groups.sda");
		Node document = parser.parse(new InputStreamReader(sda, "UTF-8"));

		InputStream sds = Groups.class.getResourceAsStream("/groups.sds");
		Schema schema = SDS.parser().parse(new InputStreamReader(sds, "UTF-8"));

		ErrorList errors = SDS.validator().validate(document, schema, null);
		//for (Error error : errors) System.out.println(error.toString());
		Iterator<Error> e = errors.iterator();
		
		t.ts1("F01", e.next() + "", "/group_tests/man_man[1]: content missing at end of 'man_man'; expected 'man2'");
		t.ts1("F02", e.next() + "", "/group_tests/man_man[2]/man2: got 'man2', but 'man1' was expected");
		t.ts1("F03", e.next() + "", "/group_tests/man_man[4]/man2: got 'man2', but 'man1' was expected");
		t.ts1("F04", e.next() + "", "/group_tests/man_man[5]: content missing at end of 'man_man'; expected 'man1'");
		t.ts1("F05", e.next() + "", "/group_tests/man_man[6]/err: got 'err', but 'man1' was expected");
		t.ts1("F06", e.next() + "", "/group_tests/man_man[7]/err: got 'err', but 'man2' was expected");
		t.ts1("F07", e.next() + "", "/group_tests/man_man[8]/err: got 'err', but 'man1' was expected");
		t.ts1("F08", e.next() + "", "/group_tests/man_man[9]/man2: got 'man2', but 'man1' was expected");
		t.ts1("F09", e.next() + "", "/group_tests/man_man[10]/err: got 'err', but 'man1' was expected");
		t.ts1("F10", e.next() + "", "/group_tests/opt_man[1]: content missing at end of 'opt_man'; expected 'man2'");
		t.ts1("F11", e.next() + "", "/group_tests/opt_man[2]/man2: 'man2' was not expected in 'opt_man'");
		t.ts1("F12", e.next() + "", "/group_tests/opt_man[4]/man2: 'man2' was not expected in 'opt_man'");
		t.ts1("F13", e.next() + "", "/group_tests/opt_man[6]/err: 'err' was not expected in 'opt_man'");
		t.ts1("F14", e.next() + "", "/group_tests/opt_man[7]/err: got 'err', but 'man2' was expected");
		t.ts1("F15", e.next() + "", "/group_tests/opt_man[8]/err: 'err' was not expected in 'opt_man'");
		t.ts1("F16", e.next() + "", "/group_tests/opt_man[9]/man2: 'man2' was not expected in 'opt_man'");
		t.ts1("F17", e.next() + "", "/group_tests/opt_man[10]/err: 'err' was not expected in 'opt_man'");
		// ABUNDANT t.ts1("F08", e.next() + "", "/group_tests/opt_man[4]/man1: 'man1' was not expected in 'opt_man'");
		t.ts1("F18", e.next() + "", "/group_tests/man_opt[4]/opt1: 'opt1' was not expected in 'man_opt'");
		t.ts1("F19", e.next() + "", "/group_tests/man_opt[6]/err: 'err' was not expected in 'man_opt'");
		t.ts1("F20", e.next() + "", "/group_tests/man_opt[7]/err: 'err' was not expected in 'man_opt'");
		t.ts1("F21", e.next() + "", "/group_tests/man_opt[8]/err: 'err' was not expected in 'man_opt'");
		t.ts1("F22", e.next() + "", "/group_tests/man_opt[9]/err: 'err' was not expected in 'man_opt'");
		t.ts1("F23", e.next() + "", "/group_tests/man_opt[10]/err: 'err' was not expected in 'man_opt'");
		t.ts1("F24", e.next() + "", "/group_tests/opt_opt[4]/opt1: 'opt1' was not expected in 'opt_opt'");
		t.ts1("F25", e.next() + "", "/group_tests/opt_opt[6]/err: 'err' was not expected in 'opt_opt'");
		t.ts1("F26", e.next() + "", "/group_tests/opt_opt[7]/err: 'err' was not expected in 'opt_opt'");
		t.ts1("F27", e.next() + "", "/group_tests/opt_opt[8]/err: 'err' was not expected in 'opt_opt'");
		t.ts1("F28", e.next() + "", "/group_tests/opt_opt[9]/err: 'err' was not expected in 'opt_opt'");
		t.ts1("F29", e.next() + "", "/group_tests/opt_opt[10]/err: 'err' was not expected in 'opt_opt'");
		t.ts1("F99", e.hasNext() + "", "false");
		
	}
}

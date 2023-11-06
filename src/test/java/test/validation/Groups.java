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

public final class Groups {

	/* 
	 * Parsing and validation of all kinds of sequence groups.
	 */
	public static void main(String[] args) throws Exception {

		Test t = new Test(s -> {
			return s;
		});
		
		InputStream sda = Groups.class.getResourceAsStream("/mgtest.sda");
		DataNode document = SDA.parse(new InputStreamReader(sda, "UTF-8"));

		InputStream sds = Groups.class.getResourceAsStream("/groups.sds");
		Validator validator = SDS.parse(new InputStreamReader(sds, "UTF-8")).newValidator();

		ErrorList errors = validator.validate(document, null);
		//for (Error error : errors) System.out.println(error.toString());
		Iterator<Error> e = errors.iterator();
		
		t.ts1("F01", e.next() + "", "/test/man_man_man[1]: content missing at end of 'man_man_man'; expected 'man2'");
		t.ts1("F02", e.next() + "", "/test/man_man_man[2]/man2: got 'man2', but 'man1' was expected");
		t.ts1("F03", e.next() + "", "/test/man_man_man[4]/man2: got 'man2', but 'man1' was expected");
		t.ts1("F04", e.next() + "", "/test/man_man_man[5]: content missing at end of 'man_man_man'; expected 'man1'");
		t.ts1("F05", e.next() + "", "/test/man_man_man[6]/err: got 'err', but 'man1' was expected");
		t.ts1("F06", e.next() + "", "/test/man_man_man[7]/err: got 'err', but 'man2' was expected");
		t.ts1("F07", e.next() + "", "/test/man_man_man[8]/err: got 'err', but 'man1' was expected");
		t.ts1("F08", e.next() + "", "/test/man_man_man[9]/man2: got 'man2', but 'man1' was expected");
		t.ts1("F09", e.next() + "", "/test/man_man_man[10]/err: got 'err', but 'man1' was expected");

		t.ts1("F10", e.next() + "", "/test/man_man_opt[2]/opt1: got 'opt1', but 'man1' was expected");
		t.ts1("F11", e.next() + "", "/test/man_man_opt[4]/opt1: got 'opt1', but 'man1' was expected");
		t.ts1("F12", e.next() + "", "/test/man_man_opt[5]: content missing at end of 'man_man_opt'; expected 'man1'");
		t.ts1("F13", e.next() + "", "/test/man_man_opt[6]/err: got 'err', but 'man1' was expected");
		t.ts1("F14", e.next() + "", "/test/man_man_opt[7]/err: 'err' was not expected in 'man_man_opt'");
		t.ts1("F15", e.next() + "", "/test/man_man_opt[8]/err: got 'err', but 'man1' was expected");
		t.ts1("F16", e.next() + "", "/test/man_man_opt[9]/opt1: got 'opt1', but 'man1' was expected");
		t.ts1("F17", e.next() + "", "/test/man_man_opt[10]/err: got 'err', but 'man1' was expected");

		t.ts1("F18", e.next() + "", "/test/man_opt_man[1]: content missing at end of 'man_opt_man'; expected 'man1'");
		t.ts1("F19", e.next() + "", "/test/man_opt_man[4]/opt1: 'opt1' was not expected in 'man_opt_man'");
		t.ts1("F20", e.next() + "", "/test/man_opt_man[5]: content missing at end of 'man_opt_man'; expected 'opt1' or 'man1'");
		t.ts1("F21", e.next() + "", "/test/man_opt_man[6]/err: got 'err', but 'opt1' or 'man1' was expected");
		t.ts1("F22", e.next() + "", "/test/man_opt_man[7]/err: got 'err', but 'man1' was expected");
		t.ts1("F23", e.next() + "", "/test/man_opt_man[8]/err: got 'err', but 'opt1' or 'man1' was expected");
		t.ts1("F24", e.next() + "", "/test/man_opt_man[9]/err: 'err' was not expected in 'man_opt_man'");
		t.ts1("F25", e.next() + "", "/test/man_opt_man[10]/err: got 'err', but 'opt1' or 'man1' was expected");

		t.ts1("F26", e.next() + "", "/test/man_opt_opt[4]/opt1: 'opt1' was not expected in 'man_opt_opt'");
		t.ts1("F27", e.next() + "", "/test/man_opt_opt[6]/err: 'err' was not expected in 'man_opt_opt'");
		t.ts1("F28", e.next() + "", "/test/man_opt_opt[7]/err: 'err' was not expected in 'man_opt_opt'");
		t.ts1("F29", e.next() + "", "/test/man_opt_opt[8]/err: 'err' was not expected in 'man_opt_opt'");
		t.ts1("F30", e.next() + "", "/test/man_opt_opt[9]/err: 'err' was not expected in 'man_opt_opt'");
		t.ts1("F31", e.next() + "", "/test/man_opt_opt[10]/err: 'err' was not expected in 'man_opt_opt'");

		System.out.print("\n              ");
		t.ts1("F32", e.next() + "", "/test/opt_man_man[1]: content missing at end of 'opt_man_man'; expected 'man2'");
		t.ts1("F33", e.next() + "", "/test/opt_man_man[2]/man2: 'man2' was not expected in 'opt_man_man'");
		t.ts1("F34", e.next() + "", "/test/opt_man_man[4]/man2: 'man2' was not expected in 'opt_man_man'");
		t.ts1("F35", e.next() + "", "/test/opt_man_man[6]/err: 'err' was not expected in 'opt_man_man'");
		t.ts1("F36", e.next() + "", "/test/opt_man_man[7]/err: got 'err', but 'man2' was expected");
		t.ts1("F37", e.next() + "", "/test/opt_man_man[8]/err: 'err' was not expected in 'opt_man_man'");
		t.ts1("F38", e.next() + "", "/test/opt_man_man[9]/man2: 'man2' was not expected in 'opt_man_man'");
		t.ts1("F39", e.next() + "", "/test/opt_man_man[10]/err: 'err' was not expected in 'opt_man_man'");

		t.ts1("F40", e.next() + "", "/test/opt_man_opt[2]/opt1: 'opt1' was not expected in 'opt_man_opt'");
		t.ts1("F41", e.next() + "", "/test/opt_man_opt[4]/opt1: 'opt1' was not expected in 'opt_man_opt'");
		t.ts1("F42", e.next() + "", "/test/opt_man_opt[6]/err: 'err' was not expected in 'opt_man_opt'");
		t.ts1("F43", e.next() + "", "/test/opt_man_opt[7]/err: 'err' was not expected in 'opt_man_opt'");
		t.ts1("F44", e.next() + "", "/test/opt_man_opt[8]/err: 'err' was not expected in 'opt_man_opt'");
		t.ts1("F45", e.next() + "", "/test/opt_man_opt[9]/opt1: 'opt1' was not expected in 'opt_man_opt'");
		t.ts1("F46", e.next() + "", "/test/opt_man_opt[10]/err: 'err' was not expected in 'opt_man_opt'");

		t.ts1("F47", e.next() + "", "/test/opt_opt_man[1]: content missing at end of 'opt_opt_man'; expected 'man1'");
		t.ts1("F48", e.next() + "", "/test/opt_opt_man[4]/opt1: 'opt1' was not expected in 'opt_opt_man'");
		t.ts1("F49", e.next() + "", "/test/opt_opt_man[6]/err: 'err' was not expected in 'opt_opt_man'");
		t.ts1("F50", e.next() + "", "/test/opt_opt_man[7]/err: got 'err', but 'man1' was expected");
		t.ts1("F51", e.next() + "", "/test/opt_opt_man[8]/err: 'err' was not expected in 'opt_opt_man'");
		t.ts1("F52", e.next() + "", "/test/opt_opt_man[9]/err: 'err' was not expected in 'opt_opt_man'");
		t.ts1("F53", e.next() + "", "/test/opt_opt_man[10]/err: 'err' was not expected in 'opt_opt_man'");
		
		t.ts1("F54", e.next() + "", "/test/opt_opt_opt[4]/opt1: 'opt1' was not expected in 'opt_opt_opt'");
		t.ts1("F55", e.next() + "", "/test/opt_opt_opt[6]/err: 'err' was not expected in 'opt_opt_opt'");
		t.ts1("F56", e.next() + "", "/test/opt_opt_opt[7]/err: 'err' was not expected in 'opt_opt_opt'");
		t.ts1("F57", e.next() + "", "/test/opt_opt_opt[8]/err: 'err' was not expected in 'opt_opt_opt'");
		t.ts1("F58", e.next() + "", "/test/opt_opt_opt[9]/err: 'err' was not expected in 'opt_opt_opt'");
		t.ts1("F59", e.next() + "", "/test/opt_opt_opt[10]/err: 'err' was not expected in 'opt_opt_opt'");
		
		t.ts1("F99", e.hasNext() + "", "false");
		
	}
}

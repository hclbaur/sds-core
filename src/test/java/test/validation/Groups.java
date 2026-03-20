package test.validation;

import java.util.Iterator;

import be.baur.sda.DataNode;
import be.baur.sda.SDA;
import be.baur.sds.SDS;
import be.baur.sds.validation.Validator;
import be.baur.sds.validation.Validator.Errors;
import test.Test;

public final class Groups {

	/* 
	 * Parsing and validation of all kinds of sequence groups.
	 */
	public static void main(String[] args) throws Exception {

		Test t = new Test(s -> {
			return s;
		});

		
		DataNode doc = SDA.parse(Test.getResourceFile("/mgtest.sda"));
		Validator validator = SDS.parse(Test.getResourceFile("/groups.sds")).newValidator();

		Errors errors = validator.validate(doc);
		//for (Error error : errors) System.out.println(error.toString());
		Iterator<?> e = errors.iterator();
		
		t.s("F01", e.next() + "", "/test/man_man_man[1]: content missing at end of 'man_man_man'; expected 'man2'");
		t.s("F02", e.next() + "", "/test/man_man_man[2]/man2: got 'man2', but 'man1' was expected");
		t.s("F03", e.next() + "", "/test/man_man_man[4]/man2: got 'man2', but 'man1' was expected");
		t.s("F04", e.next() + "", "/test/man_man_man[5]: content missing at end of 'man_man_man'; expected 'man1'");
		t.s("F05", e.next() + "", "/test/man_man_man[6]/err: got 'err', but 'man1' was expected");
		t.s("F06", e.next() + "", "/test/man_man_man[7]/err: got 'err', but 'man2' was expected");
		t.s("F07", e.next() + "", "/test/man_man_man[8]/err: got 'err', but 'man1' was expected");
		t.s("F08", e.next() + "", "/test/man_man_man[9]/man2: got 'man2', but 'man1' was expected");
		t.s("F09", e.next() + "", "/test/man_man_man[10]/err: got 'err', but 'man1' was expected");

		t.s("F10", e.next() + "", "/test/man_man_opt[2]/opt1: got 'opt1', but 'man1' was expected");
		t.s("F11", e.next() + "", "/test/man_man_opt[4]/opt1: got 'opt1', but 'man1' was expected");
		t.s("F12", e.next() + "", "/test/man_man_opt[5]: content missing at end of 'man_man_opt'; expected 'man1'");
		t.s("F13", e.next() + "", "/test/man_man_opt[6]/err: got 'err', but 'man1' was expected");
		t.s("F14", e.next() + "", "/test/man_man_opt[7]/err: 'err' was not expected in 'man_man_opt'");
		t.s("F15", e.next() + "", "/test/man_man_opt[8]/err: got 'err', but 'man1' was expected");
		t.s("F16", e.next() + "", "/test/man_man_opt[9]/opt1: got 'opt1', but 'man1' was expected");
		t.s("F17", e.next() + "", "/test/man_man_opt[10]/err: got 'err', but 'man1' was expected");

		t.s("F18", e.next() + "", "/test/man_opt_man[1]: content missing at end of 'man_opt_man'; expected 'man1'");
		t.s("F19", e.next() + "", "/test/man_opt_man[4]/opt1: 'opt1' was not expected in 'man_opt_man'");
		t.s("F20", e.next() + "", "/test/man_opt_man[5]: content missing at end of 'man_opt_man'; expected 'opt1' or 'man1'");
		t.s("F21", e.next() + "", "/test/man_opt_man[6]/err: got 'err', but 'opt1' or 'man1' was expected");
		t.s("F22", e.next() + "", "/test/man_opt_man[7]/err: got 'err', but 'man1' was expected");
		t.s("F23", e.next() + "", "/test/man_opt_man[8]/err: got 'err', but 'opt1' or 'man1' was expected");
		t.s("F24", e.next() + "", "/test/man_opt_man[9]/err: 'err' was not expected in 'man_opt_man'");
		t.s("F25", e.next() + "", "/test/man_opt_man[10]/err: got 'err', but 'opt1' or 'man1' was expected");

		t.s("F26", e.next() + "", "/test/man_opt_opt[4]/opt1: 'opt1' was not expected in 'man_opt_opt'");
		t.s("F27", e.next() + "", "/test/man_opt_opt[6]/err: 'err' was not expected in 'man_opt_opt'");
		t.s("F28", e.next() + "", "/test/man_opt_opt[7]/err: 'err' was not expected in 'man_opt_opt'");
		t.s("F29", e.next() + "", "/test/man_opt_opt[8]/err: 'err' was not expected in 'man_opt_opt'");
		t.s("F30", e.next() + "", "/test/man_opt_opt[9]/err: 'err' was not expected in 'man_opt_opt'");
		t.s("F31", e.next() + "", "/test/man_opt_opt[10]/err: 'err' was not expected in 'man_opt_opt'");

		System.out.print("\n              ");
		t.s("F32", e.next() + "", "/test/opt_man_man[1]: content missing at end of 'opt_man_man'; expected 'man2'");
		t.s("F33", e.next() + "", "/test/opt_man_man[2]/man2: 'man2' was not expected in 'opt_man_man'");
		t.s("F34", e.next() + "", "/test/opt_man_man[4]/man2: 'man2' was not expected in 'opt_man_man'");
		t.s("F35", e.next() + "", "/test/opt_man_man[6]/err: 'err' was not expected in 'opt_man_man'");
		t.s("F36", e.next() + "", "/test/opt_man_man[7]/err: got 'err', but 'man2' was expected");
		t.s("F37", e.next() + "", "/test/opt_man_man[8]/err: 'err' was not expected in 'opt_man_man'");
		t.s("F38", e.next() + "", "/test/opt_man_man[9]/man2: 'man2' was not expected in 'opt_man_man'");
		t.s("F39", e.next() + "", "/test/opt_man_man[10]/err: 'err' was not expected in 'opt_man_man'");

		t.s("F40", e.next() + "", "/test/opt_man_opt[2]/opt1: 'opt1' was not expected in 'opt_man_opt'");
		t.s("F41", e.next() + "", "/test/opt_man_opt[4]/opt1: 'opt1' was not expected in 'opt_man_opt'");
		t.s("F42", e.next() + "", "/test/opt_man_opt[6]/err: 'err' was not expected in 'opt_man_opt'");
		t.s("F43", e.next() + "", "/test/opt_man_opt[7]/err: 'err' was not expected in 'opt_man_opt'");
		t.s("F44", e.next() + "", "/test/opt_man_opt[8]/err: 'err' was not expected in 'opt_man_opt'");
		t.s("F45", e.next() + "", "/test/opt_man_opt[9]/opt1: 'opt1' was not expected in 'opt_man_opt'");
		t.s("F46", e.next() + "", "/test/opt_man_opt[10]/err: 'err' was not expected in 'opt_man_opt'");

		t.s("F47", e.next() + "", "/test/opt_opt_man[1]: content missing at end of 'opt_opt_man'; expected 'man1'");
		t.s("F48", e.next() + "", "/test/opt_opt_man[4]/opt1: 'opt1' was not expected in 'opt_opt_man'");
		t.s("F49", e.next() + "", "/test/opt_opt_man[6]/err: 'err' was not expected in 'opt_opt_man'");
		t.s("F50", e.next() + "", "/test/opt_opt_man[7]/err: got 'err', but 'man1' was expected");
		t.s("F51", e.next() + "", "/test/opt_opt_man[8]/err: 'err' was not expected in 'opt_opt_man'");
		t.s("F52", e.next() + "", "/test/opt_opt_man[9]/err: 'err' was not expected in 'opt_opt_man'");
		t.s("F53", e.next() + "", "/test/opt_opt_man[10]/err: 'err' was not expected in 'opt_opt_man'");
		
		t.s("F54", e.next() + "", "/test/opt_opt_opt[4]/opt1: 'opt1' was not expected in 'opt_opt_opt'");
		t.s("F55", e.next() + "", "/test/opt_opt_opt[6]/err: 'err' was not expected in 'opt_opt_opt'");
		t.s("F56", e.next() + "", "/test/opt_opt_opt[7]/err: 'err' was not expected in 'opt_opt_opt'");
		t.s("F57", e.next() + "", "/test/opt_opt_opt[8]/err: 'err' was not expected in 'opt_opt_opt'");
		t.s("F58", e.next() + "", "/test/opt_opt_opt[9]/err: 'err' was not expected in 'opt_opt_opt'");
		t.s("F59", e.next() + "", "/test/opt_opt_opt[10]/err: 'err' was not expected in 'opt_opt_opt'");
		
		t.s("F99", e.hasNext() + "", "false");
		
	}
}

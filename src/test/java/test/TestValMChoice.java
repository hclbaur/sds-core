package test;

import java.io.InputStream;
import java.io.InputStreamReader;

import be.baur.sda.Node;
import be.baur.sda.SDA;
import be.baur.sds.SDS;
import be.baur.sds.Schema;
import be.baur.sds.validation.ErrorList;

public final class TestValMChoice {
	
	private static be.baur.sda.serialization.Parser parser = SDA.parser();

	public static void main(String[] args) throws Exception {

		Test t = new Test(s -> {
			return s;
		});
		
		InputStream sda = TestValMChoice.class.getResourceAsStream("/choices.sda");
		Node document = parser.parse(new InputStreamReader(sda, "UTF-8"));

		InputStream sds = TestValMChoice.class.getResourceAsStream("/choices.sds");
		Schema schema = SDS.parser().parse(new InputStreamReader(sds, "UTF-8"));

		ErrorList errors = SDS.validator().validate(document, schema, null);
		//for (be.baur.sds.validation.Error error : errors) System.out.println(error.toString());
		
		t.ts1("F01", errors.get(0).toString(), "/choice_tests/man_man[3]/man2: 'man2' not expected in 'man_man'");
		t.ts1("F02", errors.get(1).toString(), "/choice_tests/man_man[4]/man1: 'man1' not expected in 'man_man'");
		t.ts1("F03", errors.get(2).toString(), "/choice_tests/man_man[5]: content missing at end of 'man_man'; expecting 'man1' or 'man2'");
		t.ts1("F04", errors.get(3).toString(), "/choice_tests/opt_man[3]/man2: 'man2' not expected in 'opt_man'");
		t.ts1("F05", errors.get(4).toString(), "/choice_tests/opt_man[4]/man1: 'man1' not expected in 'opt_man'");
		t.ts1("F06", errors.get(5).toString(), "/choice_tests/man_opt[3]/opt2: 'opt2' not expected in 'man_opt'");
		t.ts1("F07", errors.get(6).toString(), "/choice_tests/man_opt[4]/opt1: 'opt1' not expected in 'man_opt'");
		t.ts1("F08", errors.get(7).toString(), "/choice_tests/opt_opt[3]/opt2: 'opt2' not expected in 'opt_opt'");
		t.ts1("F09", errors.get(8).toString(), "/choice_tests/opt_opt[4]/opt1: 'opt1' not expected in 'opt_opt'");
		//t.ts1("F11", errors.get(10).toString(), "");
	}
}

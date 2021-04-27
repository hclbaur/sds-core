package test;

import java.io.InputStream;
import java.io.InputStreamReader;

import be.baur.sda.Node;
import be.baur.sda.SDA;
import be.baur.sds.SDS;
import be.baur.sds.Schema;
import be.baur.sds.validation.ErrorList;

public final class TestValMGroup {
	
	private static be.baur.sda.serialization.Parser parser = SDA.parser();

	public static void main(String[] args) throws Exception {

		Test t = new Test(s -> {
			return s;
		});
		
		InputStream sda = TestValMGroup.class.getResourceAsStream("/groups.sda");
		Node document = parser.parse(new InputStreamReader(sda, "UTF-8"));

		InputStream sds = TestValMGroup.class.getResourceAsStream("/groups.sds");
		Schema schema = SDS.parser().parse(new InputStreamReader(sds, "UTF-8"));

		ErrorList errors = SDS.validator().validate(document, schema, null);
		//for (be.baur.sds.validation.Error error : errors) System.out.println(error.toString());
		
		t.ts1("F01", errors.get(0).toString(), "/group_tests/man_man[1]: content missing at end of 'man_man'; expecting 'man2'");
		t.ts1("F02", errors.get(1).toString(), "/group_tests/man_man[2]/man2: got 'man2', but expected 'man1' instead");
		t.ts1("F03", errors.get(2).toString(), "/group_tests/man_man[4]/man2: got 'man2', but expected 'man1' instead");
		t.ts1("F04", errors.get(3).toString(), "/group_tests/man_man[5]: content missing at end of 'man_man'; expecting 'man1'");
		t.ts1("F05", errors.get(4).toString(), "/group_tests/opt_man[1]: content missing at end of 'opt_man'; expecting 'man2'");
		t.ts1("F06", errors.get(5).toString(), "/group_tests/opt_man[2]/man2: 'man2' not expected in 'opt_man'");
		t.ts1("F07", errors.get(6).toString(), "/group_tests/opt_man[4]/man2: 'man2' not expected in 'opt_man'");
		t.ts1("F08", errors.get(7).toString(), "/group_tests/opt_man[4]/man1: 'man1' not expected in 'opt_man'");
		t.ts1("F09", errors.get(8).toString(), "/group_tests/man_opt[4]/opt1: 'opt1' not expected in 'man_opt'");
		t.ts1("F10", errors.get(9).toString(), "/group_tests/opt_opt[4]/opt1: 'opt1' not expected in 'opt_opt'");
		//t.ts1("F12", errors.get(11).toString(), "");
		
	}
}

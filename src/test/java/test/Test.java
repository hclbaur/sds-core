package test;

import java.util.function.Function;

/** A convenience class with testing methods that accept Lamba expressions */
public class Test {

	Function<String, String> function;
	
	Test(Function<String, String> function) {
		this.function = function;
	}
	
	public void test(String scenario, String input, String expected) {
		
		String result = function.apply(input);

		if (expected == null) expected = input;
		if (result.equals(expected)) 
			System.out.print(scenario + " ");
		else {
			System.out.println("\n" + scenario + " FAILED!");
			System.out.println("    EXPECTED: " + expected);
			System.out.println("    RETURNED: " + result);
		}
	}
	
	public void testError(String scenario, String input, String expected) {
		
		try { 
			function.apply(input);
		}
		catch (Exception e) { 
			if (e.getMessage().equals(expected)) 
				System.out.print(scenario + " ");
			else {
				System.out.println("\n" + scenario + " FAILED!");
				System.out.println("    EXPECTED: " + expected);
				System.out.println("    RETURNED: " + e.getMessage());
			}
			return;
		}
		System.out.println(scenario + " FAILED - exception expected");
	}
}

package test;

import java.util.function.BiFunction;
import java.util.function.Function;

/** A convenience class with testing methods that accept Lambda expressions */
public class Test {

	Function<String, String> strfun;
	String prefix;
	
	public Test(Function<String, String> strfun, String prefix) {
		this.strfun = strfun; this.prefix = prefix;
	}
	
	public Test(Function<String, String> strfun) {
		this(strfun, "");
	}
	
	public void ts1(String scenario, String str, String expected) {
		
		String result = strfun.apply(str);

		if (expected == null) expected = str;
		expected = prefix + expected;
		
		if (result.equals(expected)) 
			System.out.print(scenario + " ");
		else {
			System.out.println("\n" + scenario + " FAILED!");
			System.out.println("    EXPECTED: " + expected);
			System.out.println("    RETURNED: " + result);
		}
	}
	
	public void ts1Error(String scenario, String input, String expected) {
		
		try { 
			strfun.apply(input);
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
	
	
	BiFunction<String, String, String> bifunction;
	
	Test(BiFunction<String, String, String> bifunction) {
		this.bifunction = bifunction;
	}
	
	public void ts2(String scenario, String input1, String input2, String expected) {
		
		String result = bifunction.apply(input1, input2);

		if (expected == null) expected = input1;
		if (result.equals(expected)) 
			System.out.print(scenario + " ");
		else {
			System.out.println("\n" + scenario + " FAILED!");
			System.out.println("    EXPECTED: " + expected);
			System.out.println("    RETURNED: " + result);
		}
	}
	
}

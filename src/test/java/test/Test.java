package test;

import java.io.File;
import java.util.function.BiFunction;
import java.util.function.Function;

/** A convenience class with testing methods that accept Lambda expressions */
public class Test {

	private Function<String, String> function;
	private BiFunction<String, String, String> bifunction;
	private String prefix;
	private int failures;
	
	public Test(Function<String, String> function, String prefix) {
		this.function = function; this.prefix = prefix;
	}
	
	public Test(Function<String, String> strfun) {
		this(strfun, "");
	}
	
	Test(BiFunction<String, String, String> bifunction, String prefix) {
		this.bifunction = bifunction; this.prefix = prefix;
	}
	
	Test(BiFunction<String, String, String> bifunction) {
		this(bifunction, "");
	}
	
	public void s(String scenario, String str, String expected) {
		
		String result = function.apply(str);

		if (expected == null) expected = str;
		expected = prefix + expected;
		
		if (result.equals(expected)) 
			System.out.print(scenario + " ");
		else {
			System.out.println("\n" + scenario + " FAILED!");
			System.out.println("    EXPECTED: " + expected);
			System.out.println("    RETURNED: " + result);
			++failures;
		}
	}
	
	public void ss(String scenario, String input1, String input2, String expected) {
		
		String result = bifunction.apply(input1, input2);

		if (expected == null) expected = input1;
		if (result.equals(expected)) 
			System.out.print(scenario + " ");
		else {
			System.out.println("\n" + scenario + " FAILED!");
			System.out.println("    EXPECTED: " + expected);
			System.out.println("    RETURNED: " + result);
			++failures;
		}
	}
	
	public void checkFailures() throws Exception {
		
		if (failures > 0)
			throw new Exception("Failed tests: " + failures);
	}

	// convenience method to load a resource file
	public static File getResourceFile(String name) {
		return new File(Test.class.getResource(name).getFile());
	}
}

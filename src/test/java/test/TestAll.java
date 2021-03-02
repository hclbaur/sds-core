package test;

public final class TestAll {

	public static void main(String[] args) throws Exception {

		System.out.print("\nNaturalIV   : ");
		test.TestNaturalInterval.main(args);
		
		System.out.print("\nIntegerIV   : ");
		test.TestIntervalInteger.main(args);
		
		System.out.print("\nDecimalIV   : ");
		test.TestIntervalDecimal.main(args);
		
		System.out.print("\nDateTimeIV  : ");
		test.TestIntervalDateTime.main(args);
		
		System.out.print("\nDateIV      : ");
		test.TestIntervalDate.main(args);
		
		System.out.print("\nSchema      : ");
		test.TestSchema.main(args);
		
		System.out.print("\nValidation1 : ");
		test.TestValidation1.main(args);
		
		System.out.print("\nValidation2 : ");
		test.TestValidation2.main(args);
	}
}

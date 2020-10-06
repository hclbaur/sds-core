package test;

public final class TestAll {

	public static void main(String[] args) throws Exception {

		System.out.print("\nSchema      : ");
		test.TestSchema.main(args);
		
		System.out.print("\nType        : ");
		test.TestType.main(args);
		
		System.out.print("\nSimpleType  : ");
		test.TestSimpleType.main(args);

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
	}
}

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
		
		System.out.print("\nSDSParser   : ");
		test.TestSDSParser.main(args);
		
		System.out.print("\nContacts    : ");
		test.validation.Contacts.main(args);
		
		System.out.print("\nAddressbook : ");
		test.validation.Addressbook.main(args);
		
		System.out.print("\nChoices     : ");
		test.validation.Choices.main(args);
		
		System.out.print("\nGroups      : ");
		test.validation.Groups.main(args);

		System.out.print("\nUnordered   : ");
		test.validation.Unordered.main(args);
		
		System.out.print("\nRussianDoll : ");
		test.validation.RussianDolls.main(args);
		
		System.out.print("\nCartesian   : ");
		test.validation.Cartesian.main(args);
	}
}

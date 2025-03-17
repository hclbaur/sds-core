package samples.types;

import java.math.BigInteger;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * An {@code IBAN} is an International Bank Account Number, following ISO
 * 13616:2020 format. Valid IBAN numbers start with a two-letter country code,
 * followed by a 2-digit checksum and an alphanumeric BBAN code of up to 30
 * characters.
 */
public final class IBAN {
	
	private final String countrycode, checksum, bban; // basic bank account number
	
	/** Private constructor */
	private IBAN(String countrycode, String checksum, String bban) {
		this.countrycode = countrycode;
		this.checksum = checksum;
		this.bban = bban;
	}

	private static final Pattern pattern = Pattern.compile("[A-Z]{2}\\d{2}[A-Z0-9]{1,30}");
	private static final BigInteger BI97 = BigInteger.valueOf(97);
	

	/** Name of the custom IBAN type. */
	public static final String TYPE = "IBAN";
	
	/**
	 * Function to construct a IBAN value from a string.
	 * @throws IllegalArgumentException if the string cannot be converted to an IBAN.
	 */
	public static final Function<String, IBAN> CONSTRUCTOR = s -> {
		return IBAN.parse(s);
	};
	
	
	/**
	 * Returns a IBAN obtained from a string in ISO 13616:2020 format. The
	 * validation logic is somewhat naive; the country code and BBAN are not
	 * checked, only the checksum is verified.
	 *
	 * @param iban a string in IBAN format.
	 * @return an IBAN
	 * @throws IllegalArgumentException if an invalid IBAN is supplied
	 */
	public static IBAN parse(String iban) {
		
		Objects.requireNonNull(iban, "IBAN must not be null");
		
		iban = iban.replaceAll("\\s+","").toUpperCase(); // remove whitespace
		if (! pattern.matcher(iban).matches()) 
			throw new IllegalArgumentException("invalid format");
		
		String countrycode = iban.substring(0, 2), checksum = iban.substring(2, 4), 
			bban = iban.substring(4), checkstring = bban + countrycode + checksum;
		
		StringBuffer s = new StringBuffer(32);
		for(char c : checkstring.toCharArray()) {
			if (c > 64)
				s.append(c - 55);
			else
				s.append((char) c);		    
		}
		
		BigInteger bi = new BigInteger(s.toString());
		if (! bi.mod(BI97).equals(BigInteger.ONE)) 
			throw new IllegalArgumentException("invalid checksum");
		
		return new IBAN(countrycode, checksum, bban);
	}

	
	/**
	 * Returns this IBAN in readable ISO 13616:2020 format.
	 */
	@Override
	public String toString() {
		int i = 0; StringBuffer readable = new StringBuffer(32);
		for (int n = bban.length() - 4; i < n; i += 4)
			readable.append(bban.substring(i, i+4)).append(" ");
		readable.append(bban.substring(i));
		return countrycode + checksum + " " + readable;
	}
	

	/**
	 * Returns the number of alphanumeric characters representing this IBAN,
	 * excluding whitespace. The maximum length of any given IBAN is 34.
	 */
	public int length() {
		return bban.length() + 4;
	}


	public static void main(String[] args) {
		System.out.println(IBAN.parse("NL64ABNC0417164300"));
	}
}

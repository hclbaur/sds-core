package be.baur.sds.serialization;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import be.baur.sda.serialization.ParseException;
import be.baur.sda.serialization.Parser;
import be.baur.sds.SDS;
import be.baur.sds.Schema;

/**
 * A {@code SchemaParser} is a <i>deserializer</i> that reads an input stream in
 * a format specific to the type of parser, to create a {@code Schema}. A sample
 * implementation is the default SDS parser.
 * 
 * @see SDSParser
 */
public interface SchemaParser extends Parser<Schema> {


	/**
	 * Creates a schema from a character input stream.
	 * 
	 * @param input an input stream
	 * @return a schema
	 * @throws IOException    if an input exception occurs
	 * @throws ParseException if a parse exception occurs
	 */
	Schema parse(Reader input) throws IOException, ParseException;


	/**
	 * Verifies a schema. This method can be used used to to validate schema which
	 * was not created by the default {@code SDSParser}. The default implementation
	 * just serializes the schema to SDS format, which is then parsed back to reveal
	 * any issues.
	 * 
	 * @param schema the schema to be verified
	 * @throws IOException    if an input exception occurs
	 * @throws ParseException if a parse exception occurs
	 * 
	 */
	default void verify(Schema schema) throws IOException, ParseException {
		SDS.parser().parse(new StringReader(schema.toString()));
	}
}

package be.baur.sds.serialization;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;

import be.baur.sds.Schema;

/**
 * A <code>Parser</code> (in SDS context) is a <i>deserializer</i> that reads an
 * input stream (in a format specific to the type of parser) and creates a
 * {@code Schema}. A sample implementation is the default {@link SDSParser}.
 */
public interface Parser {


	/**
	 * Creates a schema from a character input stream.
	 * 
	 * @param input an input stream
	 * @return a schema
	 * @throws IOException if an input exception occurs
	 * @throws ParseException if a parse exception occurs
	 * @throws SchemaException if a schema exception occurs
	 */
	Schema parse(Reader input) throws IOException, ParseException, SchemaException;


	/**
	 * Verifies a schema. This method can be used to verify a schema. This can be
	 * used to to validate schema not created by the default {@code SDSParser}, but
	 * assembled "manually" or created by another parser. The default implementation
	 * serializes the schema to SDS format, which is then parsed to reveal any
	 * issues.
	 */
	default void verify(Schema schema) throws IOException, ParseException, SchemaException {
		parse(new StringReader(schema.toString()));   //must use SDS parser???
	}
}

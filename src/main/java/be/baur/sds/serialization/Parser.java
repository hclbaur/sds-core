package be.baur.sds.serialization;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import be.baur.sda.serialization.SyntaxException;
import be.baur.sds.Schema;


/**
 * A <code>Parser</code> (in SDS context) is a <em>deserializer</em> that 
 * reads an input stream (in a format specific to the type of parser) and
 * (re-)creates an SDA schema in memory.
 */
public interface Parser {


	/**
	 * Parses a character <code>input</code> stream and creates a {@link Schema}.
	 * @return Schema - the schema node.
	 */
	Schema parse(Reader input) throws IOException, SyntaxException, SchemaException;


	/**
	 * Verifies a <code>schema</code>. This method can be used to verify a schema
	 * not obtained from an SDS definition, but constructed in some another way or
	 * by another type of parser. The default implementation serializes the schema
	 * to SDS format, which is then parsed to reveal any issues.
	 */
	default void verify(Schema schema) throws SyntaxException, IOException, SchemaException   {
		parse(new StringReader(schema.toString()));
	}
}

package be.baur.sds.validation;

import be.baur.sda.Node;
import be.baur.sds.Schema;

/**
 * A <code>Validator</code> (in SDS context) is used to check if an SDA document is
 * both well-formed and valid, e.g. if it conforms to a particular schema.
 */
public interface Validator {

	/**
	 * This method validates the supplied SDA <code>document</code> against the
	 * specified global <code>type</code> in the supplied <code>schema</code>. The
	 * returned {@link ErrorList} is empty if the document is valid, and contains
	 * one or more validation errors otherwise.<br>
	 * A <code>null</code> or empty type is acceptable if it can be determined
	 * unambiguously (when the schema has a designated or single root type). An
	 * exception is thrown if the type does not exist, or cannot be determined.
	 * 
	 * @throws IllegalArgumentException
	 */
	public ErrorList validate(Node document, Schema schema, String type);
	
}
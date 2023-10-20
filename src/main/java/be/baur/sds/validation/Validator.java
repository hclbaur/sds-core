package be.baur.sds.validation;

import be.baur.sda.DataNode;
import be.baur.sds.Schema;

/**
 * A <code>Validator</code> (in SDS context) is used to check if an SDA document
 * is <i>valid</i>, that is whether it conforms to a particular schema. A sample
 * implementation is the default {@link SDAValidator}.
 */
public interface Validator {

	/**
	 * Validates an SDA document (node). This method validates a node (and its child
	 * nodes) against a global type within the schema. The global type name may be
	 * null or empty if the schema has a default type. An exception is thrown if no
	 * appropriate type can be found.
	 * 
	 * @param node       the node to be validated
	 * @param schema     the schema to be used for validation
	 * @param globaltype a global type name, may be null
	 * @return an error list, empty if no validation errors were found
	 * @throws IllegalArgumentException - if the type is not found
	 */
	public ErrorList validate(DataNode node, Schema schema, String globaltype);
	
}
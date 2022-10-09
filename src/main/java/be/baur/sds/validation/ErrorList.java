package be.baur.sds.validation;

import java.util.ArrayList;

/**
 * An {@code ErrorList} is a convenience class for a list of errors.
 */
@SuppressWarnings("serial")
public final class ErrorList extends ArrayList<Error> {

	/**
	 * Add an error to this list. This method ignores a null argument.
	 * 
	 * @param error an error, may be null
	 * @return true if an error was added
	 */
	@Override
	public boolean add(Error error) {
		if (error == null) return false;
		return super.add(error);
	}

}

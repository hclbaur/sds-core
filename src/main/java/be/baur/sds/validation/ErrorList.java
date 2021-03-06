package be.baur.sds.validation;

import java.util.ArrayList;

/**
 * A convenience class for an {@link Error} list.
 */
@SuppressWarnings("serial")
public final class ErrorList extends ArrayList<Error> {

	/** Safe add method (prevents adding null references). */
	@Override
	public boolean add(Error error) {
		if (error == null) return false;
		return super.add(error);
	}

}

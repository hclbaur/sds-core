package be.baur.sds.types;

import java.time.LocalDate;
import java.util.function.Function;

import be.baur.sds.SDS;

/**
 * A <code>DateNodeType</code> defines an SDA node with a temporal value (a calendar
 * date without time zone).
 */
public final class DateNodeType extends ComparableNodeType <LocalDate> {


	/**
	 * Creates a date node type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public DateNodeType(String name) {
		super(name);
	}
	

	@Override
	public String getDataType() {
		return SDS.DATE_TYPE;
	}
	

	@Override
	public Function<String, LocalDate> valueConstructor() {
		return SDS.DATE_CONSTRUCTOR;
	}

}

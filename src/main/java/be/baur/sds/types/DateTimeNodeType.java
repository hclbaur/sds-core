package be.baur.sds.types;

import java.time.ZonedDateTime;
import java.util.function.Function;

import be.baur.sds.DataType;

/**
 * A <code>DateTimeNodeType</code> defines an SDA node with a temporal value (a
 * date and time including the time zone).
 */
public final class DateTimeNodeType extends ComparableNodeType <ZonedDateTime> {


	/**
	 * Creates a date-time node type with the specified name.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public DateTimeNodeType(String name) {
		super(name);
	}


	@Override
	public String getDataType() {
		return DataType.DATETIME;
	}


	@Override
	public Function<String, ZonedDateTime> valueConstructor() {
		return DataType.DATETIME_CONSTRUCTOR;
	}

}

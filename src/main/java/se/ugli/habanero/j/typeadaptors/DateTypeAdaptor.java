package se.ugli.habanero.j.typeadaptors;

import java.util.Date;

import se.ugli.habanero.j.TypeAdaptor;

public class DateTypeAdaptor implements TypeAdaptor {

	@Override
	public boolean supports(final Class<?> type) {
		return Date.class.isAssignableFrom(type);
	}

	@Override
	public Object toJdbcValue(final Object object) {
		return object;
	}

	@Override
	public String toSqlStr(final Object object) {
		if (object == null)
			return "null";
		return "'" + object.toString() + "'";
	}

	@Override
	public Object toTypeValue(final Class<?> type, final Object object) {
		return object;
	}

}

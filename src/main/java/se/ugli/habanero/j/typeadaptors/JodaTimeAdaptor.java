package se.ugli.habanero.j.typeadaptors;

import java.sql.Timestamp;

import org.joda.time.DateTime;

import se.ugli.habanero.j.TypeAdaptor;

public class JodaTimeAdaptor implements TypeAdaptor {

	@Override
	public boolean supports(final Class<?> type) {
		return type == DateTime.class;
	}

	@Override
	public Object toJdbcValue(final Object object) {
		return new Timestamp(((DateTime) object).getMillis());
	}

	@Override
	public Object toTypeValue(final Class<?> type, final Object object) {
		if (object != null)
			return new DateTime(object);
		return null;
	}

}

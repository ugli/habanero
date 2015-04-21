package se.ugli.habanero.j.typeadaptors;

import se.ugli.habanero.j.TypeAdaptor;

public class BooleanTypeAdaptor implements TypeAdaptor {

	@Override
	public boolean supports(final Class<?> type) {
		return type == Boolean.class;
	}

	@Override
	public Object toJdbcValue(final Object object) {
		return object;
	}

	@Override
	public Object toTypeValue(final Class<?> type, final Object object) {
		if (object == null || object instanceof Boolean)
			return object;
		else if (object instanceof Number)
			return ((Number) object).intValue() != 0;
		return 0;
	}

}
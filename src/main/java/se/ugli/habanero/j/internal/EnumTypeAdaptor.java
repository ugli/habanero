package se.ugli.habanero.j.internal;

import se.ugli.habanero.j.TypeAdaptor;

public class EnumTypeAdaptor implements TypeAdaptor {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object toTypeValue(final Class type, final Object object) {
		if (object != null)
			return Enum.valueOf(type, object.toString());
		return null;
	}

	@Override
	public Object toJdbcValue(final Object object) {
		if (object != null) {
			if (object instanceof Enum<?>)
				return ((Enum<?>) object).name();
			throw new IllegalStateException();
		}
		return null;
	}

	@Override
	public boolean supports(final Class<?> type) {
		return Enum.class.isAssignableFrom(type);
	}

}

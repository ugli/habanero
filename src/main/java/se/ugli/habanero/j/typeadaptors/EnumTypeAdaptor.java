package se.ugli.habanero.j.typeadaptors;

import se.ugli.habanero.j.TypeAdaptor;

public class EnumTypeAdaptor implements TypeAdaptor {

	@Override
	public boolean supports(final Class<?> type) {
		return Enum.class.isAssignableFrom(type);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object toTypeValue(final Class type, final Object object) {
		if (object != null)
			return Enum.valueOf(type, object.toString());
		return null;
	}

}

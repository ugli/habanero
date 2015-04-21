package se.ugli.habanero.j.typeadaptors;

import se.ugli.commons.Id;
import se.ugli.habanero.j.TypeAdaptor;

public class IdTypeAdaptor implements TypeAdaptor {

	@Override
	public boolean supports(final Class<?> type) {
		return type == Id.class;
	}

	@Override
	public Object toJdbcValue(final Object object) {
		if (object != null) {
			final Id id = (Id) object;
			return id.value;
		}
		return null;
	}

	@Override
	public Object toTypeValue(final Class<?> type, final Object object) {
		return Id.apply((String) object);
	}

}
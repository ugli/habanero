package se.ugli.habanero.j.typeadaptors;

import java.sql.Clob;

import se.ugli.habanero.j.TypeAdaptor;
import se.ugli.habanero.j.internal.ClobReader;

public class CharSequenceTypeAdaptor implements TypeAdaptor {

	@Override
	public boolean supports(final Class<?> type) {
		return CharSequence.class.isAssignableFrom(type);
	}

	@Override
	public Object toJdbcValue(final Object object) {
		if (object == null)
			return null;
		return object.toString();
	}

	@Override
	public String toSqlStr(final Object object) {
		if (object == null)
			return "null";
		return "'" + object.toString() + "'";
	}

	@Override
	public Object toTypeValue(final Class<?> type, final Object object) {
		if (object instanceof Clob)
			return ClobReader.read((Clob) object);
		return object;
	}

}

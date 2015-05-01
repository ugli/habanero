package se.ugli.habanero.j.typeadaptors;

import java.sql.Clob;

import se.ugli.habanero.j.TypeAdaptor;
import se.ugli.habanero.j.internal.ClobReader;

public class ClobTypeAdaptor implements TypeAdaptor {

	@Override
	public boolean supports(final Class<?> type) {
		return type == Clob.class;
	}

	@Override
	public Object toJdbcValue(final Object object) {
		return object;
	}

	@Override
	public String toSqlStr(final Object object) {
		return "'" + ClobReader.read((Clob) object) + "'";
	}

	@Override
	public Object toTypeValue(final Class<?> type, final Object object) {
		return ClobReader.read((Clob) object);
	}

}

package se.ugli.habanero.j.internal;

import java.sql.ResultSet;
import java.sql.SQLException;

import se.ugli.habanero.j.Habanero;
import se.ugli.habanero.j.ResultSetIterator;
import se.ugli.habanero.j.TypeAdaptor;

public class SingleValueIterator<T> extends ResultSetIterator<T> {

	private final Class<T> type;
	private final TypeAdaptor typeAdaptor;

	public SingleValueIterator(final Class<T> type) {
		if (type == null)
			throw new IllegalArgumentException();
		this.type = type;
		this.typeAdaptor = Habanero.getTypeAdaptor(type);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T nextObject(final ResultSet resultSet) throws SQLException {
		return (T) typeAdaptor.toTypeValue(type, resultSet.getObject(1));
	}
}

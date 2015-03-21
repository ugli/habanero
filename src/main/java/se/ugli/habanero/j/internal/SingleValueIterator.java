package se.ugli.habanero.j.internal;

import java.sql.ResultSet;
import java.sql.SQLException;

import se.ugli.habanero.j.HabaneroException;
import se.ugli.habanero.j.ResultSetIterator;
import se.ugli.habanero.j.TypeAdaptor;
import se.ugli.habanero.j.TypeRegister;
import se.ugli.habanero.j.util.Option;

public class SingleValueIterator<T> extends ResultSetIterator<T> {

	private final TypeAdaptor typeAdaptor;
	private final Class<T> type;

	public SingleValueIterator(final Class<T> type) {
		if (type == null)
			throw new IllegalArgumentException();
		this.type = type;
		final Option<TypeAdaptor> typeAdaptorOpt = TypeRegister.get(type);
		if (typeAdaptorOpt.isDefined())
			this.typeAdaptor = typeAdaptorOpt.get();
		else
			throw new HabaneroException(type.getName() + " isn't registered.");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T nextObject(final ResultSet resultSet) throws SQLException {
		return (T) typeAdaptor.toTypeValue(type, resultSet.getObject(1));
	}
}

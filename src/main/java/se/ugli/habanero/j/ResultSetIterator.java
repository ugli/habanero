package se.ugli.habanero.j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public abstract class ResultSetIterator<E> implements Iterator<E> {

	private ResultSet resultSet;

	@Override
	public final boolean hasNext() {
		try {
			return resultSet.next();
		} catch (final SQLException e) {
			throw new HabaneroException(e);
		}
	}

	@Override
	public final E next() {
		try {
			return nextObject(resultSet);
		} catch (final SQLException e) {
			throw new HabaneroException(e);
		}
	}

	@Override
	public final void remove() {
		throw new UnsupportedOperationException("remove");
	}

	final void init(final ResultSet resultSet) throws SQLException {
		this.resultSet = resultSet;
		postInit(resultSet);
	}

	protected abstract E nextObject(ResultSet resultSet) throws SQLException;

	protected void postInit(final ResultSet resultSet) throws SQLException {
	}

}

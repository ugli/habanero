package se.ugli.habanero.j;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class TypedMapIterator<T> extends ResultSetIterator<T> {

	private Iterable<String> columns;

	@Override
	protected final T nextObject(final ResultSet resultSet) throws SQLException {
		return nextObject(createMap(resultSet));
	}

	protected abstract T nextObject(TypedMap typedMap);

	@Override
	protected void postInit(final ResultSet resultSet) throws SQLException {
		columns = createColumns(resultSet);
	}

	private Iterable<String> createColumns(final ResultSet resultSet) throws SQLException {
		final List<String> result = new ArrayList<String>();
		final ResultSetMetaData metaData = resultSet.getMetaData();
		final int columnCount = metaData.getColumnCount();
		for (int columnNumber = 0; columnNumber < columnCount; columnNumber++)
			result.add(metaData.getColumnLabel(columnNumber + 1).toLowerCase());
		return result;
	}

	private TypedMap createMap(final ResultSet resultSet) throws SQLException {
		final TypedHashMap result = new TypedHashMap();
		for (final String column : columns) {
			final Object columnValue = resultSet.getObject(column);
			result.map.put(column, columnValue);
		}
		return result;
	}

}
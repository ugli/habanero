package se.ugli.habanero.j;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public abstract class TypedMapIterator<T> extends ResultSetIterator<T> {

	private static class Column {

		static Column apply(final ResultSetMetaData metaData, final int columnNumber) throws SQLException {
			final String name = metaData.getColumnLabel(columnNumber + 1).toLowerCase();
			final String table = metaData.getTableName(columnNumber + 1).toLowerCase();
			return new Column(name, table);
		}

		final String name;
		final String table;

		Column(final String name, final String table) {
			this.name = name;
			this.table = table;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final Column other = (Column) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (table == null) {
				if (other.table != null)
					return false;
			} else if (!table.equals(other.table))
				return false;
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (name == null ? 0 : name.hashCode());
			result = prime * result + (table == null ? 0 : table.hashCode());
			return result;
		}

		String getKey() {
			if (table != null && !table.trim().isEmpty())
				return table + "." + name;
			return name;
		}

	}

	private Set<Column> columns;

	@Override
	protected final T nextObject(final ResultSet resultSet) throws SQLException {
		return nextObject(createMap(columns, resultSet));
	}

	protected abstract T nextObject(TypedMap typedMap);

	@Override
	protected void postInit(final ResultSet resultSet) throws SQLException {
		columns = createColumns(resultSet);
	}

	private Set<Column> createColumns(final ResultSet resultSet) throws SQLException {
		final Set<Column> result = new HashSet<Column>();
		final ResultSetMetaData metaData = resultSet.getMetaData();
		final int columnCount = metaData.getColumnCount();
		for (int columnNumber = 0; columnNumber < columnCount; columnNumber++)
			result.add(Column.apply(metaData, columnNumber));
		return result;
	}

	private TypedMap createMap(final Set<Column> columns, final ResultSet resultSet) throws SQLException {
		final TypedHashMap result = new TypedHashMap();
		for (final Column column : columns) {
			final Object columnValue = resultSet.getObject(column.getKey());
			result.map.put(column.name, columnValue);
			result.map.put(column.getKey(), columnValue);
		}
		return result;
	}

}
package se.ugli.habanero.j.metadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import se.ugli.commons.CloseCommand;
import se.ugli.commons.Option;
import se.ugli.habanero.j.HabaneroException;

public class MetaData {

	private final DataSource dataSource;

	private MetaData(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public static MetaData apply(final DataSource dataSource) {
		return new MetaData(dataSource);
	}

	public Option<SqlType> getColumnType(final String tableName, final String columnName) {
		Connection connection = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			final DatabaseMetaData metaData = connection.getMetaData();
			resultSet = metaData.getColumns(null, null, tableName.toUpperCase(), columnName.toUpperCase());
			while (resultSet.next()) {
				final int typeNumber = resultSet.getInt("DATA_TYPE");
				final SqlType sqlType = SqlType.applyTypeNumber(typeNumber);
				return Option.apply(sqlType);
			}
			return Option.none();
		} catch (final SQLException e) {
			throw new HabaneroException(e);
		} finally {
			CloseCommand.execute(resultSet, connection);
		}
	}

	public DatabaseProductName getDatabaseProductName() {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			final DatabaseMetaData databaseMetaData = connection.getMetaData();
			final String productName = databaseMetaData.getDatabaseProductName();
			if (productName.equalsIgnoreCase("PostgreSQL"))
				return DatabaseProductName.POSTGRESQL;
			else if (productName.equalsIgnoreCase("H2"))
				return DatabaseProductName.H2;
			else if (productName.contains("DB2"))
				return DatabaseProductName.DB2;
			else
				throw new HabaneroException("Unknown product name: " + productName);
		} catch (final SQLException e) {
			throw new HabaneroException(e);
		} finally {
			CloseCommand.execute(connection);
		}
	}

}

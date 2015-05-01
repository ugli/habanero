package se.ugli.habanero.j.batch;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import se.ugli.commons.CloseCommand;
import se.ugli.habanero.j.Habanero;
import se.ugli.habanero.j.TypeAdaptor;

public class Batch {

	private Connection connection = null;
	private final DataSource dataSource;
	private Statement statement = null;

	public Batch(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void add(final BatchItem batchItem) throws SQLException {
		add(batchItem.sql, batchItem.args);
	}

	public void add(final String sql, final Object... args) throws SQLException {
		String newSql = sql;
		for (final Object arg : args)
			if (arg == null)
				newSql = insertFirstJdbcValue(newSql, "null");
			else {
				final Class<?> type = arg.getClass();
				final TypeAdaptor typeAdaptor = Habanero.getTypeAdaptor(type);
				final String columnValueAsStr = typeAdaptor.toSqlStr(arg);
				newSql = insertFirstJdbcValue(newSql, columnValueAsStr);
			}
		getStatement().addBatch(newSql);
	}

	public void close() {
		CloseCommand.execute(statement, connection);
	}

	public int[] execute() throws SQLException {
		return getStatement().executeBatch();
	}

	private Statement getStatement() throws SQLException {
		if (statement == null) {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
		}
		return statement;
	}

	private String insertFirstJdbcValue(final String sql, final String value) {
		return sql.replaceFirst("\\?", value);
	}

}

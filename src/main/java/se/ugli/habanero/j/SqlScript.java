package se.ugli.habanero.j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javax.sql.DataSource;

import se.ugli.habanero.j.internal.CloseUtil;

public class SqlScript {

	public static final String DEFAULT_STATEMENT_DELIMITER = ";";
	public static final char LINE_DELIMITER = '\n';

	public static SqlScript apply(final DataSource dataSource) {
		return new SqlScript(dataSource, DEFAULT_STATEMENT_DELIMITER);
	}

	public static SqlScript apply(final DataSource dataSource, final String statementDelimiter) {
		return new SqlScript(dataSource, statementDelimiter);
	}

	private final DataSource dataSource;
	private final String statementDelimiter;

	private SqlScript(final DataSource dataSource, final String statementDelimiter) {
		this.dataSource = dataSource;
		this.statementDelimiter = statementDelimiter;
	}

	public void run(final File source) {
		try {
			run(new Scanner(source));
		} catch (final FileNotFoundException e) {
			throw new HabaneroException(e);
		}
	}

	public void run(final File source, final String charsetName) {
		try {
			run(new Scanner(source, charsetName));
		} catch (final FileNotFoundException e) {
			throw new HabaneroException(e);
		}
	}

	public void run(final InputStream source) {
		run(new Scanner(source));
	}

	public void run(final InputStream source, final String charsetName) {
		run(new Scanner(source, charsetName));
	}

	public void run(final Readable source) {
		run(new Scanner(source));
	}

	public void run(final String source) {
		run(new Scanner(source));
	}

	private void run(final Scanner scanner) {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			makeBatch(scanner, statement);
			statement.executeBatch();
		} catch (final SQLException e) {
			throw new HabaneroException(e);
		} finally {
			CloseUtil.close(statement, connection, scanner);
		}
	}

	private void makeBatch(final Scanner scanner, final Statement statement) throws SQLException {
		StringBuilder sqlBuilder = new StringBuilder();
		while (scanner.hasNext()) {
			if (sqlBuilder.length() > 0)
				sqlBuilder.append(LINE_DELIMITER);
			sqlBuilder.append(scanner.nextLine());
			final String sql = sqlBuilder.toString();
			if (sql.trim().endsWith(statementDelimiter)) {
				statement.addBatch(sql);
				sqlBuilder = new StringBuilder();
			}
		}
	}

}

package se.ugli.habanero.j;

/*
 * Slightly modified version of the com.ibatis.common.jdbc.ScriptRunner class from the iBATIS Apache project. Only
 * removed dependency on Resource class and a constructor
 */
/*
 * Copyright 2004 Clinton Begin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

import java.io.Closeable;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.ugli.habanero.j.internal.CloseUtil;

/**
 * Tool to run database scripts
 */
public class ScriptRunner {

	private static final String DEFAULT_DELIMITER = ";";

	private final Connection connection;

	private final boolean stopOnError;
	private final boolean autoCommit;

	private final Logger log = LoggerFactory.getLogger(getClass());

	private String delimiter = DEFAULT_DELIMITER;
	private boolean fullLineDelimiter = false;

	/**
	 * Default constructor
	 */
	public ScriptRunner(final Connection connection, final boolean autoCommit, final boolean stopOnError) {
		this.connection = connection;
		this.autoCommit = autoCommit;
		this.stopOnError = stopOnError;
	}

	public void setDelimiter(final String delimiter, final boolean fullLineDelimiter) {
		this.delimiter = delimiter;
		this.fullLineDelimiter = fullLineDelimiter;
	}

	/**
	 * Runs an SQL script (read in using the Reader parameter)
	 * 
	 * @param reader
	 *            - the source of the script
	 */
	public void runScript(final Reader reader) throws IOException, SQLException {
		try {
			final boolean originalAutoCommit = connection.getAutoCommit();
			try {
				if (originalAutoCommit != this.autoCommit) {
					connection.setAutoCommit(this.autoCommit);
				}
				runScript(connection, reader);
			} finally {
				connection.setAutoCommit(originalAutoCommit);
			}
		} catch (final IOException e) {
			throw e;
		} catch (final SQLException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException("Error running script.  Cause: " + e, e);
		}
	}

	/**
	 * Runs an SQL script (read in using the Reader parameter) using the
	 * connection passed in
	 * 
	 * @param conn
	 *            - the connection to use for the script
	 * @param reader
	 *            - the source of the script
	 * @throws SQLException
	 *             if any SQL errors occur
	 * @throws IOException
	 *             if there is an error reading from the Reader
	 */
	private void runScript(final Connection conn, final Reader reader) throws IOException, SQLException {
		StringBuffer command = null;
		try {
			final LineNumberReader lineReader = new LineNumberReader(reader);
			String line = null;
			while ((line = lineReader.readLine()) != null) {
				if (command == null) {
					command = new StringBuffer();
				}
				final String trimmedLine = line.trim();
				if (trimmedLine.startsWith("--")) {
					// log.info(trimmedLine);
				} else if (trimmedLine.length() < 1 || trimmedLine.startsWith("//")) {
					// Do nothing
				} else if (trimmedLine.length() < 1 || trimmedLine.startsWith("--")) {
					// Do nothing
				} else if (!fullLineDelimiter && trimmedLine.endsWith(getDelimiter()) || fullLineDelimiter
						&& trimmedLine.equals(getDelimiter())) {
					command.append(line.substring(0, line.lastIndexOf(getDelimiter())));
					command.append(" ");
					final Statement statement = conn.createStatement();

					// log.info(command.toString());

					boolean hasResults = false;
					if (stopOnError) {
						hasResults = statement.execute(command.toString());
					} else {
						try {
							statement.execute(command.toString());
						} catch (final SQLException e) {
							if (command.toString().toUpperCase().startsWith("DROP")) {
								log.warn(e.getMessage());
							} else {
								log.error("Error executing: " + command, e);
							}
						}
					}

					if (autoCommit && !conn.getAutoCommit()) {
						conn.commit();
					}

					final ResultSet rs = statement.getResultSet();
					if (hasResults && rs != null) {
						final ResultSetMetaData md = rs.getMetaData();
						final int cols = md.getColumnCount();
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < cols; i++) {
							final String name = md.getColumnLabel(i);
							sb.append(name + "\t");
						}
						// log.info(sb.toString());
						while (rs.next()) {
							sb = new StringBuilder();
							for (int i = 0; i < cols; i++) {
								final String value = rs.getString(i);
								sb.append(value + "\t");
							}
							// log.info(sb.toString());
						}
					}

					command = null;
					try {
						statement.close();
					} catch (final Exception e) {
						// Ignore to workaround a bug in Jakarta DBCP
					}
					Thread.yield();
				} else {
					command.append(line);
					command.append(" ");
				}
			}
			if (!autoCommit) {
				conn.commit();
			}
		} catch (final SQLException e) {
			log.error("Error executing: " + command, e);
			throw e;
		} catch (final IOException e) {
			log.error("Error executing: " + command, e);
			throw e;
		} finally {
			conn.rollback();
		}
	}

	private String getDelimiter() {
		return delimiter;
	}

}
package se.ugli.habanero.j.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class DataSourceDelegate implements DataSource {

	private DataSource dataSource;

	public DataSourceDelegate(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSourceDelegate() {
	}

	public void setDelegate(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDelegate() {
		return dataSource;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public Connection getConnection(final String username, final String password) throws SQLException {
		return dataSource.getConnection(username, password);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return dataSource.getLoginTimeout();
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return dataSource.getLogWriter();
	}

	@Override
	public boolean isWrapperFor(final Class<?> iface) throws SQLException {
		return dataSource.isWrapperFor(iface);
	}

	@Override
	public void setLoginTimeout(final int seconds) throws SQLException {
		dataSource.setLoginTimeout(seconds);
	}

	@Override
	public void setLogWriter(final PrintWriter out) throws SQLException {
		dataSource.setLogWriter(out);
	}

	@Override
	public <T> T unwrap(final Class<T> iface) throws SQLException {
		return dataSource.unwrap(iface);
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return dataSource.getParentLogger();
	}

}

package se.ugli.habanero.j.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.sql.DataSource;

import se.ugli.habanero.j.HabaneroException;

public class JdbcDataSourceBuilder {

	private static class JdbcDataSource implements DataSource {

		private int loginTimeoutSec;
		private PrintWriter out;
		private final String password;
		private final String url;
		private final String user;

		protected JdbcDataSource(final String url, final String user, final String password) {
			this.url = url;
			this.user = user;
			this.password = password;
		}

		@Override
		public Connection getConnection() throws SQLException {
			return DriverManager.getConnection(url, user, password);
		}

		@Override
		public Connection getConnection(final String username, final String password) throws SQLException {
			return DriverManager.getConnection(url, username, password);
		}

		@Override
		public int getLoginTimeout() {
			return loginTimeoutSec;
		}

		@Override
		public PrintWriter getLogWriter() {
			return out;
		}

		@Override
		public boolean isWrapperFor(final Class<?> iface) throws SQLException {
			return false;
		}

		@Override
		public void setLoginTimeout(final int seconds) {
			this.loginTimeoutSec = seconds;
		}

		@Override
		public void setLogWriter(final PrintWriter out) {
			this.out = out;
		}

		@Override
		public <T> T unwrap(final Class<T> iface) {
			return null;
		}

		@Override
		public Logger getParentLogger() throws SQLFeatureNotSupportedException {
			throw new SQLFeatureNotSupportedException();
		}

	}

	private static Map<Class<?>, Driver> driverClassCache = new ConcurrentHashMap<Class<?>, Driver>();
	private static Map<String, Class<?>> driverClassNameCache = new ConcurrentHashMap<String, Class<?>>();

	public static JdbcDataSourceBuilder url(final String url) {
		return new JdbcDataSourceBuilder(url);
	}

	private static Driver createDriver(final Class<?> driverClass) {
		try {
			if (!driverClassCache.containsKey(driverClass))
				driverClassCache.put(driverClass, (Driver) driverClass.newInstance());
			return driverClassCache.get(driverClass);
		} catch (final InstantiationException e) {
			throw new HabaneroException(e);
		} catch (final IllegalAccessException e) {
			throw new HabaneroException(e);
		}
	}

	private static Class<?> createDriverClass(final String driverClassName) {
		try {
			if (!driverClassNameCache.containsKey(driverClassName))
				driverClassNameCache.put(driverClassName, Class.forName(driverClassName));
			return driverClassNameCache.get(driverClassName);
		} catch (final ClassNotFoundException e) {
			throw new HabaneroException(e);
		}
	}

	private Driver driver;
	private Class<?> driverClass;
	private String driverClassName;

	private String password;
	private final String url;
	private String user;

	private JdbcDataSourceBuilder(final String url) {
		this.url = url;
	}

	public DataSource build() {
		try {
			if (driverClassName != null)
				driverClass = createDriverClass(driverClassName);
			if (driverClass != null)
				driver = createDriver(driverClass);
			if (driver != null)
				DriverManager.registerDriver(driver);
			return new JdbcDataSource(url, user, password);
		} catch (final SQLException e) {
			throw new HabaneroException(e);
		}
	}

	public JdbcDataSourceBuilder driver(final Driver driver) {
		driverClassName = null;
		driverClass = null;
		this.driver = driver;
		return this;
	}

	public JdbcDataSourceBuilder driverClass(final Class<?> driverClass) {
		driverClassName = null;
		this.driverClass = driverClass;
		driver = null;
		return this;
	}

	public JdbcDataSourceBuilder driverClassName(final String driverClassName) {
		this.driverClassName = driverClassName;
		driverClass = null;
		driver = null;
		return this;
	}

	public JdbcDataSourceBuilder password(final String password) {
		this.password = password;
		return this;
	}

	public JdbcDataSourceBuilder user(final String user) {
		this.user = user;
		return this;
	}
}
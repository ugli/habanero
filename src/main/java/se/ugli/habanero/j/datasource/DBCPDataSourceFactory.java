package se.ugli.habanero.j.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;

import se.ugli.habanero.j.HabaneroException;

class DBCPDataSourceFactory {

	public static DataSource create(final Properties properties) {
		try {
			return BasicDataSourceFactory.createDataSource(properties);
		} catch (final Exception e) {
			throw new HabaneroException(e);
		}
	}

}

package se.ugli.habanero.j.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

class C3P0DataSourceFactory {

	static DataSource create(final Properties properties) {
		final ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setProperties(properties);
		return dataSource;
	}

}

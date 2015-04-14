package se.ugli.habanero.j.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import org.vibur.dbcp.ViburDBCPDataSource;

class ViburDataSourceFactory {

	static DataSource create(final Properties properties) {
		return new ViburDBCPDataSource(properties);
	}

}

package se.ugli.habanero.j.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

class HikariCPDataSourceFactory {

	static DataSource create(final Properties properties) {
		return new HikariDataSource(new HikariConfig(properties));
	}

}

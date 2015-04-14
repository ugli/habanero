package se.ugli.habanero.j.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import se.ugli.habanero.j.HabaneroException;

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;

class BoneCPDataSourceFactory {

	static DataSource create(final Properties properties) {
		try {
			return new BoneCPDataSource(new BoneCPConfig(properties));
		} catch (final Exception e) {
			throw new HabaneroException(e);
		}
	}

}

package se.ugli.habanero.j.datasource;

import java.util.UUID;

import javax.sql.DataSource;

import org.h2.Driver;

public class H2DataSource extends DataSourceDelegate {

	public H2DataSource() {
		this(UUID.randomUUID().toString());
	}

	public H2DataSource(final String name) {
		super(createDataSource(name));
	}

	private static DataSource createDataSource(final String name) {
		final String url = "jdbc:h2:mem:" + name + ";DB_CLOSE_DELAY=-1";
		final String user = "sa";
		return JdbcDataSourceBuilder.url(url).user(user).driver(new Driver()).build();
	}

}

package se.ugli.habanero.j.datasource;

import java.util.UUID;

import javax.sql.DataSource;

import org.h2.Driver;

public class H2DataSource extends DataSourceDelegate {

	private static DataSource createDataSource(final String name, final boolean dbCloseDelay) {
		final String url = createUrl(name, dbCloseDelay);
		final String user = "sa";
		return JdbcDataSourceBuilder.url(url).user(user).driver(new Driver()).build();
	}

	private static String createUrl(final String name, final boolean dbCloseDelay) {
		if (dbCloseDelay)
			return "jdbc:h2:mem:" + name + ";DB_CLOSE_DELAY=-1";
		return "jdbc:h2:mem:" + name;
	}

	public H2DataSource(final boolean dbCloseDelay) {
		this(UUID.randomUUID().toString(), dbCloseDelay);
	}

	public H2DataSource() {
		this(UUID.randomUUID().toString(), true);
	}

	public H2DataSource(final String name, final boolean dbCloseDelay) {
		super(createDataSource(name, dbCloseDelay));
	}

}

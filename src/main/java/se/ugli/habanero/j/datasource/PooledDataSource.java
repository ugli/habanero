package se.ugli.habanero.j.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import se.ugli.habanero.j.HabaneroException;
import se.ugli.habanero.j.internal.HabaneroProperties;
import se.ugli.habanero.j.internal.ResourceUtil;

public class PooledDataSource extends DataSourceDelegate {

	public static enum Implementation {
		BoneCP, C3P0, DBCP, HikariCP, Vibur
	}

	public static DataSource apply() {
		return apply(HabaneroProperties.get());
	}

	public static DataSource apply(final Implementation implementation) {
		return apply(HabaneroProperties.get(), implementation);
	}

	public static DataSource apply(final Properties properties) {
		if (ResourceUtil.exists("/com/zaxxer/hikari/HikariDataSource.class"))
			return apply(properties, Implementation.HikariCP);
		else if (ResourceUtil.exists("/com/jolbox/bonecp/BoneCPDataSource.class"))
			return apply(properties, Implementation.BoneCP);
		else if (ResourceUtil.exists("/com/mchange/v2/c3p0/ComboPooledDataSource.class"))
			return apply(properties, Implementation.BoneCP);
		else if (ResourceUtil.exists("/org/vibur/dbcp/ViburDBCPDataSource.class"))
			return apply(properties, Implementation.Vibur);
		else if (ResourceUtil.exists("/org/apache/commons/dbcp/BasicDataSource.class"))
			return apply(properties, Implementation.DBCP);
		throw new HabaneroException("Please depend on a pool implementation:" + Implementation.values());
	}

	public static DataSource apply(final Properties properties, final Implementation implementation) {
		if (implementation == Implementation.HikariCP)
			return new PooledDataSource(HikariCPDataSourceFactory.create(properties));
		else if (implementation == Implementation.BoneCP)
			return new PooledDataSource(BoneCPDataSourceFactory.create(properties));
		else if (implementation == Implementation.C3P0)
			return new PooledDataSource(C3P0DataSourceFactory.create(properties));
		else if (implementation == Implementation.Vibur)
			return new PooledDataSource(ViburDataSourceFactory.create(properties));
		else if (implementation == Implementation.DBCP)
			return new PooledDataSource(DBCPDataSourceFactory.create(properties));
		throw new IllegalArgumentException(implementation.name());
	}

	private PooledDataSource(final DataSource dataSource) {
		super(dataSource);
	}
}

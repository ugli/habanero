package se.ugli.habanero.j.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import se.ugli.commons.Resource;
import se.ugli.habanero.j.HabaneroException;
import se.ugli.habanero.j.internal.HabaneroProperties;

public class PooledDataSource extends DataSourceDelegate {

    public static enum Implementation {
        BoneCP, C3P0, DBCP, HikariCP, Vibur
    }

    public static DataSource apply() {
        return apply(HabaneroProperties.apply().getProperties());
    }

    public static DataSource apply(final Implementation implementation) {
        return apply(HabaneroProperties.apply().getProperties(), implementation);
    }

    public static DataSource apply(final Properties properties) {
        if (Resource.classExists("com.zaxxer.hikari.HikariDataSource"))
            return apply(properties, Implementation.HikariCP);
        else if (Resource.classExists("com.jolbox.bonecp.BoneCPDataSource"))
            return apply(properties, Implementation.BoneCP);
        else if (Resource.classExists("com.mchange.v2.c3p0.ComboPooledDataSource"))
            return apply(properties, Implementation.BoneCP);
        else if (Resource.classExists("org.vibur.dbcp.ViburDBCPDataSource"))
            return apply(properties, Implementation.Vibur);
        else if (Resource.classExists("org.apache.commons.dbcp.BasicDataSource"))
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

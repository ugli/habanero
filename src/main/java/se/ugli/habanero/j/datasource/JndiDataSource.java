package se.ugli.habanero.j.datasource;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import se.ugli.habanero.j.HabaneroException;

public class JndiDataSource extends DataSourceDelegate {

	private static DataSource lookupDataSource(final String jndiDataSourceName) {
		try {
			final InitialContext context = new InitialContext();
			return (DataSource) context.lookup(jndiDataSourceName);
		} catch (final NamingException e) {
			throw new HabaneroException(e);
		}
	}

	public JndiDataSource(final String jndiDataSourceName) {
		super(lookupDataSource(jndiDataSourceName));
	}

}

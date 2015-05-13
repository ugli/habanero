package se.ugli.habanero.j.test;

import javax.sql.DataSource;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import se.ugli.commons.Option;
import se.ugli.commons.Resource;
import se.ugli.habanero.j.batch.DataSet;
import se.ugli.habanero.j.batch.SqlScript;
import se.ugli.habanero.j.datasource.DataSourceDelegate;
import se.ugli.habanero.j.datasource.H2DataSource;

public class HabaneroTestRunner extends BlockJUnit4ClassRunner {

	private static final DataSourceDelegate dataSourceDelegate = new DataSourceDelegate();
	private static ThreadLocal<DataSourceDelegate> threadLocal = new ThreadLocal<DataSourceDelegate>();

	public static Option<DataSource> getDataSource() {
		final DataSource dataSource = threadLocal.get();
		return Option.apply(dataSource);
	}

	public HabaneroTestRunner(final Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected void runChild(final FrameworkMethod method, final RunNotifier notifier) {
		final H2DataSource dataSource = new H2DataSource();
		dataSourceDelegate.setDelegate(dataSource);
		threadLocal.set(dataSourceDelegate);
		final HabaneroTestConfig testConfig = getTestClass().getAnnotation(HabaneroTestConfig.class);
		if (testConfig != null) {
			runSchema(dataSource, testConfig.schema());
			runDataSet(dataSource, testConfig.dataSet());
		}
		super.runChild(method, notifier);
		threadLocal.set(null);
	}

	private void runDataSet(final DataSource dataSource, final String dataSet) {
		if (dataSet != null) {
			final Resource dataSetResource = Resource.apply(dataSet);
			DataSet.apply(dataSource).exec(dataSetResource);
		}

	}

	private void runSchema(final DataSource dataSource, final String schema) {
		if (schema != null && !schema.equals(HabaneroTestConfig.NO_RESOURCE)) {
			final Resource schemaResource = Resource.apply(schema);
			SqlScript.apply(dataSource).run(schemaResource);
		}
	}

}

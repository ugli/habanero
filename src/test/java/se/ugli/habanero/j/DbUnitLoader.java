package se.ugli.habanero.j;

import java.io.InputStream;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;

import se.ugli.habanero.j.HabaneroException;
import se.ugli.habanero.j.internal.CloseUtil;

public final class DbUnitLoader {

	private DbUnitLoader() {
	}

	public static void loadFromResource(final DataSource dataSource, final String dataSetResource) {
		IDatabaseConnection connection = null;
		try {
			connection = new DatabaseDataSourceConnection(dataSource);
			FlatXmlDataFileLoader fileLoader = new FlatXmlDataFileLoader();
			final FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
			builder.setColumnSensing(true);
			final InputStream resourceAsStream = DbUnitLoader.class.getResourceAsStream(dataSetResource);
			if (resourceAsStream == null) {
				throw new RuntimeException(dataSetResource + " not found");
			}
			final IDataSet dataSet = builder.build(resourceAsStream);
			DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
		} catch (final SQLException e) {
			throw new HabaneroException(e);
		} catch (final DataSetException e) {
			throw new HabaneroException(e);
		} catch (final DatabaseUnitException e) {
			throw new HabaneroException(e);
		} finally {
			CloseUtil.close(connection);
		}
	}

}
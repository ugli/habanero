package se.ugli.habanero.j.batch;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.ugli.habanero.j.Habanero;
import se.ugli.habanero.j.TypeAdaptor;

public class Batch implements AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Connection connection = null;
    private final DataSource dataSource;
    private Statement statement = null;

    public Batch(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void add(final BatchItem batchItem) throws SQLException {
        add(batchItem.sql, batchItem.args);
    }

    public void add(final String sql, final Object... args) throws SQLException {
        String newSql = sql;
        for (final Object arg : args)
            if (arg == null)
                newSql = insertFirstJdbcValue(newSql, "null");
            else {
                final Class<?> type = arg.getClass();
                final TypeAdaptor typeAdaptor = Habanero.getTypeAdaptor(type);
                final String columnValueAsStr = typeAdaptor.toSqlStr(arg);
                newSql = insertFirstJdbcValue(newSql, columnValueAsStr);
            }
        getStatement().addBatch(newSql);
    }

    @Override
    public void close() {
        if (statement != null)
            try {
                statement.close();
            }
            catch (final SQLException e) {
                logger.warn(e.getMessage(), e);
            }
        if (connection != null)
            try {
                connection.close();
            }
            catch (final SQLException e) {
                logger.warn(e.getMessage(), e);
            }
    }

    public int[] execute() throws SQLException {
        return getStatement().executeBatch();
    }

    private Statement getStatement() throws SQLException {
        if (statement == null) {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
        }
        return statement;
    }

    private static String insertFirstJdbcValue(final String sql, final String value) {
        return sql.replaceFirst("\\?", value);
    }

}

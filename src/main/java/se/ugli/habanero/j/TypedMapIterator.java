package se.ugli.habanero.j;

import java.io.InputStream;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import se.ugli.commons.CloseCommand;
import se.ugli.commons.CopyCommand;

public abstract class TypedMapIterator<T> extends ResultSetIterator<T> {

    private Iterable<String> columns;

    @Override
    protected final T nextObject(final ResultSet resultSet) throws SQLException {
        return nextObject(createMap(resultSet));
    }

    protected abstract T nextObject(TypedMap typedMap);

    @Override
    protected void postInit(final ResultSet resultSet) throws SQLException {
        columns = createColumns(resultSet);
    }

    private Iterable<String> createColumns(final ResultSet resultSet) throws SQLException {
        final List<String> result = new ArrayList<String>();
        final ResultSetMetaData metaData = resultSet.getMetaData();
        final int columnCount = metaData.getColumnCount();
        for (int columnNumber = 0; columnNumber < columnCount; columnNumber++) {
            result.add(metaData.getColumnLabel(columnNumber + 1).toLowerCase());
        }
        return result;
    }

    private TypedMap createMap(final ResultSet resultSet) throws SQLException {
        final TypedHashMap result = new TypedHashMap();
        for (final String column : columns) {
            final Object columnValue = resultSet.getObject(column);
            result.put(column, expandColumnValue(columnValue));
        }
        return result;
    }

    private Object expandColumnValue(final Object columnValue) {
        if (columnValue instanceof Clob) {
            return readClob((Clob) columnValue);
        }
        return columnValue;
    }

    private String readClob(final Clob clob) {
        InputStream inputStream = null;
        try {
            inputStream = clob.getAsciiStream();
            return CopyCommand.apply().copyToString(inputStream);
        }
        catch (final SQLException e) {
            throw new HabaneroException(e);
        }
        finally {
            CloseCommand.execute(inputStream);
        }
    }

}
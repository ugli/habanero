package se.ugli.habanero.j.metadata;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import se.ugli.habanero.j.HabaneroException;
import se.ugli.java.util.Try;

public class ResultSetMetaData {

    private final ResultSet resultSet;

    private ResultSetMetaData(final ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public static ResultSetMetaData apply(final ResultSet resultSet) {
        return new ResultSetMetaData(resultSet);
    }

    public Stream<String> getColumnLabels() {
        try {
            final java.sql.ResultSetMetaData metaData = resultSet.getMetaData();
            return IntStream.rangeClosed(1, metaData.getColumnCount())
                    .mapToObj(i -> Try.runtime(() -> metaData.getColumnLabel(i), HabaneroException::new));
        }
        catch (final SQLException e) {
            throw new HabaneroException(e);
        }
    }

}

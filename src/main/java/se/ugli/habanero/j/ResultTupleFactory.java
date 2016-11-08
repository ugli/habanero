package se.ugli.habanero.j;

import static se.ugli.java.util.stream.Collectors.toImmutableMap;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.function.Function;

import se.ugli.habanero.j.internal.BlobReader;
import se.ugli.habanero.j.internal.ClobReader;
import se.ugli.habanero.j.metadata.ResultSetMetaData;
import se.ugli.java.util.ImmutableMap;
import se.ugli.java.util.ImmutableSet;
import se.ugli.java.util.Try;

public class ResultTupleFactory implements Function<ResultSet, ResultTuple> {

    @Override
    public ResultTuple apply(final ResultSet resultSet) {
        return new ResultTupleImpl(ResultSetMetaData.apply(resultSet).getColumnLabels()
                .collect(toImmutableMap(c -> c.toLowerCase(), c -> columnValue(resultSet, c))));
    }

    private static Object columnValue(final ResultSet resultSet, final String column) {
        final Object columnValue = Try.runtime(() -> resultSet.getObject(column), HabaneroException::new);
        if (columnValue instanceof Clob)
            return ClobReader.read((Clob) columnValue);
        else if (columnValue instanceof Blob)
            return BlobReader.read((Blob) columnValue);
        return columnValue;
    }

    private static class ResultTupleImpl implements ResultTuple {

        private final ImmutableMap<String, Object> map;
        private final ImmutableSet<String> names;

        ResultTupleImpl(final ImmutableMap<String, Object> map) {
            this.map = map;
            names = map.keySet();
        }

        @Override
        public Optional<Object> get(final String name) {
            return map.get(name);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> Optional<T> get(final Class<T> type, final String name) {
            return get(name).map(e -> (T) Habanero.getTypeAdaptor(type).toTypeValue(type, e));
        }

        @Override
        public ImmutableSet<String> names() {
            return names;
        }

        @Override
        public String toString() {
            return map.toString();
        }

    }

}

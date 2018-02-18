package se.ugli.habanero.j;

import static java.util.Objects.requireNonNull;
import static java.util.stream.StreamSupport.stream;
import static se.ugli.java.lang.AutoCloseables.safeClose;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.sql.DataSource;

import se.ugli.habanero.j.batch.Batch;
import se.ugli.habanero.j.batch.BatchItem;
import se.ugli.habanero.j.datasource.H2DataSource;
import se.ugli.habanero.j.internal.ResultSetSpliterator;
import se.ugli.habanero.j.metadata.MetaData;
import se.ugli.habanero.j.metadata.SqlType;
import se.ugli.habanero.j.test.HabaneroTestRunner;
import se.ugli.habanero.j.typeadaptors.BlobTypeAdaptor;
import se.ugli.habanero.j.typeadaptors.BooleanTypeAdaptor;
import se.ugli.habanero.j.typeadaptors.CharSequenceTypeAdaptor;
import se.ugli.habanero.j.typeadaptors.ClobTypeAdaptor;
import se.ugli.habanero.j.typeadaptors.DateTypeAdaptor;
import se.ugli.habanero.j.typeadaptors.EnumTypeAdaptor;
import se.ugli.habanero.j.typeadaptors.IdTypeAdaptor;
import se.ugli.habanero.j.typeadaptors.JodaTimeAdaptor;
import se.ugli.habanero.j.typeadaptors.NumberTypeAdaptor;
import se.ugli.java.io.Resources;
import se.ugli.java.util.stream.ResourceStream;

public final class Habanero {

    public final DataSource dataSource;

    private Habanero(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    static {
        register(new NumberTypeAdaptor());
        register(new CharSequenceTypeAdaptor());
        register(new BooleanTypeAdaptor());
        register(new BlobTypeAdaptor());
        register(new ClobTypeAdaptor());
        register(new DateTypeAdaptor());

        register(new EnumTypeAdaptor());
        register(new IdTypeAdaptor());
        if (Resources.classExists("org.joda.time.DateTime"))
            register(new JodaTimeAdaptor());
    }

    public static Habanero apply() {
        final Optional<DataSource> dsOpt = HabaneroTestRunner.getDataSource();
        if (dsOpt.isPresent())
            return new Habanero(dsOpt.get());
        return new Habanero(new H2DataSource());
    }

    public static Habanero apply(final DataSource dataSource) {
        return new Habanero(dataSource);
    }

    public static TypeAdaptor getTypeAdaptor(final Class<?> type) {
        return TypeRegistry.get(type);
    }

    public static void register(final TypeAdaptor typeAdaptor) {
        TypeRegistry.add(typeAdaptor, true);
    }

    public static void register(final TypeAdaptor typeAdaptor, final boolean highestPriority) {
        TypeRegistry.add(typeAdaptor, highestPriority);
    }

    public static void unregister(final TypeAdaptor typeAdaptor) {
        TypeRegistry.remove(typeAdaptor);
    }

    public Batch batch() {
        return new Batch(dataSource);
    }

    public int[] batch(final Iterable<BatchItem> batchItems) {
        return batch(batchItems.iterator());
    }

    public int[] batch(final Iterator<BatchItem> batchIterator) {
        final Batch batch = new Batch(dataSource);
        try {
            while (batchIterator.hasNext())
                batch.add(batchIterator.next());
            return batch.execute();
        }
        catch (final SQLException e) {
            throw new HabaneroException(e);
        }
        finally {
            batch.close();
        }
    }

    public boolean execute(final String sql) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                return statement.execute();
            }
        }
        catch (final SQLException e) {
            throw new HabaneroException(e);
        }
    }

    public boolean executeCall(final String sql) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareCall(sql)) {
                return statement.execute();
            }
        }
        catch (final SQLException e) {
            throw new HabaneroException(e);
        }
    }

    public MetaData metadata() {
        return MetaData.apply(dataSource);
    }

    private static class SingleValueFunc<T> implements Function<ResultSet, T> {

        private final Class<T> type;

        public SingleValueFunc(final Class<T> type) {
            requireNonNull(type);
            this.type = type;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T apply(final ResultSet resultSet) {
            try {
                return (T) getTypeAdaptor(type).toTypeValue(type, resultSet.getObject(1));
            }
            catch (final SQLException e) {
                throw new HabaneroException(e);
            }
        }
    }

    public <T> Stream<T> queryMany(final Class<T> type, final String sql, final Object... args) {
        return queryMany(sql, args).map(new SingleValueFunc<>(type));
    }

    public <T> Stream<T> queryMany(final Group<T> group, final String sql, final Object... args) {
        try (Stream<ResultTuple> typeMapStream = queryMany(sql, args).map(new ResultTupleFactory())) {
            return group.createObjects(typeMapStream);
        }
    }

    public Stream<ResultSet> queryMany(final String sql, final Object... args) {
        return queryMany(c -> c.prepareStatement(sql), args);
    }

    @FunctionalInterface
    private static interface PrepereStatement {

        PreparedStatement prepare(Connection connection) throws SQLException;
    }

    @SuppressWarnings("resource")
    private Stream<ResultSet> queryMany(final PrepereStatement prepereStatement, final Object... args) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = prepereStatement.prepare(connection);
            prepareStatement(statement, args);
            resultSet = statement.executeQuery();
            final Stream<ResultSet> stream = stream(new ResultSetSpliterator(resultSet), false);
            return new ResourceStream<>(stream, true, resultSet, statement, connection);
        }
        catch (final SQLException e) {
            safeClose(resultSet, statement, connection);
            throw new HabaneroException(e);
        }
    }

    /**
     * Remember to close stream before leaving it to GC.
     *
     * @param <T> result type
     * @param type result type as Class
     * @param sql the SQL
     * @param args sql arguments
     * @return a stream of T
     */
    public <T> Stream<T> queryManyCall(final Class<T> type, final String sql, final Object... args) {
        return queryManyCall(sql, args).map(new SingleValueFunc<>(type));
    }

    public <T> Stream<T> queryManyCall(final Group<T> group, final String sql, final Object... args) {
        try (Stream<ResultTuple> typeMapStream = queryManyCall(sql, args).map(new ResultTupleFactory())) {
            return group.createObjects(typeMapStream);
        }
    }

    /**
     * Remember to close stream before leaving it to GC. Do not call next() on the ResultSet.
     *
     * @param sql the SQL
     * @param args sql arguments
     * @return a stream of resultSet
     */
    public Stream<ResultSet> queryManyCall(final String sql, final Object... args) {
        return queryMany(c -> c.prepareCall(sql), args);
    }

    public <T> Optional<T> queryOne(final Class<T> type, final String sql, final Object... args) {
        try (Stream<T> stream = queryMany(type, sql, args)) {
            return stream.filter(e -> e != null).findFirst();
        }
    }

    public <T> Optional<T> queryOne(final Group<T> group, final String sql, final Object... args) {
        return queryMany(group, sql, args).findFirst();
    }

    public Optional<ResultSet> queryOne(final String sql, final Object... args) {
        try (Stream<ResultSet> stream = queryMany(sql, args)) {
            return stream.findFirst();
        }
    }

    public <T> Optional<T> queryOneCall(final Class<T> type, final String sql, final Object... args) {
        try (Stream<T> stream = queryMany(type, sql, args)) {
            return stream.findFirst();
        }
    }

    public <T> Optional<T> queryOneCall(final Group<T> group, final String sql, final Object... args) {
        return queryManyCall(group, sql, args).findFirst();
    }

    public int update(final String sql, final Object... args) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                prepareStatement(statement, args);
                return statement.executeUpdate();
            }
        }
        catch (final SQLException e) {
            throw new HabaneroException(e);
        }
    }

    public int updateCall(final String sql, final Object... args) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareCall(sql)) {
                prepareStatement(statement, args);
                return statement.executeUpdate();
            }
        }
        catch (final SQLException e) {
            throw new HabaneroException(e);
        }
    }

    private static PreparedStatement prepareStatement(final PreparedStatement statement, final Object... args)
            throws SQLException {
        int parameterIndex = 1;
        for (final Object arg : args)
            if (arg instanceof SqlType && statement instanceof CallableStatement) {
                final SqlType outParam = (SqlType) arg;
                final CallableStatement callableStatement = (CallableStatement) statement;
                callableStatement.registerOutParameter(parameterIndex++, outParam.typeNumber);
            }
            else if (arg != null)
                statement.setObject(parameterIndex++, getTypeAdaptor(arg.getClass()).toJdbcValue(arg));
            else
                statement.setObject(parameterIndex++, null);
        return statement;
    }

}
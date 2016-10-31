package se.ugli.habanero.j;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.sql.DataSource;

import se.ugli.commons.Resource;
import se.ugli.habanero.j.batch.Batch;
import se.ugli.habanero.j.batch.BatchItem;
import se.ugli.habanero.j.datasource.H2DataSource;
import se.ugli.habanero.j.internal.PrepareArgumentsCommand;
import se.ugli.habanero.j.internal.SingleValueIterator;
import se.ugli.habanero.j.internal.TypedMapIdentityIterator;
import se.ugli.habanero.j.metadata.MetaData;
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
        if (Resource.classExists("org.joda.time.DateTime"))
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

    public <T> Stream<T> queryMany(final Class<T> type, final String sql, final Object... args) {
        return queryMany(new SingleValueIterator<>(type), sql, args);
    }

    public <T> Stream<T> queryMany(final Group<T> groupFunction, final String sql, final Object... args) {
        return groupFunction.createObjects(queryMany(new TypedMapIdentityIterator(), sql, args));
    }

    public <T> Stream<T> queryMany(final ResultSetIterator<T> iterator, final String sql, final Object... args) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                PrepareArgumentsCommand.apply(statement).exec(args);
                try (ResultSet resultSet = statement.executeQuery()) {
                    iterator.init(resultSet);
                    final List<T> result = new ArrayList<>();
                    while (iterator.hasNext())
                        result.add(iterator.next());
                    return result.stream().filter(o -> o != null);
                }
            }
        }
        catch (final SQLException e) {
            throw new HabaneroException(e);
        }
    }

    public <T> Stream<T> queryManyCall(final Class<T> type, final String sql, final Object... args) {
        return queryManyCall(new SingleValueIterator<>(type), sql, args);
    }

    public <T> Stream<T> queryManyCall(final Group<T> groupFunction, final String sql, final Object... args) {
        return groupFunction.createObjects(queryManyCall(new TypedMapIdentityIterator(), sql, args));
    }

    public <T> Stream<T> queryManyCall(final ResultSetIterator<T> iterator, final String sql, final Object... args) {
        try (Connection connection = dataSource.getConnection()) {
            try (CallableStatement statement = connection.prepareCall(sql)) {
                PrepareArgumentsCommand.apply(statement).exec(args);
                try (ResultSet resultSet = statement.executeQuery()) {
                    iterator.init(resultSet);
                    final List<T> result = new ArrayList<>();
                    while (iterator.hasNext())
                        result.add(iterator.next());
                    return result.stream();
                }
            }
        }
        catch (final SQLException e) {
            throw new HabaneroException(e);
        }
    }

    public <T> Optional<T> queryOne(final Class<T> type, final String sql, final Object... args) {
        return queryOne(new SingleValueIterator<>(type), sql, args);
    }

    public <T> Optional<T> queryOne(final Group<T> groupFunction, final String sql, final Object... args) {
        return queryMany(groupFunction, sql, args).findFirst();
    }

    public <T> Optional<T> queryOne(final ResultSetIterator<T> resultSetiterator, final String sql,
            final Object... args) {
        return queryMany(resultSetiterator, sql, args).peek(System.out::println).findFirst();
    }

    public <T> Optional<T> queryOneCall(final Class<T> type, final String sql, final Object... args) {
        return queryOneCall(new SingleValueIterator<>(type), sql, args);
    }

    public <T> Optional<T> queryOneCall(final Group<T> groupFunction, final String sql, final Object... args) {
        return queryManyCall(groupFunction, sql, args).findFirst();
    }

    public <T> Optional<T> queryOneCall(final ResultSetIterator<T> resultSetiterator, final String sql,
            final Object... args) {
        return queryManyCall(resultSetiterator, sql, args).findFirst();
    }

    public int update(final String sql, final Object... args) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                PrepareArgumentsCommand.apply(statement).exec(args);
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
                PrepareArgumentsCommand.apply(statement).exec(args);
                return statement.executeUpdate();
            }
        }
        catch (final SQLException e) {
            throw new HabaneroException(e);
        }
    }

}
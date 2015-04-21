package se.ugli.habanero.j;

import static se.ugli.habanero.j.internal.CloseUtil.close;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import se.ugli.commons.Option;
import se.ugli.habanero.j.datasource.H2DataSource;
import se.ugli.habanero.j.internal.PrepareArgumentsCommand;
import se.ugli.habanero.j.internal.ResourceUtil;
import se.ugli.habanero.j.internal.SingleValueIterator;
import se.ugli.habanero.j.internal.TypedMapIdentityIterator;
import se.ugli.habanero.j.typeadaptors.EnumTypeAdaptor;
import se.ugli.habanero.j.typeadaptors.JdbcTypesAdaptor;
import se.ugli.habanero.j.typeadaptors.JodaTimeAdaptor;
import se.ugli.habanero.j.typeadaptors.SerializableTypeAdaptor;

public final class Habanero {

	static {
		register(new SerializableTypeAdaptor());
		register(new JdbcTypesAdaptor());
		register(new EnumTypeAdaptor());
		if (ResourceUtil.exists("/org/joda/time/DateTime.class"))
			register(new JodaTimeAdaptor());
	}

	public static Habanero apply() {
		return new Habanero(new H2DataSource());
	}

	public static Habanero apply(final DataSource dataSource) {
		return new Habanero(dataSource);
	}

	public static TypeAdaptor getTypeAdaptor(final Class<?> type) {
		return TypeRegister.get(type);
	}

	public static void register(final TypeAdaptor typeAdaptor) {
		TypeRegister.add(typeAdaptor);
	}

	public final DataSource dataSource;

	private Habanero(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public boolean execute(final String sql) {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(sql);
			return statement.execute();
		} catch (final SQLException e) {
			throw new HabaneroException(e);
		} finally {
			close(statement, connection);
		}
	}

	public boolean executeCall(final String sql) {
		Connection connection = null;
		CallableStatement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareCall(sql);
			return statement.execute();
		} catch (final SQLException e) {
			throw new HabaneroException(e);
		} finally {
			close(statement, connection);
		}
	}

	public <T> Iterable<T> queryMany(final Class<T> type, final String sql, final Object... args) {
		return queryMany(new SingleValueIterator<T>(type), sql, args);
	}

	public <T> Iterable<T> queryMany(final GroupFunction<T> groupFunction, final String sql, final Object... args) {
		final Iterable<TypedMap> tuples = queryMany(new TypedMapIdentityIterator(), sql, args);
		return groupFunction.createObjects(tuples);
	}

	public <T> Iterable<T> queryMany(final ResultSetIterator<T> iterator, final String sql, final Object... args) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(sql);
			PrepareArgumentsCommand.apply(statement).exec(args);
			resultSet = statement.executeQuery();
			iterator.init(resultSet);
			final List<T> result = new ArrayList<T>();
			while (iterator.hasNext())
				result.add(iterator.next());
			return Collections.unmodifiableCollection(result);
		} catch (final SQLException e) {
			throw new HabaneroException(e);
		} finally {
			close(resultSet, statement, connection);
		}
	}

	public <T> Iterable<T> queryManyCall(final Class<T> type, final String sql, final Object... args) {
		return queryManyCall(new SingleValueIterator<T>(type), sql, args);
	}

	public <T> Iterable<T> queryManyCall(final GroupFunction<T> groupFunction, final String sql, final Object... args) {
		final Iterable<TypedMap> tuples = queryManyCall(new TypedMapIdentityIterator(), sql, args);
		return groupFunction.createObjects(tuples);
	}

	public <T> Iterable<T> queryManyCall(final ResultSetIterator<T> iterator, final String sql, final Object... args) {
		Connection connection = null;
		CallableStatement statement = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareCall(sql);
			PrepareArgumentsCommand.apply(statement).exec(args);
			resultSet = statement.executeQuery();
			iterator.init(resultSet);
			final List<T> result = new ArrayList<T>();
			while (iterator.hasNext())
				result.add(iterator.next());
			return Collections.unmodifiableCollection(result);
		} catch (final SQLException e) {
			throw new HabaneroException(e);
		} finally {
			close(resultSet, statement, connection);
		}
	}

	public <T> Option<T> queryOne(final Class<T> type, final String sql, final Object... args) {
		return queryOne(new SingleValueIterator<T>(type), sql, args);
	}

	public <T> Option<T> queryOne(final GroupFunction<T> groupFunction, final String sql, final Object... args) {
		final Iterable<T> result = queryMany(groupFunction, sql, args);
		final Iterator<T> objectIterator = result.iterator();
		if (objectIterator.hasNext())
			return Option.apply(objectIterator.next());
		return Option.none();
	}

	public <T> Option<T> queryOne(final ResultSetIterator<T> resultSetiterator, final String sql, final Object... args) {
		final Iterable<T> result = queryMany(resultSetiterator, sql, args);
		final Iterator<T> objectIterator = result.iterator();
		if (objectIterator.hasNext())
			return Option.apply(objectIterator.next());
		return Option.none();
	}

	public <T> Option<T> queryOneCall(final Class<T> type, final String sql, final Object... args) {
		return queryOneCall(new SingleValueIterator<T>(type), sql, args);
	}

	public <T> Option<T> queryOneCall(final GroupFunction<T> groupFunction, final String sql, final Object... args) {
		final Iterable<T> result = queryManyCall(groupFunction, sql, args);
		final Iterator<T> objectIterator = result.iterator();
		if (objectIterator.hasNext())
			return Option.apply(objectIterator.next());
		return Option.none();
	}

	public <T> Option<T> queryOneCall(final ResultSetIterator<T> resultSetiterator, final String sql,
			final Object... args) {
		final Iterable<T> result = queryManyCall(resultSetiterator, sql, args);
		final Iterator<T> objectIterator = result.iterator();
		if (objectIterator.hasNext())
			return Option.apply(objectIterator.next());
		return Option.none();
	}

	public int update(final String sql, final Object... args) {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(sql);
			PrepareArgumentsCommand.apply(statement).exec(args);
			return statement.executeUpdate();
		} catch (final SQLException e) {
			throw new HabaneroException(e);
		} finally {
			close(statement, connection);
		}
	}

	public int updateCall(final String sql, final Object... args) {
		Connection connection = null;
		CallableStatement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareCall(sql);
			PrepareArgumentsCommand.apply(statement).exec(args);
			return statement.executeUpdate();
		} catch (final SQLException e) {
			throw new HabaneroException(e);
		} finally {
			close(statement, connection);
		}
	}

}
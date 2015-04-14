package se.ugli.habanero.j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import se.ugli.habanero.j.util.Option;

public abstract class GroupFunction<E> {

	public static class GroupContext {

		private final Iterable<TypedMap> tuples;

		private GroupContext(final Iterable<TypedMap> tuples) {
			this.tuples = tuples;
		}
	}

	private final Iterable<String> columns;

	public GroupFunction(final String column1, final String... columns) {
		this.columns = createColumnIterable(column1, columns);
	}

	public final <F> Iterable<F> group(final GroupContext cxt, final GroupFunction<F> group) {
		return group.createObjects(cxt.tuples);
	}

	final Iterable<E> createObjects(final Iterable<TypedMap> tuples) {
		final List<E> result = new ArrayList<E>();
		final Map<TypedMap, List<TypedMap>> index = createIndex(tuples);
		for (final TypedMap groupedTypedMap : index.keySet())
			addCreatedObject(result, groupedTypedMap, new GroupContext(index.get(groupedTypedMap)));
		return result;
	}

	protected abstract Option<E> createObject(GroupContext cxt, TypedMap typedMap);

	private void addCreatedObject(final List<E> result, final TypedMap groupedTypedMap, final GroupContext cxt) {
		final Option<E> opt = createObject(cxt, groupedTypedMap);
		if (opt.isDefined())
			result.add(opt.get());
	}

	private void addTuple(final Map<TypedMap, List<TypedMap>> result, final TypedMap tuple) {
		final TypedHashMap groupedTypeMap = createGroupedTypedMap(tuple);
		if (!result.containsKey(groupedTypeMap))
			result.put(groupedTypeMap, new ArrayList<TypedMap>());
		result.get(groupedTypeMap).add(tuple);
	}

	private Iterable<String> createColumnIterable(final String column1, final String... columns) {
		final List<String> result = new ArrayList<String>();
		result.add(column1);
		for (final String column : columns)
			result.add(column);
		return result;
	}

	private TypedHashMap createGroupedTypedMap(final TypedMap tuple) {
		final TypedHashMap result = new TypedHashMap();
		for (final String column : columns)
			result.map.put(column, tuple.get(column));
		return result;
	}

	private final Map<TypedMap, List<TypedMap>> createIndex(final Iterable<TypedMap> tuples) {
		final Map<TypedMap, List<TypedMap>> result = new LinkedHashMap<TypedMap, List<TypedMap>>();
		for (final TypedMap tuple : tuples)
			addTuple(result, tuple);
		return result;
	}

}
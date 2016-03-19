package se.ugli.habanero.j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        final Map<String, List<TypedMap>> index = createIndex(tuples);
        for (final String groupedKey : index.keySet()) {
            final List<TypedMap> groupedTuples = index.get(groupedKey);
            final TypedMap firstTuple = groupedTuples.get(0);
            addCreatedObject(result, firstTuple, new GroupContext(groupedTuples));
        }
        return result;
    }

    protected abstract Optional<E> createObject(GroupContext cxt, TypedMap typedMap);

    private void addCreatedObject(final List<E> result, final TypedMap typeMap, final GroupContext cxt) {
        final Optional<E> opt = createObject(cxt, typeMap);
        if (opt.isPresent())
            result.add(opt.get());
    }

    private void addTuple(final Map<String, List<TypedMap>> result, final TypedMap tuple) {
        final String groupedTypeMap = createGroupKey(tuple);
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

    private String createGroupKey(final TypedMap tuple) {
        final StringBuilder result = new StringBuilder();
        for (final String column : columns) {
            result.append(column);
            result.append(tuple.get(column));
        }
        return result.toString();
    }

    private final Map<String, List<TypedMap>> createIndex(final Iterable<TypedMap> tuples) {
        final Map<String, List<TypedMap>> result = new LinkedHashMap<String, List<TypedMap>>();
        for (final TypedMap tuple : tuples)
            addTuple(result, tuple);
        return result;
    }

}
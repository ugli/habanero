package se.ugli.habanero.j;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;

public class Group<E> {

    private final GroupFunction<E> func;
    private final List<String> columns = new ArrayList<>();

    private Group(final GroupFunction<E> func, final String column1, final String... columns) {
        this.func = func;
        this.columns.add(column1);
        this.columns.addAll(asList(columns));
    }

    public static class CxtGroupBuilder<E> {

        private final Stream<ResultTuple> tuples;
        private final String column1;
        private final String[] columns;

        CxtGroupBuilder(final Stream<ResultTuple> tuples, final String column1, final String... columns) {
            this.tuples = tuples;
            this.column1 = column1;
            this.columns = columns;
        }

        public Stream<E> with(final GroupFunction<E> groupFunction) {
            return new Group<>(groupFunction, column1, columns).createObjects(tuples);
        }

    }

    public static class GroupBuilder<E> {

        private final String column1;
        private final String[] columns;

        private GroupBuilder(final String column1, final String... columns) {
            this.column1 = column1;
            this.columns = columns;
        }

        public Group<E> with(final GroupFunction<E> func) {
            return new Group<>(func, column1, columns);
        }

    }

    @SuppressWarnings("unused")
    public static <E> GroupBuilder<E> by(final Class<E> clazz, final String column1, final String... columns) {
        return new GroupBuilder<>(column1, columns);
    }

    Stream<E> createObjects(final Stream<ResultTuple> tuples) {
        return tuples.collect(groupingBy(this::groupKey, LinkedHashMap::new, toList())).entrySet().stream()
                .map(Entry::getValue).map(this::createObject).filter(Optional::isPresent).map(Optional::get);
    }

    private Optional<E> createObject(final List<ResultTuple> groupedTuples) {
        return func.apply(new GroupContext(groupedTuples.stream()), groupedTuples.get(0));
    }

    private String groupKey(final ResultTuple tuple) {
        return columns.stream().map(c -> c + tuple.get(c)).collect(joining());
    }

}
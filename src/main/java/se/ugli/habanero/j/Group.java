package se.ugli.habanero.j;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Group<E> {

    private final GroupFunction<E> func;
    private final List<String> columns = new ArrayList<>();

    private Group(final GroupFunction<E> func, final String column1, final String... columns) {
        this.func = func;
        this.columns.add(column1);
        this.columns.addAll(asList(columns));
    }

    @FunctionalInterface
    public static interface GroupFunction<R> extends BiFunction<GroupContext, TypedMap, Optional<R>> {
    }

    public static class GroupContext {

        private final List<TypedMap> tuples;

        private GroupContext(final List<TypedMap> tuples) {
            this.tuples = tuples;
        }

        public <E> CxtGroupBuilder<E> groupBy(final Class<E> clazz, final String column1, final String... columns) {
            return new CxtGroupBuilder<>(this, column1, columns);
        }

    }

    public static class CxtGroupBuilder<E> {

        private final GroupContext cxt;
        private final String column1;
        private final String[] columns;

        private CxtGroupBuilder(final GroupContext cxt, final String column1, final String... columns) {
            this.column1 = column1;
            this.columns = columns;
            this.cxt = cxt;
        }

        public Stream<E> with(final Supplier<Optional<E>> tupleSupplier) {
            final Group<E> grouping = new Group<>((c, t) -> tupleSupplier.get(), column1, columns);
            return grouping.createObjects(cxt.tuples.stream());
        }

    }

    public static class GroupBuilder<E> {

        private final String column1;
        private final String[] columns;

        GroupBuilder(final String column1, final String... columns) {
            this.column1 = column1;
            this.columns = columns;
        }

        public Group<E> with(final GroupFunction<E> func) {
            return new Group<>(func, column1, columns);
        }

    }

    public static <E> GroupBuilder<E> by(final Class<E> clazz, final String column1, final String... columns) {
        return new GroupBuilder<>(column1, columns);
    }

    final Stream<E> createObjects(final Stream<TypedMap> tuples) {
        return groupByKey(tuples).map(this::createObject).filter(Optional::isPresent).map(Optional::get);
    }

    private Optional<E> createObject(final List<TypedMap> groupedTuples) {
        return func.apply(new GroupContext(groupedTuples), groupedTuples.get(0));
    }

    private Stream<List<TypedMap>> groupByKey(final Stream<TypedMap> tuples) {
        return tuples.collect(groupingBy(this::createKey)).entrySet().stream().map(Entry::getValue);
    }

    private String createKey(final TypedMap tuple) {
        return columns.stream().map(c -> c + tuple.get(c)).collect(joining());
    }

}
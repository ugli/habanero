package se.ugli.habanero.j;

import java.util.stream.Stream;

import se.ugli.habanero.j.Group.CxtGroupBuilder;

public class GroupContext {

    private final Stream<ResultTuple> tuples;

    GroupContext(final Stream<ResultTuple> tuples) {
        this.tuples = tuples;
    }

    @SuppressWarnings("unused")
    public <E> CxtGroupBuilder<E> groupBy(final Class<E> clazz, final String column1, final String... columns) {
        return new Group.CxtGroupBuilder<>(tuples, column1, columns);
    }

}
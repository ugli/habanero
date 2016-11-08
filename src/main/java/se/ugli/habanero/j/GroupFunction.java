package se.ugli.habanero.j;

import java.util.Optional;
import java.util.function.BiFunction;

@FunctionalInterface
public interface GroupFunction<R> extends BiFunction<GroupContext, ResultTuple, Optional<R>> {
}
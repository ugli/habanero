package se.ugli.habanero.j;

import java.util.Optional;

import se.ugli.java.util.ImmutableSet;

public interface ResultTuple {

    Optional<Object> get(String name);

    <T> Optional<T> get(Class<T> type, String name);

    ImmutableSet<String> names();

}

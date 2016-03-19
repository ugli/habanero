package se.ugli.habanero.j;

import java.util.Optional;

public interface TypedMap {

    <T> T get(Class<T> type, String key);

    <T> T get(String key);

    <T> Optional<T> getOption(Class<T> type, String key);

    <T> Optional<T> getOption(String key);

    Iterable<String> keys();

}

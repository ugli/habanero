package se.ugli.habanero.j;

import se.ugli.habanero.j.util.Option;

public interface TypedMap {

	<T> T get(Class<T> type, String key);

	<T> T get(String key);

	<T> Option<T> getOption(Class<T> type, String key);

	<T> Option<T> getOption(String key);

	Iterable<String> keys();

}

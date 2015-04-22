package se.ugli.habanero.j;

import java.util.HashMap;
import java.util.Map;

import se.ugli.commons.Option;

class TypedHashMap implements TypedMap {

	final Map<String, Object> map = new HashMap<String, Object>();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(final Class<T> type, final String key) {
		if (type == null)
			throw new IllegalArgumentException();
		final TypeAdaptor typeAdaptor = Habanero.getTypeAdaptor(type);
		return (T) typeAdaptor.toTypeValue(type, get(key));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(final String key) {
		if (key == null)
			throw new IllegalArgumentException();
		return (T) map.get(key.toLowerCase());
	}

	@Override
	public <T> Option<T> getOption(final Class<T> type, final String key) {
		return Option.apply(get(type, key));
	}

	@Override
	public <T> Option<T> getOption(final String key) {
		return Option.apply(this.<T> get(key));
	}

	@Override
	public Iterable<String> keys() {
		return map.keySet();
	}
}

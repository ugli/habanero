package se.ugli.habanero.j;

import java.util.HashMap;
import java.util.Map;

import se.ugli.habanero.j.util.Option;

class TypedHashMap implements TypedMap {

	final Map<String, Object> map = new HashMap<String, Object>();

	@Override
	public Iterable<String> keys() {
		return map.keySet();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(final String key) {
		return (T) map.get(key);
	}

	@Override
	public <T> Option<T> getOption(final String key) {
		final T object = get(key);
		return Option.apply(object);
	}

	@Override
	public <T> Option<T> getOption(final Class<T> type, final String key) {
		return Option.apply(get(type, key));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(final Class<T> type, final String key) {
		if (type == null)
			throw new IllegalArgumentException();
		final Option<TypeAdaptor> typeAdaptorOpt = TypeRegister.get(type);
		if (typeAdaptorOpt.isDefined())
			return (T) typeAdaptorOpt.get().toTypeValue(type, get(key));
		throw new HabaneroException(type.getName() + " isn't registered.");
	}
}

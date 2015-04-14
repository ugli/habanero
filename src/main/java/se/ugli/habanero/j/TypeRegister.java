package se.ugli.habanero.j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class TypeRegister {

	private final static Map<Class<?>, TypeAdaptor> cache = new ConcurrentHashMap<Class<?>, TypeAdaptor>();

	private final static List<TypeAdaptor> typeAdaptors = Collections.synchronizedList(new ArrayList<TypeAdaptor>());

	static void add(final TypeAdaptor typeAdaptor) {
		typeAdaptors.add(0, typeAdaptor);
	}

	static TypeAdaptor get(final Class<?> type) {
		final TypeAdaptor cachedTypeAdaptor = cache.get(type);
		if (cachedTypeAdaptor != null)
			return cachedTypeAdaptor;
		for (final TypeAdaptor typeAdaptor : typeAdaptors)
			if (typeAdaptor.supports(type)) {
				cache.put(type, typeAdaptor);
				return typeAdaptor;
			}
		throw new HabaneroException(type.getName() + " isn't registered.");
	}

	private TypeRegister() {
	}

}

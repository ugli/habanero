package se.ugli.habanero.j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import se.ugli.habanero.j.internal.EnumTypeAdaptor;
import se.ugli.habanero.j.internal.JdbcTypesAdaptor;
import se.ugli.habanero.j.util.Option;

public final class TypeRegister {

	private TypeRegister() {

	}

	private final static List<TypeAdaptor> typeAdaptors = new ArrayList<TypeAdaptor>();
	private final static Map<Class<?>, TypeAdaptor> cache = new ConcurrentHashMap<Class<?>, TypeAdaptor>();

	public static void add(final TypeAdaptor typeAdaptor) {
		typeAdaptors.add(typeAdaptor);
	}

	public static Option<TypeAdaptor> get(final Class<?> type) {
		final TypeAdaptor cachedTypeAdaptor = cache.get(type);
		if (cachedTypeAdaptor != null)
			return Option.apply(cachedTypeAdaptor);
		for (final TypeAdaptor typeAdaptor : typeAdaptors)
			if (typeAdaptor.supports(type)) {
				cache.put(type, typeAdaptor);
				return Option.apply(typeAdaptor);
			}
		return Option.none();
	}

	static {
		add(new JdbcTypesAdaptor());
		add(new EnumTypeAdaptor());
	}

}

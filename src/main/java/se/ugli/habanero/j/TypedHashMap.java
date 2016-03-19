package se.ugli.habanero.j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class TypedHashMap implements TypedMap {

    private final Map<String, Object> map = new HashMap<String, Object>();

    @Override
    public boolean equals(final Object obj) {
        return map.equals(obj);
    }

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
    public <T> Optional<T> getOption(final Class<T> type, final String key) {
        return Optional.ofNullable(get(type, key));
    }

    @Override
    public <T> Optional<T> getOption(final String key) {
        return Optional.ofNullable(this.<T> get(key));
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public Iterable<String> keys() {
        return map.keySet();
    }

    @Override
    public String toString() {
        return map.toString();
    }

    Object put(final String key, final Object value) {
        return map.put(key, value);
    }

}

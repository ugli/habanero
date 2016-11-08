package se.ugli.habanero.j.typeadaptors;

import se.ugli.habanero.j.TypeAdaptor;
import se.ugli.java.util.Id;

public class IdTypeAdaptor implements TypeAdaptor {

    @Override
    public boolean supports(final Class<?> type) {
        return type == Id.class;
    }

    @Override
    public Object toJdbcValue(final Object object) {
        if (object != null)
            return ((Id) object).value;
        return null;
    }

    @Override
    public String toSqlStr(final Object object) {
        final Object jdbcValue = toJdbcValue(object);
        if (jdbcValue == null)
            return "null";
        return "'" + jdbcValue + "'";
    }

    @Override
    public Object toTypeValue(final Class<?> type, final Object object) {
        return Id.apply((String) object);
    }

}
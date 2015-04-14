package se.ugli.habanero.j;

public interface TypeAdaptor {

	boolean supports(Class<?> type);

	Object toJdbcValue(Object object);

	Object toTypeValue(Class<?> type, Object object);

}

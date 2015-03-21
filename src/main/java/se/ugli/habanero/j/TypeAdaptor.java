package se.ugli.habanero.j;

public interface TypeAdaptor {

	Object toTypeValue(Class<?> type, Object object);

	Object toJdbcValue(Object object);

	boolean supports(Class<?> type);

}

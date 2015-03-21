package se.ugli.habanero.j.internal;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import se.ugli.habanero.j.TypeAdaptor;

public class JdbcTypesAdaptor implements TypeAdaptor {

	private final static Set<Class<?>> classSet = new HashSet<Class<?>>();

	static {
		classSet.add(Blob.class);
		classSet.add(Boolean.class);
		classSet.add(Byte.class);
		classSet.add(Clob.class);
		classSet.add(Date.class);
		classSet.add(Double.class);
		classSet.add(Float.class);
		classSet.add(Integer.class);
		classSet.add(Long.class);
		classSet.add(Short.class);
		classSet.add(String.class);
		classSet.add(Time.class);
		classSet.add(Timestamp.class);
	}

	@Override
	public Object toTypeValue(final Class<?> type, final Object object) {
		return object;
	}

	@Override
	public Object toJdbcValue(final Object object) {
		return object;
	}

	@Override
	public boolean supports(final Class<?> type) {
		return classSet.contains(type);
	}

}

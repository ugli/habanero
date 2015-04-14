package se.ugli.habanero.j.typeadaptors;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import se.ugli.habanero.j.TypeAdaptor;

public class JdbcTypesAdaptor implements TypeAdaptor {

	private final static Set<Class<?>> CLASS_SET = new HashSet<Class<?>>();

	static {
		CLASS_SET.add(Blob.class);
		CLASS_SET.add(Boolean.class);
		CLASS_SET.add(Byte.class);
		CLASS_SET.add(Clob.class);
		CLASS_SET.add(Date.class);
		CLASS_SET.add(Double.class);
		CLASS_SET.add(Float.class);
		CLASS_SET.add(Integer.class);
		CLASS_SET.add(Long.class);
		CLASS_SET.add(Short.class);
		CLASS_SET.add(String.class);
		CLASS_SET.add(Time.class);
		CLASS_SET.add(Timestamp.class);
	}

	@Override
	public boolean supports(final Class<?> type) {
		return CLASS_SET.contains(type);
	}

	@Override
	public Object toJdbcValue(final Object object) {
		return object;
	}

	@Override
	public Object toTypeValue(final Class<?> type, final Object object) {
		return object;
	}

}

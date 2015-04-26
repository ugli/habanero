package se.ugli.habanero.j.typeadaptors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import se.ugli.commons.CloseCommand;
import se.ugli.habanero.j.HabaneroException;
import se.ugli.habanero.j.TypeAdaptor;
import se.ugli.habanero.j.internal.Base64Util;

public class SerializableTypeAdaptor implements TypeAdaptor {

	@Override
	public boolean supports(final Class<?> type) {
		return Serializable.class.isAssignableFrom(type);
	}

	@Override
	public Object toJdbcValue(final Object object) {
		if (object != null) {
			ByteArrayOutputStream baos = null;
			try {
				baos = new ByteArrayOutputStream();
				final ObjectOutputStream out = new ObjectOutputStream(baos);
				out.writeObject(object);
				return Base64Util.encode(baos.toByteArray());
			} catch (final IOException e) {
				throw new HabaneroException(e);
			} finally {
				CloseCommand.execute(baos);
			}
		}
		return null;
	}

	@Override
	public Object toTypeValue(final Class<?> type, final Object object) {
		if (object != null) {
			ObjectInputStream stream = null;
			try {
				final byte[] bytes = Base64Util.decode(object.toString());
				stream = new ObjectInputStream(new ByteArrayInputStream(bytes));
				return stream.readObject();
			} catch (final IOException e) {
				throw new HabaneroException(e);
			} catch (final ClassNotFoundException e) {
				throw new HabaneroException(e);
			} finally {
				CloseCommand.execute(stream);
			}
		}
		return null;
	}

}

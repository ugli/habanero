package se.ugli.habanero.j.typeadaptors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

import se.ugli.habanero.j.HabaneroException;
import se.ugli.habanero.j.TypeAdaptor;

public class SerializableTypeAdaptor implements TypeAdaptor {

    @Override
    public boolean supports(final Class<?> type) {
        return Serializable.class.isAssignableFrom(type);
    }

    @Override
    public Object toJdbcValue(final Object object) {
        if (object != null)
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                final ObjectOutputStream out = new ObjectOutputStream(baos);
                out.writeObject(object);
                return new String(Base64.getEncoder().encode(baos.toByteArray()));
            }
            catch (final IOException e) {
                throw new HabaneroException(e);
            }
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
        if (object != null) {
            final byte[] bytes = Base64.getDecoder().decode(object.toString());
            try (ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
                return stream.readObject();
            }
            catch (IOException | ClassNotFoundException e) {
                throw new HabaneroException(e);
            }
        }
        return null;
    }

}

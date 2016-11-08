package se.ugli.habanero.j.typeadaptors;

import java.sql.Blob;

import se.ugli.habanero.j.TypeAdaptor;
import se.ugli.habanero.j.internal.BlobReader;

public class BlobTypeAdaptor implements TypeAdaptor {

    @Override
    public boolean supports(final Class<?> type) {
        return type == Blob.class;
    }

    @Override
    public Object toJdbcValue(final Object object) {
        return object;
    }

    @Override
    public String toSqlStr(final Object object) {
        return new String(BlobReader.read((Blob) object));
    }

    @Override
    public Object toTypeValue(final Class<?> type, final Object object) {
        return BlobReader.read((Blob) object);
    }

}

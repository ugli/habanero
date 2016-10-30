package se.ugli.habanero.j.internal;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

import se.ugli.commons.InputStreams;
import se.ugli.habanero.j.HabaneroException;

public class BlobReader {

    public static byte[] read(final Blob blob) {
        try (InputStream inputStream = blob.getBinaryStream()) {
            return InputStreams.apply().copyToBytes(inputStream);
        }
        catch (final SQLException | IOException e) {
            throw new HabaneroException(e);
        }
    }

}

package se.ugli.habanero.j.internal;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Clob;
import java.sql.SQLException;

import se.ugli.habanero.j.HabaneroException;
import se.ugli.java.io.Readers;

public class ClobReader {

    private ClobReader() {
    }

    public static String read(final Clob clob) {
        try (Reader reader = clob.getCharacterStream()) {
            final Charset charset = HabaneroProperties.apply().getCharset();
            return Readers.apply().copyToString(reader, charset, charset);
        }
        catch (final SQLException | IOException e) {
            throw new HabaneroException(e);
        }
    }

}

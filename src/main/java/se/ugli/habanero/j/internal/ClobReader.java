package se.ugli.habanero.j.internal;

import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Clob;
import java.sql.SQLException;

import se.ugli.commons.CloseCommand;
import se.ugli.commons.CopyCommand;
import se.ugli.habanero.j.HabaneroException;

public class ClobReader {

    public static String read(final Clob clob) {
        Reader reader = null;
        try {
            reader = clob.getCharacterStream();
            final Charset charset = HabaneroProperties.apply().getCharset();
            return CopyCommand.apply().copyToString(reader, charset, charset);
        }
        catch (final SQLException e) {
            throw new HabaneroException(e);
        }
        finally {
            CloseCommand.execute(reader);
        }
    }

}

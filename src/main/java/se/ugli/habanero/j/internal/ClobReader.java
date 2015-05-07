package se.ugli.habanero.j.internal;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Clob;
import java.sql.SQLException;

import se.ugli.commons.CloseCommand;
import se.ugli.commons.CopyCommand;
import se.ugli.habanero.j.HabaneroException;

public class ClobReader {

	public static String read(final Clob clob) {
		InputStream inputStream = null;
		try {
			inputStream = clob.getAsciiStream();
			final Charset charset = HabaneroProperties.apply().getCharset();
			return CopyCommand.apply().copyToString(inputStream, charset);
		} catch (final SQLException e) {
			throw new HabaneroException(e);
		} finally {
			CloseCommand.execute(inputStream);
		}
	}

}

package se.ugli.habanero.j.internal;

import java.io.InputStream;
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
			return CopyCommand.apply().copyToString(inputStream);
		} catch (final SQLException e) {
			throw new HabaneroException(e);
		} finally {
			CloseCommand.execute(inputStream);
		}
	}

}

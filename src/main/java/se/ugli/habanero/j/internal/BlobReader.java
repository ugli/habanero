package se.ugli.habanero.j.internal;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

import se.ugli.commons.CloseCommand;
import se.ugli.commons.CopyCommand;
import se.ugli.habanero.j.HabaneroException;

public class BlobReader {

	public static byte[] read(final Blob blob) {
		InputStream inputStream = null;
		try {
			inputStream = blob.getBinaryStream();
			return CopyCommand.apply().copyToBytes(inputStream);
		} catch (final SQLException e) {
			throw new HabaneroException(e);
		} finally {
			CloseCommand.execute(inputStream);
		}
	}

}

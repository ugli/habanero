package se.ugli.habanero.j.batch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Scanner;

import javax.sql.DataSource;

import se.ugli.commons.Closeables;
import se.ugli.commons.Resource;
import se.ugli.habanero.j.Habanero;
import se.ugli.habanero.j.HabaneroException;

public class SqlScript {

    public static final String DEFAULT_STATEMENT_DELIMITER = ";";
    public static final char LINE_DELIMITER = '\n';

    public static SqlScript apply(final DataSource dataSource) {
        return new SqlScript(dataSource, DEFAULT_STATEMENT_DELIMITER);
    }

    public static SqlScript apply(final DataSource dataSource, final String statementDelimiter) {
        return new SqlScript(dataSource, statementDelimiter);
    }

    private final Habanero habanero;
    private final String statementDelimiter;

    private SqlScript(final DataSource dataSource, final String statementDelimiter) {
        this.habanero = Habanero.apply(dataSource);
        this.statementDelimiter = statementDelimiter;
    }

    public void run(final File source) {
        try {
            run(new Scanner(source));
        }
        catch (final FileNotFoundException e) {
            throw new HabaneroException(e);
        }
    }

    public void run(final File source, final String charsetName) {
        try {
            run(new Scanner(source, charsetName));
        }
        catch (final FileNotFoundException e) {
            throw new HabaneroException(e);
        }
    }

    public void run(final InputStream source) {
        run(new Scanner(source));
    }

    public void run(final InputStream source, final String charsetName) {
        run(new Scanner(source, charsetName));
    }

    public void run(final Readable source) {
        run(new Scanner(source));
    }

    public void run(final Resource resource) {
        run(resource.getInputStream());
    }

    public void run(final String source) {
        run(new Scanner(source));
    }

    private void run(final Scanner scanner) {
        final Batch batch = habanero.batch();
        try {
            StringBuilder sqlBuilder = new StringBuilder();
            while (scanner.hasNext()) {
                if (sqlBuilder.length() > 0)
                    sqlBuilder.append(LINE_DELIMITER);
                sqlBuilder.append(scanner.nextLine());
                final String sql = sqlBuilder.toString();
                if (sql.trim().endsWith(statementDelimiter)) {
                    batch.add(sql);
                    sqlBuilder = new StringBuilder();
                }
            }
            batch.execute();
        }
        catch (final SQLException e) {
            throw new HabaneroException(e);
        }
        finally {
            batch.close();
            Closeables.close(scanner);
        }
    }

}

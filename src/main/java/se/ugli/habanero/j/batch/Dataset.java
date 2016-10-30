package se.ugli.habanero.j.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import se.ugli.commons.Resource;
import se.ugli.habanero.j.HabaneroException;
import se.ugli.habanero.j.batch.BatchItem.BatchItemBuilder;
import se.ugli.habanero.j.metadata.MetaData;
import se.ugli.habanero.j.metadata.SqlType;

public class Dataset {

    private class SaxHandler extends DefaultHandler {

        private final Batch batch;
        private boolean started = false;
        private final Map<String, Map<String, SqlType>> tableCache = new HashMap<>();
        private final DataSource dataSource;

        public SaxHandler(final DataSource dataSource) {
            this.dataSource = dataSource;
            batch = new Batch(dataSource);
        }

        @Override
        public void endDocument() {
            try {
                batch.execute();
            }
            catch (final SQLException e) {
                throw new HabaneroException(e);
            }
        }

        @Override
        public void startElement(final String uri, final String localName, final String qName,
                final Attributes attributes) {
            if (!started)
                started = true;
            else
                addToBatch(localName, attributes);
        }

        private void addToBatch(final String tableName, final Attributes attributes) {
            try {
                final BatchItemBuilder builder = new BatchItemBuilder();
                builder.appendSql("insert into ");
                builder.appendSql(tableName);
                builder.appendSql("(");
                final int numOfAttributes = attributes.getLength();
                for (int index = 0; index < numOfAttributes; index++) {
                    final String columnName = attributes.getLocalName(index);
                    builder.appendSql(columnName);
                    final String value = attributes.getValue(index);
                    builder.addArg(convertValue(tableName, columnName, value));
                    builder.appendSql(index < numOfAttributes - 1 ? "," : ")");
                }
                builder.appendSql(" values(");
                for (int index = 0; index < numOfAttributes; index++) {
                    builder.appendSql("?");
                    builder.appendSql(index < numOfAttributes - 1 ? "," : ")");
                }
                batch.add(builder.build());
            }
            catch (final SQLException e) {
                throw new HabaneroException(e);
            }
        }

        private Object convertValue(final String tableName, final String columnName, final String value) {
            final SqlType outParam = getType(tableName, columnName);
            if (outParam == SqlType.NUMERIC || outParam == SqlType.DECIMAL)
                return new BigInteger(value);
            else if (outParam == SqlType.BIT || outParam == SqlType.TINYINT || outParam == SqlType.SMALLINT
                    || outParam == SqlType.INTEGER || outParam == SqlType.BIGINT)
                return Long.parseLong(value);
            else if (outParam == SqlType.REAL || outParam == SqlType.FLOAT || outParam == SqlType.DOUBLE)
                return Double.parseDouble(value);
            else if (outParam == SqlType.BINARY || outParam == SqlType.VARBINARY || outParam == SqlType.LONGVARBINARY)
                return value.getBytes();
            return value;
        }

        private SqlType getType(final String tableName, final String columnName) {
            if (!tableCache.containsKey(tableName))
                tableCache.put(tableName, new HashMap<String, SqlType>());
            final Map<String, SqlType> columnCache = tableCache.get(tableName);
            if (!columnCache.containsKey(columnName)) {
                final Optional<SqlType> columnTypeOpt = MetaData.apply(dataSource).getColumnType(tableName, columnName);
                if (columnTypeOpt.isPresent())
                    columnCache.put(columnName, columnTypeOpt.get());
                else
                    throw new HabaneroException("Couldn't find type for " + tableName + "." + columnName);
            }
            return columnCache.get(columnName);
        }
    }

    public static Dataset apply(final DataSource dataSource) {
        return new Dataset(dataSource);
    }

    private final DataSource dataSource;

    private Dataset(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void exec(final File file) {
        try (InputStream resourceStream = new FileInputStream(file)) {
            exec(resourceStream);
        }
        catch (final IOException e) {
            throw new HabaneroException(e);
        }
    }

    public void exec(final Resource resource) {
        try (InputStream resourceStream = resource.asInputStream()) {
            exec(resourceStream);
        }
        catch (final IOException e) {
            throw new HabaneroException(e);
        }
    }

    public void exec(final InputStream inputStream) {
        final SaxHandler saxHandler = new SaxHandler(dataSource);
        try {
            createSaxParser().parse(inputStream, saxHandler);
        }
        catch (SAXException | IOException e) {
            throw new HabaneroException(e);
        }
        finally {
            saxHandler.batch.close();
        }
    }

    private static SAXParser createSaxParser() {
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            return factory.newSAXParser();
        }
        catch (ParserConfigurationException | SAXException e) {
            throw new HabaneroException(e);
        }
    }

}

package se.ugli.habanero.j.dataset;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import se.ugli.commons.CloseCommand;
import se.ugli.commons.Resource;
import se.ugli.habanero.j.HabaneroException;

public class DataSet {

	private class SaxHandler extends DefaultHandler {

		private final Connection connection;
		private boolean started = false;
		private Statement statement;

		public SaxHandler(final Connection connection) {
			this.connection = connection;
		}

		@Override
		public void endDocument() {
			try {
				statement.executeBatch();
			} catch (final SQLException e) {
				throw new HabaneroException(e);
			} finally {
				CloseCommand.execute(statement);
			}
		}

		@Override
		public void startDocument() {
			try {
				statement = connection.createStatement();
			} catch (final SQLException e) {
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
				String columns = "(";
				String values = "values(";
				final int numOfAttributes = attributes.getLength();
				for (int index = 0; index < numOfAttributes; index++) {
					final String column = attributes.getLocalName(index);
					final String value = attributes.getValue(index);
					columns += column;
					values += "'" + value + "'";
					if (index < numOfAttributes - 1) {
						columns += ",";
						values += ",";
					} else {
						columns += ")";
						values += ")";
					}
				}
				statement.addBatch("insert into " + tableName + columns + " " + values);
			} catch (final SQLException e) {
				throw new HabaneroException(e);
			}
		}

	}

	public static DataSet apply(final DataSource dataSource) {
		return new DataSet(dataSource);
	}

	private final DataSource dataSource;

	private DataSet(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void exec(final Resource resource) {
		Connection connection = null;
		InputStream resourceStream = null;
		try {
			connection = dataSource.getConnection();
			resourceStream = resource.getInputStream();
			createSaxParser().parse(resourceStream, new SaxHandler(connection));
		} catch (final SAXException e) {
			throw new HabaneroException(e);
		} catch (final IOException e) {
			throw new HabaneroException(e);
		} catch (final SQLException e) {
			throw new HabaneroException(e);
		} finally {
			CloseCommand.execute(connection, resourceStream);
		}
	}

	private SAXParser createSaxParser() {
		try {
			final SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			return factory.newSAXParser();
		} catch (final ParserConfigurationException e) {
			throw new HabaneroException(e);
		} catch (final SAXException e) {
			throw new HabaneroException(e);
		}
	}

}

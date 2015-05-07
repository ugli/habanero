package se.ugli.habanero.j.internal;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

import se.ugli.commons.Resource;

public final class HabaneroProperties {

	// TODO singleton

	public final static String RESOURCE = "/habanero.properties";
	private final Properties properties;

	public static Properties getProperties() {
		final Properties properties = new Properties();
		try {
			properties.load(Resource.apply(RESOURCE).getInputStream());
		} catch (final IOException e) {
			System.err.println(e.getMessage());
		} catch (final RuntimeException e) {
			System.err.println(e.getMessage());
		}
		return properties;
	}

	public static HabaneroProperties apply() {
		return new HabaneroProperties(getProperties());
	}

	private HabaneroProperties(final Properties properties) {
		this.properties = properties;
	}

	public Charset getCharset() {
		final String charsetName = properties.getProperty("charset", "UTF8");
		return Charset.forName(charsetName);
	}

}

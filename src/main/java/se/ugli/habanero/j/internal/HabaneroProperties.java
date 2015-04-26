package se.ugli.habanero.j.internal;

import java.io.IOException;
import java.util.Properties;

import se.ugli.commons.Resource;
import se.ugli.habanero.j.HabaneroException;

public final class HabaneroProperties {

	public final static String RESOURCE = "/habanero.properties";

	public static Properties get() {
		try {
			final Properties properties = new Properties();
			properties.load(Resource.apply(RESOURCE).getInputStream());
			return properties;
		} catch (final IOException e) {
			throw new HabaneroException(e);
		}
	}

	private HabaneroProperties() {
	}

}

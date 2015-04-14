package se.ugli.habanero.j.internal;

import java.io.IOException;
import java.util.Properties;

import se.ugli.habanero.j.HabaneroException;

public final class HabaneroProperties {

	public final static String RESOURCE = "/habanero.properties";

	public static Properties get() {
		try {
			if (ResourceUtil.exists(RESOURCE)) {
				final Properties properties = new Properties();
				properties.load(HabaneroProperties.class.getResourceAsStream(RESOURCE));
				return properties;
			}
			throw new HabaneroException(RESOURCE + " not found");
		} catch (final IOException e) {
			throw new HabaneroException(e);
		}
	}

	private HabaneroProperties() {
	}

}

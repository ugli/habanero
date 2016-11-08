package se.ugli.habanero.j.internal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import se.ugli.java.io.Resource;

public final class HabaneroProperties {

    public final static String RESOURCE = "/habanero.properties";
    private static Properties _cache;

    public static HabaneroProperties apply() {
        return apply(RESOURCE, true);
    }

    private static Properties readProperties(final String resourcePath) {
        final Properties properties = new Properties();
        final Resource resource = Resource.apply(resourcePath);
        if (resource.exists())
            try (InputStream inputStream = resource.asInputStream()) {
                properties.load(inputStream);
            }
            catch (final IOException | RuntimeException e) {
                System.err.println(e.getMessage());
            }
        return properties;
    }

    static HabaneroProperties apply(final String resourcePath, final boolean useCache) {
        if (useCache) {
            if (_cache == null)
                _cache = readProperties(resourcePath);
            return new HabaneroProperties(_cache);
        }
        return new HabaneroProperties(readProperties(resourcePath));
    }

    private final Properties properties;

    private HabaneroProperties(final Properties properties) {
        this.properties = properties;
    }

    public Charset getCharset() {
        final String charsetName = properties.getProperty("charset", "UTF8");
        return Charset.forName(charsetName);
    }

    public Properties getProperties() {
        return properties;
    }

}

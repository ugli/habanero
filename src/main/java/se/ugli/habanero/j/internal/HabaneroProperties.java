package se.ugli.habanero.j.internal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import se.ugli.commons.Closeables;
import se.ugli.commons.Resource;

public final class HabaneroProperties {

    public final static String RESOURCE = "/habanero.properties";
    private static Properties _cache;

    public static HabaneroProperties apply() {
        return apply(RESOURCE, true);
    }

    private static Properties readProperties(final String resourcePath) {
        final Properties properties = new Properties();
        final Resource resource = Resource.apply(resourcePath);
        InputStream inputStream = null;
        if (resource.exists())
            try {
                inputStream = resource.getInputStream();
                properties.load(inputStream);
            }
            catch (final IOException e) {
                System.err.println(e.getMessage());
            }
            catch (final RuntimeException e) {
                System.err.println(e.getMessage());
            }
            finally {
                Closeables.close(inputStream);
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

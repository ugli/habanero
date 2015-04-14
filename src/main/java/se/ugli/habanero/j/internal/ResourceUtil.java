package se.ugli.habanero.j.internal;

public final class ResourceUtil {

	public static boolean exists(final String className) {
		return ResourceUtil.class.getResource(className) != null;
	}

	private ResourceUtil() {
	}

}

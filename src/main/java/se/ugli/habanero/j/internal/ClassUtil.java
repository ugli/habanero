package se.ugli.habanero.j.internal;

import se.ugli.habanero.j.Habanero;

public final class ClassUtil {

	private ClassUtil() {

	}

	public static boolean isTypePresent(final String className) {
		return Habanero.class.getResource(className) != null;
	}

}

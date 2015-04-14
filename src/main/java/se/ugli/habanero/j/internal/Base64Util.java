package se.ugli.habanero.j.internal;

public final class Base64Util {

	private static final byte base64ToInt[] = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1,
			-1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8,
			9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29,
			30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51 };

	private static final char intToBase64[] = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
			'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9', '+', '/' };

	public static byte[] decode(final String s) {
		final int sLen = s.length();
		final int numGroups = sLen / 4;
		if (4 * numGroups != sLen)
			throw new IllegalArgumentException("String length must be a multiple of four.");
		int missingBytesInLastGroup = 0;
		int numFullGroups = numGroups;
		if (sLen != 0) {
			if (s.charAt(sLen - 1) == '=') {
				missingBytesInLastGroup++;
				numFullGroups--;
			}
			if (s.charAt(sLen - 2) == '=')
				missingBytesInLastGroup++;
		}
		final byte[] result = new byte[3 * numGroups - missingBytesInLastGroup];
		int inCursor = 0, outCursor = 0;
		for (int i = 0; i < numFullGroups; i++) {
			final int ch0 = base64toInt(s.charAt(inCursor++), base64ToInt);
			final int ch1 = base64toInt(s.charAt(inCursor++), base64ToInt);
			final int ch2 = base64toInt(s.charAt(inCursor++), base64ToInt);
			final int ch3 = base64toInt(s.charAt(inCursor++), base64ToInt);
			result[outCursor++] = (byte) (ch0 << 2 | ch1 >> 4);
			result[outCursor++] = (byte) (ch1 << 4 | ch2 >> 2);
			result[outCursor++] = (byte) (ch2 << 6 | ch3);
		}
		if (missingBytesInLastGroup != 0) {
			final int ch0 = base64toInt(s.charAt(inCursor++), base64ToInt);
			final int ch1 = base64toInt(s.charAt(inCursor++), base64ToInt);
			result[outCursor++] = (byte) (ch0 << 2 | ch1 >> 4);
			if (missingBytesInLastGroup == 1) {
				final int ch2 = base64toInt(s.charAt(inCursor++), base64ToInt);
				result[outCursor++] = (byte) (ch1 << 4 | ch2 >> 2);
			}
		}
		return result;
	}

	public static String encode(final byte[] a) {
		final int aLen = a.length;
		final int numFullGroups = aLen / 3;
		final int numBytesInPartialGroup = aLen - 3 * numFullGroups;
		final int resultLen = 4 * ((aLen + 2) / 3);
		final StringBuffer result = new StringBuffer(resultLen);
		int inCursor = 0;
		for (int i = 0; i < numFullGroups; i++) {
			final int byte0 = a[inCursor++] & 0xff;
			final int byte1 = a[inCursor++] & 0xff;
			final int byte2 = a[inCursor++] & 0xff;
			result.append(intToBase64[byte0 >> 2]);
			result.append(intToBase64[byte0 << 4 & 0x3f | byte1 >> 4]);
			result.append(intToBase64[byte1 << 2 & 0x3f | byte2 >> 6]);
			result.append(intToBase64[byte2 & 0x3f]);
		}
		if (numBytesInPartialGroup != 0) {
			final int byte0 = a[inCursor++] & 0xff;
			result.append(intToBase64[byte0 >> 2]);
			if (numBytesInPartialGroup == 1) {
				result.append(intToBase64[byte0 << 4 & 0x3f]);
				result.append("==");
			} else {
				final int byte1 = a[inCursor++] & 0xff;
				result.append(intToBase64[byte0 << 4 & 0x3f | byte1 >> 4]);
				result.append(intToBase64[byte1 << 2 & 0x3f]);
				result.append('=');
			}
		}
		return result.toString();
	}

	private static int base64toInt(final char c, final byte[] alphaToInt) {
		final int result = alphaToInt[c];
		if (result < 0)
			throw new IllegalArgumentException("Illegal character " + c);
		return result;
	}

	private Base64Util() {
	}

}

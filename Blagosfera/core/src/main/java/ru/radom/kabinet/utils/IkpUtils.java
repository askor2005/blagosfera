package ru.radom.kabinet.utils;

public class IkpUtils {

	public static String longToIkpHash(long in) {
		String tmp = "" + in;
		if (in < 0) {
			tmp = tmp.substring(1);
		}
		while (tmp.length() < 19) {
			tmp = "0" + tmp;
		}

		if (in < 0) {
			tmp = "1" + tmp;
			return tmp;
		}
		return "2" + tmp;
	}

	public static long ikpHashToLong(String in) {
		if (in == null || in.length() != 20) {
			return 0;
		}
		String out = in.substring(1);
		if (in.charAt(0) == '1') {
			out = "-" + out;
		}
		long ret = 0;

		try {
			ret = Long.parseLong(out);
		} catch (Exception e) {
			System.err.println("[IKPHashToBigint] " + e.getLocalizedMessage());
		}
		return ret;
	}

	public static void main(String[] args) {
		System.out.println(longToIkpHash(6505101398573992632L)); // 26505101398573992632
	}

}

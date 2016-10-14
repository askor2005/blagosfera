package ru.radom.kabinet.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Random;
import java.util.regex.Pattern;

public class StringUtils {

	private static final Logger logger = LoggerFactory.getLogger(StringUtils.class);

	private final static char[] RANDOM_CHARS = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'V', 'U', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#0.00");

	private static final String[] TRANSLIT_RUS_ALPHABET = { "а", "б", "в", "г", "д", "е", "ё", "ж", "з", "и", "й", "к", "л", "м", "н", "о", "п", "р", "с", "т", "у", "ф", "х", "ц", "ч", "ш", "щ", "ъ", "ы", "ь", "э", "ю", "я" };
	private static final String[] TRANSLIT_ENG_ALPHABET = { "a", "b", "v", "g", "d", "e", "e", "zh", "z", "i", "i", "k", "l", "m", "n", "o", "p", "r", "s", "t", "u", "f", "h", "c", "ch", "sh", "sh", "", "y", "", "e", "yu", "ya" };

	static {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		MONEY_FORMAT.setDecimalFormatSymbols(symbols);
		MONEY_FORMAT.setGroupingUsed(false);
	}

	public static String randomString(final int count) {
		String random = "";
		for (int i = 0; i < count; i++) {
			random += RANDOM_CHARS[RandomUtils.getInteger(RANDOM_CHARS.length - 1)];
		}
		return random;
	}

    public static String randomNumericString() {
        return String.valueOf((int) (100000 + new Random().nextFloat() * 900000));
    }

	public static long toLong(String string, long defaultValue) {
		try {
			return Long.parseLong(string);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static boolean isEmpty(String string) {
		return ((string == null) || (string.length() == 0));
	}

	public static boolean hasLength(String string) {
		return ((string != null) && (string.length() > 0));
	}

	// public static String isoToUtf(String string) {
	// try {
	// return new String((string.getBytes("ISO-8859-1")), "UTF-8");
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// return null;
	// }
	// }

	public static String toCp1251(String string) {
		try {
			return new String(string.getBytes("UTF-8"), "cp1251");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public static String toIso(String string) {
		try {
			return new String(string.getBytes("UTF-8"), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public static String clearUrl(String url) {
		return url.toLowerCase().replaceAll(" ", "_").replaceAll("\\.", "").replaceAll("\\\\", "_").replaceAll(",", "").replaceAll("-", "_").replaceAll("__", "_").replaceAll("__", "_").replaceAll("«", "").replaceAll("»", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\"", "").replaceAll("“", "").replaceAll("\\?", "").replaceAll("'", "").replaceAll(":", "").replaceAll("&", "").replaceAll("%", "").replaceAll("<", "").replaceAll(">", "").replaceAll(";", "").replaceAll("!", "").replaceAll("\\(", "").replaceAll("\\)", "");
	}

	public static String getDeclension(long number, String string1, String string24, String string50) {
		long digit = (number < 0 ? (number - number * 2) : number) % 100;
		digit = digit > 20 ? digit % 10 : digit;
		return (digit == 1 ? string1 : (digit > 4 || digit < 1 ? string50 : string24));
	}

	/**
	 * Проверка email на корректность
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		return Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$").matcher(email).matches();
		//return Pattern.compile(".+@.+\\..+").matcher(email).matches();
	}

	public static String HtmlToPlain(String text) {
		return text.replaceAll("<p>", "").replaceAll("</p>", "").replaceAll("<br>", "").replaceAll("<br/>", "");
	}

	public static String formatMoney(BigDecimal money) {
		if (money == null) {
			money = BigDecimal.ZERO;
		}
		money = money.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		return MONEY_FORMAT.format(money);
	}

	public static BigDecimal parseMoney(String money, BigDecimal defaultValue) {
		try {
			return new BigDecimal(MONEY_FORMAT.parse(money).doubleValue()).setScale(2, BigDecimal.ROUND_HALF_EVEN);
		} catch (Exception e) {
			// ignore
			return defaultValue;
		}
	}

	public static String toBase64(String string) {
		try {
			return Base64.encodeBase64String(string.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public static String formatBytes(final long length) {
		if (length < 1024L) {
			return Long.toString(length) + " b";
		} else if (length < 1048576L) {
			return formatDouble(length / 1024D) + " kb";
		} else if (length < 1073741824L) {
			return formatDouble(length / 1048576D) + " mb";
		} else if (length < 1099511627776L) {
			return formatDouble(length / 1073741824D) + " gb";
		} else {
			return formatDouble(length / 1099511627776D) + " tb";
		}
	}

	private static String formatDouble(double d) {
		return MONEY_FORMAT.format(d);
	}

	public static String toTranslit(String string) {
		return org.apache.commons.lang3.StringUtils.replaceEach(string, TRANSLIT_RUS_ALPHABET, TRANSLIT_ENG_ALPHABET);
	}

	public static String getNextCopyName(String name) {
		if (name.matches("^.*?\\([0-9]{1,}\\)$")) {
			int number = Integer.parseInt(name.substring(name.lastIndexOf('(') + 1, name.lastIndexOf(')')));
			return name.substring(0, name.lastIndexOf('(') - 1) + " (" + (number + 1) + ")";
		} else {
			return name + " (1)";
		}
	}

    public static String nvlNumeric(final Object o){
        return (o == null) ? "0" : o.toString();
    }

	// Убрает лишние пробелы: в начале строки, в конце строки, подряд идущие пробелы
	public static String removeExtraSpaces(String str) {
		return str.trim().replaceAll("\\s+", " ");
	}

	// Убирает лишние пробелы и экранирует строку от html кода
	public static String prepareString(String string) {
		String result = string;
		result = removeExtraSpaces(result);
		result = StringEscapeUtils.escapeHtml4(result);
		return result;
	}

	// Убирает все кроме цифр и точки в полях для чисел. А также заменяет запятую на точку.
	public static String prepareNumber(String number) {
		String result = number;
		result = result.replace(",", ".");
		result = result.replaceAll("[^0-9\\.]", "");
		return result;
	}

	// Удалить все теги, которые не являются тегами html
	public static String removeUnsafeTags(String html) {
		return Jsoup.clean(html, Whitelist.basic());
	}

	// Разбивает строку на строки длинной interval
	public static String[] splitStringEvery(String s, int interval) {
		int arrayLength = (int) Math.ceil(((s.length() / (double)interval)));
		String[] result = new String[arrayLength];

		int j = 0;
		int lastIndex = result.length - 1;
		for (int i = 0; i < lastIndex; i++) {
			result[i] = s.substring(j, j + interval);
			j += interval;
		} //Add the last bit
		result[lastIndex] = s.substring(j);

		return result;
	}

	/**
	 * Содержит ли массив строк строку вне зависимости от раскладки
	 * @param str
	 * @param array
	 * @return
	 */
	public static boolean containsCaseInsensitive(String str, String[] array){
		if (array == null || str == null) {
			return false;
		}
		for (String string : array){
			if (string.equalsIgnoreCase(str)){
				return true;
			}
		}
		return false;
	}
}

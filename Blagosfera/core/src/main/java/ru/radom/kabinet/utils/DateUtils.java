package ru.radom.kabinet.utils;

import org.apache.commons.lang3.time.FastDateFormat;

import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Вспомогательный класс для работы с датами
 */
public class DateUtils {
	/**
	 * Набор форматов
	 */
	public static class Format {
		/**
		 * формат "dd.MM.yyyy"
		 */
		public static final String DATE = "dd.MM.yyyy";

		/**
		 * формат "dd.MM.yyyy HH:mm:ss"
		 */
		public static final String DATE_TIME = "dd.MM.yyyy HH:mm:ss";

		/**
		 * формат "dd.MM.yyyy HH:mm"
		 */
		public static final String DATE_TIME_SHORT = "dd.MM.yyyy HH:mm";

        /**
         * формат "HH:mm"
         */
        public static final String TIME_SHORT = "HH:mm";
	}

	public static String formatDate(Date date, String pattern) {
		return FastDateFormat.getInstance(pattern).format(date);
	}

	public static Date getDayBegin(Date date) {
		if (date == null) {
			return null;
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar.getTime();
		}
	}
	
	public static Date getDayEnd(Date date) {
		if (date == null) {
			return null;
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
			return calendar.getTime();
		}
	}

	public static boolean isOlderThan(Date date, int field, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, amount);
		return calendar.compareTo(Calendar.getInstance()) < 0;
	}

	public static Date add(Date date, int field, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, amount);
		return calendar.getTime();
	}

	public static Date append(Date date, Date time) {
		if (date == null || time == null) {
			return null;
		}
		Calendar dateCalendar = Calendar.getInstance();
		dateCalendar.setTime(date);
		Calendar timeCalendar = Calendar.getInstance();
		timeCalendar.setTime(time);
		dateCalendar.add(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
		dateCalendar.add(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
		dateCalendar.add(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));
		dateCalendar.add(Calendar.MILLISECOND, timeCalendar.get(Calendar.MILLISECOND));
		return dateCalendar.getTime();
	}

	public static Date parseDate(String dateString, Date defaultValue) {
		return parseDate(dateString, defaultValue, Format.DATE);
	}

	public static Date parseDate(String dateString, Date defaultValue, String format) {
		try {
			return new SimpleDateFormat(format).parse(dateString);
		} catch (Exception e) {
			// ignore
			return defaultValue;
		}
	}

    public static int getDistanceDays(Date from, Date to) {
        return (int) Math.ceil((double) (to.getTime() - from.getTime()) / 1000 / 60 / 60 / 24);
    }

	public static int getDistanceHours(Date from, Date to) {
		return (int) Math.ceil((double) (to.getTime() - from.getTime()) / 1000 / 60 / 60);
	}
	
	public static int getDistanceMinutes(Date from, Date to) {
		return (int) Math.ceil((double) (to.getTime() - from.getTime()) / 1000 / 60);
	}

    public static int getDistanceSeconds(Date from, Date to) {
		return (int) Math.ceil((double) (to.getTime() - from.getTime()) / 1000);
	}

	public static String getHumanReadableDistance(Date from, Date to, String week1, String week24, String week50, String day1, String day24, String day50, String hour1, String hour24, String hour50) {
		int hoursDistance = getDistanceHours(from, to);
		return getHumanReadableDistance(hoursDistance, week1, week24, week50, day1, day24, day50, hour1, hour24, hour50);
	}

	public static String getHumanReadableDistance(int hoursDistance, String week1, String week24, String week50, String day1, String day24, String day50, String hour1, String hour24, String hour50) {
		int hours = hoursDistance % 24;
		int days = (hoursDistance / 24) % 7;
		int weeks = hoursDistance / 24 / 7;
		return (weeks > 0 ? weeks + " " + StringUtils.getDeclension(weeks, week1, week24, week50) + " " : "") + (days > 0 ? days + " " + StringUtils.getDeclension(days, day1, day24, day50) + " " : "") + (hours > 0 ? hours + " " + StringUtils.getDeclension(hours, hour1, hour24, hour50) : "");
	}
	
	
	//в винительном падеже, например чтобы писать после предлога через
	public static String getHumanReadableDistanceAccusative(int hoursDistance) {
		return getHumanReadableDistance(hoursDistance, "неделю", "недели", "недель", "день", "дня", "дней", "час", "часа", "часов");
	}
	
	/**
	 * Преобразование даты в строку
	 *
	 * @param date
	 *            Дата
	 * @param pattern
	 *            Шаблон
	 * @return - строка с датой
	 */
	public static String dateToString(Date date, String pattern) {
		if (date == null) {
			return "";
		}

		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.format(date);
	}

	/**
	 * Преобразование даты в строку
	 *
	 * @param timestamp
	 *            Дата
	 * @param pattern
	 *            Шаблон
	 * @return - строка с датой
	 */
	public static String timestampToString(Timestamp timestamp, String pattern) {
		if (timestamp == null) {
			return "";
		}

		return dateToString(timestamp, pattern);
	}

	/**
	 * Преобразование строки в дату
	 *
	 * @param dateString
	 *            Строка
	 * @param pattern
	 *            Шаблон
	 * @return Date
	 */
	public static Date stringToDate(String dateString, String pattern) {
		if (dateString == null || "".equals(dateString.trim())) {
			return null;
		}

		ParsePosition pos = new ParsePosition(0);

		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		formatter.setLenient(false);
		formatter.applyPattern(pattern);

		Date date = formatter.parse(dateString, pos);

		return date;
	}

	/**
	 * Преобразование строки в дату
	 *
	 * @param dateString
	 *            Строка
	 * @param pattern
	 *            Шаблон
	 * @return Timestamp
	 */
	public static Timestamp stringToTimestamp(String dateString, String pattern) {
		if (dateString.equals(""))
			return null;

		Date date = stringToDate(dateString, pattern);
		Timestamp timestamp = new Timestamp(date.getTime());

		return timestamp;
	}
	

}

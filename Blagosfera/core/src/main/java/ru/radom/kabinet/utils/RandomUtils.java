package ru.radom.kabinet.utils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;

public class RandomUtils {

	private final static Random RANDOM = new Random(new java.util.Date().getTime());

	public static BigDecimal getBigDecimal(BigDecimal max) {
		return max.multiply(new BigDecimal(RANDOM.nextDouble())).setScale(2, BigDecimal.ROUND_HALF_DOWN);
	}

	public static Long getLong(long max) {
		return Math.round(max * RANDOM.nextDouble());
	}

	public static Date getDate(Date startDate, Date endDate) {
		return new Date(startDate.getTime() + Math.round((endDate.getTime() - startDate.getTime()) * RANDOM.nextDouble()));
	}

	public static int getInteger(int max) {
		return (int) Math.round(max * RANDOM.nextDouble());
	}

}

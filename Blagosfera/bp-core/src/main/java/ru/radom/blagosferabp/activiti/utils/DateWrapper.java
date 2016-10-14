package ru.radom.blagosferabp.activiti.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Otts Alexey on 25.11.2015.<br/>
 * Обертка для работы с датами
 */
public class DateWrapper {

    /**
     * календарь, который используется для извлечения значений
     */
    private final Calendar inst;

    /**
     * Получаем обертку из даты
     */
    public static DateWrapper wrap(Date date) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        return new DateWrapper(instance);
    }

    /**
     * Получаем обертку из календаря
     */
    public static DateWrapper wrap(Calendar calendar) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(calendar.getTimeInMillis());
        return new DateWrapper(instance);
    }

    /**
     * Получаем обертку по времени
     */
    public static DateWrapper wrap(Long time) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time);
        return new DateWrapper(instance);
    }

    public DateWrapper(Calendar inst) {
        this.inst = inst;
    }

    public int milis() {
        return inst.get(Calendar.MILLISECOND);
    }

    public int second() {
        return inst.get(Calendar.SECOND);
    }

    public int minute() {
        return inst.get(Calendar.MINUTE);
    }

    public int hour() {
        return inst.get(Calendar.HOUR_OF_DAY);
    }

    public int day() {
        return inst.get(Calendar.DAY_OF_MONTH);
    }

    public int month() {
        return inst.get(Calendar.MONTH);
    }

    public int year() {
        return inst.get(Calendar.YEAR);
    }

    public long time() {
        return inst.getTimeInMillis();
    }

    public Date date() {
        return inst.getTime();
    }

    public DateWrapper addMilis(int milis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(inst.getTimeInMillis());
        c.add(Calendar.MILLISECOND, milis);
        return new DateWrapper(c);
    }
    public DateWrapper addSecond(int second) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(inst.getTimeInMillis());
        c.add(Calendar.SECOND, second);
        return new DateWrapper(c);
    }
    public DateWrapper addMinute(int minute) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(inst.getTimeInMillis());
        c.add(Calendar.MINUTE, minute);
        return new DateWrapper(c);
    }
    public DateWrapper addHour(int hour) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(inst.getTimeInMillis());
        c.add(Calendar.HOUR_OF_DAY, hour);
        return new DateWrapper(c);
    }
    public DateWrapper addDay(int day) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(inst.getTimeInMillis());
        c.add(Calendar.DAY_OF_MONTH, day);
        return new DateWrapper(c);
    }
    public DateWrapper addMonth(int month) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(inst.getTimeInMillis());
        c.add(Calendar.MONTH, month);
        return new DateWrapper(c);
    }
    public DateWrapper addYear(int year) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(inst.getTimeInMillis());
        c.add(Calendar.YEAR, year);
        return new DateWrapper(c);
    }
}

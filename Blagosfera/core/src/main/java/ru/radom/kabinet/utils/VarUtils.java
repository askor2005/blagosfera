package ru.radom.kabinet.utils;

/**
 * Класс для приведения типов данных и вслучае неудачи получения значения по умолчанию.
 * Created by vgusev on 02.08.2015.
 */
public class VarUtils {

    public static Long getLong(String longValueStr, Long defaultValue) {
        Long result = defaultValue;
        if (longValueStr != null) {
            try {
                result = Long.valueOf(longValueStr);
            } catch (Exception e) {
                // do nothing
            }
        }
        return result;
    }

    public static Integer getInt(String intValueStr, Integer defaultValue) {
        Integer result = defaultValue;
        if (intValueStr != null) {
            try {
                result = Integer.valueOf(intValueStr);
            } catch (Exception e) {
                // do nothing
            }
        }
        return result;
    }

    public static Double getDouble(String doubleValueStr, Double defaultValue) {
        Double result = defaultValue;
        if (doubleValueStr != null) {
            try {
                result = Double.valueOf(doubleValueStr);
            } catch (Exception e) {
                // do nothing
            }
        }
        return result;
    }

    public static boolean getBool(String booleanValueStr, boolean defaultValue){
        boolean result = defaultValue;
        if (booleanValueStr != null) {
            try {
                result = Boolean.valueOf(booleanValueStr).booleanValue();
            } catch (Exception e) {
                e.printStackTrace();
                // do nothing
            }
        }
        return result;
    }
}

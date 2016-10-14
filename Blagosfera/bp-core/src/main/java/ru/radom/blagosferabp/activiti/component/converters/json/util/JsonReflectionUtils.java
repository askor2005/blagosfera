package ru.radom.blagosferabp.activiti.component.converters.json.util;

import ru.radom.blagosferabp.activiti.component.converters.json.util.extractor.ValueExtractor;
import ru.radom.blagosferabp.activiti.component.converters.json.util.extractor.impl.*;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * Created by alex on 05.10.2015.<br/>
 * Утильный класс для получения {@link ValueExtractor}'a, который умеет распаковывать значение для конкретного поля объекта
 */
public class JsonReflectionUtils {

    public static ValueExtractor getExtractorForField(Field field) {
        Class<?> type = field.getType();
        return getExtractorForType(type);
    }

    private static ValueExtractor getExtractorForType(Class<?> type) {
        if (type.equals(String.class)) {
            return new StringExtractor();
        } else if (isIntegral(type)) {
            return new LongExtractor();
        } else if (isReal(type)) {
            return new DoubleExtractor();
        } else if (isBoolean(type)) {
            return new BooleanExtractor();
        } else if(Map.class.isAssignableFrom(type)) {
            return new MapExtractor();
        } else if(Collection.class.isAssignableFrom(type)) {
            return new CollectionExtractor();
        }
        throw new IllegalArgumentException("Unsupported type: " + type);
    }

    private static boolean isBoolean(Class<?> type) {
        return type.equals(Boolean.class) || type.equals(boolean.class);
    }

    private static boolean isReal(Class<?> type) {
        return type.equals(float.class) || type.equals(double.class) ||
                type.equals(Float.class) || type.equals(Double.class);
    }

    private static boolean isIntegral(Class<?> type) {
        return type.equals(int.class) || type.equals(long.class) || type.equals(Integer.class) || type.equals(Long.class);
    }
}

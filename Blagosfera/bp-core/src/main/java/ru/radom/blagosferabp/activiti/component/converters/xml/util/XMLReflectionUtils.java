package ru.radom.blagosferabp.activiti.component.converters.xml.util;

import ru.radom.blagosferabp.activiti.component.converters.xml.util.extractor.attribute.AttributeValueExtractor;
import ru.radom.blagosferabp.activiti.component.converters.xml.util.extractor.attribute.impl.DoubleAttributeValueExtractor;
import ru.radom.blagosferabp.activiti.component.converters.xml.util.extractor.attribute.impl.LongAttributeValueExtractor;
import ru.radom.blagosferabp.activiti.component.converters.xml.util.extractor.attribute.impl.StringAttributeValueExtractor;

import java.lang.reflect.Field;

/**
 * Created by alex on 06.10.2015.<br/>
 * Утильный класс для получения {@link AttributeValueExtractor}'a, который умеет распаковывать значение для конкретного поля объекта
 */
public class XMLReflectionUtils {

    public static AttributeValueExtractor getExtractorForField(Field field) {
        Class<?> type = field.getType();
        return getExtractorForType(type);
    }

    private static AttributeValueExtractor getExtractorForType(Class<?> type) {
        if(type.equals(String.class)) {
            return new StringAttributeValueExtractor();
        } else if(isIntegral(type)) {
            return new LongAttributeValueExtractor();
        } else if(isReal(type)) {
            return new DoubleAttributeValueExtractor();
        }
        throw new IllegalArgumentException("Unsupported type: " + type);
    }

    private static boolean isReal(Class<?> type) {
        return type.equals(float.class) || type.equals(double.class) ||
                type.equals(Float.class) || type.equals(Double.class);
    }

    private static boolean isIntegral(Class<?> type) {
        return type.equals(int.class) || type.equals(long.class) || type.equals(Integer.class) || type.equals(Long.class);
    }
}

package ru.radom.blagosferabp.activiti.component.converters.xml.util.extractor.attribute.impl;

import ru.radom.blagosferabp.activiti.component.converters.xml.util.extractor.attribute.AttributeValueExtractor;

/**
 * Created by alex on 06.10.2015.<br/>
 * Преобразователь для чисел с плавующей точкой
 */
public class BooleanAttributeValueExtractor implements AttributeValueExtractor<Boolean> {

    @Override
    public Boolean extract(String attributeValue) {
        return Boolean.parseBoolean(attributeValue);
    }
}

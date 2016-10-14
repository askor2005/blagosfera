package ru.radom.blagosferabp.activiti.component.converters.xml.util.extractor.attribute.impl;

import ru.radom.blagosferabp.activiti.component.converters.xml.util.extractor.attribute.AttributeValueExtractor;

/**
 * Created by alex on 06.10.2015.<br/>
 * Преобразователь для целочисленного типа
 */
public class LongAttributeValueExtractor implements AttributeValueExtractor<Long> {
    @Override
    public Long extract(String attributeValue) {
        return Long.parseLong(attributeValue);
    }
}

package ru.radom.blagosferabp.activiti.component.converters.json.util.extractor.impl;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by alex on 05.10.2015.<br/>
 * Распаковщик целочисленных значений
 */
public class LongExtractor extends NullableExtractor<Long> {

    @Override
    protected Long extractNonNull(JsonNode node) {
        return node.asLong();
    }
}

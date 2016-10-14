package ru.radom.blagosferabp.activiti.component.converters.json.util.extractor.impl;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by alex on 05.10.2015.<br/>
 * Распаковщик строковых значений
 */
public class StringExtractor extends NullableExtractor<String> {

    @Override
    protected String extractNonNull(JsonNode node) {
        return node.asText();
    }
}

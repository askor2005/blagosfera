package ru.radom.blagosferabp.activiti.component.converters.json.util.extractor.impl;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by alex on 05.10.2015.<br/>
 * Распаковщик логических значений
 */
public class BooleanExtractor extends NullableExtractor<Boolean> {
    @Override
    protected Boolean extractNonNull(JsonNode node) {
        return node.asBoolean();
    }
}

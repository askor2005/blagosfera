package ru.radom.blagosferabp.activiti.component.converters.json.util.extractor.impl;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by alex on 05.10.2015.<br/>
 * Распаковщик дробных значений
 */
public class DoubleExtractor extends NullableExtractor<Double> {

    @Override
    protected Double extractNonNull(JsonNode node) {
        return node.asDouble();
    }
}

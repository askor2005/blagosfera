package ru.radom.blagosferabp.activiti.component.converters.json.util.extractor;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by alex on 05.10.2015.<br/>
 * Утильный класс, который помогает вытаскивать значения из json
 */
public interface ValueExtractor<T> {

    T extractValue(JsonNode node);
}

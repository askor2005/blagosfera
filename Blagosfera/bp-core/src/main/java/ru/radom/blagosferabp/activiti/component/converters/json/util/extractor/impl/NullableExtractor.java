package ru.radom.blagosferabp.activiti.component.converters.json.util.extractor.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import ru.radom.blagosferabp.activiti.component.converters.json.util.extractor.ValueExtractor;

/**
 * Created by alex on 05.10.2015.<br/>
 * Абстрактный экстрактор значений для значений, которые могут принимать null
 */
public abstract class NullableExtractor<T> implements ValueExtractor<T> {

    @Override
    public T extractValue(JsonNode node) {
        if(node == null || node.getNodeType().equals(JsonNodeType.NULL)) {
            return defaultValue();
        }
        return extractNonNull(node);
    }

    /**
     * Распоковать не пустое значение
     */
    protected abstract T extractNonNull(JsonNode node);

    /**
     * Значение, которое должно быть возвращено, если значение - null
     */
    private T defaultValue() {
        return null;
    }
}

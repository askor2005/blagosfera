package ru.radom.blagosferabp.activiti.component.converters.json.util.extractor.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Otts Alexey on 12.11.2015.<br/>
 * Extractor для {@link Map}
 */
public class CollectionExtractor extends NullableExtractor<Collection> {


    private ObjectMapper objectMapper;

    @Override
    protected Collection extractNonNull(JsonNode node) {
        if(objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        try {
            return objectMapper.convertValue(node, List.class);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

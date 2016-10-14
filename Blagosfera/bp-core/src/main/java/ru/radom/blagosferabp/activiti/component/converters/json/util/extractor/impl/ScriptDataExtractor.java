package ru.radom.blagosferabp.activiti.component.converters.json.util.extractor.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.radom.blagosfera.scripting.commons.ScriptData;

/**
 * Created by Otts Alexey on 12.11.2015.<br/>
 * Extractor для {@link ScriptData}
 */
public class ScriptDataExtractor extends NullableExtractor<ScriptData> {

    private StringExtractor stringExtractor;

    private MapExtractor mapExtractor;

    @Override
    @SuppressWarnings("unchecked")
    protected ScriptData extractNonNull(JsonNode node) {
        if(node.getNodeType().equals(JsonNodeType.OBJECT)) {
            ScriptData scriptData = new ScriptData();
            ObjectNode objectNode = (ObjectNode) node;
            if(stringExtractor == null) {
                stringExtractor = new StringExtractor();
            }
            scriptData.setScript(stringExtractor.extractValue(objectNode.get("script")));
            if(mapExtractor == null) {
                mapExtractor = new MapExtractor();
            }
            scriptData.setContext(mapExtractor.extractValue(objectNode.get("context")));
            scriptData.setOutputMapping(mapExtractor.extractValue(objectNode.get("outputMapping")));
            return scriptData;
        }
        return null;
    }
}

package ru.radom.blagosferabp.activiti.component.converters.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.bpmn.model.IntermediateCatchEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by alex on 27.11.2015.<br/>
 * Конвертер не стандартных экземпляров {@link IntermediateCatchEvent}
 */
@Component
@Scope("prototype")
public class CustomIntermediateCatchEventJsonConverter extends CustomFlowNodeCommonJsonConverter<IntermediateCatchEvent> {

    /** */
    public CustomIntermediateCatchEventJsonConverter(String stencilId, Class<? extends IntermediateCatchEvent> clazz) {
        super(stencilId, clazz);
    }

    @Override
    public void fillCirclesTypes(List<String> circles) {
        circles.add(stencilId);
    }

    @Override
    protected void afterConvertElementToJson(ObjectNode propertiesNode, IntermediateCatchEvent event) {
        addEventProperties(event, propertiesNode);
    }

    @Override
    protected void afterConvertElementToJson(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap, IntermediateCatchEvent event) {
        if (isNotNullNode(getProperty(PROPERTY_SIGNALREF, elementNode))) {
            convertJsonToSignalDefinition(elementNode, event);
        } else if (isNotNullNode(getProperty(PROPERTY_MESSAGEREF, elementNode))) {
            convertJsonToMessageDefinition(elementNode, event);
        } else if (isNotNullNode(getProperty(PROPERTY_TIMER_DATE, elementNode)) ||
            isNotNullNode(getProperty(PROPERTY_TIMER_CYCLE, elementNode)) ||
            isNotNullNode(getProperty(PROPERTY_TIMER_DURATON, elementNode))) {
            convertJsonToTimerDefinition(elementNode, event);
        }
    }

    private boolean isNotNullNode(JsonNode node) {
        return node != null && !node.getNodeType().equals(JsonNodeType.NULL);
    }
}

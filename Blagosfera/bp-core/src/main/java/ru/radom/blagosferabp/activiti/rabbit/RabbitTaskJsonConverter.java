package ru.radom.blagosferabp.activiti.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.editor.language.json.converter.BaseBpmnJsonConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.component.converters.json.ExtendedBaseBpmnJsonConverter;
import ru.radom.blagosferabp.activiti.component.converters.json.util.extractor.ValueExtractor;
import ru.radom.blagosferabp.activiti.component.converters.json.util.extractor.impl.*;
import ru.radom.blagosferabp.activiti.dao.StencilDAO;
import ru.radom.blagosferabp.activiti.dto.util.StencilConverter;
import ru.radom.blagosferabp.activiti.model.StencilEntity;
import ru.radom.blagosferabp.activiti.rabbit.bundle.RabbitTaskBundle;
import ru.radom.blagosferabp.activiti.stencil.exchange.Property;
import ru.radom.blagosferabp.activiti.stencil.exchange.Stencil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Otts Alexey on 06.11.2015.<br/>
 * TODO comment me pls
 */
@Component
public class RabbitTaskJsonConverter extends ExtendedBaseBpmnJsonConverter {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private StencilDAO stencilDAO;

    @Autowired
    private StencilConverter stencilConverter;

    @Override
    public void fillTypes(Map<String, BaseBpmnJsonConverter> convertersToBpmnMap, Map<Class<? extends BaseElement>, BaseBpmnJsonConverter> convertersToJsonMap) {
        convertersToBpmnMap.put(RabbitTaskBundle.RABBIT_TASK_STENCIL, this);
        convertersToJsonMap.put(RabbitTask.class, this);
    }

    @Override
    public void fillRectanglesTypes(List<String> rectangles) {
        rectangles.add(RabbitTaskBundle.RABBIT_TASK_STENCIL);
    }

    @Override
    protected String getStencilId(BaseElement baseElement) {
        return ((RabbitTask)baseElement).getStencilEntityId();
    }

    @Override
    protected void convertElementToJson(ObjectNode propertiesNode, BaseElement baseElement) {
        ObjectWriter writer = new ObjectMapper().writer();
        RabbitTask task = (RabbitTask) baseElement;
        for (Map.Entry<String, Object> entry : task.getCustomParameters().entrySet()) {
            try {
                Object value = entry.getValue();
                String stringValue;
                if(value instanceof String) {
                    stringValue = (String) value;
                } else {
                    stringValue = writer.writeValueAsString(value);
                }
                setPropertyValue(entry.getKey(), stringValue, propertiesNode);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Error during value converting " + entry.getKey() + " of " + baseElement.getClass());
            }
        }
        if (StringUtils.isNotEmpty(task.getResultVariableName())) {
            propertiesNode.put(PROPERTY_SERVICETASK_RESULT_VARIABLE, task.getResultVariableName());
        }
    }

    @Override
    protected BaseElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap) {
        JsonNode stencilEntityIdNode = getProperty(RabbitTaskBundle.STENCIL_ENTITY_ID_NODE, elementNode);
        if(stencilEntityIdNode.getNodeType().equals(JsonNodeType.NULL)) {
            throw new IllegalArgumentException("StencilEntityId property is not set");
        }
        String stencilId = stencilEntityIdNode.asText();
        StencilEntity entity = stencilDAO.getOne(stencilId);
        if(entity == null) {
            throw new IllegalArgumentException("Компонент " + stencilId + " не найден в базе. Возможно он был удален.");
        }
        RabbitTask task = applicationContext.getBean(RabbitTask.class, new Object[]{});
        task.setStencilEntityId(stencilId);
        task.setQueueToSend(entity.getQueueToSend());

        Map<String, Object> properties = extractProperties(elementNode, entity);

        task.setCustomParameters(properties);
        task.setAnswerType(entity.getAnswerType());

        if (StringUtils.isNotEmpty(getPropertyValueAsString(PROPERTY_SERVICETASK_RESULT_VARIABLE, elementNode))) {
            task.setResultVariableName(getPropertyValueAsString(PROPERTY_SERVICETASK_RESULT_VARIABLE, elementNode));
        }
        return task;
    }

    private Map<String, Object> extractProperties(JsonNode elementNode, StencilEntity entity) {
        Map<String, Object> properties = new HashMap<>();
        Stencil stencil = stencilConverter.convert(entity);
        for (Property property : stencil.getProperties()) {
            String id = property.getId();
            if(RabbitTaskBundle.STENCIL_ENTITY_ID_NODE.equals(id) || stencilConverter.isPropertyDefault(id)) {
                continue;
            }

            String type = property.getType().toLowerCase();
            ValueExtractor extractor = resolveExtractor(type);
            if(extractor == null) {
                continue;
            }
            JsonNode node = getProperty(id.toLowerCase(), elementNode);
            properties.put(id, extractor.extractValue(node));
        }
        return properties;
    }

    private ValueExtractor resolveExtractor(String type) {
        switch (type) {
            case Property.CHOICE:
            case Property.COLOR:
            case Property.STRING:
            case Property.TEXT:
                return new StringExtractor();
            case Property.BOOLEAN:
                return new BooleanExtractor();
            case Property.INTEGER:
                return new LongExtractor();
            case Property.FLOAT:
                return new DoubleExtractor();
            case Property.SCRIPT:
                return new ScriptDataExtractor();
            default:
                if(type.contains("complex")) {
                    return new MapExtractor();
                }
                return null;
        }
    }
}

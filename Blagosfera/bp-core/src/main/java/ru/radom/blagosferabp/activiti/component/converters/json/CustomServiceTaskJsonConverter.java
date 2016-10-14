package ru.radom.blagosferabp.activiti.component.converters.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.bpmn.model.ServiceTask;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by alex on 04.10.2015.<br/>
 * Конвертер не стандартных экземпляров {@link ServiceTask}
 */
@Component
@Scope("prototype")
public class CustomServiceTaskJsonConverter extends CustomFlowNodeCommonJsonConverter<ServiceTask> {

    /** */
    public CustomServiceTaskJsonConverter(String stencilId, Class<? extends ServiceTask> clazz) {
        super(stencilId, clazz);
    }

    @Override
    public void fillRectanglesTypes(List<String> rectangles) {
        rectangles.add(stencilId);
    }

    @Override
    protected void afterConvertElementToJson(ObjectNode propertiesNode, ServiceTask task) {
        if (StringUtils.isNotEmpty(task.getResultVariableName())) {
            propertiesNode.put(PROPERTY_SERVICETASK_RESULT_VARIABLE, task.getResultVariableName());
        }
    }

    @Override
    protected void afterConvertElementToJson(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap, ServiceTask task) {
        if (StringUtils.isNotEmpty(getPropertyValueAsString(PROPERTY_SERVICETASK_RESULT_VARIABLE, elementNode))) {
            task.setResultVariableName(getPropertyValueAsString(PROPERTY_SERVICETASK_RESULT_VARIABLE, elementNode));
        }
    }
}

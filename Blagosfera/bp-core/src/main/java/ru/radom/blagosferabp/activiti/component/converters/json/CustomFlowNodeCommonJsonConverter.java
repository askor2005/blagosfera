package ru.radom.blagosferabp.activiti.component.converters.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.editor.language.json.converter.BaseBpmnJsonConverter;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;
import ru.radom.blagosferabp.activiti.component.converters.json.util.JsonReflectionUtils;
import ru.radom.blagosferabp.activiti.component.converters.json.util.extractor.ValueExtractor;
import ru.radom.blagosferabp.activiti.component.reflection.FlowNodeParameter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Otts Alexey on 27.11.2015.<br/>
 * Общий код, для пребразования модели в json и обратно
 */
public abstract class CustomFlowNodeCommonJsonConverter<T extends FlowNode> extends ExtendedBaseBpmnJsonConverter {

    @Autowired
    protected ApplicationContext applicationContext;

    /**
     * Id графического компонента
     */
    protected final String stencilId;

    /**
     * Класс сервисного таска
     */
    protected final Class<? extends T> clazz;

    /**
     * Поле таска -> поле json компонента
     */
    protected final Map<Field, String> taskFieldToStencilParam = new HashMap<>();

    /**
     * Поле таска -> extractor
     */
    protected final Map<Field, ValueExtractor> taskFieldToExtractor = new HashMap<>();

    /** */
    public CustomFlowNodeCommonJsonConverter(String stencilId, Class<? extends T> clazz) {
        this.stencilId = stencilId;
        this.clazz = clazz;

        ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                FlowNodeParameter parameter = field.getDeclaredAnnotation(FlowNodeParameter.class);
                if (parameter != null) {
                    String stencilParameter = parameter.stencilParameter();
                    if ("".equals(stencilParameter)) {
                        stencilParameter = field.getName();
                    }
                    taskFieldToStencilParam.put(field, stencilParameter);
                    taskFieldToExtractor.put(field, JsonReflectionUtils.getExtractorForField(field));
                }
            }
        });
    }


    @Override
    public void fillTypes(
        Map<String, BaseBpmnJsonConverter> convertersToBpmnMap,
        Map<Class<? extends BaseElement>, BaseBpmnJsonConverter> convertersToJsonMap
    ) {
        convertersToBpmnMap.put(stencilId, this);
        convertersToJsonMap.put(clazz, this);
    }

    @Override
    protected void convertElementToJson(ObjectNode propertiesNode, BaseElement baseElement) {
        ObjectWriter writer = new ObjectMapper().writer();
        for (Map.Entry<Field, String> entry : taskFieldToStencilParam.entrySet()) {
            try {
                Object value = PropertyUtils.getProperty(baseElement, entry.getKey().getName());
                String stringValue;
                if(value instanceof String) {
                    stringValue = (String) value;
                } else {
                    stringValue = writer.writeValueAsString(value);
                }
                setPropertyValue(entry.getValue(), stringValue, propertiesNode);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new IllegalStateException("Error during getting property " + entry.getKey() + " of " + baseElement.getClass());
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Error during value converting " + entry.getKey() + " of " + baseElement.getClass());
            }
        }
        afterConvertElementToJson(propertiesNode, (T)baseElement);
    }

    /**
     * Действия которые нужно совершить дополнительно после преобразования в json
     */
    protected void afterConvertElementToJson(ObjectNode propertiesNode, T flowNode) {
    }

    @Override
    protected BaseElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap) {
        T task = instantiateTask();
        for (Map.Entry<Field, String> entry : taskFieldToStencilParam.entrySet()) {
            Field field = entry.getKey();
            JsonNode property = getProperty(entry.getValue(), elementNode);
            ValueExtractor extractor = taskFieldToExtractor.get(field);
            try {
                PropertyUtils.setProperty(task, field.getName(), extractor.extractValue(property));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new IllegalStateException("Error during setting property " + field.getName() + "of " + clazz);
            }
        }
        afterConvertElementToJson(elementNode, modelNode, shapeMap, task);
        return task;
    }

    /**
     * Действия которые нужно совершить дополнительно после преобразования из json
     */
    protected void afterConvertElementToJson(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap, T flowNode) {}

    @Override
    protected String getStencilId(BaseElement baseElement) {
        return stencilId;
    }

    @SneakyThrows
    protected T instantiateTask() {
        return applicationContext.getBean(clazz, new Object[] {});
    }
}

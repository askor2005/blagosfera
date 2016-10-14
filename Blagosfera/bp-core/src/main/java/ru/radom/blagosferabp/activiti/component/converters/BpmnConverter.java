package ru.radom.blagosferabp.activiti.component.converters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import ru.radom.blagosferabp.activiti.component.CustomFlowNodeRegistry;
import ru.radom.blagosferabp.activiti.component.ModelBundle;
import ru.radom.blagosferabp.activiti.component.converters.json.*;
import ru.radom.blagosferabp.activiti.component.converters.json.BpmnJsonConverter;
import ru.radom.blagosferabp.activiti.component.converters.json.ExtendedBaseBpmnJsonConverter;
import ru.radom.blagosferabp.activiti.component.converters.xml.*;
import ru.radom.blagosferabp.activiti.component.reflection.CustomIntermediateCatchEvent;
import ru.radom.blagosferabp.activiti.component.reflection.CustomServiceTask;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by alex on 02.10.2015.<br/>
 * Конвертер бизнесс процессов
 */
@Log4j2
@Component("bpmnConverter")
public class BpmnConverter {

    private BpmnJsonConverter jsonConverter = new BpmnJsonConverter();

    private BpmnXMLConverter xmlConverter = new BpmnXMLConverter();

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CustomServiceTaskXMLConverter customServiceTaskXMLConverter;

    @Autowired
    private CustomIntermediateCatchEventXMLConverter customIntermediateCatchEventXMLConverter;

    @Autowired
    private CustomFlowNodeRegistry customFlowNodeRegistry;

    @PostConstruct
    private void postConstruct() throws IllegalAccessException, InstantiationException {
        fillCustomFlowNodeConverter(CustomServiceTask.class, customServiceTaskXMLConverter, CustomServiceTaskJsonConverter.class);
        fillCustomFlowNodeConverter(CustomIntermediateCatchEvent.class, customIntermediateCatchEventXMLConverter, CustomIntermediateCatchEventJsonConverter.class);
    }

    private <T extends FlowNode> void fillCustomFlowNodeConverter(
        Class<? extends Annotation> annotationClass,
        CustomFlowNodeCommonXMLConverter<T> converter,
        Class<? extends CustomFlowNodeCommonJsonConverter<T>> jsonConverterClass
    ) {
        Map<Class<? extends FlowNode>, ModelBundle> bundles = customFlowNodeRegistry.getBundlesByClass().get(annotationClass);
        Map<Class<? extends FlowNode>, String> beanNamesByClass = customFlowNodeRegistry.getBeanNamesByClass().get(annotationClass);
        Method type = ReflectionUtils.findMethod(annotationClass, "type");
        for (Map.Entry<Class<? extends FlowNode>, ModelBundle> entry : bundles.entrySet()) {
            Class<T> taskClass = (Class<T>)entry.getKey();
            ModelBundle modelBundle = entry.getValue();

            ExtendedBaseBpmnJsonConverter bpmnJSONConverter = modelBundle.getJsonConverter();
            if (bpmnJSONConverter == null) {
                bpmnJSONConverter = applicationContext.getBean(
                        jsonConverterClass,
                        modelBundle.getStencilId(),
                        taskClass
                );
            }
            jsonConverter.addConverter(bpmnJSONConverter);

            Annotation annotation = taskClass.getAnnotation(annotationClass);
            String taskType = (String) ReflectionUtils.invokeMethod(type, annotation);

            if(!StringUtils.hasText(taskType)) {
                taskType = beanNamesByClass.get(taskClass);
            }
            converter.addCustomTask(taskClass, taskType, modelBundle);
        }
    }


    /**
     * Конвертировать модель бизнесс процесса в json
     */
    public ObjectNode convertToJson(BpmnModel model) {
        return jsonConverter.convertToJson(model);
    }

    /**
     * Конвертировать модель бизнесс процесса в XML
     */
    public byte[] convertToXML(BpmnModel model) {
        return xmlConverter.convertToXML(model);
    }

    /**
     * Конвертировать json в модель бизнесс процесса
     */
    public BpmnModel convertToBpmnModel(JsonNode modelNode) {
        return jsonConverter.convertToBpmnModel(modelNode);
    }

}

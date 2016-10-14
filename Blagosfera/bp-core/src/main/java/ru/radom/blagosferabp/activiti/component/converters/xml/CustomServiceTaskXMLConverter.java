package ru.radom.blagosferabp.activiti.component.converters.xml;

import org.activiti.bpmn.converter.BaseBpmnXMLConverter;
import org.activiti.bpmn.converter.ServiceTaskXMLConverter;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ServiceTask;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.component.reflection.CustomServiceTask;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * Created by alex on 06.10.2015.<br/>
 * Конвертер для {@link ServiceTask} помеченных аннотацией {@link CustomServiceTask}
 */
@Component
public class CustomServiceTaskXMLConverter extends CustomFlowNodeCommonXMLConverter<ServiceTask> {

    /**
     * Ориганильный конвертер для {@link ServiceTask}. <br/>
     * Нужен он, потому что механизм парсинга построен так,
     * что не может быть для одного и того же xml тега больше одного ковертера. И нужно обращаться к его методам.
     */
    protected ServiceTaskXMLConverter originalConverter = new ServiceTaskXMLConverter();

    @Override
    protected BaseBpmnXMLConverter getOriginalConverter() {
        return originalConverter;
    }

    @Override
    protected Class<? extends BaseElement> getBpmnElementType() {
        return ServiceTask.class;
    }

    @Override
    protected String getXMLElementName() {
        return ELEMENT_TASK_SERVICE;
    }

    @Override
    protected void afterExtractAttributes(XMLStreamReader xtr, BpmnModel model, ServiceTask task) throws Exception {
        task.setResultVariableName(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_SERVICE_RESULTVARIABLE));
        if (StringUtils.isEmpty(task.getResultVariableName())) {
            task.setResultVariableName(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "resultVariable"));
        }
    }

    @Override
    protected void afterWriteAdditionalAttributes(ServiceTask task, BpmnModel model, XMLStreamWriter xtw) throws Exception {
        if (StringUtils.isNotEmpty(task.getResultVariableName())) {
            writeQualifiedAttribute(ATTRIBUTE_TASK_SERVICE_RESULTVARIABLE, task.getResultVariableName(), xtw);
        }
    }
}

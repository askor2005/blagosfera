package ru.radom.blagosferabp.activiti.component.converters.xml;

import org.activiti.bpmn.converter.BaseBpmnXMLConverter;
import org.activiti.bpmn.converter.CatchEventXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.IntermediateCatchEvent;
import org.activiti.bpmn.model.ServiceTask;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLStreamWriter;

/**
 * Created by alex on 29.11.2015.<br/>
 * Конвертер для {@link IntermediateCatchEvent} помеченных аннотацией {@link ru.radom.blagosferabp.activiti.component.reflection.CustomIntermediateCatchEvent}
 */
@Component
public class CustomIntermediateCatchEventXMLConverter extends CustomFlowNodeCommonXMLConverter<IntermediateCatchEvent> {

    /**
     * Оригинальный конвертер для {@link ServiceTask}. <br/>
     * Нужен он, потому что механизм парсинга построен так,
     * что не может быть для одного и того же xml тега больше одного ковертера. И нужно обращаться к его методам.
     */
    protected CatchEventXMLConverter originalConverter = new CatchEventXMLConverter();

    @Override
    protected BaseBpmnXMLConverter getOriginalConverter() {
        return originalConverter;
    }

    @Override
    protected Class<? extends IntermediateCatchEvent> getBpmnElementType() {
        return IntermediateCatchEvent.class;
    }

    @Override
    protected String getXMLElementName() {
        return ELEMENT_EVENT_CATCH;
    }

    @Override
    protected void afterWriteAdditionalChildElement(IntermediateCatchEvent flowNode, BpmnModel model, XMLStreamWriter xtw) throws Exception {
        writeEventDefinitions(flowNode, flowNode.getEventDefinitions(), model, xtw);
    }
}

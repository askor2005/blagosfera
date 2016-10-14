package ru.radom.blagosferabp.activiti.component.converters.xml;

import org.activiti.bpmn.converter.BaseBpmnXMLConverter;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.ServiceTask;

/**
 * Created by Otts Alexey on 06.11.2015.<br/>
 * Убирает лишние методы, так как они будут одинаковыми во всех реализациях
 */
public abstract class ExtendedXMLConverter extends BaseBpmnXMLConverter {
    @Override
    protected Class<? extends BaseElement> getBpmnElementType() {
        return ServiceTask.class;
    }

    @Override
    protected String getXMLElementName() {
        return ELEMENT_TASK_SERVICE;
    }
}

package ru.radom.blagosferabp.activiti.rabbit.bundle;

import org.activiti.bpmn.converter.BaseBpmnXMLConverter;
import org.activiti.engine.parse.BpmnParseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.component.ModelBundle;
import ru.radom.blagosferabp.activiti.component.converters.json.ExtendedBaseBpmnJsonConverter;
import ru.radom.blagosferabp.activiti.component.stencil.rules.StencilRule;
import ru.radom.blagosferabp.activiti.rabbit.RabbitTaskBpmnParseHandler;
import ru.radom.blagosferabp.activiti.rabbit.RabbitTaskJsonConverter;
import ru.radom.blagosferabp.activiti.rabbit.RabbitTaskXMLConverter;
import ru.radom.blagosferabp.activiti.stencil.exchange.Stencil;

import java.util.List;

/**
 * Created by Otts Alexey on 06.11.2015.<br/>
 * {@link ModelBundle} для {@link ru.radom.blagosferabp.activiti.rabbit.RabbitTask}
 */
@Component
public class RabbitTaskBundle implements ModelBundle {
    public final static String RABBIT_TASK_STENCIL = "rabbitTaskStencil";

    public final static String STENCIL_ENTITY_ID_NODE = "__stencil__entity__id__";

    public final static String TASK_TYPE = "rabbitTask";

    public final static String SINGLE_PROPERTY_SEND_TASK_ID = "_single_property_send_task_id_";

    @Autowired
    private RabbitTaskJsonConverter rabbitTaskJsonConverter;

    @Autowired
    private RabbitTaskXMLConverter rabbitTaskXMLConverter;

    @Autowired
    private RabbitTaskBpmnParseHandler rabbitTaskBpmnParseHandler;

    @Override
    public String getStencilId() {
        return RABBIT_TASK_STENCIL;
    }

    @Override
    public Stencil getStencil() {
        return null;
    }

    @Override
    public ExtendedBaseBpmnJsonConverter getJsonConverter() {
        return rabbitTaskJsonConverter;
    }

    @Override
    public BaseBpmnXMLConverter getXMLConverter() {
        return rabbitTaskXMLConverter;
    }

    @Override
    public BpmnParseHandler getBpmnParseHandler() {
        return rabbitTaskBpmnParseHandler;
    }

    @Override
    public List<StencilRule> getRequiredRules() {
        return null;
    }
}

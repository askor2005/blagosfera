package ru.radom.blagosferabp.activiti.rabbit;

import org.activiti.bpmn.constants.BpmnXMLConstants;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.handler.AbstractActivityBpmnParseHandler;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.radom.blagosfera.scripting.commons.ScriptData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Otts Alexey on 06.11.2015.<br/>
 * Парсит {@link RabbitTask} в {@link RabbitTaskBehaviour}
 */
@Component
public class RabbitTaskBpmnParseHandler extends AbstractActivityBpmnParseHandler<RabbitTask> {

    @Autowired
    private ApplicationContext applicationContext;

    //private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected Class<? extends BaseElement> getHandledType() {
        return RabbitTask.class;
    }

    @Override
    protected void executeParse(BpmnParse bpmnParse, RabbitTask element) {
        ActivityImpl activity = createActivityOnCurrentScope(bpmnParse, element, BpmnXMLConstants.ELEMENT_TASK_SERVICE);
        ExpressionManager expressionManager = bpmnParse.getExpressionManager();
        Map<String, Object> parameters = parseParameters(element.getCustomParameters(), expressionManager);

        RabbitTaskBehaviour behaviour =
            applicationContext.getBean(
                RabbitTaskBehaviour.class,
                element.getQueueToSend(),
                element.getAnswerType(),
                element.getResultVariableName(),
                parameters
            );

        activity.setActivityBehavior(behaviour);
    }

    private Object parseSingle(Object value, ExpressionManager expressionManager) {
        if(value instanceof String) {
            String s = (String) value;
            int i = s.indexOf("${");
            if(i != -1 && s.indexOf("}", i + 2) != -1) {
                value = expressionManager.createExpression(s);
            }
        } else if(value instanceof Map) {
            //noinspection unchecked
            value = parseParameters((Map<String, Object>) value, expressionManager);
        } else if(value instanceof Collection) {
            //noinspection unchecked
            value = parseCollection((Collection<Object>) value, expressionManager);
        } else if(value instanceof ScriptData) { //TODO сделать универсальный конвертер
            ScriptData res = new ScriptData();
            ScriptData data = (ScriptData) value;
            res.setScript(data.getScript());
            res.setOutputMapping(data.getOutputMapping());
            res.setContext(parseParameters(data.getContext(), expressionManager));
            value = res;
        }
        return value;
    }

    private Map<String, Object> parseParameters(Map<String, Object> paramsToResolve, ExpressionManager expressionManager) {
        if(paramsToResolve == null) {
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        for (Map.Entry<String, Object> entry : paramsToResolve.entrySet()) {
            Object value = entry.getValue();
            value = parseSingle(value, expressionManager);
            params.put(entry.getKey(), value);
        }
        return params;
    }

    @SuppressWarnings("unchecked")
    private Object parseCollection(Collection<Object> value, ExpressionManager expressionManager) {
        Collection<Object> res;
        try {
            res = value.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            res = new ArrayList<>();
        }
        for (Object c : value) {
            res.add(parseSingle(c, expressionManager));
        }
        return res;
    }


}

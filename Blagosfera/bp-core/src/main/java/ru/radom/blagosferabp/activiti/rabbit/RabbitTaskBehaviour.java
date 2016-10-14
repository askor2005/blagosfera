package ru.radom.blagosferabp.activiti.rabbit;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.behavior.TaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.radom.blagosfera.scripting.commons.ScriptData;
import ru.radom.blagosferabp.activiti.model.StencilEntity;

import java.util.*;

/**
 * Created by Otts Alexey on 05.11.2015.<br/>
 * Поведение для {@link RabbitTask}
 */
@Component
@Scope("prototype")
public class RabbitTaskBehaviour extends TaskActivityBehavior  implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    /**
     * Очередь, в которую отправляется сообщение, когда задача начинает выполняться
     */
    private String queueToSend;

    /**
     * Тип ответа:
     * <li>
     *     <ul>{@link StencilEntity#FIRE_AND_FORGET}</ul>
     *     <ul>{@link StencilEntity#IMMEDIATE_WAIT}</ul>
     *     <ul>{@link StencilEntity#WAIT_ANSWER_MESSAGE}</ul>
     * </li>
     */
    private String answerType;

    /**
     * Переменная в скоупе, в которую запишется результат
     */
    private String resultVariableName;

    /**
     * Не стандартные параметры (название -> значение)
     */
    private Map<String, Object> customParameters;

    /** */
    public RabbitTaskBehaviour(String queueToSend, String answerType, String resultVariableName, Map<String, Object> customParameters) {
        this.queueToSend = queueToSend;
        this.answerType = answerType;
        this.resultVariableName = resultVariableName;
        this.customParameters = customParameters;
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void execute(ActivityExecution execution) throws Exception {
        TaskEntity task = TaskEntity.createAndInsert(execution);
        task.setExecution(execution);
        if(resultVariableName != null) {
            task.setVariableLocal("__result_variable_name__", resultVariableName);
        }
        Object toSend;
        String singleProperty = (String) customParameters.get("_single_property_send_task_id_");
        if(singleProperty != null) {
            toSend = processParameter(execution, customParameters.get(singleProperty));
        } else {
            Map<String, Object> payload = new HashMap<>();
            for (Map.Entry<String, Object> entry : customParameters.entrySet()) {
                payload.put(entry.getKey(), processParameter(execution, entry.getValue()));
            }
            toSend = payload;
        }

        final Map<String, Object> parametersForTask;
        if (toSend != null && toSend instanceof Map) {
            parametersForTask = (Map)toSend;
        } else {
            parametersForTask = Collections.emptyMap();
        }

        BPMHandler bpmHandler = (BPMHandler)applicationContext.getBean(queueToSend);
        String taskId = task.getId();
        switch (answerType) {
            case StencilEntity.FIRE_AND_FORGET: //не ожидаем ответа
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        bpmHandler.handle(parametersForTask, taskId);
                    }
                });
                task.complete(null, false);
                break;
            case StencilEntity.IMMEDIATE_WAIT: //ожидаем ответа с блокировкой
                Object answer = bpmHandler.handle(parametersForTask, taskId);
                Map<String, Object> vars = null;
                if(resultVariableName != null) {
                    vars = new HashMap<>();
                    vars.put(resultVariableName, answer);
                    execution.setVariable(resultVariableName, answer);
                }
                task.complete(vars, false);
                break;
            case StencilEntity.WAIT_ANSWER_MESSAGE: //ждем сообщения о выполнении таска
            default:
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        bpmHandler.handle(parametersForTask, taskId);
                    }
                });
        }
    }

    @Override
    public void signal(ActivityExecution execution, String signalName, Object signalData) throws Exception {
        if (!((ExecutionEntity) execution).getTasks().isEmpty())
            throw new ActivitiException("RabbitTask should not be signalled before complete");
        leave(execution);
    }

    @SuppressWarnings("unchecked")
    private Object processParameter(ActivityExecution execution, Object param) {
        if(param instanceof Expression) {
            try {
                param = ((Expression) param).getValue(execution);
            } catch (ActivitiException ex) {
                param = null;
            }
        } else if(param instanceof Map) {
            param = processMap((Map) param, execution);
        } else if(param instanceof Collection) {
            param = processCollection((Collection) param, execution);
        } else if(param instanceof ScriptData) {
            ScriptData res = new ScriptData();
            ScriptData data = (ScriptData) param;
            res.setScript(data.getScript());
            res.setOutputMapping(data.getOutputMapping());
            res.setContext(processMap(data.getContext(), execution));
            param = res;
        }
        return param;
    }

    private Map<String, Object> processMap(Map<String, Object> paramsToResolve, ActivityExecution execution) {
        if(paramsToResolve == null) {
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        for (Map.Entry<String, Object> entry : paramsToResolve.entrySet()) {
            Object value = entry.getValue();
            value = processParameter(execution, value);
            params.put(entry.getKey(), value);
        }
        return params;
    }

    @SuppressWarnings("unchecked")
    private Object processCollection(Collection<Object> value, ActivityExecution execution) {
        Collection<Object> res;
        try {
            res = value.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            res = new ArrayList<>();
        }
        for (Object c : value) {
            res.add(processParameter(execution, c));
        }
        return res;
    }
}

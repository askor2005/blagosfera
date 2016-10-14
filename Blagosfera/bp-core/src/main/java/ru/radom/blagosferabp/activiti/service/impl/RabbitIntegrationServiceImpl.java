/*
package ru.radom.blagosferabp.activiti.service.impl;

import lombok.extern.log4j.Log4j2;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.radom.blagosferabp.activiti.BPMBlagosferaUtils;
import ru.radom.blagosferabp.activiti.service.RabbitIntegrationService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

*/
/**
 * Created by Otts Alexey on 05.11.2015.<br/>
 * Реализация для {@link RabbitIntegrationService}
 *//*

@Log4j2
@Service
public class RabbitIntegrationServiceImpl implements RabbitIntegrationService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = BPMBlagosferaUtils.FINISH_TASK_QUEUE)
    public void finishRabbitTaskWorker(Message message, @Header(BPMBlagosferaUtils.TASK_ID_HEADER_KEY) String taskId) {
        try {
            Object resultVariableName = taskService.getVariableLocal(taskId, "__result_variable_name__");
            Object value = rabbitTemplate.getMessageConverter().fromMessage(message);
            Map<String, Object> vars = null;
            if(value instanceof Map) {
                //noinspection unchecked
                vars = (Map<String, Object>) value;
            }
            if(resultVariableName != null) {
                vars = new HashMap<>(1);
                vars.put((String) resultVariableName, value);
            }
            taskService.complete(taskId, vars);
        } catch (ActivitiObjectNotFoundException e) {
            log.warn("Task '" + taskId + "' was requested to complete but does not found");
        } catch (ActivitiException e) {
            throw new AmqpRejectAndDontRequeueException("Error was occurred when process '" + taskId + "' task: " + e.getMessage(), e);
        } catch (Exception e) {
            //TODO понять что надо делать в такой ситации
            log.error(e.getMessage(), e);
        }
    }

    @RabbitListener(queues = BPMBlagosferaUtils.SIGNAL_QUEUE)
    public void signalActivitiWorker(@Payload Map payload, @Header(BPMBlagosferaUtils.SIGNAL_HEADER_KEY) String signalRef) {
        */
/*try {
            runtimeService.signalEventReceived(signalRef, payload);
        } catch (Exception e) {
            //TODO понять что надо делать в такой ситации
            log.error(e.getMessage(), e);
        }*//*

        System.err.println(new Date());
        runtimeService.signalEventReceived(signalRef, payload);
        System.err.println(new Date());
    }

}
*/

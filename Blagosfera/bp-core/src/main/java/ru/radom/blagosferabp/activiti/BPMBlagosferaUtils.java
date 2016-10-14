package ru.radom.blagosferabp.activiti;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Address;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.radom.blagosferabp.activiti.component.stencil.StencilSet;
import ru.radom.blagosferabp.activiti.service.DeploymentService;
import ru.radom.blagosferabp.activiti.service.StencilService;
import ru.radom.blagosferabp.activiti.stencil.exchange.StencilForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Otts Alexey on 05.11.2015.<br/>
 * Утили для взяимодействия с BPM через RabbitMQ
 */
@Service
@Transactional
public class BPMBlagosferaUtils {


    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private DeploymentService deploymentService;

    @Autowired
    private StencilService stencilService;

    /**
     * Ключ по которому в хедере сообщения лежит id задачи
     */
    /*public final static String TASK_ID_HEADER_KEY = "_TASK_ID_";

    *//**
     * Ключ по которому в хедере сообщения лежит наименование переменной, которую возвращает таск
     *//*
    public final static String TASK_RESULT_VAR_NAME_HEADER_KEY = "_TASK_RESULT_VAR_NAME_";

    *//**
     * Exchange через который осуществляется пересылка сообщений о начале работы задачи
     *//*
    public final static String START_TASK_EXCHANGE = "bp-rabbit-task-start-exchange";

    *//**
     * Exchange через который осуществляется пересылка сообщений о завершении работы задачи
     *//*
    public final static String FINISH_TASK_EXCHANGE = "bp-rabbit-task-finish-exchange";

    *//**
     * Очередь в которую нужно посылать сообщение
     *//*
    public final static String FINISH_TASK_QUEUE = "bp.rabbit.task.finish";

    *//**
     * Exchange взаимодейстия с BPM
     *//*
    public final static String BPM_EXCHANGE = "bp-rabbit-exchange";

    *//**
     * Очередь, в которую падают сигналы для движка BPM
     *//*
    public final static String SIGNAL_QUEUE = "bp.rabbit.signal";

    *//**
     * Ключ в хедере в котором находится название сигнала
     *//*
    public final static String SIGNAL_HEADER_KEY = "__SIGNAL__REF__";

    *//**
     * Развернуть модель
     *//*
    public static final String DEPLOY_MODEL = "bp.stencil.exchange.deploy";*/

    /**
     * Вызывает сигнал, который обрабатывается в BPM
     * @param signalRef         id задачи
     * @param pairs             пыра которые будут упакованы в Map и отправлены
     */
    public void raiseSignal(String signalRef, Object... pairs) {
        int length = pairs.length;
        if(length % 2 != 0) {
            throw new IllegalArgumentException("Pairs length must be even!");
        }
        Map<String, Object> payload = new HashMap<>(length / 2);
        for (int i = 0; i < length; i+=2) {
            Object key = pairs[i];
            if(!(key instanceof String)) {
                throw new IllegalArgumentException(Integer.toString(i + 1) + " argument must be String!");
            }
            payload.put(((String) key), pairs[i + 1]);
        }
        raiseSignal(signalRef, payload);
    }

    /**
     * Вызывает сигнал, который обрабатывается в BPM
     * @param signalRef         id задачи
     * @param payload           содержимое, которое хотим отправить
     */
    public void raiseSignal(String signalRef, Map<String, Object> payload) {
        runtimeService.signalEventReceived(signalRef, payload);
    }

    /**
     * Послать сообщение о завершении задачи
     * @param taskId            id задачи
     * @param payload           содержимое, которое хотим отправить
     */
    public void finishTask(String taskId, Object payload) {
        /*Message message;
        if(payload instanceof Message) {
            message = (Message) payload;
            message.getMessageProperties().setHeader(TASK_ID_HEADER_KEY, taskId);
        } else {
            message = packRabbitTaskMessage(rabbitTemplate, taskId, payload);
        }
        rabbitTemplate.send(FINISH_TASK_EXCHANGE, FINISH_TASK_QUEUE, message);*/
        Object resultVariableName = taskService.getVariableLocal(taskId, "__result_variable_name__");
        //Object value = rabbitTemplate.getMessageConverter().fromMessage(message);
        Map<String, Object> vars = null;
        if(payload instanceof Map) {
            //noinspection unchecked
            vars = (Map<String, Object>) payload;
        }
        if(resultVariableName != null) {
            vars = new HashMap<>(1);
            vars.put((String) resultVariableName, payload);
        }
        taskService.complete(taskId, vars);
    }

    /**
     * Упоковать сообщение для RabbitTask
     * @param rabbitTemplate    шаблон, через который собираемся пересылать
     * @param taskId            id задачи
     * @param payload           содержимое, которое хотим отправить
     */
    /*public static Message packRabbitTaskMessage(RabbitTemplate rabbitTemplate, String taskId, Object payload) {
        MessageConverter converter = rabbitTemplate.getMessageConverter();
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader(TASK_ID_HEADER_KEY, taskId);
        return converter.toMessage(payload, messageProperties);
    }

    *//**
     * Универсальная обертка для задач.
     * При обработке сообщения проверяются хедеры replyTo и {@link #TASK_ID_HEADER_KEY}, и посылается ответ в нужные очереди.<br/>
     * @param rabbitTemplate    шаблон через который будем отправлять ответ
     * @param message           сообщение, которое обрабатываем
     * @param fun               функция обработчик задачи
     *//*
    public static <T, U> void commonRabbitTaskExecutorWithConverter(RabbitTemplate rabbitTemplate, Message message, Function<T, U> fun) {
        commonRabbitTaskExecutor(rabbitTemplate, message, (msg) -> {
            T converted = (T) rabbitTemplate.getMessageConverter().fromMessage(msg);
            return fun.apply(converted);
        });
    }

    *//**
     * Универсальная обертка для задач.
     * При обработке сообщения проверяются хедеры replyTo и {@link #TASK_ID_HEADER_KEY}, и посылается ответ в нужные очереди.<br/>
     * @param rabbitTemplate    шаблон через который будем отправлять ответ
     * @param message           сообщение, которое обрабатываем
     * @param fun               функция обработчик задачи
     *//*
    public static <U> void commonRabbitTaskExecutor(RabbitTemplate rabbitTemplate, Message message, Function<Message, U> fun) {

        try {
            U result = fun.apply(message);
            if(TransactionSynchronizationManager.isActualTransactionActive() && !TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
                answerAfterTransaction(rabbitTemplate, message, result);
            } else {
                simpleAnswer(rabbitTemplate, message, result);
            }
        } catch (Exception e) {
            //TODO что то надо делать при возникновении исключения
            throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
        }

    }

    private static <U> void answerAfterTransaction(final RabbitTemplate rabbitTemplate, final Message message, final U result) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCompletion(int status) {
                simpleAnswer(rabbitTemplate, message, result);
            }
        });
    }

    private static <U> void simpleAnswer(RabbitTemplate rabbitTemplate, Message message, U result) {
        Address replyToAddress = message.getMessageProperties().getReplyToAddress();
        String taskId = (String) message.getMessageProperties().getHeaders().get(TASK_ID_HEADER_KEY);
        if (replyToAddress != null || taskId != null) {
            Message reply = getMessage(rabbitTemplate, result);
            if (taskId != null) {
                BPMBlagosferaUtils.finishTask(rabbitTemplate, taskId, MessageBuilder.fromMessage(reply).build());
            }
            if (replyToAddress != null) {
                rabbitTemplate.send(replyToAddress.getExchangeName(), replyToAddress.getRoutingKey(), reply);
            }
        }
    }

    private static Message getMessage(RabbitTemplate rabbitTemplate, Object result) {
        Message reply;
        if(result != null) {
            reply = rabbitTemplate.getMessageConverter().toMessage(result, null);
        } else {
            reply = MessageBuilder.withBody(new byte[]{}).build();
        }
        return reply;
    }*/

    public void deployModelWorker(String modelSource) {
        deploymentService.deployModel(modelSource);
    }

    public StencilForm createStencil(StencilForm stencil) {
        return stencilService.createStencil(stencil);
    }

    public StencilForm updateStencil(StencilForm stencil) {
        return stencilService.updateStencil(stencil);
    }

    public void deleteStencil(String stencilId) {
        stencilService.deleteStencil(stencilId);
    }

    public StencilSet getStencilSet(){
        return stencilService.getStencilSet();
    }

    public List<StencilForm> getCustomStencils() {
        return stencilService.getCustomStencils();
    }
}

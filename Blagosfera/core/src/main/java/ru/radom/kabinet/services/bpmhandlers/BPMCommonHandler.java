package ru.radom.kabinet.services.bpmhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.bpmhandlers.user.BPMUserMessageHandler;
import ru.radom.kabinet.utils.SpringUtils;
import ru.radom.kabinet.utils.VarUtils;

import java.util.Map;

/**
 * Общий обработчик тасков.
 * Действия которые должны выполняться транзакциооно должны быть выполнены одним таском!
 * Created by vgusev on 29.07.2016.
 */
@Service
@Transactional
public class BPMCommonHandler {

    private static final Logger logger = LoggerFactory.getLogger(BPMUserMessageHandler.class);

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /*@Autowired
    private SerializeService serializeService;*/

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    /*@RabbitListener(queues = "core.common.queue")
    @Transactional
    public Object commonHandler(Message message*//*,
                              @Header(value = BPMBlagosferaUtils.TASK_ID_HEADER_KEY) String taskId*//*) {
        Map<String, Object> data = (Map<String, Object>) rabbitTemplate.getMessageConverter().fromMessage(message);
        Object result = null;
        try {
            BPMHandler bpmHandler = SpringUtils.getBean((String)data.get("handlerName"));
            logger.info("Запуск таска " + data.get("handlerName"));
            result = bpmHandler.handle(data);
        } catch (Exception e) {
            if (data.containsKey("userIdForErrorMessage")) {
                Long userId = VarUtils.getLong(String.valueOf(data.get("userIdForErrorMessage")), null);
                if (userId != null) {
                    // Отправить сигнал пользователю
                }
            }
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            *//*if (taskId != null) {
                if (result == null) {
                    result = Collections.emptyMap();
                }
                blagosferaEventPublisher.publishEvent(new BpmFinishTaskEvent(this, taskId, result));
            }*//*
        }
        return result;
    }*/

}

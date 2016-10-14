package ru.radom.kabinet.services.bpmhandlers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.user.UserMessageEvent;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.user.dto.BPMUserMessageDto;
import ru.radom.kabinet.services.sharer.UserDataService;

import java.util.Map;

/**
 *
 * Created by vgusev on 29.07.2016.
 */
@Service("userMessageHandler")
@Transactional
public class BPMUserMessageHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    @Autowired
    private UserDataService userDataService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMUserMessageDto bpmUserMessage = serializeService.toObject(parameters, BPMUserMessageDto.class);
        return handlerMessage(bpmUserMessage);
    }

    public Object handlerMessage(BPMUserMessageDto bpmUserMessage) {
        User user = userDataService.getByIdFullData(bpmUserMessage.getUserId());
        UserMessageEvent event = new UserMessageEvent(this, bpmUserMessage.getParameters(), bpmUserMessage.getContent(), user);
        blagosferaEventPublisher.publishEvent(event);
        return serializeService.toPrimitiveObject(bpmUserMessage);
    }
}

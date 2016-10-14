package ru.radom.kabinet.services.bpmhandlers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.user.dto.BPMUserMessageDto;
import ru.radom.kabinet.services.bpmhandlers.user.dto.BPMUsersMessageDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 29.07.2016.
 */
@Service("usersMessageHandler")
@Transactional
public class BPMUsersMessageHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private BPMUserMessageHandler bpmUserMessageHandler;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        List<Object> result = new ArrayList<>();
        BPMUsersMessageDto bpmUsersMessageDto = serializeService.toObject(parameters, BPMUsersMessageDto.class);
        for (Long userId : bpmUsersMessageDto.getUserIds()) {
            BPMUserMessageDto bpmUserMessageDto = new BPMUserMessageDto();
            bpmUserMessageDto.setUserId(userId);
            bpmUserMessageDto.setContent(bpmUsersMessageDto.getContent());
            bpmUserMessageDto.setParameters(bpmUsersMessageDto.getParameters());

            result.add(bpmUserMessageHandler.handlerMessage(bpmUserMessageDto));
        }
        return result;
    }
}

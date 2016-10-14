package ru.radom.kabinet.services.bpmhandlers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.user.dto.BPMUserWebSocketDto;
import ru.radom.kabinet.services.bpmhandlers.user.dto.BPMUsersWebSocketDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 05.08.2016.
 */
@Service("usersWebSocketHandler")
@Transactional
public class BPMUsersWebSocketHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private BPMUserWebSocketHandler bpmUserWebSocketHandler;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        List<Object> result = new ArrayList<>();
        BPMUsersWebSocketDto bpmUsersWebSocketDto = serializeService.toObject(parameters, BPMUsersWebSocketDto.class);
        for (Long userId : bpmUsersWebSocketDto.getUserIds()) {
            BPMUserWebSocketDto bpmUserWebSocketDto = new BPMUserWebSocketDto();
            bpmUserWebSocketDto.setUserId(userId);
            bpmUserWebSocketDto.setEventType(bpmUsersWebSocketDto.getEventType());
            bpmUserWebSocketDto.setParameters(bpmUsersWebSocketDto.getParameters());

            result.add(bpmUserWebSocketHandler.handleDto(bpmUserWebSocketDto));
        }
        return result;
    }

}

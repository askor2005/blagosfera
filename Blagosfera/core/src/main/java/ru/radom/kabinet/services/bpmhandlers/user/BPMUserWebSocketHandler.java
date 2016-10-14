package ru.radom.kabinet.services.bpmhandlers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.StompService;
import ru.radom.kabinet.services.bpmhandlers.user.dto.BPMUserWebSocketDto;
import ru.radom.kabinet.services.sharer.UserDataService;

import java.util.Map;

/**
 *
 * Created by vgusev on 05.08.2016.
 */
@Service("userWebSocketHandler")
@Transactional
public class BPMUserWebSocketHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private StompService stompService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMUserWebSocketDto bpmUserWebSocketDto = serializeService.toObject(parameters, BPMUserWebSocketDto.class);
        return handleDto(bpmUserWebSocketDto);
    }

    public Object handleDto(BPMUserWebSocketDto bpmUserWebSocketDto) {
        User user = userDataService.getByIdMinData(bpmUserWebSocketDto.getUserId());
        stompService.send(user.getEmail(), bpmUserWebSocketDto.getEventType(), bpmUserWebSocketDto.getParameters());

        return serializeService.toPrimitiveObject(user);
    }
}

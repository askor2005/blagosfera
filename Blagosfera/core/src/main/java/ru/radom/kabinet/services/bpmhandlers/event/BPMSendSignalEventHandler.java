package ru.radom.kabinet.services.bpmhandlers.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.bpm.BpmRaiseSignalEvent;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.event.dto.BPMSendSignalEventDto;

import java.util.Map;

/**
 *
 * Created by vgusev on 05.08.2016.
 */
@Service("sendSignalEventHandler")
@Transactional
public class BPMSendSignalEventHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMSendSignalEventDto bpmSendSignalEventDto = serializeService.toObject(parameters, BPMSendSignalEventDto.class);

        Map<String, Object> payload = bpmSendSignalEventDto.getParameters();
        BpmRaiseSignalEvent bpmRaiseSignalEvent = new BpmRaiseSignalEvent(
                this,
                bpmSendSignalEventDto.getSignalId(),
                payload
        );
        blagosferaEventPublisher.publishEvent(bpmRaiseSignalEvent);
        return null;
    }
}
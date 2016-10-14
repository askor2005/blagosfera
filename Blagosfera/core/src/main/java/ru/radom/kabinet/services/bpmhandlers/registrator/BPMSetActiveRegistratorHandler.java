package ru.radom.kabinet.services.bpmhandlers.registrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.registrator.dto.BPMSetActiveRegistratorHandlerDto;
import ru.radom.kabinet.services.registration.RegistratorService;

import java.util.Map;

/**
 *
 * Created by vgusev on 03.08.2016.
 */
@Service("setActiveRegistratorHandler")
@Transactional
public class BPMSetActiveRegistratorHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private RegistratorService registratorService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMSetActiveRegistratorHandlerDto bpmSetActiveRegistratorHandlerDto = serializeService.toObject(parameters, BPMSetActiveRegistratorHandlerDto.class);
        registratorService.setActiveRegistrator(bpmSetActiveRegistratorHandlerDto.getRegistratorId(), bpmSetActiveRegistratorHandlerDto.isActive());
        return bpmSetActiveRegistratorHandlerDto.isActive();
    }
}
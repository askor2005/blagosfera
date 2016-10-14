package ru.radom.kabinet.services.bpmhandlers.registrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.SharerService;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.registrator.dto.BPMRegistratorNeedFillFieldsDto;

import java.util.Map;

/**
 *
 * Created by vgusev on 03.08.2016.
 */
@Service("registratorNeedFillFieldsHandler")
@Transactional
public class BPMRegistratorNeedFillFieldsHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private SharerService sharerService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMRegistratorNeedFillFieldsDto bpmRegistratorNeedFillFields = serializeService.toObject(parameters, BPMRegistratorNeedFillFieldsDto.class);
        return sharerService.isFillRegistratorFields(bpmRegistratorNeedFillFields.getRegistratorId());
    }
}
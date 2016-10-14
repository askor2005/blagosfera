package ru.radom.kabinet.services.bpmhandlers.document;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.document.services.DocumentService;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.document.dto.BPMSetActiveDocumentDto;

import java.util.Map;

/**
 *
 * Created by vgusev on 02.08.2016.
 */
@Service("setActiveDocumentHandler")
@Transactional
public class BPMSetActiveDocumentHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private DocumentService documentService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMSetActiveDocumentDto bpmSetActiveDocument = serializeService.toObject(parameters, BPMSetActiveDocumentDto.class);
        boolean isActive = BooleanUtils.toBooleanDefaultIfNull(bpmSetActiveDocument.getActive(), false);
        documentService.setActiveDocument(bpmSetActiveDocument.getDocumentId(), isActive);
        return true;
    }
}
package ru.radom.kabinet.services.bpmhandlers.document;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.document.DocumentFolder;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.document.services.DocumentFolderDataService;
import ru.radom.kabinet.document.services.DocumentService;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.document.dto.BPMSetActiveDocumentFolderDto;

import java.util.Map;

/**
 *
 * Created by vgusev on 02.08.2016.
 */
@Service("setActiveDocumentFolderHandler")
@Transactional
public class BPMSetActiveDocumentFolderHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private DocumentFolderDataService documentFolderDataService;

    @Autowired
    private DocumentService documentService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMSetActiveDocumentFolderDto bpmSetActiveDocumentFolder = serializeService.toObject(parameters, BPMSetActiveDocumentFolderDto.class);
        DocumentFolder documentFolder = documentFolderDataService.getById(bpmSetActiveDocumentFolder.getDocumentFolderId());
        boolean isActive = BooleanUtils.toBooleanDefaultIfNull(bpmSetActiveDocumentFolder.getActive(), false);
        Map<String, Object> result = null;
        if (documentFolder != null && documentFolder.getDocuments() != null) {
            for (Document document : documentFolder.getDocuments()) {
                documentService.setActiveDocument(document.getId(), isActive);
            }
            result = serializeService.toPrimitiveObject(documentFolder);
        }
        return result;
    }
}
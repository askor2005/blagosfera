package ru.radom.kabinet.services.bpmhandlers.document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.document.DocumentFolder;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateSetting;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateSettingCustomSourceHandler;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.document.services.DocumentFolderDataService;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.document.dto.BPMCreateDocumentsFromSettingsDto;
import ru.radom.kabinet.services.document.DocumentTemplateSettingService;
import ru.radom.kabinet.utils.SpringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * Created by vgusev on 09.08.2016.
 */
@Service("createDocumentsFromSettingsHandler")
@Transactional
public class BPMCreateDocumentsFromSettingsHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private DocumentFolderDataService documentFolderDataService;

    @Autowired
    private DocumentTemplateSettingService documentTemplateSettingService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMCreateDocumentsFromSettingsDto bpmCreateDocumentsFromSettingsDto  = serializeService.toObject(parameters, BPMCreateDocumentsFromSettingsDto.class);

        Object[] params;
        if (bpmCreateDocumentsFromSettingsDto.getCustomSourceParameters() != null) {
            params = new Object[bpmCreateDocumentsFromSettingsDto.getCustomSourceParameters().size()];
            for (Integer position : bpmCreateDocumentsFromSettingsDto.getCustomSourceParameters().keySet()) {
                params[position] = bpmCreateDocumentsFromSettingsDto.getCustomSourceParameters().get(position);
            }
            //params = bpmCreateDocumentsFromSettingsDto.getCustomSourceParameters().toArray(new Object[bpmCreateDocumentsFromSettingsDto.getCustomSourceParameters().size()]);
        } else {
            params = new Object[]{};
        }
        DocumentTemplateSettingCustomSourceHandler customSourceHandler = SpringUtils.getBean(bpmCreateDocumentsFromSettingsDto.getCustomSourceHandler(), params);

        List<DocumentTemplateSetting> documentTemplateSettings = documentTemplateSettingService.getByIds(bpmCreateDocumentsFromSettingsDto.getSettingsIds());
        DocumentFolder documentFolder = null;
        if (documentTemplateSettings != null && !documentTemplateSettings.isEmpty()) {

            Set<Document> documents = new HashSet<>();

            for (DocumentTemplateSetting documentTemplateSetting : documentTemplateSettings) {
                Document document = documentTemplateSettingService.createDocument(
                        documentTemplateSetting,
                        bpmCreateDocumentsFromSettingsDto.getCreatorId(),
                        customSourceHandler,
                        bpmCreateDocumentsFromSettingsDto.getDocumentParameters()
                );
                documents.add(document);
            }

            documentFolder = new DocumentFolder();
            documentFolder.setDescription("");
            documentFolder.setName("");
            documentFolder.setDocuments(documents);
            documentFolder = documentFolderDataService.save(documentFolder);
        }

        return serializeService.toPrimitiveObject(documentFolder);
    }
}
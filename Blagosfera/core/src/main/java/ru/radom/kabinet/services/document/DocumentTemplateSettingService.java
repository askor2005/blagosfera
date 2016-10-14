package ru.radom.kabinet.services.document;

import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateSetting;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateSettingCustomSourceHandler;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 15.07.2016.
 */
public interface DocumentTemplateSettingService {

    /**
     * Загрузить по ИД
     * @param id
     * @return
     */
    DocumentTemplateSetting getById(Long id);

    /**
     * Загрузить по списку ИД
     * @param ids
     * @return
     */
    List<DocumentTemplateSetting> getByIds(List<Long> ids);

    /**
     * Создать документ на основе параметров
     * @param documentTemplateSetting
     * @param documentTemplateSettingCustomSourceHandler
     * @param documentParameters
     * @return
     */
    Document createDocument(DocumentTemplateSetting documentTemplateSetting, Long creatorId, DocumentTemplateSettingCustomSourceHandler documentTemplateSettingCustomSourceHandler, Map<String, String> documentParameters);

    /**
     * 
     * @param documentTemplateSetting
     * @return
     */
    DocumentTemplateSetting save(DocumentTemplateSetting documentTemplateSetting);
}

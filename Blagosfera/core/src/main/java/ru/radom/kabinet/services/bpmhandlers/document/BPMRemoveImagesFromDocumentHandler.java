package ru.radom.kabinet.services.bpmhandlers.document;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.document.Document;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.document.services.DocumentDomainService;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.document.dto.BPMRemoveImagesFromDocumentDto;

import java.util.Map;

/**
 *
 * Created by vgusev on 15.08.2016.
 */
@Service("removeImagesFromDocumentHandler")
@Transactional
public class BPMRemoveImagesFromDocumentHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private DocumentDomainService documentDomainService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMRemoveImagesFromDocumentDto bpmRemoveImagesFromDocumentDto = serializeService.toObject(parameters, BPMRemoveImagesFromDocumentDto.class);
        Document document = documentDomainService.getById(bpmRemoveImagesFromDocumentDto.documentId);

        org.jsoup.nodes.Document doc = Jsoup.parse(document.getContent());
        doc.select("img").remove();
        document.setContent(doc.body().html());

        document = documentDomainService.save(document);
        return serializeService.toPrimitiveObject(document);
    }
}
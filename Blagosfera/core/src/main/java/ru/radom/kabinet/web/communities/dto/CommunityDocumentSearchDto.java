package ru.radom.kabinet.web.communities.dto;

import ru.askor.blagosfera.domain.document.Document;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 17.06.2016.
 */
public class CommunityDocumentSearchDto {

    public Long id;

    public String fileLink;

    public String documentName;

    public CommunityDocumentSearchDto(Document document) {
        id = document.getId();
        fileLink = "/document/service/exportDocumentToPdf?document_id=" + document.getId();
        documentName = document.getName();
    }

    public static List<CommunityDocumentSearchDto> toListDto(List<Document> documents) {
        List<CommunityDocumentSearchDto> result = null;
        if (documents != null) {
            result = new ArrayList<>();
            for (Document document : documents) {
                result.add(new CommunityDocumentSearchDto(document));
            }
        }
        return result;
    }
}

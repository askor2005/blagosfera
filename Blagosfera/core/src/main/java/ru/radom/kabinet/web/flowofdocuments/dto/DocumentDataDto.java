package ru.radom.kabinet.web.flowofdocuments.dto;

import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.document.DocumentParticipant;

import java.util.List;

/**
 *
 * Created by vgusev on 01.06.2016.
 */
public class DocumentDataDto {

    public Long id;
    public String hashCode;
    public String name;
    public boolean isSigned;

    public static DocumentDataDto toDto(Document document, boolean isSigned) {
        DocumentDataDto result = null;
        if (document != null) {
            result = new DocumentDataDto();
            result.id = document.getId();
            result.hashCode = document.getHashCode();
            result.name = document.getName();
            result.isSigned = isSigned;
        }
        return result;
    }

}

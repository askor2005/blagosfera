package ru.radom.kabinet.web.communities.dto;

import ru.askor.blagosfera.domain.document.Document;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 23.07.2016.
 */
public class DocumentFromCommunityRequestDto {

    public Long id;

    public String hashCode;

    public String name;

    public boolean documentActive;

    public boolean signed;

    public boolean canUnsignDocument;

    public DocumentFromCommunityRequestDto(Document document, boolean isDocumentSigned) {
        id = document.getId();
        name = document.getName();
        hashCode = document.getHashCode();
        documentActive = document.isActive();
        canUnsignDocument = document.isCanUnsignDocument();
        signed = isDocumentSigned;
    }
}

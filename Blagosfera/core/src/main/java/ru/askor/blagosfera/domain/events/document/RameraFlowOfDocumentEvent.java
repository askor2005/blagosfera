package ru.askor.blagosfera.domain.events.document;

import lombok.Getter;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.document.DocumentCreator;
import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.askor.blagosfera.domain.user.User;

/**
 *
 * Created by vgusev on 26.06.2015.
 */
@Getter
public class RameraFlowOfDocumentEvent extends BlagosferaEvent {

    private User user;

    private DocumentCreator creator;

    private Document document;

    private RameraFlowOfDocumentEventType documentEventType;

    private String link;

    public RameraFlowOfDocumentEvent(Object source, DocumentCreator creator, User user, Document document, RameraFlowOfDocumentEventType documentEventType, String link) {
        super(source);
        this.creator = creator;
        this.user = user;
        this.document = document;
        this.documentEventType = documentEventType;
        this.link = link;
    }
}

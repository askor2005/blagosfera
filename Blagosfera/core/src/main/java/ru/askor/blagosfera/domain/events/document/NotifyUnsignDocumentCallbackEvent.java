package ru.askor.blagosfera.domain.events.document;

import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.events.BlagosferaEvent;

public class NotifyUnsignDocumentCallbackEvent extends BlagosferaEvent {

    private Document document;
    private long userId;
    private NotifyUnsignDocumentCallback callback;

    public NotifyUnsignDocumentCallbackEvent(Object source, Document document, long userId, NotifyUnsignDocumentCallback callback) {
        super(source);
        this.document = document;
        this.userId = userId;
        this.callback = callback;
    }

    public void doCallback() {
        callback.notifyUnsignDocument(document, userId);
    }
}

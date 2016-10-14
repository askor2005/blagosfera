package ru.askor.blagosfera.domain.events.document;

import ru.askor.blagosfera.domain.document.Document;

public interface NotifyUnsignDocumentCallback {

    void notifyUnsignDocument(Document document, Long userId);
}

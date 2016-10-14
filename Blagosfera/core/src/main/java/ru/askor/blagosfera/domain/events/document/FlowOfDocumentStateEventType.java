package ru.askor.blagosfera.domain.events.document;

public enum FlowOfDocumentStateEventType {

    DOCUMENT_SIGNED("DOCUMENT_SIGNED"), // Документ подписан
    DOCUMENT_UNSIGNED("DOCUMENT_UNSIGNED");

    private String typeName = null;

    FlowOfDocumentStateEventType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}

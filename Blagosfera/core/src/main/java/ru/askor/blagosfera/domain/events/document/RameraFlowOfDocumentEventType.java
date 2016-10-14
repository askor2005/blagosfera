package ru.askor.blagosfera.domain.events.document;

/**
 * События документооборота
 * Created by vgusev on 26.06.2015.
 */
public enum RameraFlowOfDocumentEventType {
    FILL_USER_FIELDS,
    SIGN_DOCUMENT,
    ALL_SIGNED,
    UNSIGN_DOCUMENT
}

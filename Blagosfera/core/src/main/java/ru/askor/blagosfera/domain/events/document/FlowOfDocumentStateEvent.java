package ru.askor.blagosfera.domain.events.document;

import lombok.Getter;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.events.BlagosferaEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Событие изменения состояния документа.
 * Created by vgusev on 11.08.2015.
 */
@Getter
public class FlowOfDocumentStateEvent extends BlagosferaEvent {

    private FlowOfDocumentStateEventType stateEventType;

    private Map<String, String> parameters = new HashMap<>();

    private Document document;

    public FlowOfDocumentStateEvent(Object source, Map<String, String> parameters, FlowOfDocumentStateEventType stateEventType) {
        super(source);
        this.parameters = parameters;
        this.stateEventType = stateEventType;
    }

    public FlowOfDocumentStateEvent(Object source, Map<String, String> parameters, FlowOfDocumentStateEventType stateEventType, Document document) {
        super(source);
        this.stateEventType = stateEventType;
        this.parameters = parameters;
        this.document = document;
    }
}

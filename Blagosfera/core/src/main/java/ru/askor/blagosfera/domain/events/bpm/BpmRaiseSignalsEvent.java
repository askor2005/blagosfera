package ru.askor.blagosfera.domain.events.bpm;

import ru.askor.blagosfera.domain.events.BlagosferaEvent;

import java.util.ArrayList;
import java.util.List;

public class BpmRaiseSignalsEvent extends BlagosferaEvent {

    private List<BpmRaiseSignalEvent> events = new ArrayList<>();

    public BpmRaiseSignalsEvent(Object source) {
        super(source);
    }

    public List<BpmRaiseSignalEvent> getEvents() {
        return events;
    }
}

package ru.askor.blagosfera.domain.events.bpm;

import ru.askor.blagosfera.domain.events.BlagosferaEvent;

public class BpmFinishTaskEvent extends BlagosferaEvent {

    private String taskId;
    private Object payload;

    public BpmFinishTaskEvent(Object source, String taskId, Object payload) {
        super(source);
        this.taskId = taskId;
        this.payload = payload;
    }

    public String getTaskId() {
        return taskId;
    }

    public Object getPayload() {
        return payload;
    }
}

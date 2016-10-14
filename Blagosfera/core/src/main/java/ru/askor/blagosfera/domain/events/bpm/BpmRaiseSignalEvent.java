package ru.askor.blagosfera.domain.events.bpm;

import ru.askor.blagosfera.domain.events.BlagosferaEvent;

import java.util.HashMap;
import java.util.Map;

public class BpmRaiseSignalEvent extends BlagosferaEvent {

    private String signal;
    private Map<String, Object> payload;

    public BpmRaiseSignalEvent(Object source, String signal, Map<String, Object> payload) {
        super(source);
        this.signal = signal;
        this.payload = payload;
    }

    public BpmRaiseSignalEvent(Object source, String signal, Object... pairs) {
        super(source);
        this.signal = signal;

        int length = pairs.length;
        if(length % 2 != 0) {
            throw new IllegalArgumentException("Pairs length must be even!");
        }
        Map<String, Object> payload = new HashMap<>(length / 2);
        for (int i = 0; i < length; i+=2) {
            Object key = pairs[i];
            if(!(key instanceof String)) {
                throw new IllegalArgumentException(Integer.toString(i + 1) + " argument must be String!");
            }
            payload.put(((String) key), pairs[i + 1]);
        }

        this.payload = payload;
    }



    public String getSignal() {
        return signal;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }
}

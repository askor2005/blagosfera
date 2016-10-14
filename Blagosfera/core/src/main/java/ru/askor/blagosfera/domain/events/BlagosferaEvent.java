package ru.askor.blagosfera.domain.events;

import org.springframework.context.ApplicationEvent;

/**
 * @author dfilinberg
 */
public class BlagosferaEvent extends ApplicationEvent {

    public BlagosferaEvent(Object source) {
        super(source);
    }
}

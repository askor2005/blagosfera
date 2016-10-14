package ru.askor.blagosfera.domain.events.user;

import lombok.Getter;
import ru.askor.blagosfera.domain.contacts.Contact;
import ru.askor.blagosfera.domain.events.BlagosferaEvent;

/**
 * @author dfilinberg
 */
@Getter
public class ContactEvent extends BlagosferaEvent {

    private final ContactEventType type;
    private final Contact contact;

    public ContactEvent(Object source, ContactEventType type, Contact contact) {
        super(source);

        this.type = type;
        this.contact = contact;
    }
}

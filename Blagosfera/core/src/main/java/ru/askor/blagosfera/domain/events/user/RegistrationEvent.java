package ru.askor.blagosfera.domain.events.user;

import lombok.Getter;
import ru.askor.blagosfera.domain.Verifiable;
import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.askor.blagosfera.domain.user.User;

/**
 * @author vzuev
 */
@Getter
public class RegistrationEvent extends BlagosferaEvent {

    private final RegistrationEventType type;
    private final Verifiable object;
    private final User registrator;
    private final String comment;

    public RegistrationEvent(final Object source, final RegistrationEventType type, final Verifiable object, final User registrator) {

        this(source, type, object, registrator, null);
    }

    public RegistrationEvent(final Object source, final RegistrationEventType type, final Verifiable object,
                             final User registrator, final String comment) {
        super(source);

        this.type = type;
        this.object = object;
        this.registrator = registrator;
        this.comment = comment;
    }

}

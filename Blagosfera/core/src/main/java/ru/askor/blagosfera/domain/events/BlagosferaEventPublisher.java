package ru.askor.blagosfera.domain.events;

import org.springframework.context.ApplicationEventPublisherAware;
import ru.askor.voting.business.event.VotingEvent;

/**
 * Created by Maxim Nikitin on 18.03.2016.
 */
public interface BlagosferaEventPublisher extends ApplicationEventPublisherAware {

    void publishEvent(BlagosferaEvent event);

    void publishEvent(VotingEvent event);
}

package ru.askor.blagosfera.domain.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.askor.voting.business.event.VotingEvent;

/**
 * Created by Maxim Nikitin on 18.03.2016.
 */
@Component("blagosferaEventPublisher")
public class BlagosferaEventPublisherImpl implements BlagosferaEventPublisher {

    private ApplicationEventPublisher applicationEventPublisher;

    public BlagosferaEventPublisherImpl() {
    }

    @Override
    public void publishEvent(BlagosferaEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publishEvent(VotingEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}

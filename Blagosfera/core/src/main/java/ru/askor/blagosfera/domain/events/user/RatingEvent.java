package ru.askor.blagosfera.domain.events.user;

import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.radom.kabinet.model.rating.Rating;

/**
 * @author vzuev
 */
public class RatingEvent extends BlagosferaEvent {

    private final Rating rating;
    private final Number count;

    public RatingEvent(final Object source, final Rating rating, final Number count) {
        super(source);

        this.rating = rating;
        this.count = count;
    }

    public Rating getRating() {
        return rating;
    }

    public Number getCount() {
        return count;
    }
}

package ru.askor.blagosfera.domain.events.user;

import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.services.ProfileFilling;

public class DeletionNotificationSharerEvent extends BlagosferaEvent {

    private User user;
    private ProfileFilling profileFilling;

    public DeletionNotificationSharerEvent(Object source, User user, ProfileFilling profileFilling) {
        super(source);
        this.user = user;
        this.profileFilling = profileFilling;
    }

    public User getUser() {
        return user;
    }

    public ProfileFilling getProfileFilling() {
        return profileFilling;
    }

}

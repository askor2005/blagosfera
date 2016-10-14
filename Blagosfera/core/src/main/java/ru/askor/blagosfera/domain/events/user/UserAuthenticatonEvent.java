package ru.askor.blagosfera.domain.events.user;

import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.askor.blagosfera.domain.sessions.UserSession;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;

/**
 * Created by ebelyaev on 12.08.2015.
 */
public class UserAuthenticatonEvent extends BlagosferaEvent {

    private UserSession userSession;
    private UserDetailsImpl userDetails;
    private UserAuthenticatonEventType eventType;

    public UserAuthenticatonEvent(Object source, UserSession userSession, UserDetailsImpl userDetails, UserAuthenticatonEventType eventType) {
        super(source);
        this.userSession = userSession;
        this.userDetails = userDetails;
        this.eventType = eventType;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public UserDetailsImpl getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetailsImpl userDetails) {
        this.userDetails = userDetails;
    }

    public UserAuthenticatonEventType getEventType() {
        return eventType;
    }

    public void setEventType(UserAuthenticatonEventType eventType) {
        this.eventType = eventType;
    }
}

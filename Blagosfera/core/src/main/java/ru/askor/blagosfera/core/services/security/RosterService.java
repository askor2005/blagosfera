package ru.askor.blagosfera.core.services.security;

import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import ru.askor.blagosfera.domain.events.user.UserAuthenticatonEvent;

public interface RosterService {

    void onSessionConnectedEvent(SessionConnectedEvent event);

    void onHttpSessionDestroyedEvent(HttpSessionDestroyedEvent event);

    void onUserAuthenticatonEvent(UserAuthenticatonEvent event);

    boolean isUserOnline(String username);

    boolean isUserAuthenticated(String username);
}

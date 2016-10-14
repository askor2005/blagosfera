package ru.radom.kabinet.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;

public class ConcurrentSessionControlStrategy extends ConcurrentSessionControlAuthenticationStrategy {

    public ConcurrentSessionControlStrategy(SessionRegistry sessionRegistry) {
        super(sessionRegistry);
    }

    @Override
    protected int getMaximumSessionsForThisUser(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getUser().isAllowMultipleSessions() ? -1 : 1;
    }
}

package ru.radom.kabinet.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;

/**
 *
 * Created by Maxim Nikitin on 23.03.2016.
 */
public class SecurityUtils {

    private SecurityUtils() {
    }

    public static UserDetailsImpl getUserDetails() {
        SecurityContext securityContext = SecurityContextHolder.getContext();

        if (securityContext != null && securityContext.getAuthentication() != null && securityContext.getAuthentication().getPrincipal() != null) {
            Object principal = securityContext.getAuthentication().getPrincipal();
            return principal instanceof UserDetailsImpl ? (UserDetailsImpl) principal : null;
        }

        return null;
    }

    public static User getUser() {
        UserDetailsImpl userDetails = getUserDetails();
        return userDetails == null ? null : userDetails.getUser();
    }
}

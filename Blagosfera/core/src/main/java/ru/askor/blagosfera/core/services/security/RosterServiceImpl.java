package ru.askor.blagosfera.core.services.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import ru.askor.blagosfera.core.services.contacts.ContactsService;
import ru.askor.blagosfera.data.redis.services.UserSessionDataService;
import ru.askor.blagosfera.domain.contacts.Contact;
import ru.askor.blagosfera.domain.events.user.UserAuthenticatonEvent;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;
import ru.radom.kabinet.SharerService;
import ru.radom.kabinet.model.ContactStatus;
import ru.radom.kabinet.services.StompService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.web.contacts.dto.ContactDto;

import java.security.Principal;
import java.util.List;

@Transactional
@Service("rosterService")
public class RosterServiceImpl implements RosterService {

    @Autowired
    private SharerService sharerService;

    @Autowired
    private UserSessionDataService userSessionDataService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private SimpUserRegistry simpUserRegistry;

    @Autowired
    private ContactsService contactsService;

    @Autowired
    private StompService stompService;

    public RosterServiceImpl() {
    }

    /*@EventListener
    public void onSessionConnectEvent(SessionConnectEvent event) {
    }*/

    @EventListener
    @Override
    public void onSessionConnectedEvent(SessionConnectedEvent event) {
        try {
            Principal principal = event.getUser();
            UserDetailsImpl userDetails = (UserDetailsImpl) ((AbstractAuthenticationToken) principal).getPrincipal();
            userDataService.updateLogoutDate(userDetails.getUser().getId());
            List<Contact> contacts = contactsService.getContactsByOtherId(userDetails.getUser().getId(), ContactStatus.ACCEPTED, ContactStatus.ACCEPTED);

            for (Contact contact : contacts) {
                stompService.send(contact.getUser().getEmail(), "contact_online", ContactDto.toDto(contact));
            }
        } catch (Throwable ignored) {
        }
    }

    /*@EventListener
    public void onSessionDisconnectEvent(SessionDisconnectEvent event) {
        // TODO send offline to contacts
    }*/

    @EventListener
    @Override
    public void onHttpSessionDestroyedEvent(HttpSessionDestroyedEvent event) {
        userSessionDataService.delete(event.getSession().getId());

        try {
            SecurityContext securityContext = (SecurityContext) event.getSession().getAttribute("SPRING_SECURITY_CONTEXT");

            if (securityContext != null) {
                UserDetailsImpl userDetails = (UserDetailsImpl) securityContext.getAuthentication().getPrincipal();
                userDataService.updateLogoutDate(userDetails.getUser().getId());
                List<Contact> contacts = contactsService.getContactsByOtherId(userDetails.getUser().getId(), ContactStatus.ACCEPTED, ContactStatus.ACCEPTED);

                for (Contact contact : contacts) {
                    stompService.send(contact.getUser().getEmail(), "contact_offline", ContactDto.toDto(contact));
                }

                //BPMBlagosferaUtils.raiseSignal(rabbitTemplate, BPMRabbitSignals.USER_BECOME_OFFLINE, "user", sharerService.convertUserToSend(userDetails.getUser()));
            }
        } catch (Throwable ignored) {
        }
    }

    @EventListener
    @Override
    public void onUserAuthenticatonEvent(UserAuthenticatonEvent event) {
        switch (event.getEventType()) {
            case LOGIN:
                userSessionDataService.save(event.getUserSession());

                //BPMBlagosferaUtils.raiseSignal(rabbitTemplate, BPMRabbitSignals.USER_BECOME_ONLINE, "user", sharerService.convertUserToSend(event.getUserDetails().getUser()));
                break;
            case LOGOUT:
        }
    }

    @Override
    public boolean isUserOnline(String username) {
        SimpUser simpUser = simpUserRegistry.getUser(username);
        return simpUser != null && simpUser.getSessions().size() > 0;
    }

    @Override
    public boolean isUserAuthenticated(String username) {
        return userSessionDataService.getUserSessions(username).size() > 0;
    }
}

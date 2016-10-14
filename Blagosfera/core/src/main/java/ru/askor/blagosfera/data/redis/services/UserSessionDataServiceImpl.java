package ru.askor.blagosfera.data.redis.services;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.redis.entities.UserSessionEntity;
import ru.askor.blagosfera.data.redis.repositories.UserSessionRepository;
import ru.askor.blagosfera.domain.sessions.UserSession;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vtarasenko on 28.04.2016.
 */
@Transactional
@Service("userSessionDataService")
public class UserSessionDataServiceImpl implements UserSessionDataService {
    @Autowired
    private SessionRegistry sessionRegistry;
    @Autowired
    private UserSessionRepository userSessionRepository;

    public UserSessionDataServiceImpl() {
    }

    @Override
    public void save(UserSession userSession) {
        UserSessionEntity userSessionEntity = userSessionRepository.findOne(userSession.getSessionId());

        if (userSessionEntity == null) {
            userSessionEntity = new UserSessionEntity();
        }

        userSessionEntity.setSessionId(userSession.getSessionId());
        userSessionEntity.setBrowser(userSession.getBrowser());
        userSessionEntity.setDevice(userSession.getDevice());
        userSessionEntity.setHash(userSession.getHash());
        userSessionEntity.setIp(userSession.getIp());
        userSessionEntity.setLoginDate(userSession.getLoginDate());
        userSessionEntity.setLoginType(userSession.getLoginType());
        userSessionEntity.setOs(userSession.getOs());
        userSessionEntity.setUseragent(userSession.getUseragent());
        userSessionEntity.setUsername(userSession.getUsername());
        userSessionEntity.setReferer(userSession.getReferer());
        userSessionRepository.save(userSessionEntity);
    }

    @Override
    public void delete(String sessionId) {
        userSessionRepository.delete(sessionId);
    }

    @Override
    public List<UserSession> getUserSessions(String username) {
         return userSessionRepository.findByUsername(username).stream().map(userSessionEntity -> userSessionEntity.toDomain()).filter(
                 userSession -> {
                     if (sessionRegistry.getSessionInformation(userSession.getSessionId()) != null) {
                         return true;
                     } else {
                         userSessionRepository.delete(userSession.getSessionId());
                         return false;
                     }
                 }
         ).collect(Collectors.toList());
    }
}

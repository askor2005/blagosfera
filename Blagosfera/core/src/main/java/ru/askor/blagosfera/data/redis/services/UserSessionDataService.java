package ru.askor.blagosfera.data.redis.services;

import ru.askor.blagosfera.domain.sessions.UserSession;

import java.util.List;

/**
 * Created by vtarasenko on 28.04.2016.
 */
public interface UserSessionDataService {

    void save(UserSession userSession);

    void delete(String sessionId);

    List<UserSession> getUserSessions(String username);
}

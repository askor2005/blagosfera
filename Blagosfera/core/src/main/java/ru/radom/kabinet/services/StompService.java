package ru.radom.kabinet.services;

import ru.askor.blagosfera.domain.user.User;

import java.util.Collection;
import java.util.Map;

/**
 * Created by mnikitin on 10.05.2016.
 */
public interface StompService {

    void showPopupWorker(Map<String, Object> data);

    void executeClientScriptWorker(Map<String, Object> data);

    /**
     * send message to users
     * @param users
     * @param destination
     * @param payload
     */
    void send(Collection<User> users, String destination, Object payload);

    /**
     * send message to user
     * @param username
     * @param destination
     * @param payload
     */
    void send(String username, String destination, Object payload);

    /**
     * send message
     * @param destination
     * @param payload
     */
    void send(String destination, Object payload);
}

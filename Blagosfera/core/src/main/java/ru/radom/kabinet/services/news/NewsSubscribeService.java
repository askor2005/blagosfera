package ru.radom.kabinet.services.news;

import ru.askor.blagosfera.domain.user.User;

import java.util.List;

/**
 * Интерфейс сервиса, обрабатывающего логику подписок на новости
 */
public interface NewsSubscribeService {

    /**
     * Позволяет получить список авторов, на которых подписан пользователь.
     * @param userId подписчик
     * @return List<Sharer>
     */
    List<User> getAuthorsBySharer(Long userId);

}

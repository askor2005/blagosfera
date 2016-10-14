package ru.radom.kabinet.services.news;

import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.news.News;

/**
 * Интерфейс сервиса, обрабатывающего логику фильтров новостей
 */
public interface NewsFilterService {

    /**
     * Проверяет, пропускает ли фильтр пользователя указанную новость
     * @param news новость
     * @param userEntity пользователь
     * @return true - новость прошла через фильтр, false - иначе
     */
    boolean isNewsSuitableForSharersFilter(News news, UserEntity userEntity);

}

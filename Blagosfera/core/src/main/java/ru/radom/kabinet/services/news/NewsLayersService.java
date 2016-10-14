package ru.radom.kabinet.services.news;

import ru.askor.blagosfera.domain.news.NewsItem;
import ru.radom.kabinet.model.news.News;

/**
 * Created by igolovko on 04.02.2016.
 */
public interface NewsLayersService {

    /**
     * Позволяет собрать полноценное domain представление сущности новостей для указанного Sharer'a.
     * @param news исходная новость
     * @param userId пользователь, для которого заполняет информация о его голосе
     * @return объект класса NewsItem
     */
    NewsItem makeDomainForSharer(News news, Long userId);
}

package ru.radom.kabinet.services.news;

import ru.askor.blagosfera.domain.news.NewsItem;

/**
 * Интерфейс сервиса, производящего валидацию новостного контента
 */
public interface NewsValidationService {

    /**
     * Позволяет провести валидацию контента новости. В случае провала бросает RuntimeException.
     * @param newsItem domain объект новости
     */
    void validateNewsIsNotEmpty(NewsItem newsItem);

    /**
     * Позволяет отфильтровать новостной контент в соответсвии с требованиями системы.
     * Вырезает запрещенные теги. Размещает вложения картинок и видео в правильных полях.
     * @param newsItem domain объект новости
     * @return отфильтрованный domain объект новости
     */
    NewsItem filterNews(NewsItem newsItem);

    /**
     * Позволяет проверить число вложений картинок и видео в контент новости.
     * В случае превышения допустимого числа выбрасывает RuntimeException
     * @param
     */
    void validateAttachmentsCount(NewsItem newsItem);

    /**
     * Позволяет проверить число тегов в новости.
     * В случае превышения допустимого числа выбрасывает RuntimeException
     * @param
     */
    void validateTagsCount(NewsItem newsItem);

    /**
     * Позволяет проверить консистенотность данных.
     * Пример потери консистентности - вместо категории новостей задана ссылка на должность в объединении
     * @param newsItem
     */
    void validateDataConsistency(NewsItem newsItem);

}

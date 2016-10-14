package ru.askor.blagosfera.domain.events.news;

import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.radom.kabinet.model.news.News;

public class NewsEvent extends BlagosferaEvent {

    private NewsEventType type;
    private News news;

    public NewsEvent(Object source, NewsEventType type, News news) {
        super(source);
        this.type = type;
        this.news = news;
    }

    public NewsEventType getType() {
        return type;
    }

    public void setType(NewsEventType type) {
        this.type = type;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

}

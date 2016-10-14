package ru.askor.blagosfera.domain.events.news;

import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.radom.kabinet.model.discussion.CommentEntity;
import ru.radom.kabinet.model.news.News;

public class CommentEvent extends BlagosferaEvent {

    private News news;
    private CommentEntity commentEntity;

    public CommentEvent(Object source, News news, CommentEntity commentEntity) {
        super(source);
        this.news = news;
        this.commentEntity = commentEntity;
    }

    public News getNews() {
        return news;
    }

    public CommentEntity getCommentEntity() {
        return this.commentEntity;
    }

}

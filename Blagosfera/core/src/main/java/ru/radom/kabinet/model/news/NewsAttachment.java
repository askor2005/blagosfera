package ru.radom.kabinet.model.news;

import ru.askor.blagosfera.domain.news.NewsAttachmentDomain;
import ru.askor.blagosfera.domain.news.NewsAttachmentType;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;

/**
 * Сущность, олицетворяющая вложение в новость (фото или видео)
 */
@Entity
@Table(name = "news_attachments")
public class NewsAttachment extends LongIdentifiable {

    //Ссылка на источник вложения
    @Column(name = "src", nullable = false)
    private String src;

    //Тип вложения
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NewsAttachmentType type;

    //Ширина изображения или превью видео
    @Column(name = "width", nullable = true)
    private Integer width;

    //Высота изображения или превью видео
    @Column(name = "height", nullable = true)
    private Integer height;

    //Новость, к которой привязано вложение
    @JoinColumn(name = "news_id", nullable = false)
    @ManyToOne
    private News news;

	/*
     * --------->CONSTRUCTORS REGION<-------------
     */
    public NewsAttachment() {}

    public NewsAttachment(NewsAttachmentDomain domain) {
        this.src = domain.getSrc();
        this.type = domain.getType();
        this.width = domain.getWidth();
        this.height = domain.getHeight();
    }
	/*
     * --------->END CONSTRUCTORS REGION<-------------
     */


    /*
     * --------->GETTERS AND SETTERS REGION<-------------
     */
    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public NewsAttachmentType getType() {
        return type;
    }

    public void setType(NewsAttachmentType type) {
        this.type = type;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
    /*
     * --------->END GETTERS AND SETTERS REGION<-------------
     */

    public NewsAttachmentDomain toDomain() {
        NewsAttachmentDomain result = new NewsAttachmentDomain();

        result.setSrc(src);
        result.setType(type);
        result.setWidth(width);
        result.setHeight(height);

        return result;
    }

}

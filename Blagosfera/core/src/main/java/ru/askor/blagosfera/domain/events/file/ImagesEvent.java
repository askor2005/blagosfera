package ru.askor.blagosfera.domain.events.file;

import ru.askor.blagosfera.domain.events.BlagosferaEvent;

public class ImagesEvent extends BlagosferaEvent {

    private String objectType;
    private Long objectId;
    private String url;
    private String urlOriginal;

    public ImagesEvent(Object source, String objectType, Long objectId, String url) {
        super(source);
        this.objectType = objectType;
        this.objectId = objectId;
        this.url = url;
    }

    public ImagesEvent(Object source, String objectType, Long objectId, String url, String urlOriginal) {
        super(source);
        this.objectType = objectType;
        this.objectId = objectId;
        this.url = url;
        this.urlOriginal = urlOriginal;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlOriginal() {
        return urlOriginal;
    }

    public void setUrlOriginal(String urlOriginal) {
        this.urlOriginal = urlOriginal;
    }
}

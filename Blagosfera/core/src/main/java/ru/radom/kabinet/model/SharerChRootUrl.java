package ru.radom.kabinet.model;

//Возможные значения для поля chRootUrl сущности Sharer
public enum SharerChRootUrl {
    NEED_CHANGE_PASSWORD("/instruction");

    SharerChRootUrl(String url) {
        this.url = url;
    }

    private String url;

    public String getUrl() {
        return  url;
    }
}

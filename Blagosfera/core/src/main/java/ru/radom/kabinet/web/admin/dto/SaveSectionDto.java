package ru.radom.kabinet.web.admin.dto;

/**
 *
 * Created by vgusev on 18.11.2015.
 */
public class SaveSectionDto {

    // ИД раздела
    private Long id;
    // Имя раздела
    private String title;
    // Хинт раздела
    private String hint;
    // Ссылка раздела
    private String link;
    // ИД статичной страницы раздела
    private Long pageId;
    // Альтернативная ссылка контента раздела
    private String forwardUrl;
    // Флаг - опубликован раздел
    private boolean published;

    private boolean openInNewLink;

    public boolean isOpenInNewLink() {
        return openInNewLink;
    }

    public void setOpenInNewLink(boolean openInNewLink) {
        this.openInNewLink = openInNewLink;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public String getForwardUrl() {
        return forwardUrl;
    }

    public void setForwardUrl(String forwardUrl) {
        this.forwardUrl = forwardUrl;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}

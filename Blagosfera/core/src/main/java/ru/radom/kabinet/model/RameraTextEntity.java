package ru.radom.kabinet.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by ebelyaev on 26.08.2015.
 */
@Entity
@Table(name = "ramera_texts")
public class RameraTextEntity extends LongIdentifiable {

    @Column(name = "code", nullable = false, length = 100, unique = true)
    private String code;

    @Column(name = "text", columnDefinition = "text")
    private String text;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "is_html")
    private Boolean html;

    public RameraTextEntity() {
    }

    public RameraTextEntity(String code, String text, String description, boolean html) {
        this.code = code;
        this.text = text;
        this.description = description;
        this.html = html;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isHtml() {
        return html;
    }

    public void setHtml(Boolean html) {
        this.html = html;
    }
}

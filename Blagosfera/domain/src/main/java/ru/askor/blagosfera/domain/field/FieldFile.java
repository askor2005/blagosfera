package ru.askor.blagosfera.domain.field;

import java.io.Serializable;

/**
 *
 * Created by vgusev on 16.03.2016.
 */
public class FieldFile implements Serializable {

    public static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String url;

    public FieldFile() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

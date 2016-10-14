package ru.askor.blagosfera.domain.community.schema;

/**
 * Created by mnikitin on 20.06.2016.
 */
public class CommunitySchemaConnectionType {

    private Long id;
    private String color;
    private String name;
    private boolean reversable;

    public CommunitySchemaConnectionType() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReversable() {
        return reversable;
    }

    public void setReversable(Boolean reversable) {
        this.reversable = reversable;
    }
}

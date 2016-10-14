package ru.askor.blagosfera.domain.community.schema;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mnikitin on 17.06.2016.
 */
public class CommunitySchema {

    private Long id;
    private List<CommunitySchemaUnit> units = new ArrayList<>();
    private Long communityId;
    private String bgImageUrl;
    private Integer width;
    private Integer height;
    private Integer scrollLeft;
    private Integer scrollTop;

    public CommunitySchema() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CommunitySchemaUnit> getUnits() {
        return units;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getBgImageUrl() {
        return bgImageUrl;
    }

    public void setBgImageUrl(String bgImageUrl) {
        this.bgImageUrl = bgImageUrl;
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

    public Integer getScrollLeft() {
        return scrollLeft;
    }

    public void setScrollLeft(Integer scrollLeft) {
        this.scrollLeft = scrollLeft;
    }

    public Integer getScrollTop() {
        return scrollTop;
    }

    public void setScrollTop(Integer scrollTop) {
        this.scrollTop = scrollTop;
    }
}

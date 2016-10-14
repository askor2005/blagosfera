package ru.radom.kabinet.dto.news;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Dto данных новостного фильтра
 */
public class NewsFilterDataDto {

    private Long communityId;

    private  Long categoryId;

    private  Long authorId;

    private  Date dateFrom;

    private  Date dateTo;

    private List<String> tags = new ArrayList<>();


    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}

package ru.askor.blagosfera.domain.news.filter;

import ru.askor.blagosfera.domain.common.Tag;
import ru.radom.kabinet.dto.news.NewsFilterDataDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Builder для объектов класса  NewsFilterData
 */
public class NewsFilterDataBuilder {

    private Long communityId = null;

    private Long categoryId = null;

    private Long authorId = null;

    private Date dateFrom = null;

    private Date dateTo = null;

    //Список тегов для фильтрации
    private List<Tag> tags = new ArrayList<>();


    public NewsFilterDataBuilder() {
    }

    public NewsFilterDataBuilder(NewsFilterDataDto dto) {
        communityId = dto.getCommunityId();
        categoryId = dto.getCategoryId();
        authorId = dto.getAuthorId();
        dateFrom = dto.getDateFrom();
        dateTo = dto.getDateTo();
        tags = dto.getTags().stream()
                .map(t -> new Tag(t))
                .collect(Collectors.toList());
    }

    public NewsFilterDataBuilder setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public NewsFilterDataBuilder setAuthorId(Long authorId) {
        this.authorId = authorId;
        return this;
    }

    public NewsFilterDataBuilder setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
        return this;
    }

    public NewsFilterDataBuilder setDateTo(Date dateTo) {
        this.dateTo = dateTo;
        return this;
    }

    public NewsFilterDataBuilder setTags(List<Tag> tags) {
        this.tags = tags;
        return this;
    }

    public NewsFilterData createNewsFilterData() {
        return new NewsFilterData(communityId, categoryId, authorId, dateFrom, dateTo, tags);
    }

}

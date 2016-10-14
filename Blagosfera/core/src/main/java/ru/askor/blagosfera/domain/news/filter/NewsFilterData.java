package ru.askor.blagosfera.domain.news.filter;

import ru.askor.blagosfera.domain.common.Tag;
import ru.radom.kabinet.dto.news.NewsFilterDataDto;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс объекта, олицетворяющий критерии фильтрации новостей
 */
public class NewsFilterData {

    private final Long communityId;

    //Идентификатор категории новостей, по которой должна выполняться фильтрация
    private final Long categoryId;

    //Идентификатор автора новости, по которому должна выполняться фильтрация
    private final Long authorId;

    //Ранняя точка фильтрации по дате создания новости
    private final Date dateFrom;

    //Поздняя точка фильтрации по дате создания новости
    private final Date dateTo;

    //Список тегов для фильтрации
    private final List<Tag> tags;


    public NewsFilterData(Long communityId, Long categoryId, Long authorId, Date dateFrom, Date dateTo, List<Tag> tags) {
        this.communityId = communityId;
        this.categoryId = categoryId;
        this.authorId = authorId;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.tags = tags;
    }


    public Long getCommunityId() {
        return communityId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public NewsFilterDataDto toDto() {
        NewsFilterDataDto result = new NewsFilterDataDto();

        result.setAuthorId(authorId);
        result.setCategoryId(categoryId);
        result.setDateFrom(dateFrom);
        result.setDateTo(dateTo);
        result.setTags(tags.stream()
                .map(t -> t.getText())
                .collect(Collectors.toList()));

        return result;
    }
}

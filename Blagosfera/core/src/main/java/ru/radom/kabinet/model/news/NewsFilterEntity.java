package ru.radom.kabinet.model.news;

import ru.askor.blagosfera.domain.news.filter.NewsFilterData;
import ru.askor.blagosfera.domain.news.filter.NewsFilterDataBuilder;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.common.TagEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сущность новостного фильтра.
 * Каждый пользователь имеет по одному фильтру для каждого объединения и один для делового портала (community == null)
 */
@Entity
@Table(name = "news_filters",
        uniqueConstraints = @UniqueConstraint(columnNames = {"sharer_id", "community_id"}))
public class NewsFilterEntity extends LongIdentifiable {

    @JoinColumn(name = "sharer_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @JoinColumn(name = "community_id", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private CommunityEntity community;

    @Column(name = "author_id", nullable = true)
    private Long authorId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "date_from", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date dateFrom;

    @Column(name = "date_to", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date dateTo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "news_filters_tags",
            joinColumns = { @JoinColumn(name = "news_filter_id", nullable = false, updatable = false) },
            inverseJoinColumns = {@JoinColumn(name = "tag_id", nullable = false, updatable = false)})
    List<TagEntity> tags;


    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public CommunityEntity getCommunity() {
        return community;
    }

    public void setCommunity(CommunityEntity community) {
        this.community = community;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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

    public List<TagEntity> getTags() {
        return tags;
    }

    public void setTags(List<TagEntity> tags) {
        this.tags = tags;
    }

    public NewsFilterData toDomain() {
        NewsFilterDataBuilder builder = new NewsFilterDataBuilder();

        return builder.setAuthorId(authorId)
                .setCategoryId(categoryId)
                .setDateFrom(dateFrom)
                .setDateTo(dateTo)
                .setTags(tags.stream()
                        .map(t -> t.toDomain())
                        .collect(Collectors.toList()))
                .createNewsFilterData();
    }

}

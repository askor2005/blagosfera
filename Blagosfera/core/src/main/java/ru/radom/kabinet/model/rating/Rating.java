package ru.radom.kabinet.model.rating;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.MetaValue;
import ru.askor.blagosfera.domain.user.RatingDomain;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.discussion.CommentEntity;
import ru.radom.kabinet.model.news.News;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ratings")
public class Rating extends LongIdentifiable {

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Any(metaColumn = @Column(name = "content_type", length = 50), fetch = FetchType.EAGER)
    @AnyMetaDef(idType = "long", metaType = "string", metaValues = {
            @MetaValue(targetEntity = News.class, value = Discriminators.NEWS),
            @MetaValue(targetEntity = CommentEntity.class, value = Discriminators.COMMENT)
    })

    @JoinColumn(name = "content_id")
    private Ratable content;

    @Column(nullable = false)
    private Boolean deleted;


    public Rating() {

    }

    public Rating(final UserEntity user, final Double weight, final Date created, final Ratable content, final Boolean deleted) {
        super();
        this.user = user;
        this.weight = weight;
        this.created = created;
        this.content = content;
        this.deleted = deleted;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Ratable getContent() {
        return content;
    }

    public void setContent(Ratable content) {
        this.content = content;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public RatingDomain toDomain() {
        RatingDomain ratingDomain = new RatingDomain();
        ratingDomain.setCreated(created);
        ratingDomain.setUser(getUser().toDomain());
        ratingDomain.setWeight(weight);
        return ratingDomain;
    }
}

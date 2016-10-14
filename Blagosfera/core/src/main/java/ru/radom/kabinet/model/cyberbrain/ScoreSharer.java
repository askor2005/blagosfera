package ru.radom.kabinet.model.cyberbrain;

import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = ScoreSharer.TABLE_NAME)
public class ScoreSharer extends LongIdentifiable {
    public static final String TABLE_NAME = "cyberbrain_score_sharers";

    public static class Columns {
        public static final String CREATION_DATE = "creation_date";
        public static final String SHARER = "sharer_id";
        public static final String SCORE_OBJECT = "score_object_id";
        public static final String SCORE = "score";
        public static final String COMMUNITY = "community_id";
        public static final String SOURCE_ORIGIN = "source_origin_id";
        public static final String SOURCE = "source_id";
    }

    @Column(name = Columns.CREATION_DATE, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate; // Дата создания записи

    @JoinColumn(name = Columns.SHARER, nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity userEntity;

    @JoinColumn(name = Columns.SCORE_OBJECT, nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ScoreObject scoreObject;

    @Column(name = Columns.SCORE, nullable = false)
    private BigDecimal score;

    @JoinColumn(name = Columns.COMMUNITY)
    @ManyToOne(fetch = FetchType.LAZY)
    private CommunityEntity community;

    @JoinColumn(name = Columns.SOURCE_ORIGIN)
    @ManyToOne(fetch = FetchType.LAZY)
    private CyberbrainObject sourceOrigin; // Источник происхождения записи

    @Column(name = Columns.SOURCE)
    private Long source; // Идентификатор записи источника

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public ScoreObject getScoreObject() {
        return scoreObject;
    }

    public void setScoreObject(ScoreObject scoreObject) {
        this.scoreObject = scoreObject;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public CommunityEntity getCommunity() {
        return community;
    }

    public void setCommunity(CommunityEntity community) {
        this.community = community;
    }

    public CyberbrainObject getSourceOrigin() {
        return sourceOrigin;
    }

    public void setSourceOrigin(CyberbrainObject sourceOrigin) {
        this.sourceOrigin = sourceOrigin;
    }

    public Long getSource() {
        return source;
    }

    public void setSource(Long source) {
        this.source = source;
    }
}
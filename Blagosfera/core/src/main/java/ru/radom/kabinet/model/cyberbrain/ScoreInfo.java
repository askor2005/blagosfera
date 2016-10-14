package ru.radom.kabinet.model.cyberbrain;

import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = ScoreInfo.TABLE_NAME)
public class ScoreInfo extends LongIdentifiable {
    public static final String TABLE_NAME = "cyberbrain_score_info";

    public static class Columns {
        public static final String SCORE_OBJECT = "score_object_id";
        public static final String SCORE = "score";
        public static final String PERIOD_FROM = "period_from";
        public static final String PERIOD_TO = "period_to";
    }

    @JoinColumn(name = Columns.SCORE_OBJECT, nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ScoreObject scoreObject;

    @Column(name = Columns.SCORE, nullable = false)
    private BigDecimal score;

    @Column(name = Columns.PERIOD_FROM, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date periodFrom;

    @Column(name = Columns.PERIOD_TO, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date periodTo;

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

    public Date getPeriodFrom() {
        return periodFrom;
    }

    public void setPeriodFrom(Date periodFrom) {
        this.periodFrom = periodFrom;
    }

    public Date getPeriodTo() {
        return periodTo;
    }

    public void setPeriodTo(Date periodTo) {
        this.periodTo = periodTo;
    }
}
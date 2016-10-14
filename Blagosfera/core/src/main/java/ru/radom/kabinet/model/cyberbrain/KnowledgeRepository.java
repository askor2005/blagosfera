package ru.radom.kabinet.model.cyberbrain;

import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.utils.DateUtils;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = KnowledgeRepository.TABLE_NAME)
public class KnowledgeRepository extends LongIdentifiable implements CyberbrainExportData {
    public static final String TABLE_NAME = "cyberbrain_knowledge_repository";

    public static class Columns {
        public static final String FIX_TIME_CHANGE = "fix_time_change";
        public static final String TIME_READY = "time_ready";
        public static final String OWNER = "owner";
        public static final String TAG_OWNER = "tag_owner";
        public static final String ATTRIBUTE = "attribute";
        public static final String TAG = "tag";
        public static final String MERA = "mera";
        public static final String TASK = "task";
        public static final String NEXT = "next";
        public static final String CHANGE_IF = "change_if";
        public static final String STATUS = "status";
        public static final String STRESS = "stress";
        public static final String ATTENTION = "attention";
        public static final String SHOW_IN_QUESTIONS = "show_in_questions";
        public static final String COMMUNITY = "community_id";
        public static final String LIFECYCLE_STATUS = "lifecycle_status";
        public static final String IS_TOPICAL = "is_topical";
        public static final String SOURCE_ORIGIN = "source_origin_id";
        public static final String SOURCE = "source_id";
    }

    @Column(name = Columns.FIX_TIME_CHANGE, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fixTimeChange; // Дата и время записи строки в таблицу

    @Column(name = Columns.TIME_READY)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeReady; // Дата готовности

    @JoinColumn(name = Columns.OWNER)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity owner; // ID человека в системе

    @JoinColumn(name = Columns.TAG_OWNER)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Thesaurus tagOwner; // содержит индекс ссылку на IDEssence

    @JoinColumn(name = Columns.ATTRIBUTE)
    @ManyToOne(fetch = FetchType.LAZY)
    private Thesaurus attribute; // содержит индекс ссылку на IDEssence

    @JoinColumn(name = Columns.TAG)
    @ManyToOne(fetch = FetchType.LAZY)
    private Thesaurus tag; // содержит индекс ссылку на IDEssence

    @Column(name = Columns.MERA)
    private Double mera; // Число Double

    @JoinColumn(name = Columns.TASK)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserTask task; // содержит индекс ссылку IDTask

    @Column(name = Columns.NEXT)
    private Long next; // содержит индекс ссылку на id

    @Column(name = Columns.CHANGE_IF)
    private Long changeIf; // содержит индекс ссылку на id

    @Column(name = Columns.STATUS)
    private Integer status; // Набор значений от 1 до 8

    @Column(name = Columns.STRESS)
    private Double stress; // Число Double

    @Column(name = Columns.ATTENTION)
    private Double attention; // число Double

    @Column(name = Columns.SHOW_IN_QUESTIONS)
    private Boolean showInQuestions; // отображать в вопросах

    @JoinColumn(name = Columns.COMMUNITY)
    @ManyToOne(fetch = FetchType.LAZY)
    private CommunityEntity community;

    @Column(name = Columns.LIFECYCLE_STATUS)
    private Integer lifecycleStatus; // Состояние ЖЦ

    @Column(name = Columns.IS_TOPICAL)
    private Boolean isTopical; // Запись является актуальной

    @JoinColumn(name = Columns.SOURCE_ORIGIN)
    @ManyToOne(fetch = FetchType.LAZY)
    private CyberbrainObject sourceOrigin; // Источник происхождения записи

    @Column(name = Columns.SOURCE)
    private Long source; // Идентификатор записи источника

    public Date getFixTimeChange() {
        return fixTimeChange;
    }

    public void setFixTimeChange(Date fixTimeChange) {
        this.fixTimeChange = fixTimeChange;
    }

    public Date getTimeReady() {
        return timeReady;
    }

    public void setTimeReady(Date timeReady) {
        this.timeReady = timeReady;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    public Thesaurus getTagOwner() {
        return tagOwner;
    }

    public void setTagOwner(Thesaurus tagOwner) {
        this.tagOwner = tagOwner;
    }

    public Thesaurus getAttribute() {
        return attribute;
    }

    public void setAttribute(Thesaurus attribute) {
        this.attribute = attribute;
    }

    public Thesaurus getTag() {
        return tag;
    }

    public void setTag(Thesaurus tag) {
        this.tag = tag;
    }

    public Double getMera() {
        return mera;
    }

    public void setMera(Double mera) {
        this.mera = mera;
    }

    public UserTask getTask() {
        return task;
    }

    public void setTask(UserTask task) {
        this.task = task;
    }

    public Long getNext() {
        return next;
    }

    public void setNext(Long next) {
        this.next = next;
    }

    public Long getChangeIf() {
        return changeIf;
    }

    public void setChangeIf(Long changeIf) {
        this.changeIf = changeIf;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Double getStress() {
        return stress;
    }

    public void setStress(Double stress) {
        this.stress = stress;
    }

    public Double getAttention() {
        return attention;
    }

    public void setAttention(Double attention) {
        this.attention = attention;
    }

    public Boolean getShowInQuestions() {
        return showInQuestions;
    }

    public void setShowInQuestions(Boolean showInQuestions) {
        this.showInQuestions = showInQuestions;
    }

    public CommunityEntity getCommunity() {
        return community;
    }

    public void setCommunity(CommunityEntity community) {
        this.community = community;
    }

    public Integer getLifecycleStatus() {
        return lifecycleStatus;
    }

    public void setLifecycleStatus(Integer lifecycleStatus) {
        this.lifecycleStatus = lifecycleStatus;
    }

    public Boolean getIsTopical() {
        return isTopical;
    }

    public void setIsTopical(Boolean isTopical) {
        this.isTopical = isTopical;
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

    /**
     * Сделать новый объект копию
     */
    @Override
    public KnowledgeRepository clone() {
        KnowledgeRepository clone = new KnowledgeRepository();
        clone.setFixTimeChange(new Date(System.currentTimeMillis()));
        clone.setTimeReady(getTimeReady());
        clone.setOwner(getOwner());
        clone.setTagOwner(getTagOwner());
        clone.setAttribute(getAttribute());
        clone.setTag(getTag());
        clone.setMera(getMera());
        clone.setTask(getTask());
        clone.setChangeIf(getChangeIf());
        clone.setNext(getNext());
        clone.setStatus(getStatus());
        clone.setStress(getStress());
        clone.setAttention(getAttention());
        clone.setShowInQuestions(getShowInQuestions());
        clone.setCommunity(getCommunity());
        clone.setLifecycleStatus(getLifecycleStatus());
        clone.setIsTopical(getIsTopical());
        clone.setSourceOrigin(getSourceOrigin());
        clone.setSource(getSource());

        return clone;
    }

    @Override
    public String getExportData(boolean withHeaders) {
        String strFixTimeChange = getFixTimeChange() != null ? DateUtils.dateToString(getFixTimeChange(), DateUtils.Format.DATE_TIME) : "";
        String strTimeReady = getTimeReady() != null ? DateUtils.dateToString(getTimeReady(), DateUtils.Format.DATE_TIME) : "";
        String strOwner = getOwner() != null ? getOwner().getFullName() : "";
        String strOwnerId = getOwner() != null ? getOwner().getId().toString() : "";
        String strTagOwner = getTagOwner() != null ? getTagOwner().getEssence() : "";
        String strTagOwnerId = getTagOwner() != null ? getTagOwner().getId().toString() : "";
        String strAttribute = getAttribute() != null ? getAttribute().getEssence() : "";
        String strAttributeId = getAttribute() != null ? getAttribute().getId().toString() : "";
        String strTag = getTag() != null ? getTag().getEssence() : "";
        String strTagId = getTag() != null ? getTag().getId().toString() : "";
        String strMera = getMera() != null ? getMera().toString() : "";
        String strTask = getTask() != null ? getTask().getDescription() : "";
        String strTaskId = getTask() != null ? getTask().getId().toString() : "";
        String strNextId = getNext() != null ? getNext().toString() : "";
        String strChangeIfId = getChangeIf() != null ? getChangeIf().toString() : "";
        String strStatus = getStatus() != null ? getStatus().toString() : "";
        String strStress = getStress() != null ? getStress().toString() : "";
        String strAttention = getAttention() != null ? getAttention().toString() : "";
        String strShowInQuestions = getShowInQuestions() != null ? (getShowInQuestions() ? "1" : "0") : "";
        String strCommunity = getCommunity() != null ? getCommunity().getName() : "";
        String strCommunityId = getCommunity() != null ? getCommunity().getId().toString() : "";
        String strLifecycleStatus = getLifecycleStatus() != null ? getLifecycleStatus().toString() : "";
        String strIsTopical = getIsTopical() != null ? (getIsTopical() ? "1" : "0") : "";
        String strSourceOrigin = getSourceOrigin() != null ? getSourceOrigin().getName() : "";
        String strSourceOriginId = getSourceOrigin() != null ? getSourceOrigin().getId().toString() : "";
        String strSource = getSource() != null ? getSource().toString() : "";

        String data = getId().toString() + "\t" + strFixTimeChange + "\t" + strTimeReady + "\t" + strOwnerId + "\t" + strOwner + "\t" +
                strTagOwnerId + "\t" + strTagOwner + "\t" + strAttributeId + "\t" + strAttribute + "\t" +
                strTagId + "\t" + strTag + "\t" + strMera + "\t" + strTaskId + "\t" + strTask + "\t" + strNextId + "\t" +
                strChangeIfId + "\t" + strStatus + "\t" + strStress + "\t" + strAttention + "\t" +
                strShowInQuestions + "\t" + strCommunityId + "\t" + strCommunity + "\t" + strLifecycleStatus + "\t" + strIsTopical + "\t" +
                strSourceOriginId + "\t" + strSourceOrigin + "\t" + strSource + "\t" + "\n";

        if (withHeaders) {
            String headers = "ID\tFIX_TIME_CHANGE\tTIME_READY\tOWNER_ID\tOWNER\t" +
                    "TAG_OWNER_ID\tTAG_OWNER\tATTRIBUTE_ID\tATTRIBUTE\t" +
                    "TAG_ID\tTAG\tMERA\tTASK_ID\tTASK\tNEXT_ID\t" +
                    "CHANGE_IF_ID\tSTATUS\tSTRESS\tATTENTION\tSHOW_IN_QUESTIONS\t" +
                    "COMMUNITY_ID\tCOMMUNITY\tLIFECYCLE_STATUS\tIS_TOPICAL\t" +
                    "SOURCE_ORIGIN_ID\tSOURCE_ORIGIN\tSOURCE_ID\n";

            data = headers + data;
        }

        return data;
    }
}
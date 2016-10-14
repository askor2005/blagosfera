package ru.radom.kabinet.model.cyberbrain;

import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.utils.DateUtils;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = UserTask.TABLE_NAME)
public class UserTask extends LongIdentifiable implements CyberbrainExportData {
    public static final String TABLE_NAME = "cyberbrain_user_tasks";

    public static class Columns {
        public static final String PERFORMER = "performer_id";
        public static final String DATE_EXECUTION = "date_execution";
        public static final String LIFECYCLE = "lifecycle";
        public static final String DESCRIPTION = "description";
        public static final String CUSTOMER = "customer_id";
        public static final String TRACK = "track_id";
        public static final String STATUS_FROM = "status_from";
        public static final String STATUS_TO = "status_to";
        public static final String COMMUNITY = "community_id";
        public static final String SOURCE_ORIGIN = "source_origin_id";
        public static final String SOURCE = "source_id";
    }

    @JoinColumn(name = Columns.PERFORMER)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity performer;    // Исполнитель задания

    @Column(name = Columns.DATE_EXECUTION)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateExecution;  // Дата исполнения задания

    /**
     * ЖЦ задачи:
     * 0 - задача новая
     * 1 - задача решена
     * 2 - задача подтвержденна
     */
    @Column(name = Columns.LIFECYCLE)
    private Integer lifecycle; // ЖЦ задачи

    @Column(name = Columns.DESCRIPTION)
    private String description;  // Описание

    @JoinColumn(name = Columns.CUSTOMER)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity customer;     // Заказчик

    @JoinColumn(name = Columns.TRACK)
    @ManyToOne(fetch = FetchType.LAZY)
    private KnowledgeRepository track; // Объект

    @JoinColumn(name = Columns.STATUS_FROM)
    @ManyToOne(fetch = FetchType.LAZY)
    private Thesaurus statusFrom; // Из какого состояния

    @JoinColumn(name = Columns.STATUS_TO)
    @ManyToOne(fetch = FetchType.LAZY)
    private Thesaurus statusTo;    // В какое состояние

    @OneToMany(mappedBy = "userTask", fetch = FetchType.LAZY)
    private List<UserSubtask> subtasks; // список подзадач

    @JoinColumn(name = Columns.COMMUNITY)
    @ManyToOne(fetch = FetchType.LAZY)
    private CommunityEntity community;

    @JoinColumn(name = Columns.SOURCE_ORIGIN)
    @ManyToOne(fetch = FetchType.LAZY)
    private CyberbrainObject sourceOrigin; // Источник происхождения записи

    @Column(name = Columns.SOURCE)
    private Long source; // Идентификатор записи источника

    public UserEntity getPerformer() {
        return performer;
    }

    public void setPerformer(UserEntity performer) {
        this.performer = performer;
    }

    public Date getDateExecution() {
        return dateExecution;
    }

    public void setDateExecution(Date dateExecution) {
        this.dateExecution = dateExecution;
    }

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserEntity getCustomer() {
        return customer;
    }

    public void setCustomer(UserEntity customer) {
        this.customer = customer;
    }

    public KnowledgeRepository getTrack() {
        return track;
    }

    public void setTrack(KnowledgeRepository track) {
        this.track = track;
    }

    public Thesaurus getStatusFrom() {
        return statusFrom;
    }

    public void setStatusFrom(Thesaurus statusFrom) {
        this.statusFrom = statusFrom;
    }

    public Thesaurus getStatusTo() {
        return statusTo;
    }

    public void setStatusTo(Thesaurus statusTo) {
        this.statusTo = statusTo;
    }

    public List<UserSubtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<UserSubtask> subtasks) {
        this.subtasks = subtasks;
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

    @Override
    public String getExportData(boolean withHeaders) {
        String strPerformer = getPerformer() != null ? getPerformer().getFullName() : "";
        String strPerformerId = getPerformer() != null ? getPerformer().getId().toString() : "";
        String strDateExecution = getDateExecution() != null ? DateUtils.dateToString(getDateExecution(), DateUtils.Format.DATE_TIME) : "";
        String strLifecycle = getLifecycle() != null ? getLifecycle().toString() : "";
        String strDescription = getDescription() != null ? getDescription() : "";
        String strCustomer = getCustomer() != null ? getCustomer().getFullName() : "";
        String strCustomerId = getCustomer() != null ? getCustomer().getId().toString() : "";
        String strTrack = getTrack() != null ? getTrack().getTagOwner().getEssence() : "";
        String strTrackId = getTrack() != null ? getTrack().getId().toString() : "";
        String strStatusFrom = getStatusFrom() != null ? getStatusFrom().getEssence() : "";
        String strStatusFromId = getStatusFrom() != null ? getStatusFrom().getId().toString() : "";
        String strStatusTo = getStatusTo() != null ? getStatusTo().getEssence() : "";
        String strStatusToId = getStatusTo() != null ? getStatusTo().getId().toString() : "";

        String strSubtasksId = "";
        String strCommunity = getCommunity() != null ? getCommunity().getName() : "";
        String strCommunityId = getCommunity() != null ? getCommunity().getId().toString() : "";
        String strSourceOrigin = getSourceOrigin() != null ? getSourceOrigin().getName() : "";
        String strSourceOriginId = getSourceOrigin() != null ? getSourceOrigin().getId().toString() : "";
        String strSource = getSource() != null ? getSource().toString() : "";

        List<UserSubtask> list = getSubtasks();

        if (list != null) {
            for (UserSubtask userSubtask : list) {
                if (strSubtasksId.equals("")) {
                    strSubtasksId = userSubtask.getUserSubtask().getId().toString();
                } else {
                    strSubtasksId += ";" + userSubtask.getUserSubtask().getId().toString();
                }
            }
        }

        String data = getId().toString() + "\t" + strPerformerId + "\t" + strPerformer + "\t" + strDateExecution + "\t" +
                strLifecycle + "\t" + strDescription + "\t" + strCustomerId + "\t" + strCustomer + "\t" +
                strTrackId + "\t" + strTrack + "\t" + strStatusFromId + "\t" + strStatusFrom + "\t" + strStatusToId + "\t" +
                strStatusTo + "\t" + strSubtasksId + "\t" + strCommunityId + "\t" + strCommunity + "\t" +
                strSourceOriginId + "\t" + strSourceOrigin + "\t" + strSource + "\n";

        if (withHeaders) {
            String headers = "ID\tPERFORMER_ID\tPERFORMER\tDATE_EXECUTION\tLIFECYCLE\tDESCRIPTION\tCUSTOMER_ID\t" +
                    "CUSTOMER\tTRACK_ID\tTRACK\tSTATUS_FROM_ID\tSTATUS_FROM\tSTATUS_TO_ID\tSTATUS_TO\t" +
                    "SUBTASK_IDS\tCOMMUNITY_ID\tCOMMUNITY\tSOURCE_ORIGIN_ID\tSOURCE_ORIGIN\tSOURCE_ID\n";

            data = headers + data;
        }

        return data;
    }
}
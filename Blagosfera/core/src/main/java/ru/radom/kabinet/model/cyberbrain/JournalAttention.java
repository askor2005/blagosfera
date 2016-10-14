package ru.radom.kabinet.model.cyberbrain;

import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.utils.DateUtils;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = JournalAttention.TABLE_NAME)
public class JournalAttention extends LongIdentifiable implements CyberbrainExportData {
    public static final String TABLE_NAME = "cyberbrain_journal_attention";

    public static class Columns {
        public static final String FIX_TIME_KVANT = "fix_time_kvant";
        public static final String TEXT_KVANT = "text_kvant";
        public static final String TAG_KVANT = "tag_kvant";
        public static final String PERFORMER_KVANT = "performer_kvant";
        public static final String ATTENTION_KVANT = "attention_kvant";
        public static final String COMMUNITY = "community_id";
        public static final String SOURCE_ORIGIN = "source_origin_id";
        public static final String SOURCE = "source_id";
    }

    @Column(name = Columns.FIX_TIME_KVANT, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fixTimeKvant; // Дата и время создания записи

    @Column(name = Columns.TEXT_KVANT, length = 8000)
    private String textKvant; // Краткое описание задачи. Текстовый блок 4-8 кБ

    @Column(name = Columns.TAG_KVANT, nullable = false, length = 2000)
    private String tagKvant; // Теговое описание задачи. Текстовое поле 2 кБ. Разделитель точка

    @JoinColumn(name = Columns.PERFORMER_KVANT, nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity performerKvant; // Исполнитель события. Ссылка на пользователя системы

    @Column(name = Columns.ATTENTION_KVANT)
    private Integer attentionKvant; // Квант внимания. Целое натуральное число

    @JoinColumn(name = Columns.COMMUNITY)
    @ManyToOne(fetch = FetchType.LAZY)
    private CommunityEntity community;

    @JoinColumn(name = Columns.SOURCE_ORIGIN)
    @ManyToOne(fetch = FetchType.LAZY)
    private CyberbrainObject sourceOrigin; // Источник происхождения записи

    @Column(name = Columns.SOURCE)
    private Long source; // Идентификатор записи источника

    public Date getFixTimeKvant() {
        return fixTimeKvant;
    }

    public void setFixTimeKvant(Date fixTimeKvant) {
        this.fixTimeKvant = fixTimeKvant;
    }

    public String getTextKvant() {
        return textKvant;
    }

    public void setTextKvant(String textKvant) {
        this.textKvant = textKvant;
    }

    public String getTagKvant() {
        return tagKvant;
    }

    public void setTagKvant(String tagKvant) {
        this.tagKvant = tagKvant;
    }

    public UserEntity getPerformerKvant() {
        return performerKvant;
    }

    public void setPerformerKvant(UserEntity performerKvant) {
        this.performerKvant = performerKvant;
    }

    public Integer getAttentionKvant() {
        return attentionKvant;
    }

    public void setAttentionKvant(Integer attentionKvant) {
        this.attentionKvant = attentionKvant;
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
        String strFixTimeKvant = getPerformerKvant() != null ? DateUtils.dateToString(getFixTimeKvant(), DateUtils.Format.DATE_TIME) :"";
        String strTextKvant = getTextKvant() != null ? getTextKvant() : "";
        String strTagKvant = getTagKvant() != null ? getTagKvant() : "";
        String strPerformerKvant = getPerformerKvant() != null ? getPerformerKvant().getFullName() : "";
        String strPerformerKvantId = getPerformerKvant() != null ? getPerformerKvant().getId().toString() : "";
        String strAttentionKvant = getAttentionKvant() != null ? getAttentionKvant().toString() : "";
        String strCommunity = getCommunity() != null ? getCommunity().getName() : "";
        String strCommunityId = getCommunity() != null ? getCommunity().getId().toString() : "";
        String strSourceOrigin = getSourceOrigin() != null ? getSourceOrigin().getName() : "";
        String strSourceOriginId = getSourceOrigin() != null ? getSourceOrigin().getId().toString() : "";
        String strSource = getSource() != null ? getSource().toString() : "";

        String data = getId().toString() + "\t" + strFixTimeKvant + "\t" + strTextKvant + "\t" + strTagKvant + "\t" +
                strPerformerKvantId + "\t" + strPerformerKvant + "\t" + strAttentionKvant + "\t" +
                strCommunityId + "\t" + strCommunity + "\t" + strSourceOriginId + "\t" + strSourceOrigin + "\t" + strSource + "\n";

        if (withHeaders) {
            String headers = "ID\tFIX_TIME_KVANT\tTEXT_KVANT\tTAG_KVANT\tPERFORMER_KVANT_ID\tPERFORMER_KVANT\tATTENTION_KVANT\t" +
                    "COMMUNITY_ID\tCOMMUNITY\tSOURCE_ORIGIN_ID\tSOURCE_ORIGIN\tSOURCE_ID\n";

            data = headers + data;
        }

        return data;
    }
}
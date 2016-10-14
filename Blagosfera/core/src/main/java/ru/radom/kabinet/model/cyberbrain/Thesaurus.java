package ru.radom.kabinet.model.cyberbrain;

import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.utils.DateUtils;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = Thesaurus.TABLE_NAME)
public class Thesaurus extends LongIdentifiable implements CyberbrainExportData {
    public static final String TABLE_NAME = "cyberbrain_thesaurus";

    public static class Columns {
        public static final String IS_SERVICE_TAG = "is_service_tag";
        public static final String ESSENCE = "essence";
        public static final String SINONIM = "sinonim";
        public static final String FIX_DATE_ESSENCE = "fix_date_essence";
        public static final String ESSENCE_OWNER = "essence_owner";
        public static final String FREQUENCY_ESSENCE = "frequency_essence";
        public static final String ATTENTION_FREQUENCY = "attention_frequency";
        public static final String IS_PERSONAL_DATA = "is_personal_data";
        public static final String IS_OBJECT = "is_object";
        public static final String IS_NUMBERED = "is_numbered";
        public static final String COMMUNITY = "community_id";
        public static final String SOURCE_ORIGIN = "source_origin_id";
        public static final String SOURCE = "source_id";
    }

    //является служебным тегом
    @Column(name = Columns.IS_SERVICE_TAG)
    private Boolean isServiceTag;

    //название сущности
    @Column(name = Columns.ESSENCE, length = 512)
    private String essence;

    //название синонима сущности
    @Column(name = Columns.SINONIM, length = 512)
    private String sinonim;

    //время создания записи
    @Column(name = Columns.FIX_DATE_ESSENCE, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fixDateEssence;

    //пользователь указавший сущность впервые
    @JoinColumn(name = Columns.ESSENCE_OWNER, nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity essenceOwner;

    //частота встречаемости
    @Column(name = Columns.FREQUENCY_ESSENCE)
    private Integer frequencyEssence;

    //объем внимания (мин)
    @Column(name = Columns.ATTENTION_FREQUENCY)
    private Integer attentionFrequency;

    //объект является персональными данными
    @Column(name = Columns.IS_PERSONAL_DATA)
    private Boolean isPersonalData;

    //является объектом (если нет то это множество)
    @Column(name = Columns.IS_OBJECT)
    private Boolean isObject;

    //является номерным объектом
    @Column(name = Columns.IS_NUMBERED)
    private Boolean isNumbered;

    //идентификатор объединения к которому принадлежит запись
    @JoinColumn(name = Columns.COMMUNITY)
    @ManyToOne(fetch = FetchType.LAZY)
    private CommunityEntity community;

    //источник происхождения записи
    @JoinColumn(name = Columns.SOURCE_ORIGIN)
    @ManyToOne(fetch = FetchType.LAZY)
    private CyberbrainObject sourceOrigin;

    //идентификатор записи источника
    @Column(name = Columns.SOURCE)
    private Long source;

    public Boolean getIsServiceTag() {
        return isServiceTag;
    }

    public void setIsServiceTag(Boolean isServiceTag) {
        this.isServiceTag = isServiceTag;
    }

    public String getEssence() {
        return essence;
    }

    public void setEssence(String essence) {
        this.essence = essence;
    }

    public String getSinonim() {
        return sinonim;
    }

    public void setSinonim(String sinonim) {
        this.sinonim = sinonim;
    }

    public Date getFixDateEssence() {
        return fixDateEssence;
    }

    public void setFixDateEssence(Date fixDateEssence) {
        this.fixDateEssence = fixDateEssence;
    }

    public UserEntity getEssenceOwner() {
        return essenceOwner;
    }

    public void setEssenceOwner(UserEntity essenceOwner) {
        this.essenceOwner = essenceOwner;
    }

    public Integer getFrequencyEssence() {
        return frequencyEssence;
    }

    public void setFrequencyEssence(Integer frequencyEssence) {
        this.frequencyEssence = frequencyEssence;
    }

    public Integer getAttentionFrequency() {
        return attentionFrequency;
    }

    public void setAttentionFrequency(Integer attentionFrequency) {
        this.attentionFrequency = attentionFrequency;
    }

    public Boolean getIsPersonalData() {
        return isPersonalData;
    }

    public void setIsPersonalData(Boolean isPersonalData) {
        this.isPersonalData = isPersonalData;
    }

    public Boolean getIsObject() {
        return isObject;
    }

    public void setIsObject(Boolean isObject) {
        this.isObject = isObject;
    }

    public Boolean getIsNumbered() {
        return isNumbered;
    }

    public void setIsNumbered(Boolean isNumbered) {
        this.isNumbered = isNumbered;
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
        String strIsServiceTag = getIsServiceTag() != null ? (getIsServiceTag() ? "1" : "0") : "";
        String strEssence = getEssence() != null ? getEssence() : "";
        String strSinonim = getSinonim() != null ? getSinonim() : "";
        String strFixDateEssence = getFixDateEssence() != null ? DateUtils.dateToString(getFixDateEssence(), DateUtils.Format.DATE_TIME) : "";
        String strEssenceOwner = getEssenceOwner() != null ? getEssenceOwner().getFullName() : "";
        String strEssenceOwnerId = getEssenceOwner() != null ? getEssenceOwner().getId().toString() : "";
        String strFrequencyEssence = getFrequencyEssence() != null ? getFrequencyEssence().toString() : "";
        String strAttentionFrequency = getAttentionFrequency() != null ? getAttentionFrequency().toString() : "";
        String strIsPersonalData = getIsPersonalData() != null ? (getIsPersonalData() ? "1" : "0") : "";
        String strIsObject = getIsObject() != null ? (getIsObject() ? "1" : "0") : "";
        String strIsNumbered = getIsNumbered() != null ? (getIsNumbered() ? "1" : "0") : "";
        String strCommunity = getCommunity() != null ? getCommunity().getName() : "";
        String strCommunityId = getCommunity() != null ? getCommunity().getId().toString() : "";
        String strSourceOrigin = getSourceOrigin() != null ? getSourceOrigin().getName() : "";
        String strSourceOriginId = getSourceOrigin() != null ? getSourceOrigin().getId().toString() : "";
        String strSource = getSource() != null ? getSource().toString() : "";

        String data = getId().toString() + "\t" + strIsServiceTag + "\t" + strEssence + "\t" + strSinonim + "\t" +
                strFixDateEssence + "\t" + strEssenceOwnerId + "\t" + strEssenceOwner + "\t" + strFrequencyEssence + "\t" +
                strAttentionFrequency + "\t" + strIsPersonalData + "\t" + strIsObject + "\t" + strIsNumbered + "\t" +
                strCommunityId + "\t" + strCommunity + "\t" + strSourceOriginId + "\t" + strSourceOrigin + "\t" + strSource + "\n";

        if (withHeaders) {
            String headers = "ID\tIS_SERVICE_TAG\tESSENCE\tSINONIM\tFIX_DATE_ESSENCE\tESSENCE_OWNER_ID\tESSENCE_OWNER\t" +
                    "FREQUENCY_ESSENCE\tATTENTION_FREQUENC\tIS_PERSONAL_DATA\tIS_OBJECT\tIS_NUMBERED\t" +
                    "COMMUNITY_ID\tCOMMUNITY\tSOURCE_ORIGIN_ID\tSOURCE_ORIGIN\tSOURCE_ID\n";

            data = headers + data;
        }

        return data;
    }
}
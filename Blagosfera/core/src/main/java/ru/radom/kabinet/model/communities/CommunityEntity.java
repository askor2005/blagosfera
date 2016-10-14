package ru.radom.kabinet.model.communities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import ru.askor.blagosfera.data.jpa.entities.account.TransactionDetailEntity;
import ru.askor.blagosfera.domain.Address;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityAccessType;
import ru.askor.blagosfera.domain.community.CommunityData;
import ru.askor.blagosfera.domain.community.OkvedDomain;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateSetting;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.FieldsGroup;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.OkvedEntity;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.schema.CommunitySchemaEntity;
import ru.radom.kabinet.model.document.DocumentTemplateSettingEntity;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldFileEntity;
import ru.radom.kabinet.model.fields.FieldValueEntity;
import ru.radom.kabinet.model.fields.FieldsGroupEntity;
import ru.askor.blagosfera.domain.RadomAccount;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.utils.FieldConstants;
import ru.radom.kabinet.utils.ImagesUtils;

import javax.persistence.*;
import java.util.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name = "communities")
@Table(name = "communities")
public class CommunityEntity extends LongIdentifiable implements RadomAccount {

    public static final String DEFAULT_AVATAR_URL = "https://images.blagosfera.su/images/VGHF3HUFH5J/DVMVHWVAHJ.png";

    //название объединения
    @Column(length = 1000, nullable = false, unique = false)
    private String name;

    //доступ к объединению сторонним лицам
    @Column(name = "access_type", nullable = true)
    private CommunityAccessType accessType;

    //признак невидимое
    @Column(nullable = true)
    private Boolean invisible;

    @JsonIgnore
    @JoinColumn(name = "creator_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity creator;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "community", orphanRemoval = true)
    @OrderBy("id")
    private List<CommunityMemberEntity> members;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "community", orphanRemoval = true)
    @OrderBy("id")
    private List<OrganizationCommunityMemberEntity> organizationCommunityMembers;

    @Column(name = "avatar_url", length = 100, nullable = true)
    private String avatarUrl;

    @Column(name = "created_at", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "community", orphanRemoval = true, optional = true)
    private CommunitySchemaEntity schema;

    /**
     * Основной вид деятельности объединения
     */
    @JsonIgnore
    @JoinColumn(name = "main_okved_id", nullable = true)
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private OkvedEntity mainOkved;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "communities_to_okveds", joinColumns = @JoinColumn(name = "community_id"), inverseJoinColumns = @JoinColumn(name = "okved_id"))
    private Set<OkvedEntity> okveds = new HashSet<>();

    @JsonIgnore
    @JoinColumn(name = "parent_id", nullable = true, updatable = false)
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private CommunityEntity parent;

    @JsonIgnore
    @JoinColumn(name = "root_id", nullable = true)
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private CommunityEntity root;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @OrderBy("name")
    private List<CommunityEntity> children;

    @Column(nullable = false)
    private boolean deleted;

    @Column(name = "delete_comment", nullable = true)
    private String deleteComment;

    @JoinColumn(name = "deleter_id", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private UserEntity deleter;

    /**
     * Объединение в рамках юр лица - сертифицировано
     */
    @JsonIgnore
    @Column(name = "verified", nullable = true)
    private Boolean verified;

    /**
     * Участник системы, который провел сертификацию юр лица
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verifier_id", nullable = true)
    private UserEntity verifier;

    /**
     * Дата сертификации юр лица
     */
    @JsonIgnore
    @Column(name = "verification_date", nullable = true)
    private Date verificationDate;

    /**
     * Сфера деятельности объединения. Реализация через универсальные списки.
     */
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "ramera_communities_activity_scopes",
            joinColumns = {@JoinColumn(name = "community_id")},
            inverseJoinColumns = {@JoinColumn(name = "activity_scope_id")})
    private List<RameraListEditorItem> rameraActivityScopes;

    @Column(name = "need_create_documents")
    private Boolean needCreateDocuments;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {})
    @JoinTable(name = "community_document_template",
            joinColumns = {
                    @JoinColumn(name = "community_id", nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "document_template_id", nullable = false)})
    private List<DocumentTemplateSettingEntity> documentTemplateSettings = new ArrayList<>();

    public CommunityEntity() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommunityEntity)) return false;

        CommunityEntity that = (CommunityEntity) o;

        //return !(getId() != null ? !getId().equals(that.getId()) : that.getId() != null);
        return (getId() != null) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpen() {
        return CommunityAccessType.OPEN.equals(accessType);
    }

    public CommunityAccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(CommunityAccessType accessType) {
        this.accessType = accessType;
    }

    public Boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public UserEntity getCreator() {
        return creator;
    }

    public void setCreator(UserEntity creator) {
        this.creator = creator;
    }

    public List<CommunityMemberEntity> getMembers() {
        return members;
    }

    public void setMembers(List<CommunityMemberEntity> members) {
        this.members = members;
    }

    public List<OrganizationCommunityMemberEntity> getOrganizationCommunityMembers() {
        return organizationCommunityMembers;
    }

    public void setOrganizationCommunityMembers(List<OrganizationCommunityMemberEntity> organizationCommunityMembers) {
        this.organizationCommunityMembers = organizationCommunityMembers;
    }

    public Boolean getInvisible() {
        return invisible;
    }

    public void setInvisible(Boolean invisible) {
        this.invisible = invisible;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String getAvatar() {
        // return "/images/community/" + getId();
        return ImagesUtils.getResizeUrl(avatarUrl, "c250");
    }

    @Override
    @Deprecated
    public String getLink() {
        // TODO Нужно удалить
        return null;
        //return "/group/" + getSeoLink();
    }

    public CommunitySchemaEntity getSchema() {
        return schema;
    }

    public void setSchema(CommunitySchemaEntity schema) {
        this.schema = schema;
    }

    public OkvedEntity getMainOkved() {
        return mainOkved;
    }

    public void setMainOkved(OkvedEntity mainOkved) {
        this.mainOkved = mainOkved;
    }

    public Set<OkvedEntity> getOkveds() {
        return okveds;
    }

    public void setOkveds(Set<OkvedEntity> okveds) {
        // Количество дополнительных видов деятельности максимум - 56
        // TODO по хорошему надо сделать один API метод по редактированию объединения и туда заложить логику
        if (okveds != null && okveds.size() > 56) {
            okveds = ImmutableSet.copyOf(Iterables.limit(okveds, 56));
        }
        this.okveds = okveds;
    }

    public void addOkved(OkvedEntity okved) {
        this.okveds.add(okved);
    }

    public CommunityEntity getParent() {
        return parent;
    }

    public void setParent(CommunityEntity parent) {
        this.parent = parent;
    }

    public CommunityEntity getRoot() {
        return root;
    }

    public void setRoot(CommunityEntity root) {
        this.root = root;
    }

    public List<CommunityEntity> getChildren() {
        return children;
    }

    public void setChildren(List<CommunityEntity> children) {
        this.children = children;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getDeleteComment() {
        return deleteComment;
    }

    public void setDeleteComment(String deleteComment) {
        this.deleteComment = deleteComment;
    }

    public UserEntity getDeleter() {
        return deleter;
    }

    public void setDeleter(UserEntity deleter) {
        this.deleter = deleter;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public UserEntity getVerifier() {
        return verifier;
    }

    public void setVerifier(UserEntity verifier) {
        this.verifier = verifier;
    }

    public Date getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(Date verificationDate) {
        this.verificationDate = verificationDate;
    }

    public List<DocumentTemplateSettingEntity> getDocumentTemplateSettings() {
        return documentTemplateSettings;
    }

    public Boolean getNeedCreateDocuments() {
        return needCreateDocuments;
    }

    public void setNeedCreateDocuments(Boolean needCreateDocuments) {
        this.needCreateDocuments = needCreateDocuments;
    }

    @Override
    public String getObjectType() {
        return Discriminators.COMMUNITY;
    }

    public List<RameraListEditorItem> getRameraActivityScopes() {
        return rameraActivityScopes;
    }

    public void setRameraActivityScopes(List<RameraListEditorItem> rameraActivityScopes) {
        this.rameraActivityScopes = rameraActivityScopes;
    }

    @Override
    public String getIkp() {
        return String.valueOf(getId());
    }

    public static Community toDomainSafe(CommunityEntity communityEntity) {
        Community result = null;
        if (communityEntity != null) {
            result = communityEntity.toDomain();
        }
        return result;
    }

    public Community toDomain() {
        return toDomain(
                getName(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,0,
                false,
                null,
                null,
                null,
                null
        );
    }

    public Community toDomain(String fullRuName, String shortRuName, String fullEnName, String shortEnName,
                              String seoLink, String link, String announcement,
                              ParticipantsTypes communityType, RameraListEditorItem associationForm,
                              int membersCount, int subgroupsCount, boolean isWithAllData,
                              List<FieldEntity> fields, List<FieldValueEntity> fieldValues,
                              String factCountry, String registrationCountry) {
        CommunityAccessType communityAccessType = getAccessType();
        boolean isVisible = !BooleanUtils.toBoolean(isInvisible());
        User creator = getCreator() != null ? getCreator().toDomain() : null;
        final List<User> members = new ArrayList<>();
        final List<Community> communitiesMembers = new ArrayList<>();
        String avatarUrl = getAvatarUrl();
        Date createdAt = getCreatedAt();
        OkvedDomain mainOkved = (isWithAllData && this.getMainOkved() != null) ? this.getMainOkved().toDomain() : null;
        final List<OkvedDomain> additionalOkveds = new ArrayList<>();
        Community parent = null;
        Community root = null;
        final List<Community> children = new ArrayList<>();
        boolean isDeleted = isDeleted();
        String deleteComment = getDeleteComment();
        User deleter = getDeleter() != null ? getDeleter().toDomain() : null;
        boolean isVerified = BooleanUtils.toBooleanDefaultIfNull(getVerified(), false);
        User verifier = (isWithAllData && getVerifier() != null) ? getVerifier().toDomain() : null;
        Date verificationDate = getVerificationDate();
        final List<ListEditorItem> communityActivityScopes = new ArrayList<>();
        CommunityData communityData = null;
        ListEditorItem associationFormDomain = null;

        if (isWithAllData && this.getMembers() != null) {
            List<CommunityMemberEntity> communityMembers = this.getMembers();
            for (CommunityMemberEntity member : communityMembers) {
                if (member.getUser() != null) {
                    members.add(member.getUser().toDomain());
                }
            }
        }

        if (isWithAllData && this.getOrganizationCommunityMembers() != null) {
            List<OrganizationCommunityMemberEntity> communityMembers = this.getOrganizationCommunityMembers();
            for (OrganizationCommunityMemberEntity member : communityMembers) {
                communitiesMembers.add(member.getOrganization().toDomain());
            }
        }

        if (isWithAllData && this.getParent() != null) {
            parent = this.getParent().toDomain();
        }
        if (isWithAllData && this.getRoot() != null) {
            root = this.getRoot().toDomain();
        }
        if (isWithAllData && this.getChildren() != null) {
            List<CommunityEntity> childCommunities = this.getChildren();
            for (CommunityEntity communityEntity : childCommunities) {
                children.add(communityEntity.toDomain());
            }
        }

        if (fieldValues != null) {
            communityData = getCommunityDataFromFields(fields, fieldValues, factCountry, registrationCountry);
        }

        if (isWithAllData && getRameraActivityScopes() != null) {
            List<RameraListEditorItem> activityScopes = getRameraActivityScopes();
            for (RameraListEditorItem activityScope : activityScopes) {
                communityActivityScopes.add(activityScope.toDomain());
            }
        }

        associationFormDomain = associationForm != null ? associationForm.toDomain() : null;

        if (isWithAllData && this.getOkveds() != null) {
            Set<OkvedEntity> okvedList = this.getOkveds();
            for (OkvedEntity okved : okvedList) {
                additionalOkveds.add(okved.toDomain());
            }
        }

        List<DocumentTemplateSetting> documentTemplateSettings = null;
        if (getDocumentTemplateSettings() != null && !getDocumentTemplateSettings().isEmpty()) {
            documentTemplateSettings = new ArrayList<>();
            documentTemplateSettings.addAll(DocumentTemplateSettingEntity.toDomainList(getDocumentTemplateSettings()));
        }

        if (link == null) {
            link = "/group/" + getId();
        }

        Community community = new Community(
                getId(),
                fullRuName,
                shortRuName,
                fullEnName,
                shortEnName,
                communityAccessType,
                isVisible,
                creator,
                members,
                communitiesMembers,
                avatarUrl,
                createdAt,
                membersCount,
                subgroupsCount,
                mainOkved,
                additionalOkveds,
                parent,
                root,
                children,
                isDeleted,
                deleteComment,
                deleter,
                isVerified,
                verifier,
                verificationDate,
                communityActivityScopes,
                associationFormDomain,
                communityData,
                seoLink,
                link,
                announcement,
                communityType,
                BooleanUtils.toBooleanDefaultIfNull(needCreateDocuments, false),
                documentTemplateSettings
        );

        if (getSchema() != null) {
            community.setSchema(getSchema().toDomain());
        }

        return community;
    }

    private CommunityData getCommunityDataFromFields(List<FieldEntity> fields, List<FieldValueEntity> fieldValues, String factCountry, String registrationCountry) {
        CommunityData result = null;
        String region = null;
        String district = null;
        String city = null;
        String street = null;
        String building = null;
        String geoPosition = null;
        String geoLocation = null;
        String office = null;
        String officeType = "офис";

        String registrationRegion = null;
        String registrationDistrict = null;
        String registrationCity = null;
        String registrationStreet = null;
        String registrationBuilding = null;
        String registrationGeoPosition = null;
        String registrationGeoLocation = null;
        String registrationOffice = null;
        String registrationOfficeType = "офис";

        String inn = null;
        String description = null;

        if (CollectionUtils.isNotEmpty(fields) && fieldValues != null) {
            result = new CommunityData();
            Map<Long, FieldsGroup> allFieldsGroups = new HashMap<>();

            for (FieldEntity field : fields) {
                FieldValueEntity fieldValue = null;
                for (FieldValueEntity fv : fieldValues) {
                    if (fv.getField().equals(field)) {
                        fieldValue = fv;
                        break;
                    }
                }

                String strValue = FieldsService.getFieldStringValue(fieldValue);
                switch (field.getInternalName()) {
                    case FieldConstants.COMMUNITY_INN:
                        inn = strValue;
                        break;
                    case FieldConstants.COMMUNITY_DESCRIPTION:
                        description = strValue;
                        break;

                    case FieldConstants.COMMUNITY_FACT_REGION:
                        region = strValue;
                        break;
                    case FieldConstants.COMMUNITY_FACT_DISTRICT:
                        district = strValue;
                        break;
                    case FieldConstants.COMMUNITY_FACT_CITY:
                        city = strValue;
                        break;
                    case FieldConstants.COMMUNITY_FACT_STREET:
                        street = strValue;
                        break;
                    case FieldConstants.COMMUNITY_FACT_BUILDING:
                        building = strValue;
                        break;
                    case FieldConstants.COMMUNITY_FACT_GEO_POSITION:
                        geoPosition = strValue;
                        break;
                    case FieldConstants.COMMUNITY_FACT_GEO_LOCATION:
                        geoLocation = strValue;
                        break;
                    case FieldConstants.COMMUNITY_FACT_OFFICE:
                        office = strValue;
                        break;

                    // Юридический адрес юр лица
                    case FieldConstants.COMMUNITY_LEGAL_REGION:
                        registrationRegion = strValue;
                        break;
                    case FieldConstants.COMMUNITY_LEGAL_DISTRICT:
                        registrationDistrict = strValue;
                        break;
                    case FieldConstants.COMMUNITY_LEGAL_CITY:
                        registrationCity = strValue;
                        break;
                    case FieldConstants.COMMUNITY_LEGAL_STREET:
                        registrationStreet = strValue;
                        break;
                    case FieldConstants.COMMUNITY_LEGAL_BUILDING:
                        registrationBuilding = strValue;
                        break;
                    case FieldConstants.COMMUNITY_LEGAL_GEO_POSITION:
                        registrationGeoPosition = strValue;
                        break;
                    case FieldConstants.COMMUNITY_LEGAL_GEO_LOCATION:
                        registrationGeoLocation = strValue;
                        break;
                    case FieldConstants.COMMUNITY_LEGAL_OFFICE:
                        registrationOffice = strValue;
                        break;
                }

                FieldsGroupEntity fieldsGroupEntity = field.getFieldsGroup();
                FieldsGroup fieldsGroup;
                if (!allFieldsGroups.containsKey(fieldsGroupEntity.getId())) {
                    fieldsGroup = fieldsGroupEntity.toDomain(false, false);
                    fieldsGroup.getAssociationForms().addAll(RameraListEditorItem.toDomainList(fieldsGroupEntity.getAssociationForms(), false, false));
                    allFieldsGroups.put(fieldsGroupEntity.getId(), fieldsGroup);
                } else {
                    fieldsGroup = allFieldsGroups.get(fieldsGroupEntity.getId());
                }

                List<FieldFileEntity> fieldFiles = fieldValue != null ? fieldValue.getFieldFiles() : null;
                boolean isHidden = fieldValue != null ? fieldValue.isHidden() : false;

                fieldsGroup.getFields().add(
                        new Field(
                                field.getId(),
                                strValue,
                                field.getInternalName(),
                                field.getName(),
                                isHidden,
                                field.isHideable(),
                                field.getComment(),
                                field.getType(),
                                field.getExample(),
                                field.getPoints(),
                                field.isRequired(),
                                field.getPosition(),
                                BooleanUtils.toBooleanDefaultIfNull(field.getAttachedFile(), false),
                                FieldFileEntity.toDomainList(fieldFiles),
                                field.getFieldsGroup().getInternalName(),
                                field.getMask(),
                                field.getPlaceholder()
                        )
                );
            }

            Address actualAddress = new Address(
                    factCountry,
                    region,
                    district,
                    city,
                    street,
                    building,
                    geoPosition,
                    geoLocation,
                    office,
                    officeType
            );
            Address registrationAddress = new Address(
                    registrationCountry,
                    registrationRegion,
                    registrationDistrict,
                    registrationCity,
                    registrationStreet,
                    registrationBuilding,
                    registrationGeoPosition,
                    registrationGeoLocation,
                    registrationOffice,
                    registrationOfficeType
            );
            result.setActualAddress(actualAddress);
            result.setRegistrationAddress(registrationAddress);
            result.setFieldGroups(new ArrayList<>(allFieldsGroups.values()));
            result.setDescription(description);
            result.setInn(inn);
        }
        return result;
    }


}
package ru.askor.blagosfera.domain.community;

import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.Verifiable;
import ru.askor.blagosfera.domain.community.schema.CommunitySchema;
import ru.askor.blagosfera.domain.document.IDocumentParticipant;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateSetting;
import ru.askor.blagosfera.domain.field.IFieldOwner;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.askor.blagosfera.domain.loa.LetterOfAuthorityScope;
import ru.askor.blagosfera.domain.notification.NotificationSender;
import ru.askor.blagosfera.domain.user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by vgusev on 08.03.2016.
 */
public class Community implements IDocumentParticipant, Verifiable, LetterOfAuthorityScope, IFieldOwner, Serializable, NotificationSender {

    public static final long serialVersionUID = 1L;

    // Мнемокод формы объединения - Потребительское общество
    public static final String COOPERATIVE_SOCIETY_LIST_ITEM_CODE = "community_cooperative_society";

    // Мнемокод формы объединения - КУч
    public static final String COOPERATIVE_PLOT_ASSOCIATION_FORM_CODE = "cooperative_plot";

    // Мнемокод формы объединения - Редакторы благосферы
    public static final String BLAGOSFERA_EDITORS_ASSOCIATION_FORM_CODE = "blagosfera_editors";

    private Long id;

    /**
     * Полное название на русском языке
     */
    private String fullRuName;

    /**
     * Короткое название на русском языке
     */
    private String shortRuName;

    /**
     * Полное название на англ. языке
     */
    private String fullEnName;

    /**
     * Короткое название на англ. языке
     */
    private String shortEnName;

    /**
     * доступ к объединению сторонним лицам
     */
    private CommunityAccessType accessType;

    /**
     * признак невидимое
     */
    private boolean isVisible;

    /**
     * Создатель объединения
     */
    private User creator;

    /**
     * Участники объединения - физ лица
     */
    private List<User> members = new ArrayList<>();

    /**
     * Участники объединения - юр лица
     */
    private List<Community> communitiesMembers = new ArrayList<>();

    /**
     * Ссылка на картинку объединения
     */
    private String avatarUrl;

    /**
     * Дата создания объединения
     */
    private Date createdAt;

    /**
     * Количество участников
     */
    private Integer membersCount;

    /**
     * Количество подгрупп
     */
    private Integer subgroupsCount;

    /**
     * Организационная схема объединения
     */
    private CommunitySchema schema;

    /**
     * Основной вид деятельности объединения
     */
    private OkvedDomain mainOkved;

    /**
     * Дополнительные виды деятельности
     */
    private List<OkvedDomain> okveds = new ArrayList<>();

    /**
     * Родительское объединение
     */
    private Community parent;

    /**
     * Корневое объединени
     */
    private Community root;

    /**
     * Дочерние объединения
     */
    private List<Community> children = new ArrayList<>();

    /**
     * Признак, что объединение было удалено
     */
    private boolean isDeleted;

    /**
     * Комментарий при удалении объединения
     */
    private String deleteComment;

    /**
     * Участник который удалил объединение
     */
    private User deleter;

    /**
     * Объединение в рамках юр лица - сертифицировано
     */
    private boolean isVerified;

    /**
     * Участник системы, который провел сертификацию юр лица
     */
    private User verifier;

    /**
     * Дата сертификации юр лица
     */
    private Date verificationDate;

    /**
     * Сфера деятельности объединения.
     * (Семейно-Родовой Сектор, Государственный Сектор...)
     */
    private List<ListEditorItem> activityScopes = new ArrayList<>();

    /**
     *
     */
    private ListEditorItem associationForm;

    /**
     * Дополнительные данные объединения
     */
    private CommunityData communityData;

    /**
     *
     */
    private String seoLink;

    /**
     * Ссылка на объединение
     */
    private String link;

    /**
     * Краткое описание целей и задач
     */
    private String announcement;

    /**
     *
     */
    private ParticipantsTypes communityType;

    /**
     * Флаг - необходимо при вступлении в объединение создаватьдокументы для подписания новому участнику
     */
    private boolean needCreateDocuments;

    /**
     *
     */
    private List<DocumentTemplateSetting> documentTemplateSettings = new ArrayList<>();

    public Community() {}

    public Community(Long id, String fullRuName, String shortRuName, String fullEnName,
                     String shortEnName, CommunityAccessType accessType, boolean isVisible,
                     User creator, List<User> members, List<Community> communitiesMembers,
                     String avatarUrl, Date createdAt, Integer membersCount,
                     Integer subgroupsCount, OkvedDomain mainOkved, List<OkvedDomain> okveds,
                     Community parent, Community root, List<Community> children,
                     boolean isDeleted, String deleteComment, User deleter, boolean isVerified,
                     User verifier, Date verificationDate, List<ListEditorItem> activityScopes,
                     ListEditorItem associationForm, CommunityData communityData, String seoLink,
                     String link, String announcement, ParticipantsTypes communityType,
                     boolean needCreateDocuments,
                     List<DocumentTemplateSetting> documentTemplateSettings) {
        setId(id);
        setFullRuName(fullRuName);
        setShortRuName(shortRuName);
        setFullEnName(fullEnName);
        setShortEnName(shortEnName);
        setAccessType(accessType);
        setVisible(isVisible);
        setCreator(creator);
        getMembers().addAll(members);
        getCommunitiesMembers().addAll(communitiesMembers);
        setAvatarUrl(avatarUrl);
        setCreatedAt(createdAt);
        setMembersCount(membersCount);
        setSubgroupsCount(subgroupsCount);
        setMainOkved(mainOkved);
        getOkveds().addAll(okveds);
        setParent(parent);
        setRoot(root);
        getChildren().addAll(children);
        setDeleted(isDeleted);
        setDeleteComment(deleteComment);
        setDeleter(deleter);
        setVerified(isVerified);
        setVerifier(verifier);
        setVerificationDate(verificationDate);
        getActivityScopes().addAll(activityScopes);
        setAssociationForm(associationForm);
        setCommunityData(communityData);
        setSeoLink(seoLink);
        setLink(link);
        setAnnouncement(announcement);
        setCommunityType(communityType);
        setNeedCreateDocuments(needCreateDocuments);
        if (documentTemplateSettings != null) {
            getDocumentTemplateSettings().addAll(documentTemplateSettings);
        }
    }

    public String getName() {
        return getFullRuName();
    }

    public String getIkp() {
        return String.valueOf(getId());
    }

    public String getAvatar() {
        return getAvatarUrl();
    }

    public boolean isRoot() {
        return root == null;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullRuName() {
        return fullRuName;
    }

    public void setFullRuName(String fullRuName) {
        this.fullRuName = fullRuName;
    }

    public String getShortRuName() {
        return shortRuName;
    }

    public void setShortRuName(String shortRuName) {
        this.shortRuName = shortRuName;
    }

    public String getFullEnName() {
        return fullEnName;
    }

    public void setFullEnName(String fullEnName) {
        this.fullEnName = fullEnName;
    }

    public String getShortEnName() {
        return shortEnName;
    }

    public void setShortEnName(String shortEnName) {
        this.shortEnName = shortEnName;
    }

    public CommunityAccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(CommunityAccessType accessType) {
        this.accessType = accessType;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<User> getMembers() {
        return members;
    }

    public List<Community> getCommunitiesMembers() {
        return communitiesMembers;
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

    public Integer getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(Integer membersCount) {
        this.membersCount = membersCount;
    }

    public Integer getSubgroupsCount() {
        return subgroupsCount;
    }

    public void setSubgroupsCount(Integer subgroupsCount) {
        this.subgroupsCount = subgroupsCount;
    }

    public CommunitySchema getSchema() {
        return schema;
    }

    public void setSchema(CommunitySchema schema) {
        this.schema = schema;
    }

    public OkvedDomain getMainOkved() {
        return mainOkved;
    }

    public void setMainOkved(OkvedDomain mainOkved) {
        this.mainOkved = mainOkved;
    }

    public List<OkvedDomain> getOkveds() {
        return okveds;
    }

    public Community getParent() {
        return parent;
    }

    public void setParent(Community parent) {
        this.parent = parent;
    }

    public Community getRoot() {
        return root;
    }

    public void setRoot(Community root) {
        this.root = root;
    }

    public List<Community> getChildren() {
        return children;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getDeleteComment() {
        return deleteComment;
    }

    public void setDeleteComment(String deleteComment) {
        this.deleteComment = deleteComment;
    }

    public User getDeleter() {
        return deleter;
    }

    public void setDeleter(User deleter) {
        this.deleter = deleter;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public User getVerifier() {
        return verifier;
    }

    public void setVerifier(User verifier) {
        this.verifier = verifier;
    }

    public Date getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(Date verificationDate) {
        this.verificationDate = verificationDate;
    }

    public List<ListEditorItem> getActivityScopes() {
        return activityScopes;
    }

    public ListEditorItem getAssociationForm() {
        return associationForm;
    }

    public void setAssociationForm(ListEditorItem associationForm) {
        this.associationForm = associationForm;
    }

    public CommunityData getCommunityData() {
        return communityData;
    }

    public void setCommunityData(CommunityData communityData) {
        this.communityData = communityData;
    }

    public String getSeoLink() {
        return seoLink;
    }

    public void setSeoLink(String seoLink) {
        this.seoLink = seoLink;
    }

    @Override
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public ParticipantsTypes getCommunityType() {
        return communityType;
    }

    public void setCommunityType(ParticipantsTypes communityType) {
        this.communityType = communityType;
    }

    public boolean isNeedCreateDocuments() {
        return needCreateDocuments;
    }

    public void setNeedCreateDocuments(boolean needCreateDocuments) {
        this.needCreateDocuments = needCreateDocuments;
    }

    public List<DocumentTemplateSetting> getDocumentTemplateSettings() {
        return documentTemplateSettings;
    }


}

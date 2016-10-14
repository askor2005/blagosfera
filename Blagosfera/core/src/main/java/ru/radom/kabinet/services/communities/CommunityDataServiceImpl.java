package ru.radom.kabinet.services.communities;

/**
 * Created by vgusev on 09.03.2016.
 */

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import lombok.Data;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityMemberRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.OrganizationCommunityMemberRepository;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentTemplateSettingRepository;
import ru.askor.blagosfera.data.jpa.services.settings.SystemSettingService;
import ru.askor.blagosfera.data.jpa.specifications.community.CommunityMemberSpecifications;
import ru.askor.blagosfera.data.jpa.specifications.community.CommunitySpecifications;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.community.*;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateSetting;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.FieldFile;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.OkvedDao;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.dao.communities.dto.FieldValueParameterDto;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.dao.fields.FieldValueDao;
import ru.radom.kabinet.dao.rameralisteditor.RameraListEditorItemDAO;
import ru.radom.kabinet.dao.web.CommunitySectionDao;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.OkvedEntity;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.model.document.DocumentTemplateSettingEntity;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldFileEntity;
import ru.radom.kabinet.model.fields.FieldValueEntity;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.radom.kabinet.model.web.CommunitySection;
import ru.radom.kabinet.services.document.DocumentTemplateSettingService;
import ru.radom.kabinet.services.field.FieldException;
import ru.radom.kabinet.services.field.FieldValidateResult;
import ru.radom.kabinet.services.field.FieldValidatorBundle;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.utils.*;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для работы с моделью объединения
 * Created by vgusev on 08.03.2016.
 */
@Service("communityDomainService")
@Transactional
public class CommunityDataServiceImpl implements CommunityDataService {
    private static final Logger logger = LoggerFactory.createLogger(CommunityDataService.class);
    @Autowired
    private SystemSettingService systemSettingsService;

    private static final String GET_BY_ID_FULL_DATA_CACHE = "communityFullData";

    private static final String GET_BY_ID_MEDIUM_DATA_CACHE = "communityMediumData";

    private static final String GET_BY_ID_MIN_DATA_CACHE = "communityMinData";

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CommunityMemberRepository communityMemberRepository;

    @Autowired
    private OrganizationCommunityMemberRepository organizationCommunityMemberRepository;

    @Autowired
    private FieldValueDao fieldValueDao;

    @Autowired
    private FieldDao fieldDao;

    /*@Autowired
    private FieldsGroupDao fieldsGroupDao;*/

    @Autowired
    private RameraListEditorItemDAO rameraListEditorItemDAO;

    @Autowired
    private CommunitySectionDao communitySectionDao;

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private OkvedDao okvedDao;

    // TODO нужно будет выпилить и переделать всё на repository
    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private FieldsService fieldsService;

    @Autowired
    private CommunityCustomFieldValidator communityCustomFieldValidator;

    @Autowired
    private DocumentTemplateSettingService documentTemplateSettingService;

    @Autowired
    private DocumentTemplateSettingRepository documentTemplateSettingRepository;

    private FieldValidatorBundle fieldValidatorBundle;

    @Autowired
    private void setFieldValidatorBundle(FieldValidatorBundle fieldValidatorBundle) {
        this.fieldValidatorBundle = fieldValidatorBundle;
        this.fieldValidatorBundle.setCustomFieldValidator(communityCustomFieldValidator);
    }

    private List<String> COMMUNITY_NAMES_FIELD_NAMES = Arrays.asList(
            FieldConstants.COMMUNITY_FULL_RU_NAME, FieldConstants.COMMUNITY_SHORT_RU_NAME,
            FieldConstants.COMMUNITY_FULL_EN_NAME, FieldConstants.COMMUNITY_SHORT_EN_NAME,
            FieldConstants.COMMUNITY_SHORT_LINK_NAME, FieldConstants.COMMUNITY_BRIEF_DESCRIPTION,
            FieldConstants.COMMUNITY_TYPE, FieldConstants.COMMUNITY_ASSOCIATION_FORM
    );

    private String getSeoLink(CommunityEntity communityEntity) {
        FieldValueEntity fieldValue = fieldValueDao.get(communityEntity, FieldConstants.COMMUNITY_SHORT_LINK_NAME);
        return FieldsService.getFieldStringValue(fieldValue);
    }

    @Data
    private class CommunityRequiredFields {
        private String fullRuName;
        private String shortRuName;
        private String fullEnName;
        private String shortEnName;
        private String seoLink;
        private String link;
        private String announcement;
        private ParticipantsTypes communityType;
        private Long associationFormId;
    }

    private CommunityRequiredFields getCommunityRequiredFields(CommunityEntity communityEntity) {
        CommunityRequiredFields communityRequiredFields = new CommunityRequiredFields();
        List<FieldValueEntity> fieldValues = fieldValueDao.getByFieldList(communityEntity, COMMUNITY_NAMES_FIELD_NAMES);
        for (FieldValueEntity fieldValue : fieldValues) {
            String value = FieldsService.getFieldStringValue(fieldValue);
            switch (fieldValue.getField().getInternalName()) {
                case FieldConstants.COMMUNITY_FULL_RU_NAME:
                    communityRequiredFields.setFullRuName(value);
                    break;
                case FieldConstants.COMMUNITY_SHORT_RU_NAME:
                    communityRequiredFields.setShortRuName(value);
                    break;
                case FieldConstants.COMMUNITY_FULL_EN_NAME:
                    communityRequiredFields.setFullEnName(value);
                    break;
                case FieldConstants.COMMUNITY_SHORT_EN_NAME:
                    communityRequiredFields.setShortEnName(value);
                    break;
                case FieldConstants.COMMUNITY_SHORT_LINK_NAME:
                    if (value == null) {
                        value = String.valueOf(communityEntity.getId());
                    }
                    communityRequiredFields.setLink("/group/" + value);
                    communityRequiredFields.setSeoLink(value);
                    break;
                case FieldConstants.COMMUNITY_BRIEF_DESCRIPTION:
                    communityRequiredFields.setAnnouncement(value);
                    break;
                case FieldConstants.COMMUNITY_TYPE:
                    try {
                        communityRequiredFields.setCommunityType(ParticipantsTypes.valueOf(value));
                    } catch (Exception e) {
                        // do nothing
                    }
                    break;
                case FieldConstants.COMMUNITY_ASSOCIATION_FORM: // ИД формы объединения
                    communityRequiredFields.setAssociationFormId(VarUtils.getLong(value, null));
                    break;
            }
        }
        if (communityRequiredFields.getLink() == null) {
            communityRequiredFields.setLink("/group/" + String.valueOf(communityEntity.getId()));
        }
        return communityRequiredFields;
    }

    private FieldValueEntity getFieldValue(List<FieldValueEntity> fieldValues, String internalName) {
        FieldValueEntity result = null;
        if (fieldValues != null && !fieldValues.isEmpty()) {
            for (FieldValueEntity fieldValue : fieldValues) {
                if (fieldValue.getField().getInternalName().equals(internalName)) {
                    result = fieldValue;
                    break;
                }
            }
        }
        return result;
    }

    private String getListEditorValue(FieldValueEntity fieldValue) {
        String result = null;
        String rameraItemId = fieldValue != null && fieldValue.getStringValue() != null ? fieldValue.getStringValue() : null;
        Long itemId = VarUtils.getLong(rameraItemId, null);
        if (itemId != null) {
            RameraListEditorItem rameraListEditorItem = rameraListEditorItemDAO.getById(itemId);
            if (rameraListEditorItem != null) {
                result = rameraListEditorItem.getText();
            }
        }
        return result;
    }

    private String getListEditorValue(List<FieldValueEntity> fieldValues, String internalName) {
        String result = null;
        FieldValueEntity fieldValue = getFieldValue(fieldValues, internalName);
        if (fieldValue != null) {
            result = getListEditorValue(fieldValue);
        }
        return result;
    }

    /**
     * Загрузить объединение со всеми данными
     * @param communityId
     * @return
     */
    @Override
    //@Cacheable(value = GET_BY_ID_FULL_DATA_CACHE, key = "#communityId")
    public Community getByIdFullData(Long communityId) {
        CommunityEntity communityEntity = communityRepository.findOne(communityId);
        List<FieldValueEntity> fieldValues = fieldValueDao.getByObject(communityEntity);
        return toDomainData(communityEntity, fieldValues, true);
    }

    @Override
    public Community getByIdMediumData(Long communityId, List<String> fieldNames) {
        CommunityEntity community = communityRepository.findOne(communityId);
        List<FieldValueEntity> fieldValues = fieldValueDao.getByFieldList(community, fieldNames);
        return toDomainData(community, fieldValues, true);
    }

    @Override
    //@Cacheable(value = GET_BY_ID_MEDIUM_DATA_CACHE, key = "#communityId")
    public Community getByIdMediumData(Long communityId) {
        return toDomainData(communityRepository.findOne(communityId), Collections.emptyList(), true);
    }

    /**
     * Загрузить объединение со всеми данными
     * @param communityId
     * @return
     */
    @Override
    //@Cacheable(value = GET_BY_ID_MIN_DATA_CACHE, key = "#communityId")
    public Community getByIdMinData(Long communityId) {
        return toDomainData(communityRepository.findOne(communityId), null, false);
    }

    private Community toDomainData(CommunityEntity communityEntity, List<FieldValueEntity> fieldValues, boolean withAllData) {
        Community community = null;
        if (withAllData || fieldValues != null) {
            CommunityRequiredFields communityRequiredFields = getCommunityRequiredFields(communityEntity);

            String factCountry = null;
            String registrationCountry = null;
            List<FieldEntity> fields = null;
            if (fieldValues != null) {
                fields = fieldDao.getListByObjectType(Discriminators.COMMUNITY);
                factCountry = getListEditorValue(fieldValues, FieldConstants.COMMUNITY_LEGAL_FACT_COUNTRY);
                registrationCountry = getListEditorValue(fieldValues, FieldConstants.COMMUNITY_LEGAL_REGISTRATION_COUNTRY);
            }

            if (fields != null) {
                List<FieldEntity> foundFields = new ArrayList<>();
                if (ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.equals(communityRequiredFields.getCommunityType())) {
                    for (FieldEntity field : fields) {
                        if (field.getFieldsGroup() != null && field.getFieldsGroup().getInternalName() != null) {
                            if (!field.getFieldsGroup().getInternalName().startsWith("COMMUNITY_WITHOUT_ORGANIZATION_")) {
                                foundFields.add(field);
                            }
                        }
                    }
                } else if (ParticipantsTypes.COMMUNITY_WITHOUT_ORGANIZATION.equals(communityRequiredFields.getCommunityType())) {
                    for (FieldEntity field : fields) {
                        if (field.getFieldsGroup() != null && field.getFieldsGroup().getInternalName() != null) {
                            if (field.getFieldsGroup().getInternalName().startsWith("COMMUNITY_WITHOUT_ORGANIZATION_") ||
                                    field.getFieldsGroup().getInternalName().equals("COMMUNITY_COMMON")) {
                                foundFields.add(field);
                            }
                        }
                    }
                }

                fields = foundFields;
            }

            RameraListEditorItem associationForm = null;
            if (communityRequiredFields.getAssociationFormId() != null) {
                associationForm = rameraListEditorItemDAO.getById(communityRequiredFields.getAssociationFormId());
            }

            int membersCount = communityRepository.getMembersCount(communityEntity.getId());
            int subgroupsCount = communityRepository.getSubgroupsCount(communityEntity.getId());

            if (communityEntity != null) {
                community = communityEntity.toDomain(
                        communityRequiredFields.getFullRuName(),
                        communityRequiredFields.getShortRuName(),
                        communityRequiredFields.getFullEnName(),
                        communityRequiredFields.getShortEnName(),
                        communityRequiredFields.getSeoLink(),
                        communityRequiredFields.getLink(),
                        communityRequiredFields.getAnnouncement(),
                        communityRequiredFields.getCommunityType(),
                        associationForm,
                        membersCount, subgroupsCount,
                        withAllData,
                        fields,
                        fieldValues,
                        factCountry,
                        registrationCountry);
            }
        } else {
            community = communityEntity.toDomain();
        }
        return community;
    }

    @Override
    public List<Community> getByFieldsMinData(FieldValueParameterDto... fieldValueParameters) {
        return toDomainMinDataListSafe(communityRepository.findAll(CommunitySpecifications.findByFieldValues(fieldValueParameters)));
    }

    private List<Community> toDomainMinDataListSafe(List<CommunityEntity> communities) {
        return communities.stream().map(community -> toDomainData(community, null, false)).collect(Collectors.toList());
    }

    @Override
    public List<Community> getByEmptyFieldMinData(String fieldName) {
        FieldEntity field = fieldDao.getByInternalName(fieldName);
        List<CommunityEntity> entities = new ArrayList<>();
        List<FieldValueEntity> fieldValues = fieldValueDao.getListWhereEmptyValue(field);
        entities.addAll(
                fieldValues.stream()
                        .filter(fieldValue -> fieldValue != null)
                        .map(fieldValue -> (CommunityEntity) fieldValue.getObject())
                        .collect(Collectors.toList()));
        List<Community> result = new ArrayList<>();
        for (CommunityEntity community : entities) {
            result.add(toDomainData(community, null, false));
        }
        return result;
    }

    @Override
    public List<Community> getTopVisitForUser(Long userId, Long page, String name) {
        long perPage = Integer.parseInt(systemSettingsService.getSystemSetting("sharer.right.communities.pagesize"));
        Long[] ids = name.length() > 0 ?  communityRepository.getTopVisitUserCommunitiesByName(userId, page * perPage, perPage, DateUtils.add(new Date(), Calendar.MONTH, -1), "%"+name.toUpperCase()+"%") : communityRepository.getTopVisitUserCommunities(userId,page*perPage,perPage,DateUtils.add(new Date(),Calendar.MONTH,-1));
        ArrayList<Community> result = new ArrayList<>();
        for (Long id : ids) {
            result.add(getByIdMediumData(id));
        }
        return result;
    }

    /**
     * Фильтрация списка объединений
     * @param userId
     * @param isAdmin
     * @param statusList
     * @param creator
     * @param firstResult
     * @param maxResults
     * @param query
     * @param accessType
     * @param communityType
     * @param activityScopeId
     * @param parent
     * @param checkParent
     * @param deleted
     * @param orderBy
     * @param asc
     * @return
     */
    @Override
    //@Cacheable("communityList")
    public List<Community> getList(
            Long userId, boolean isAdmin, List<CommunityMemberStatus> statusList,
            Boolean creator, int firstResult, int maxResults, String query,
            CommunityAccessType accessType, String communityType, Long activityScopeId,
            CommunityEntity parent, boolean checkParent, Boolean deleted, String orderBy, boolean asc) {

        List<CommunityEntity> communityEntities = communityDao.getList(userId, isAdmin, statusList,
                creator, firstResult, maxResults, query,
                accessType, communityType, activityScopeId,
                parent, checkParent, deleted, orderBy, asc);

        List<Community> result = new ArrayList<>();
        for (CommunityEntity community : communityEntities) {
            result.add(toDomainData(community, null, true));
        }

        return result;
    }

    /**
     * Количество объединений в фильтрации
     * @param userId
     * @param isAdmin
     * @param statusList
     * @param creator
     * @param query
     * @param accessType
     * @param communityType
     * @param activityScopeId
     * @param parent
     * @param checkParent
     * @param deleted
     * @return
     */
    @Override
    //@Cacheable("communityListCount")
    public long getListCount(
            Long userId, boolean isAdmin, List<CommunityMemberStatus> statusList,
            Boolean creator, String query, CommunityAccessType accessType, String communityType,
            Long activityScopeId, CommunityEntity parent, boolean checkParent, Boolean deleted) {
        return communityDao.getListCount(
                userId, isAdmin, statusList,
                creator, query, accessType, communityType,
                activityScopeId, parent, checkParent, deleted);
    }

    @Override
    //@Cacheable("searchCommunityMember")
    public List<CommunityMember> getByCommunityIdsAndUserId(List<Long> communityIds, Long userId) {
        List<CommunityMemberEntity> communityMemberEntities = communityMemberRepository.findByCommunity_IdInAndUser_Id(communityIds, userId);
        return CommunityMemberEntity.toDomainList(communityMemberEntities);
    }

    @Override
    //@Cacheable("allCommunitySections")
    public List<CommunitySectionDomain> getAllCommunitySections() {
        List<CommunitySection> communitySections = communitySectionDao.getRoots();
        return CommunitySection.toDomainList(communitySections, true);
    }

    @Override
    //@Cacheable("communitySection")
    public CommunitySectionDomain getCommunitySectionByLink(String link) {
        CommunitySection communitySection = communitySectionDao.getByLink(link);
        CommunitySectionDomain result = null;
        if (communitySection != null) {
            result = communitySection.toDomain(false);
        }
        return result;
    }

    @Override
    //@Cacheable("isSharerMember")
    public boolean isSharerMember(Long communityId, Long userId) {
        CommunityMemberEntity member = communityMemberRepository.findByCommunity_IdAndUser_Id(communityId, userId);
        return member != null;
    }

    @Override
    public boolean isConsumerSociety(Long communityId) {
        RameraListEditorItem poAssociationForm = rameraListEditorItemDAO.getByCode(Community.COOPERATIVE_SOCIETY_LIST_ITEM_CODE);
        RameraListEditorItem kuchAssociationForm = rameraListEditorItemDAO.getByCode(Community.COOPERATIVE_PLOT_ASSOCIATION_FORM_CODE);
        Community community = getByIdMediumData(communityId);
        Long associationFormId = community.getAssociationForm() != null ? community.getAssociationForm().getId() : null;
        boolean result = false;
        if (associationFormId != null && (associationFormId.equals(poAssociationForm.getId()) || associationFormId.equals(kuchAssociationForm.getId()))) {
            result = true;
        }
        return result;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = GET_BY_ID_FULL_DATA_CACHE, key = "#community.id"),
            @CacheEvict(value = GET_BY_ID_MIN_DATA_CACHE, key = "#community.id"),
            @CacheEvict(value = GET_BY_ID_MEDIUM_DATA_CACHE, key = "#community.id")
    })
    public Community save(Community community, User editor) {
        Community result;
        if (community.getId() == null) {
            result = createCommunity(community, editor);
        } else {
            result = editCommunity(community, editor);
        }
        return result;
    }

    @Override
    public void delete(Long id, User deleter) {
        // TODO
    }

    private void validateCommunity(Community community, User creator, boolean isCreate, boolean isVerified) {
        Map<String, String> map = new HashMap<>();

        if (community.getFullRuName() == null) {
            map.put(FieldConstants.COMMUNITY_FULL_RU_NAME, "Название объединения не задано");
        }
        if (community.getFullRuName() != null && (community.getFullRuName().length() < 3 || community.getFullRuName().length() > 1000)) {
            map.put(FieldConstants.COMMUNITY_FULL_RU_NAME, "Допустимый размер названия от 3 до 1000 символов");
        }

        if (community.getAccessType() == null) {
            map.put("access_type", "Не задан уровень доступа");
        }
        if (ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.equals(community.getCommunityType()) && !CommunityAccessType.CLOSE.equals(community.getAccessType())) {
            map.put("access_type", "Объединение в рамках юр.лица может быть только закрытым");
        }
        if (community.getParent() == null && (community.getActivityScopes() == null || community.getActivityScopes().isEmpty())) {
            map.put("activity_scope", "Не выбрана ни одна сфера деятельности");
        }

        if (!isCreate) {
            ExceptionUtils.check(isVerified, "Нельзя изменять данные у сертифицированной организации");
        }

        if (!map.isEmpty()) {
            System.err.println("Ошибки в данных объединения:");
            for (String key : map.keySet()) {
                String value = map.get(key);
                System.err.println(value);
            }
            throw new CommunityException("Ошибки в данных объединения", map);
        }
    }

    /**
     * Сохранить значения полей объединения
     * @param community
     */
    private void saveCommunityEntityFields(Community community) {
        try {
            if (community.getCommunityData() != null && community.getCommunityData().getFields() != null) {
                List<Field> communityFields = community.getCommunityData().getFields();
                fieldsService.saveFields(community, communityFields, fieldValidatorBundle);
            }
        } catch (FieldException e) {
            if (e.getFieldValidateResults() != null) {
                Map<String, String> communityErrors = new HashMap<>();
                for (FieldValidateResult fieldValidateResult : e.getFieldValidateResults()) {
                    communityErrors.put(fieldValidateResult.getField().getInternalName(), fieldValidateResult.getMessage());
                }
                throw new CommunityException(e.getMessage(), communityErrors);
            }
        }
    }

    /**
     * Сохранить значения файлов
     * @param community
     */
    private void saveCommunityFieldFiles(Community community) {
        if (community.getCommunityData() != null && community.getCommunityData().getFields() != null) {
            List<Field> fields = community.getCommunityData().getFields();
            for (Field field : fields) {
                if (field.getFieldFiles() != null && !field.getFieldFiles().isEmpty()) {
                    List<FieldFile> fieldFiles = field.getFieldFiles();
                    fieldsService.saveFieldFiles(field.getId(), community.getId(), fieldFiles);
                }
            }
        }
    }


    /**
     * Создать объединение
     * @param community
     * @param creator
     */
    private Community createCommunity(Community community, User creator) {
        UserEntity creatorEntity = sharerDao.getById(creator.getId());
        CommunityEntity parentEntity = null;
        CommunityEntity rootEntity = null;
        if (community.getParent() != null && community.getParent().getId() != null) {
            parentEntity = communityRepository.findOne(community.getParent().getId());
            rootEntity = parentEntity;
            while(rootEntity.getParent() != null) {
                rootEntity = rootEntity.getParent();
            }
        }
        community.setFullRuName(community.getCommunityData().getFieldValueByInternalName(FieldConstants.COMMUNITY_FULL_RU_NAME));

        CommunityEntity communityEntity = new CommunityEntity();
        communityEntity.setName(community.getFullRuName());
        communityEntity.setAccessType(community.getAccessType());
        communityEntity.setInvisible(!community.isVisible());
        communityEntity.setCreator(creatorEntity);
        //communityEntity.setMembers();
        //organizationCommunityMembers;

        setOkveds(community, communityEntity);
        setActivityScopes(community, communityEntity);
        setCommunityType(community);
        setAssociationForm(community);
        setSeoLink(community);

        communityEntity.setAvatarUrl(CommunityEntity.DEFAULT_AVATAR_URL);
        communityEntity.setCreatedAt(new Date());
        //CommunitySchemaEntity schema;
        communityEntity.setParent(parentEntity);
        communityEntity.setRoot(rootEntity);
        communityEntity.setDeleted(false);
        communityEntity.setVerified(false);

        validateCommunity(community, creator, true, false);

        communityRepository.save(communityEntity);

        community.setId(communityEntity.toDomain().getId());
        saveCommunityEntityFields(community);
        saveCommunityFieldFiles(community);

        return getByIdFullData(communityEntity.getId());
    }

    private void setOkveds(Community community, CommunityEntity communityEntity) {
        OkvedEntity okvedEntity = null;
        if (community.getMainOkved() != null) {
            okvedEntity = okvedDao.getById(community.getMainOkved().getId());
        }
        Set<OkvedEntity> additionalOkveds = new HashSet<>();
        if (community.getOkveds() != null) {
            for (OkvedDomain okved : community.getOkveds()) {
                if (okved != null && okved.getId() != null) {
                    additionalOkveds.add(okvedDao.getById(okved.getId()));
                }
            }
        }
        communityEntity.setMainOkved(okvedEntity);
        communityEntity.setOkveds(additionalOkveds);
    }

    private void setActivityScopes(Community community, CommunityEntity communityEntity) {
        List<RameraListEditorItem> rameraActivityScopes = null;
        if (community.getActivityScopes() != null) {
            rameraActivityScopes = new ArrayList<>();
            for (ListEditorItem activityScope : community.getActivityScopes()) {
                if (activityScope.getId() != null) {
                    rameraActivityScopes.add(rameraListEditorItemDAO.getById(activityScope.getId()));
                }
            }
        }
        communityEntity.setRameraActivityScopes(rameraActivityScopes);
    }

    private void setCommunityType(Community community) {
        if (community.getCommunityData().getFieldByInternalName(FieldConstants.COMMUNITY_TYPE) == null) {
            FieldEntity fieldCommunityTypeEntity = fieldDao.getByInternalName(FieldConstants.COMMUNITY_TYPE);
            community.getCommunityData().addField(fieldCommunityTypeEntity.toDomain());
        }
        Field fieldCommunityType = community.getCommunityData().getFieldByInternalName(FieldConstants.COMMUNITY_TYPE);
        if (ParticipantsTypes.COMMUNITY_WITHOUT_ORGANIZATION.equals(community.getCommunityType())) {
            fieldCommunityType.setValue(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName());
            community.setCommunityType(ParticipantsTypes.COMMUNITY_WITHOUT_ORGANIZATION);
        } else {
            fieldCommunityType.setValue(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName());
            community.setCommunityType(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION);
        }
    }

    private void setAssociationForm(Community community) {
        Field associationFormField = community.getCommunityData().getFieldByInternalName(FieldConstants.COMMUNITY_ASSOCIATION_FORM);
        if (associationFormField == null) {
            FieldEntity associationFormFieldEntity = fieldDao.getByInternalName(FieldConstants.COMMUNITY_ASSOCIATION_FORM);
            associationFormField = associationFormFieldEntity.toDomain();
            community.getCommunityData().addField(associationFormField);
        }
        if (community.getAssociationForm() != null && community.getAssociationForm().getId() != null) {
            associationFormField.setValue(String.valueOf(community.getAssociationForm().getId()));
        }
    }

    private void setSeoLink(Community community) {
        String seoLink = null;
        Field seoLinkField = community.getCommunityData().getFieldByInternalName(FieldConstants.COMMUNITY_SHORT_LINK_NAME);
        if (seoLinkField == null) {
            FieldEntity seoLinkFieldEntity = fieldDao.getByInternalName(FieldConstants.COMMUNITY_SHORT_LINK_NAME);
            seoLinkField = seoLinkFieldEntity.toDomain();
        }
        if (!StringUtils.isBlank(community.getSeoLink())) {
            seoLink = community.getSeoLink();
        }
        if (StringUtils.isBlank(seoLink)) {
            if (StringUtils.isBlank(seoLinkField.getValue())) {
                String strForHash = "GROUP" + System.currentTimeMillis();
                if (community.getId() != null) {
                    strForHash += community.getId();
                }
                seoLink = IkpUtils.longToIkpHash(MurmurHash.hash64(strForHash));
            } else {
                seoLink = seoLinkField.getValue();
            }
        }
        seoLinkField.setValue(seoLink);
    }

    /**
     * Редактирование объединения
     * @param community
     * @param editor
     */
    private Community editCommunity(Community community, User editor) {
        CommunityEntity communityEntity = communityRepository.findOne(community.getId());

        community.setFullRuName(community.getCommunityData().getFieldValueByInternalName(FieldConstants.COMMUNITY_FULL_RU_NAME));

        communityEntity.setName(community.getFullRuName());
        communityEntity.setAccessType(community.getAccessType());
        communityEntity.setInvisible(!community.isVisible());
        setOkveds(community, communityEntity);
        setActivityScopes(community, communityEntity);
        setCommunityType(community);
        setAssociationForm(community);
        setSeoLink(community);

        communityRepository.save(communityEntity);

        validateCommunity(community, editor, false, BooleanUtils.toBooleanDefaultIfNull(communityEntity.getVerified(), false));

        saveCommunityEntityFields(community);
        return getByIdFullData(communityEntity.getId());
    }

    @Override
    public Community getBySeoLinkOrIdMediumData(String seoLink) {
        Long id = communityDao.findCommunityId(seoLink);
        Community result = null;
        if (id != null) {
            result = getByIdMediumData(id);
        }
        return result;
    }

    @Override
    public List<Community> getParents(Community community) {
        CommunityEntity communityEntity = communityRepository.findOne(community.getId());
        List<CommunityEntity> communities = communityDao.getParents(communityEntity);
        List<Community> result = new ArrayList<>();
        for (CommunityEntity communityParentEntity : communities) {
            result.add(toDomainData(communityParentEntity, null, true));
        }
        return result;
    }

    @Override
    public List<Community> findCommunitiesByUserPermission(User user, String permission) {
        List<Community> communities = new ArrayList<>();

        List<CommunityMemberEntity> members = communityMemberRepository.findAll(
                        Specifications.where(CommunityMemberSpecifications.userId(user.getId()))
                        .and(CommunityMemberSpecifications.hasPermission(permission))
                        .and(CommunityMemberSpecifications.noParent()));

        for (CommunityMemberEntity member : members) {
            communities.add(toDomainData(member.getCommunity(), null, false));
        }
        return communities;
    }

    @Override
    public List<Community> findCommunitiesByParentAndUserPermission(User user, String permission, Long parentId) {
        List<Community> communities = new ArrayList<>();
        List<CommunityMemberEntity> members = communityMemberRepository.findAll(
                        Specifications.where(CommunityMemberSpecifications.userId(user.getId()))
                        .and(CommunityMemberSpecifications.hasPermission(permission))
                        .and(CommunityMemberSpecifications.parentCommunityId(parentId)));

        for (CommunityMemberEntity member : members) {
            communities.add(toDomainData(member.getCommunity(), null, false));
        }

        return new ArrayList<>(communities);
    }

    /**
     *
     * @param seoLink
     * @return
     */
    @Override
    public Long findCommunityId(String seoLink) {
        // seoLink Может быть как ИДом так и seoLink
        return communityDao.findCommunityId(seoLink);
    }

    @Override
    public Community getByAssociationFormInternalName(String associationFormInternalName) {
        RameraListEditorItem communityAssociationForm = rameraListEditorItemDAO.getByCode(associationFormInternalName);
        return toDomainData(communityDao.getFirstByAssociationForm(communityAssociationForm), null, false);
    }

    @Override
    public List<Community> getPossibleCommunitiesMembers(Long userId, Long communityId, String query, int firstResult, int maxResults) {
        return toDomainMinDataListSafe(
                communityDao.getPossibleCommunitiesMembers(
                        sharerDao.loadById(userId),
                        communityDao.loadById(communityId),
                        query,
                        firstResult,
                        maxResults
                )
        );
    }

    @Override
    public long getPossibleCommunitiesMembersCount(Long userId, Long communityId, String query) {
        return communityDao.getPossibleCommunitiesMembersCount(userId, communityDao.loadById(communityId), query);
    }

    @Override
    public List<FieldFile> getCommunityFieldFiles(Long communityId, Long fieldId) {
        FieldValueEntity fieldValue = fieldValueDao.get(communityDao.loadById(communityId), fieldDao.loadById(fieldId));
        return FieldFileEntity.toDomainList(fieldValue.getFieldFiles());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = GET_BY_ID_FULL_DATA_CACHE, key = "#communityId"),
            @CacheEvict(value = GET_BY_ID_MIN_DATA_CACHE, key = "#communityId")
    })
    public void setFieldValueHidden(Long communityId, Long fieldId, boolean hidden) {
        fieldsService.setFieldValueHidden(getByIdMinData(communityId), fieldId, hidden);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = GET_BY_ID_FULL_DATA_CACHE, key = "#communityId"),
            @CacheEvict(value = GET_BY_ID_MIN_DATA_CACHE, key = "#communityId")
    })
    public void setFieldValuesGroupHidden(Long communityId, Long fieldsGroupId, boolean hidden) {
        fieldsService.setFieldValuesGroupHidden(getByIdMinData(communityId), fieldsGroupId, hidden);
    }

    @Override
    public List<Community> getByParentId(Long parentId, int page, int perPage) {
        Pageable pageable = new PageRequest(page, perPage);
        return toDomainListFullData(communityRepository.findByParent_Id(parentId, pageable));
    }

    @Override
    public List<Community> getByParentId(Long parentId) {
        return toDomainListFullData(communityRepository.findByParent_Id(parentId));
    }

    @Override
    public List<DocumentTemplateSetting> saveDocumentTemplateSettings(Long communityId, List<DocumentTemplateSetting> documentTemplateSettings, boolean needCreateDocuments) {
        List<DocumentTemplateSetting> result = new ArrayList<>();
        ExceptionUtils.check(needCreateDocuments && (documentTemplateSettings == null || documentTemplateSettings.isEmpty()), "Не установлены шаблоны документов");
        List<DocumentTemplateSettingEntity> documentTemplateSettingEntities = new ArrayList<>();
        if (documentTemplateSettings != null) {
            for (DocumentTemplateSetting documentTemplateSetting : documentTemplateSettings) {
                documentTemplateSetting = documentTemplateSettingService.save(documentTemplateSetting);
                result.add(documentTemplateSetting);
                DocumentTemplateSettingEntity documentTemplateSettingEntity = documentTemplateSettingRepository.getOne(documentTemplateSetting.getId());
                documentTemplateSettingEntities.add(documentTemplateSettingEntity);
            }
        }
        CommunityEntity community = communityRepository.getOne(communityId);
        community.getDocumentTemplateSettings().clear();
        community.getDocumentTemplateSettings().addAll(documentTemplateSettingEntities);
        community.setNeedCreateDocuments(needCreateDocuments);
        communityRepository.save(community);
        return result;
    }
    @Override
    public List<Community> getCommunitiesCreated(Long userId) {
        List<CommunityEntity> entities = communityRepository.findByCreator_Id(userId);
        List<Community> communitiesCreated = new ArrayList<>();
        for (CommunityEntity entity : entities) {
            communitiesCreated.add(entity.toDomain());
        }
        return communitiesCreated;
    }
    @Override
    public List<Community> getCommunitiesMember(Long userId) {
        List<Community> communitiesMember = new ArrayList<>();
        List<CommunityEntity> entities = communityDao.getByMember(userId);
        for (CommunityEntity entity : entities) {
            communitiesMember.add(entity.toDomain());
        }
        return communitiesMember;
    }

    private List<Community> toDomainListFullData(List<CommunityEntity> communityEntities) {
        List<Community> result = null;
        if (communityEntities != null && communityEntities.size() > 0) {
            result = new ArrayList<>();
            for (CommunityEntity communityEntity : communityEntities) {
                List<FieldValueEntity> fieldValues = fieldValueDao.getByObject(communityEntity);
                result.add(toDomainData(communityEntity, fieldValues, true));
            }
        }
        return result;
    }
}
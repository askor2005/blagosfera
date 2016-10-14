package ru.radom.kabinet.document.services.participants;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.OkvedDomain;
import ru.askor.blagosfera.domain.document.*;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.FieldType;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.communities.dto.FieldValueParameterDto;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.document.dto.DocumentParticipantSourceDto;
import ru.radom.kabinet.document.dto.PossibleSourceParticipantDto;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.module.rameralisteditor.service.ListEditorItemDomainService;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.FieldConstants;
import ru.radom.kabinet.utils.VarUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Created by vgusev on 07.04.2016.
 */
@Service
@Transactional
public class CommunityWithOrganizationParticipantSourceService implements DocumentParticipantSourceService {

    // Роль заполнения пользовательских полей в документах организации
    private static final String COMMUNITY_ROLE_DOCUMENT_FIELDS_WRITE = "ROLE_DOCUMENT_FIELDS_WRITE";

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private UserParticipantSourceService userParticipantSourceService;

    @Autowired
    private ListEditorItemDomainService listEditorItemDomainService;

    @Autowired
    private FieldDao fieldDao;

    @Autowired
    private CommunityDataService communityDomainService;

    @Autowired
    private CommunitiesService communitiesService;



    @Override
    public ParticipantsTypes getType() {
        return ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION;
    }

    @Override
    public DocumentParticipantSourceDto getParticipantSource(
            IDocumentParticipant documentParticipant, String participantName, List<Long> filteredFieldIds,
            List<FieldType> excludedFieldTypes, List<FieldType> includedFieldTypes, boolean needFillSystemFields, int index) {
        Community community = (Community)documentParticipant;
        DocumentParticipantSourceDto result = getFlowOfDocumentParticipantFromCommunity(community, participantName, true, filteredFieldIds, excludedFieldTypes, includedFieldTypes, needFillSystemFields);
        if (needFillSystemFields) {
            addSystemFieldsToCommunity(community, result, filteredFieldIds);
        }
        result.setIndex(index);
        return result;
    }

    @Override
    public DocumentParticipantSourceDto getParticipantSource(Long participantId, String participantName, List<Long> filteredFieldIds, List<FieldType> excludedFieldTypes, List<FieldType> includedFieldTypes, boolean needFillSystemFields, int index) {
        Community community = communityDomainService.getByIdFullData(participantId);
        return getParticipantSource(
                community, participantName, filteredFieldIds, excludedFieldTypes, includedFieldTypes, needFillSystemFields, index
        );
    }

    @Override
    public IDocumentParticipant getSourceParticipantById(Long id) {
        return communityDomainService.getByIdMinData(id);
    }

    @Override
    public List<User> getUsersFromParticipantForFillUserFields(DocumentParticipant documentParticipant) {
        List<User> result = new ArrayList<>();
        Long communityId = documentParticipant.getSourceParticipantId();
        //Community community = communityDomainService.getByIdMinData(documentParticipant.getSourceParticipantId());
        // Получить всех участников организации
        //CommunityMemberStatusList statusList = new CommunityMemberStatusList();
        //statusList.add(CommunityMemberStatus.MEMBER);
        List<User> users = userDataService.getMembersOfCommunityFullData(documentParticipant.getSourceParticipantId());
        //List<CommunityMemberEntity> members = communityMemberDao.getAccounts(community, statusList, 0, Integer.MAX_VALUE, "", null);
        for (User user : users) {
            // Если есть права на заполнение пользовательских полей
            if (communitiesService.hasPermission(communityId, user.getId(), COMMUNITY_ROLE_DOCUMENT_FIELDS_WRITE)) {
                result.add(user);
            }
        }
        return result;
    }

    @Override
    public List<User> getUsersFromParticipantForSignDocument(DocumentParticipant documentParticipant) {
        List<User> result;
        // Необходимо получить руководителя и гл. буха и т.д. объединения в рамках юр лица
        if (CollectionUtils.isNotEmpty(documentParticipant.getChildren())) {
            List<Long> ids = documentParticipant.getChildren().stream()
                    .filter(DocumentParticipant::isNeedSignDocument)
                    .map(DocumentParticipant::getSourceParticipantId)
                    .collect(Collectors.toList());
            result = userDataService.getByIds(ids);
        } else {
            result = Collections.emptyList();
        }
        return result;
    }

    @Override
    public List<PossibleSourceParticipantDto> getPossibleSourceParticipants(DocumentClassDataSource dataSource) {
        List<PossibleSourceParticipantDto> result = new ArrayList<>();
        List<Community> communities = loadCommunitiesByAssociationForm(dataSource.getAssociationForm(), dataSource.getAssociationFormSearchType());
        for (Community community : communities){
            PossibleSourceParticipantDto possibleSourceParticipant = new PossibleSourceParticipantDto();
            possibleSourceParticipant.setId(community.getId());
            possibleSourceParticipant.setName(community.getName());
            result.add(possibleSourceParticipant);
        }
        return result;
    }

    @Override
    public boolean isListDataSource() {
        return false;
    }

    // Загрузка возможных участников документа (объединений)
    private List<Community> loadCommunitiesByAssociationForm(ListEditorItem listEditorItem, AssociationFormSearchType associationFormSearchType) {
        List<Community> result = new ArrayList<>();

        String associationFormStringValue = listEditorItem == null ? null : String.valueOf(listEditorItem.getId());
        //FieldEntity associationFormField = fieldDao.getByInternalName(COMMUNITY_ASSOCIATION_FORM_FIELD_NAME);

        associationFormSearchType = associationFormSearchType == null ? AssociationFormSearchType.SEARCH_SUB_STRUCTURES : associationFormSearchType;

        if (listEditorItem != null) {
            // Поиск по форме объединения и юр лица
            result.addAll(communityDomainService.getByFieldsMinData(
                    new FieldValueParameterDto(FieldConstants.COMMUNITY_ASSOCIATION_FORM, associationFormStringValue),
                    new FieldValueParameterDto(FieldConstants.COMMUNITY_TYPE, ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName())
            ));

            if (listEditorItem.getChild() != null && AssociationFormSearchType.SEARCH_SUB_STRUCTURES.equals(associationFormSearchType)) {
                for (ListEditorItem child : listEditorItem.getChild()) {
                    result.addAll(loadCommunitiesByAssociationForm(child, associationFormSearchType));
                }
            }
        } else if (AssociationFormSearchType.SEARCH_SUB_STRUCTURES.equals(associationFormSearchType)) {
            // Загрузить все объединения по полю
            result.addAll(communityDomainService.getByFieldsMinData(
                    new FieldValueParameterDto(FieldConstants.COMMUNITY_TYPE, ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName())
            ));
            /*List<FieldValueEntity> fieldValues = fieldValueDao.getAccounts(associationFormField);
            for (FieldValueEntity fieldValue : fieldValues) {
                if (fieldValue != null) {
                    result.add((CommunityEntity) fieldValue.getObject());
                }
            }*/
        } else if (AssociationFormSearchType.SEARCH_EQUALS.equals(associationFormSearchType)) {
            // Загрузить все объединения у которых не установлена форма объединения
            result.addAll(communityDomainService.getByEmptyFieldMinData(FieldConstants.COMMUNITY_ASSOCIATION_FORM));
        }
        return result;
    }

    @Override
    public List<DocumentParticipant> getParticipantsOfUser(DocumentParticipant documentParticipant, Long userId) {
        List<DocumentParticipant> foundParticipants = new ArrayList<>();
        // Ищем дочерних участников организации
        if (documentParticipant.getChildren() != null) {
            for (DocumentParticipant childParticipant : documentParticipant.getChildren()) {
                // Дочерние участники сообщества всегда физ лица
                if (childParticipant.getSourceParticipantId().equals(userId)) {
                    foundParticipants.add(childParticipant);
                }
            }
        }
        return foundParticipants;
    }

    @Override
    public String getSourceName(Long sourceParticipantId) {
        Community community = communityDomainService.getByIdMinData(sourceParticipantId);
        return community.getName();
    }

    /**
     * Получить объект участника документа - сообщества с установленными полями.
     *
     * @param community
     * @param needLoadFields
     * @param filteredFieldIds
     * @param excludedFieldTypes
     * @param includedFieldTypes
     * @param needFillSystemFields
     * @return
     */
    public DocumentParticipantSourceDto getFlowOfDocumentParticipantFromCommunity(Community community, String participantName, boolean needLoadFields, List<Long> filteredFieldIds,
                                                                               List<FieldType> excludedFieldTypes, List<FieldType> includedFieldTypes, boolean needFillSystemFields) {
        DocumentParticipantSourceDto documentParticipantSource = new DocumentParticipantSourceDto();
        documentParticipantSource.setName(participantName);
        documentParticipantSource.setId(community.getId());

        // Заполнение полей.
        if (needLoadFields) {
            List<ParticipantField> participantFields = new ArrayList<>();
            //List<FieldValueEntity> fieldValues = fieldValueDao.getListByFieldsGroups(fieldsGroups, community);

            //Map<FieldEntity, FieldValueEntity> fieldValuesMap = community.getFieldsValuesMap();
            List<Field> communityFields = community.getCommunityData() != null ? community.getCommunityData().getFields() : null;

            if (communityFields != null) {
                documentParticipantSource.setChildMap(new HashMap<>());
                // Поля пользователей - участников объединения
                //List<FieldsGroupEntity> sharerFieldsGroups = new ArrayList<>();
                Map<String, String> sharerFieldsMap = new HashMap<>();
                // Добавляем всех участников объединения как список физ лиц
                if (community.getId() != null) {
                    List<User> membersSharers = userDataService.getMembersOfCommunityFullData(community.getId());
                    extractParticipantList(
                            membersSharers, documentParticipantSource, "Все участники объединения",
                            excludedFieldTypes, includedFieldTypes, needFillSystemFields
                    );
                }

                for (Field field : communityFields) {
                    //FieldValueEntity fieldValue = fieldValuesMap.get(field);
                    if (field != null) {
                        boolean needLoadedValue = true;
                        // Если переданы поля для фильтрации
                        if (filteredFieldIds != null && filteredFieldIds.size() > 0 && !filteredFieldIds.contains(field.getId())) {
                            needLoadedValue = false;
                        }
                        String stringFieldValue = "";
                        if (needLoadedValue) {


                            switch (field.getType()) {
                                case PARTICIPANTS_LIST:
                                    stringFieldValue = getFieldValue(field, communityFields);
                                    List<User> sharersFromParticipantList = getSharersFromParticipantListField(stringFieldValue);

                                    stringFieldValue = extractParticipantList(
                                            sharersFromParticipantList, documentParticipantSource, field.getName(),
                                            excludedFieldTypes, includedFieldTypes, needFillSystemFields
                                    );
                                    break;
                                case SHARER:
                                    sharerFieldsMap.put(field.getInternalName() + "_ID", field.getInternalName());
                                    break;
                                case IMAGE:
                                    if (needLoadedValue) {
                                        if (field.getFieldFiles() != null && field.getFieldFiles().size() > 0) {
                                            stringFieldValue = field.getFieldFiles().get(0).getUrl();
                                        }
                                        if (stringFieldValue == null || stringFieldValue.equals("")) {
                                            stringFieldValue = field.getExample();
                                        }
                                    }
                                    break;

                                case SELECT: // Если поле - выбираемое значение из списка
                                case UNIVERSAL_LIST:
                                    ListEditorItem universalListItem = listEditorItemDomainService.getById(VarUtils.getLong(stringFieldValue, -1l));
                                    if (universalListItem != null) {
                                        stringFieldValue = universalListItem.getText();
                                    }
                                    break;

                                default:
                                    stringFieldValue = getFieldValue(field, communityFields);
                                    break;
                            }

                            if (stringFieldValue == null) {
                                stringFieldValue = "";
                            }
                            // Если есть типы полей, которые не нужно заменять в документе
                            boolean needAddField = false;
                            if (excludedFieldTypes != null) {
                                if (!excludedFieldTypes.contains(field.getType())) {
                                    needAddField = true;
                                }
                            } else {
                                // Если есть типы полей которые можно заменять в документе
                                if (includedFieldTypes != null) {
                                    if (includedFieldTypes.contains(field.getType())) {
                                        needAddField = true;
                                    }
                                } else {
                                    needAddField = true;
                                }
                            }

                            if (needAddField) {
                                ParticipantField participantField = new ParticipantField(field.getId(), null, field.getName(), field.getInternalName(), stringFieldValue, field.getType());
                                participantFields.add(participantField);
                            }
                        }
                    }
                }

                // Загружаем пользователей
                for (String sharerIdFieldName : sharerFieldsMap.keySet()) {
                    String sharerNameFieldName = sharerFieldsMap.get(sharerIdFieldName);

                    //FieldValueEntity idFieldValue = null;
                    //FieldEntity nameField = null;
                    Field nameField = null;
                    Field idField = null;
                    //for (FieldEntity field : fieldValuesMap.keySet()) {
                    for (Field field : communityFields) {
                        //FieldValueEntity fieldValue = fieldValuesMap.get(field);
                        if (field != null) {
                            if (field.getInternalName().equalsIgnoreCase(sharerIdFieldName)) {
                                idField = field;
                            }
                            if (field.getInternalName().equalsIgnoreCase(sharerNameFieldName)) {
                                nameField = field;
                            }
                        }
                        if (idField != null && nameField != null) {
                            break;
                        }
                    }

                    String stringFieldValue = "";
                    if (idField != null) {
                        stringFieldValue = FieldsService.getFieldStringValue(idField);
                    }
                    /*if (sharerFieldsGroups.size() == 0) {
                        sharerFieldsGroups.addAll(fieldsGroupDao.getByInternalNamePrefix(PERSON_COMMON_FIELDS_PREFIX));
                    }*/
                    Long userId = VarUtils.getLong(stringFieldValue, -1l);
                    if (userId > -1l && nameField != null) {
                        User user = userDataService.getByIdFullData(userId);
                        if (user != null) {
                            DocumentParticipantSourceDto participantSourceInCommunity =
                                    userParticipantSourceService.getParticipantSource(
                                            user, nameField.getName(), null, excludedFieldTypes, includedFieldTypes, needFillSystemFields, 1
                                    );
                            documentParticipantSource.getChildMap().put(nameField.getName(), Collections.singletonList(participantSourceInCommunity));
                        }
                    }
                }
                if (community.getId() != null) {
                    String participantChildName = "Создатель объединения";
                    User creator = userDataService.getByIdFullData(community.getCreator().getId());
                    DocumentParticipantSourceDto participantSourceInCommunity =
                            userParticipantSourceService.getParticipantSource(
                                    creator, participantChildName, null, excludedFieldTypes, includedFieldTypes, needFillSystemFields, 1
                            );
                    documentParticipantSource.getChildMap().put(participantChildName, Collections.singletonList(participantSourceInCommunity));
                }
                documentParticipantSource.setParticipantFields(participantFields);
            }
        }
        return documentParticipantSource;
    }

    private String getFieldValue(String internalName, List<Field> fields) {
        String result = null;
        if (fields != null && !fields.isEmpty()) {
            for (Field field : fields) {
                if (internalName.equals(field.getInternalName())) {
                    result = FieldsService.getFieldStringValue(field);
                    break;
                }
            }
        }
        return result == null ? "" : result;
    }

    private String getFieldValue(Field field, List<Field> fields) {
        String result = FieldsService.getFieldStringValue(field);
        if (StringUtils.isBlank(result)) {
            if (FieldConstants.COMMUNITY_FACT_CITY.equals(field.getInternalName())) {
                result = getFieldValue(FieldConstants.COMMUNITY_FACT_REGION, fields);
            } else if (FieldConstants.COMMUNITY_LEGAL_CITY.equals(field.getInternalName())) {
                result = getFieldValue(FieldConstants.COMMUNITY_LEGAL_REGION, fields);
            }  else if (FieldConstants.COMMUNITY_LEGAL_CITY_DESCRIPTION.equals(field.getInternalName()) ||
                    FieldConstants.COMMUNITY_LEGAL_CITY_DESCRIPTION_SHORT.equals(field.getInternalName())) {
                result = getFieldValue(FieldConstants.COMMUNITY_LEGAL_REGION_DESCRIPTION, fields);
            } else if (FieldConstants.COMMUNITY_FACT_CITY_DESCRIPTION.equals(field.getInternalName()) ||
                    FieldConstants.COMMUNITY_FACT_CITY_DESCRIPTION_SHORT.equals(field.getInternalName())) {
                result = getFieldValue(FieldConstants.COMMUNITY_FACT_REGION_DESCRIPTION, fields);
            }
        }
        return result;
    }

    private List<User> getSharersFromParticipantListField(String stringFieldValue) {
        List<User> result = new ArrayList<>();

        if (stringFieldValue != null && !stringFieldValue.equals("")) {
            String[] idsStr = stringFieldValue.split(";");
            for (String idStr : idsStr) {
                Long userId = VarUtils.getLong(idStr, -1l);
                if (userId > -1) {
                    User user = userDataService.getByIdFullData(userId);
                    if (user != null) {
                        result.add(user);
                    }
                }
            }
        }

        return result;
    }

    // Получить значение поля из поля типа - список участников
    private String getStringValueFromParticipantListField(List<User> users) {
        String result = "";
        List<String> fios = new ArrayList<>();
        for (User sharer : users) {
            fios.add(sharer.getFullName());
        }
        if (fios.size() > 1) {
            result = StringUtils.join(fios, ", ");
        } else if (fios.size() == 1) {
            result = fios.get(0);
        }
        return result;
    }

    private String extractParticipantList(List<User> sharersFromParticipantList,
                                          DocumentParticipantSourceDto documentParticipantSource, String fieldName,
                                          List<FieldType> excludedFieldTypes, List<FieldType> includedFieldTypes,
                                          boolean needFillSystemFields) {
        String stringFieldValue = getStringValueFromParticipantListField(sharersFromParticipantList);

        /*if (sharerFieldsGroups.size() == 0) {
            sharerFieldsGroups.addAll(fieldsGroupDao.getByInternalNamePrefix(PERSON_COMMON_FIELDS_PREFIX));
        }*/
        Set<Long> addedSharers = new HashSet<>();
        int index = 1;
        for (User user : sharersFromParticipantList) {
            if (addedSharers.contains(user.getId())) {
                continue;
            }
            addedSharers.add(user.getId());
            // Загружаем пользователей как дочерних участников объединения
            DocumentParticipantSourceDto participantSourceInCommunity =
                    userParticipantSourceService.getParticipantSource(
                            user, fieldName, null, excludedFieldTypes, includedFieldTypes, needFillSystemFields, index++
                    );
            if(!documentParticipantSource.getChildMap().containsKey(fieldName)) {
                documentParticipantSource.getChildMap().put(fieldName, new ArrayList<>());
            }
            documentParticipantSource.getChildMap().get(fieldName).add(participantSourceInCommunity);

            /*DocumentParticipantEntity participantInCommunity = getFlowOfDocumentParticipantFromSharer(sharer, fieldName, null, excludedFieldTypes, includedFieldTypes);

            participantInCommunity.setParticipantTemplateTypeName(fieldName);
            participantInCommunity.setParticipantTypeName(ParticipantsTypes.INDIVIDUAL.getName());
            participantInCommunity.setSourceParticipantId(sharer.getId());
            if(!documentParticipantSource.getChildMap().containsKey(participantInCommunity.getParticipantTemplateTypeName())) {
                documentParticipantSource.getChildMap().put(participantInCommunity.getParticipantTemplateTypeName(), new ArrayList<>());
            }
            documentParticipantSource.getChildMap().get(participantInCommunity.getParticipantTemplateTypeName()).add(participantInCommunity);
            if (needFillSystemFields) {
                // Добавляем поля пользователя в поля объединения
                addSystemFieldsToSharer(sharer, participantInCommunity, null, index++);
            }*/
        }
        return stringFieldValue;
    }

    /**
     * Добавить значения системных полей сообществу.
     *
     * @param community - объединение
     * @param documentParticipantSource - участник документа
     * @param filteredFieldIds - поля
     */
    private void addSystemFieldsToCommunity(Community community, DocumentParticipantSourceDto documentParticipantSource, List<Long> filteredFieldIds) {
        // Загружаем системные поля по группам полей
        List<FieldEntity> systemFields = fieldDao.getListByObjectAndTypes(Discriminators.COMMUNITY, Arrays.asList(FieldType.SYSTEM, FieldType.SYSTEM_IMAGE));

        for (FieldEntity field : systemFields) {
            // TODO
            //if (!community.getFieldsValuesMap().containsKey(field)) {
            documentParticipantSource.getParticipantFields().add(new ParticipantField(field.getId(), "", field.getName(), field.getInternalName(), "", field.getType()));
            //}
        }


        /*for (FieldEntity field : community.getFieldsValuesMap().keySet()) {
            if (FieldType.SYSTEM.equals(field.getType())) {
                boolean foundField = false;
                for (ParticipantField participantField : documentParticipant.getParticipantFields()) {
                    if (participantField.getInternalName().equals(field.getInternalName())) {
                        foundField = true;
                        break;
                    }
                }
                if (!foundField) {
                    documentParticipant.getParticipantFields().add(new ParticipantField(field.getId(), "", field.getName(), field.getInternalName(), "", field.getType()));
                }
            }
        }*/

        /*for (FieldEntity field : systemFields) {
            documentParticipant.getParticipantFields().add(new ParticipantField(field.getId(), "", field.getName(), field.getInternalName(), ""));
        }*/

        for (ParticipantField participantField : documentParticipantSource.getParticipantFields()) {
            boolean needLoadValue = true;
            if (filteredFieldIds != null && filteredFieldIds.size() > 0) {
                needLoadValue = filteredFieldIds.contains(participantField.getId());
            }
            if (participantField.getInternalName().equalsIgnoreCase(FieldConstants.COMMUNITY_ID_FIELD_NAME)) { // IKP сообщества
                if (community != null && needLoadValue) {
                    participantField.setValue(community.getSeoLink());
                } else {
                    participantField.setValue("");
                }
            } else if (participantField.getInternalName().equalsIgnoreCase(FieldConstants.COMMUNITY_FULL_OKVEDS_FIELD_NAME)) { // Виды деятельности с кодами
                List<String> values = new ArrayList<>();
                for (OkvedDomain okved : community.getOkveds()) {
                    values.add("- " + okved.getCode() + " " + okved.getLongName());
                }
                participantField.setValue(StringUtils.join(values, "<br/>"));
            } else if (participantField.getInternalName().equalsIgnoreCase(FieldConstants.COMMUNITY_SHORT_OKVEDS_FIELD_NAME)) { // Виды деятельности без кодов
                List<String> values = new ArrayList<>();
                for (OkvedDomain okved : community.getOkveds()) {
                    values.add("- " + okved.getLongName());
                }
                participantField.setValue(StringUtils.join(values, "<br/>"));
            } else if (participantField.getInternalName().equalsIgnoreCase(FieldConstants.COMMUNITY_OKVED_CODES_FIELD_NAME)) { // Коды видов деятельности через запятую
                List<String> values = new ArrayList<>();
                if (community.getOkveds() != null) {
                    for (OkvedDomain okved : community.getOkveds()) {
                        values.add(getOkvedFormattedCode(okved));
                    }
                }
                participantField.setValue(StringUtils.join(values, ","));
            } else if (participantField.getInternalName().equalsIgnoreCase(FieldConstants.COMMUNITY_MAIN_OKVED_CODE_FIELD_NAME)) {
                if (community.getMainOkved() != null) {
                    participantField.setValue(getOkvedFormattedCode(community.getMainOkved()));
                } else {
                    participantField.setValue("");
                }
            } else if (FieldConstants.COMMUNITY_AVATAR.equals(participantField.getInternalName())) {
                participantField.setValue(community.getAvatarUrl());
            } else if ("COMMUNITY_ASSOCIATION_FORM_NAME".equals(participantField.getInternalName())) {
                participantField.setValue(community.getAssociationForm() != null ? community.getAssociationForm().getText() : "");
            }
        }
    }

    private String getOkvedFormattedCode(OkvedDomain okved) {
        String code = okved.getCode() == null ? "" : okved.getCode();
        String[] codeParts = code.split("\\.");
        List<String> parts = new ArrayList<>(Arrays.asList(codeParts));
        for (int i = 0; i < 3 - codeParts.length; i++) {
            parts.add("&nbsp;&nbsp;");
        }
        List<String> codeList = new ArrayList<>();
        for (String part : parts) {
            codeList.add(String.format("%-2s", part).replaceAll(" ", "&nbsp;"));
        }
        return StringUtils.join(codeList, ".");
    }

    @Override
    public DocumentParticipant convertSourceParticipantToDocumentParticipant(DocumentTemplate documentTemplate, DocumentParticipantSourceDto sourceParticipant) {
        DocumentParticipant result = new DocumentParticipant();
        result.setSourceParticipantId(sourceParticipant.getId());
        result.setParticipantTypeName(sourceParticipant.getType().getName());
        result.setParticipantTemplateTypeName(sourceParticipant.getName());
        result.setChildren(new ArrayList<>());

        // Список найденных наименований типов участников шаблона среди подписантов
        List<String> foundChildParticipantTemplateTypeNames = new ArrayList<>();
        for (DocumentTemplateParticipant templateParticipant : documentTemplate.getDocumentTemplateParticipants()) {
            boolean equalsParentParticipantName =
                    templateParticipant.getParentParticipantName() != null &&
                    sourceParticipant.getName().equalsIgnoreCase(templateParticipant.getParentParticipantName());
            if (equalsParentParticipantName) {
                foundChildParticipantTemplateTypeNames.add(templateParticipant.getParticipantName());
            }
        }

        Map<String, List<User>> childUsers = getSharersFromCommunity(sourceParticipant.getId());
        // Ищем поля дочерних участников в шаблоне документа
        for (String childParticipantTemplateTypeName : childUsers.keySet()) {
            // Если поле дочернего участника есть в документе
            boolean foundParticipant = documentTemplate.getContent().contains(sourceParticipant.getName() + ":" + childParticipantTemplateTypeName + ":");
            // Если участник есть в списке подписантов документа
            boolean needSign = foundChildParticipantTemplateTypeNames.contains(childParticipantTemplateTypeName);
            foundParticipant = foundParticipant || needSign;

            if (foundParticipant) {
                for (User childUser : childUsers.get(childParticipantTemplateTypeName)) {
                    DocumentParticipant childDocumentParticipant = new DocumentParticipant();
                    childDocumentParticipant.setSourceParticipantId(childUser.getId());
                    // Наименование типа участника в системе
                    // Дочерний участник всегда физ лицо!
                    childDocumentParticipant.setParticipantTypeName(ParticipantsTypes.INDIVIDUAL.getName());
                    // Наименование типа участника в шаблоне
                    childDocumentParticipant.setParticipantTemplateTypeName(childParticipantTemplateTypeName);
                    result.getChildren().add(childDocumentParticipant);
                    //childDocumentParticipant.setParent(result);
                    childDocumentParticipant.setNeedSignDocument(needSign);
                }
            }
        }
        return result;
    }

    /**
     * Получить всех участников из объединения.
     * @param communityId
     * @return
     */
    private Map<String, List<User>> getSharersFromCommunity(long communityId) {
        Map<String,List<User>> result = new HashMap<>();

        Community community = communityDomainService.getByIdFullData(communityId);

        //List<FieldEntity> fields = fieldDao.getByGroups(fieldsGroups);
        if (community.getCommunityData() != null && community.getCommunityData().getFields() != null) {
            List<Field> fields = community.getCommunityData().getFields();
            List<String> sharerIdsFieldName = new ArrayList<>();
            Map<String, String> sharerIdsFieldToFieldNameMap = new HashMap<>();
            for (Field field : fields) {
                if (field != null) {
                    switch (field.getType()) {
                        case PARTICIPANTS_LIST:
                            List<User> sharerList = getSharersFromParticipantListField(FieldsService.getFieldStringValue(field));
                            result.put(field.getName(), sharerList);
                            break;
                        case SHARER:
                            String fieldInternalName = field.getInternalName() + "_ID";
                            sharerIdsFieldName.add(fieldInternalName);
                            sharerIdsFieldToFieldNameMap.put(fieldInternalName, field.getName());
                            break;
                    }
                }
            }
            for (String sharerIdFieldName : sharerIdsFieldName) {
                fields.stream().filter(field -> field.getInternalName().equalsIgnoreCase(sharerIdFieldName)).forEach(field -> {
                    if (field.getValue() != null) {
                        List<User> userList = getSharersFromParticipantListField(FieldsService.getFieldStringValue(field));
                        result.put(sharerIdsFieldToFieldNameMap.get(sharerIdFieldName), userList);
                    }
                });
            }
        }

        return result;
    }
}

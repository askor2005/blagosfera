package ru.radom.kabinet.voting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.community.*;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.community.CommunityMemberAppointRequestEvent;
import ru.askor.blagosfera.domain.events.community.CommunityOtherEvent;
import ru.askor.blagosfera.domain.events.document.FlowOfDocumentStateEvent;
import ru.askor.blagosfera.domain.events.voting.VoterErrorEvent;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.FieldFile;
import ru.askor.blagosfera.domain.field.FieldsGroup;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.business.event.VotingEvent;
import ru.askor.voting.business.event.VotingEventType;
import ru.askor.voting.business.services.BatchVotingService;
import ru.askor.voting.domain.*;
import ru.radom.kabinet.dao.RameraTextDao;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.document.services.DocumentDomainService;
import ru.radom.kabinet.model.RameraTextEntity;
import ru.radom.kabinet.model.communities.postappointbehavior.impl.PlotBuhgalterPostAppointBahavior;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.module.rameralisteditor.service.ListEditorItemDomainService;
import ru.radom.kabinet.services.communities.*;
import ru.radom.kabinet.services.communities.kuch.CommonCreateKuchSettings;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.FieldConstants;
import ru.radom.kabinet.utils.VarUtils;

import java.util.*;

@Transactional
@Service("cooperativePlotCreationService")
public class CooperativePlotCreationService {

    @Autowired
    private CommunityDataService communityDomainService;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Autowired
    private CommunityPostDomainService communityPostDomainService;

    @Autowired
    private CommunityPermissionDomainService communityPermissionDomainService;

    @Autowired
    private FieldsService fieldsService;

    @Autowired
    private FieldDao fieldDao;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private BatchVotingService batchVotingService;

    @Autowired
    private CooperativePlotDocumentsService cooperativePlotDocumentsService;

    @Autowired
    private CooperativePlotSovietDocumentsService cooperativePlotSovietDocumentsService;

    @Autowired
    private CooperativeSecondMeetingService cooperativeSecondMeetingService;

    @Autowired
    private CommonVotingService commonVotingService;
    
    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    @Autowired
    private DocumentDomainService documentDomainService;

    @Autowired
    private ListEditorItemDomainService listEditorItemDomainService;

    @Autowired
    private RameraTextDao rameraTextDao;

    public static final String DOCUMENT_VOTING_BATCH_ID_ATTR_NAME = "votingBatchId";

    // Мнемокод на инструкцию бухгалтера - кассира
    private static final String BUHGALTER_INSTRUCTION = "APPOINT_REQUEST_BUHGALTER_POST_IN_KUCH";

    // Поле КУч - Председатель КУч
    private static final String PRESIDENT_OF_COOPERATIVE_PLOT_FIELD_INTERNAL_NAME = "PRESIDENT_OF_COOPERATIVE_PLOT";

    // Поле КУч - Ревизор КУч
    private static final String REVISOR_OF_COOPERATIVE_PLOT_FIELD_INTERNAL_NAME = "REVISOR_OF_COOPERATIVE_PLOT";

    // Наименование группы полей - юр адрес
    private static final String COMMUNITY_WITH_ORGANIZATION_LEGAL_ADDRESS_GROUP_NAME = "COMMUNITY_WITH_ORGANIZATION_LEGAL_ADDRESS";

    // Наименоваение поля - устав объединения
    private static final String COMMUNITY_CHARTER_DESCRIPTION_FIELD_NAME = "COMMUNITY_CHARTER_DESCRIPTION";

    public CooperativePlotCreationService() {}

    @EventListener
    public void onBlagosferaEvent(FlowOfDocumentStateEvent event) {
        try {
            switch (event.getStateEventType()) {
                case DOCUMENT_SIGNED: // Документ подписан всеми
                    if (event.getParameters() != null) {
                        // Документы 1го этапа собрания
                        if (event.getParameters().containsKey(CooperativePlotDocumentsService.IS_PROTOCOL_OF_FIRST_MEETING)) { // Протокол по созданию КУч 1й этап
                            onSignProtocolOfCreateKuchMeeting(event);
                        } else if (event.getParameters().containsKey(CooperativePlotDocumentsService.IS_STATEMENT_TO_SOVIET_FOR_CREATE_KUCH)) { // Заявление в ПО о создании КУч
                            onSignStatementToSovietForCreateKuch(event);
                        } else if (event.getParameters().containsKey(CooperativePlotSovietDocumentsService.IS_PROTOCOL_MEETING_SOVIET)) { // Протокол собрания совета по созданию КУч
                            onSignProtocolOfMeetingSoviet(event);
                        } else if (event.getParameters().containsKey(CooperativePlotSovietDocumentsService.IS_DOCUMENT_OF_STATE_KUCH)) { // Положение КУч
                            onSignDocumentOfStateOfKuch(event);

                            // Документы 2го этапа собрания
                        } else if (event.getParameters().containsKey(CooperativePlotDocumentsService.IS_PROTOCOL_OF_SECOND_MEETING)) { // Протокол по выбору председателя и ревизора
                            onSignProtocolOfChoosePresidentAndRevisorMeeting(event);
                        } else if (event.getParameters().containsKey(CooperativePlotDocumentsService.IS_STATEMENT_TO_SOVIET_FOR_CHOOSE_PRESIDENT_AND_REVISOR_KUCH)) { // Заявление о выборе председателя и ревизора
                            onSignStatementToSovientForChoosePresidentAndRevisorMeeting(event);
                        } else if (event.getParameters().containsKey(CooperativePlotSovietDocumentsService.IS_DOCUMENT_OF_PROXY_PRESIDENT_KUCH)) { // Доверенность председателю КУч подписана
                            onSignProxyPresidentKuch(event);
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @EventListener
    public void onVotingEvent(VotingEvent event) {
        if (event.getEventType() == VotingEventType.BATCH_VOTING_STATE_CHANGE) {
            BatchVoting batchVoting = event.getBatchVoting();

            if (!batchVoting.getParameters().getBehavior().equals(CooperativeFirstPlotBatchVoting.NAME)
                    && !batchVoting.getParameters().getBehavior().equals(CooperativeSecondPlotBatchVoting.NAME)) {
                return;
            }

            Long votingOwnerId = null;

            try {
                batchVoting = batchVotingService.getBatchVoting(batchVoting.getId(), true, true);
                Voting firstFailedVoting = batchVotingService.getFirstFailedVoting(batchVoting);

                votingOwnerId = batchVoting.getOwnerId();

                if (batchVoting.getState() == BatchVotingState.FINISHED && firstFailedVoting == null) {
                    if (batchVoting.getParameters().getBehavior().equals(CooperativeFirstPlotBatchVoting.NAME)) { // Собрание для создания КУч
                        // Создаём протокол собрания 1го этапа
                        cooperativePlotDocumentsService.createMeetingProtocolOfCreateKuch(batchVoting);
                    } else if (batchVoting.getParameters().getBehavior().equals(CooperativeSecondPlotBatchVoting.NAME)) { // Собрание для выбора председателя и ревизора КУч
                        // Создаём протокол собрания 2го этапа
                        cooperativePlotDocumentsService.createMeetingProtocolOfChoosePresidentAndRevisorKuch(batchVoting);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                publishErrorMessage(e.getMessage(), votingOwnerId);
            }
        }
    }

    /**
     * Подписали протокол собрания 1го этапа
     * @param flowOfDocumentStateEvent
     */
    private void onSignProtocolOfCreateKuchMeeting(FlowOfDocumentStateEvent flowOfDocumentStateEvent) throws Exception {
        String batchVotingIdStr = flowOfDocumentStateEvent.getParameters().get(CooperativePlotCreationService.DOCUMENT_VOTING_BATCH_ID_ATTR_NAME);
        Long batchVotingId = VarUtils.getLong(batchVotingIdStr, -1l);
        if (batchVotingId > -1l) {
            BatchVoting batchVoting = batchVotingService.getBatchVoting(batchVotingId, true, true);
            // Создать заявление в совет ПО на создание КУч
            cooperativePlotDocumentsService.createStatementToSovietAboutCreateKuch(batchVoting);
        }
    }

    /**
     * Подписали заявление в Совет ПО о создании КУч
     * @param flowOfDocumentStateEvent
     */
    private void onSignStatementToSovietForCreateKuch(FlowOfDocumentStateEvent flowOfDocumentStateEvent) throws Exception {
        // Заявление подписано всеми (и советом ПО), значит надо создать документ - "Положение КУч"
        String batchVotingIdStr = flowOfDocumentStateEvent.getParameters().get(CooperativePlotCreationService.DOCUMENT_VOTING_BATCH_ID_ATTR_NAME);
        Long batchVotingId = VarUtils.getLong(batchVotingIdStr, -1l);
        if (batchVotingId > -1l) {
            BatchVoting batchVoting = batchVotingService.getBatchVoting(batchVotingId, true, true);
            // Создать протокол совета о создании КУч
            cooperativePlotSovietDocumentsService.createProtocolSovietMeetingForCreateKuch(batchVoting);
            //cooperativePlotSovietDocumentsService.createDocumentStatusKuch(batchVoting);
        }
    }

    /**
     * Подписали документ "Протокол собрания совета"
     * @param flowOfDocumentStateEvent
     */
    private void onSignProtocolOfMeetingSoviet(FlowOfDocumentStateEvent flowOfDocumentStateEvent) throws Exception {
        String batchVotingIdStr = flowOfDocumentStateEvent.getParameters().get(CooperativePlotCreationService.DOCUMENT_VOTING_BATCH_ID_ATTR_NAME);
        Long batchVotingId = VarUtils.getLong(batchVotingIdStr, -1l);
        if (batchVotingId > -1l) {
            BatchVoting firstBatchVoting = batchVotingService.getBatchVoting(batchVotingId, true, true);

            // Создать КУч
            Community kuchCommunity = createSubGroup(firstBatchVoting);
        }
    }

    /**
     * Подписали документ "Положение КУч"
     * @param flowOfDocumentStateEvent
     */
    private void onSignDocumentOfStateOfKuch(FlowOfDocumentStateEvent flowOfDocumentStateEvent) throws Exception {
        // создать собрание 2го этапа по выбору председателя и ревизора КУч ПО
        String batchVotingIdStr = flowOfDocumentStateEvent.getParameters().get(CooperativePlotCreationService.DOCUMENT_VOTING_BATCH_ID_ATTR_NAME);
        Long batchVotingId = VarUtils.getLong(batchVotingIdStr, -1l);
        if (batchVotingId > -1l) {
            BatchVoting firstBatchVoting = batchVotingService.getBatchVoting(batchVotingId, true, true);

            // Получаем из параметров положения КУч ПО ИД созданного КУч ПО
            String kuchCommunityIdStr = flowOfDocumentStateEvent.getParameters().get(BatchVotingConstants.KUCH_COMMUNITY_ID_ATTR_NAME);
            Long kuchCommunityId = VarUtils.getLong(kuchCommunityIdStr, -1l);
            Community kuchCommunity = communityDomainService.getByIdFullData(kuchCommunityId);

            // Создать собрание для выбора председателя и ревизора КУч
            cooperativeSecondMeetingService.createCooperativeMeetingForVotingPresidentAndRevisor(firstBatchVoting, kuchCommunity);
        }
    }

    /**
     * Подписали протокол собрания 2го этапа
     * @param flowOfDocumentStateEvent
     */
    private void onSignProtocolOfChoosePresidentAndRevisorMeeting(FlowOfDocumentStateEvent flowOfDocumentStateEvent) throws Exception {
        String batchVotingIdStr = flowOfDocumentStateEvent.getParameters().get(CooperativePlotCreationService.DOCUMENT_VOTING_BATCH_ID_ATTR_NAME);
        Long batchVotingId = VarUtils.getLong(batchVotingIdStr, -1l);
        if (batchVotingId > -1l) {
            BatchVoting batchVoting = batchVotingService.getBatchVoting(batchVotingId, true, true);
            Document protocolSecondMeeting = flowOfDocumentStateEvent.getDocument();
            // Создать заявление в совет ПО об утверждении председателя и ревизора
            cooperativePlotDocumentsService.createStatementToSovietAboutChoosePresidentAndRevisorKuch(batchVoting, protocolSecondMeeting);
        }
    }

    /**
     * Подписали заявление в Совет ПО об утверждении председателя и ревизора КУч
     * @param flowOfDocumentStateEvent
     */
    private void onSignStatementToSovientForChoosePresidentAndRevisorMeeting(FlowOfDocumentStateEvent flowOfDocumentStateEvent) throws Exception {
        // Создаётся доверенность председателю КУч вести договоры переговоры и тд
        String batchVotingIdStr = flowOfDocumentStateEvent.getParameters().get(CooperativePlotCreationService.DOCUMENT_VOTING_BATCH_ID_ATTR_NAME);
        Long batchVotingId = VarUtils.getLong(batchVotingIdStr, -1l);
        if (batchVotingId > -1l) {
            BatchVoting batchVoting = batchVotingService.getBatchVoting(batchVotingId, true, true);

            // ИД протокола собрания 2го этапа
            Long protocolId = VarUtils.getLong(flowOfDocumentStateEvent.getParameters().get(CooperativePlotDocumentsService.PROTOCOL_SECOND_MEETING_ATTR_ID), -1l);
            // Протокол собрания 2го этапа
            Document protocolOfSecondMeeting = documentDomainService.getById(protocolId);

            // Создать доверенность для председателя КУч.
            cooperativePlotSovietDocumentsService.createProxyDocumentForPresidentOfKuch(batchVoting, protocolOfSecondMeeting);
        }
    }

    /**
     * Подписали доверенность председателю КУч
     * @param flowOfDocumentStateEvent
     * @throws Exception
     */
    private void onSignProxyPresidentKuch(FlowOfDocumentStateEvent flowOfDocumentStateEvent) throws Exception {
        // Заявление подписано всеми, значит Председателя и Ревизора ставим на должности в новом КУч
        String batchVotingIdStr = flowOfDocumentStateEvent.getParameters().get(CooperativePlotCreationService.DOCUMENT_VOTING_BATCH_ID_ATTR_NAME);
        Long batchVotingId = VarUtils.getLong(batchVotingIdStr, -1l);
        if (batchVotingId > -1l) {
            BatchVoting batchVoting = batchVotingService.getBatchVoting(batchVotingId, true, true);

            // Доверенность, которую подписали
            Document proxyDocumentForPresidentOfKuch = flowOfDocumentStateEvent.getDocument();

            // ИД протокола собрания 2го этапа
            Long protocolId = VarUtils.getLong(flowOfDocumentStateEvent.getParameters().get(CooperativePlotDocumentsService.PROTOCOL_SECOND_MEETING_ATTR_ID), -1l);
            // Протокол собрания 2го этапа
            Document protocolOfSecondMeeting = documentDomainService.getById(protocolId);

            // Устанавливаем председателя и ревизора в КУч
            Community kuchCommunity = commonVotingService.getKuchFromSecondMeeting(batchVoting);
            setPresidentAndRevisor(batchVoting, kuchCommunity, proxyDocumentForPresidentOfKuch, protocolOfSecondMeeting);
        }
    }

    /**
     * Метод создания подгруппы для ЧУЧ.
     * @param batchVoting
     */
    private Community createSubGroup(BatchVoting batchVoting) throws Exception {
        boolean batchVotingValid = true;
        for (Voting voting : batchVoting.getVotings()) {
            if (voting.getResult().getResultType() != VotingResultType.VALID) {
                batchVotingValid = false;
            }
        }

        if (!batchVotingValid) return null;

        Long communityId = Long.valueOf(batchVoting.getAdditionalData().get(BatchVotingConstants.COMMUNITY_ID_ATTR_NAME));
        Community community = communityDomainService.getByIdFullData(communityId);

        if (community == null) {
            throw new Exception("Не установлено ПО в котором создаётся КУч!");
        }

        List<User> members = new ArrayList<>();
        for (RegisteredVoter voter : batchVoting.getVotersAllowed()) {
            members.add(userDataService.getByIdMinData(voter.getVoterId()));
        }
        // Сообщение о том, что КУч создан должно приходить совету ПО и председателю совета ПО.
        List<User> receiversOfEvent = new ArrayList<>(members);
        // Добавить список участников из совета ПО и прведседателя совета ПО
        receiversOfEvent.addAll(communitiesService.getMembersSovietOfCooperative(community));
        receiversOfEvent.add(communitiesService.getPresidentSovietOfCooperative(community));

        List<OkvedDomain> okveds = new ArrayList<>();
        okveds.addAll(community.getOkveds());

        List<ListEditorItem> activityScopes = new ArrayList<>();
        activityScopes.addAll(community.getActivityScopes());

        String plotName = batchVoting.getAdditionalData().get(BatchVotingConstants.COOPERATIVE_PLOT_NAME_ATTR_NAME);

        String fullName = commonVotingService.getFullCooperativePlotNameForCreateCommunity(plotName, community); //"Кооперативный участок " + commonVotingService.getFullCooperativePlotName(plotName, community);
        String shortName = commonVotingService.getShortCooperativePlotNameForCreateCommunity(plotName, community); //"КУч " + commonVotingService.getShortCooperativePlotName(plotName, community);

        FieldsGroup fieldsGroup = new FieldsGroup();

        CommunityData communityData = new CommunityData();
        communityData.setFieldGroups(new ArrayList<>());
        communityData.getFieldGroups().add(fieldsGroup);

        Community subgroup = new Community();
        subgroup.setFullRuName(fullName);
        subgroup.setAccessType(community.getAccessType());
        subgroup.setVisible(community.isVisible());
        subgroup.setAnnouncement(null);
        subgroup.getOkveds().addAll(okveds);
        //subgroup.setDescription(batchVoting.getAdditionalData().get(CommonVotingService.BATCH_VOTING_TARGETS_ATTR_NAME));
        subgroup.setSeoLink(null);
        subgroup.getActivityScopes().addAll(activityScopes);
        subgroup.setParent(community);
        subgroup.setCommunityData(communityData);

        String descriptionCommunity = batchVoting.getAdditionalData().get(BatchVotingConstants.BATCH_VOTING_TARGETS_ATTR_NAME);

        //community.getChildren().add(subgroup);

        User batchVotingOwner = userDataService.getByIdFullData(batchVoting.getOwnerId());
        // Создатель объединения - организатор КУч. http://projects.ramera.ru/browse/RAMERA-607
        User communityCreator = batchVotingOwner;//community.getCreator();
        //Long ceoId = Long.valueOf(FieldsService.getFieldStringValue(community.getFieldValue("COMMUNITY_CHAIRMAN_OF_THE_BOARD1_ID")));
        // Руководитель объединения - организатор КУч. http://projects.ramera.ru/browse/RAMERA-607
        User communityCeo = batchVotingOwner;//sharerDao.getById(ceoId);

        /*Map<FieldEntity, String> fieldsMap = new HashMap<>();
        Map<FieldEntity, String> fieldsFileUrlMap = new HashMap<>();*/

        FieldEntity fieldEntity = fieldDao.getByInternalName(FieldConstants.COMMUNITY_TYPE);
        Field field = fieldEntity.toDomain();
        field.setValue(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName());
        fieldsGroup.getFields().add(field);

        //fieldsMap.put(field, CommunityEntity.COMMUNITY_WITH_ORGANIZATION);

        // Получием объект универсального списка RameraListEditor по мнемокоду,
        // Затем у него получаем ИД (кооперативный участок)
        ListEditorItem listEditorItem = listEditorItemDomainService.getByCode(Community.COOPERATIVE_PLOT_ASSOCIATION_FORM_CODE);
        if (listEditorItem == null) {
            throw new Exception("Форма объединения с кодом \"" + Community.COOPERATIVE_PLOT_ASSOCIATION_FORM_CODE + "\" не найдена!");
        }
        String associationFormId = String.valueOf(listEditorItem.getId()); // Кооперативный участок
        fieldEntity = fieldDao.getByInternalName(FieldConstants.COMMUNITY_ASSOCIATION_FORM);
        field = fieldEntity.toDomain();
        field.setValue(associationFormId);
        fieldsGroup.getFields().add(field);
        //fieldsMap.put(field, associationFormId);

        // Сохраняем наименование объединения в полях
        // Полное наименование на русском
        fieldEntity = fieldDao.getByInternalName(FieldConstants.COMMUNITY_FULL_RU_NAME);
        field = fieldEntity.toDomain();
        field.setValue(fullName);
        fieldsGroup.getFields().add(field);

        //Кооперативный Участок Такой-то Потребительского Общества Такого-то
        //fieldsMap.put(field, fullName);
        // Короткое наименование на русском
        //КУч Проверка-1 - ПО РОС "РА-ДОМ"
        fieldEntity = fieldDao.getByInternalName(FieldConstants.COMMUNITY_SHORT_RU_NAME);
        field = fieldEntity.toDomain();
        field.setValue(shortName);
        fieldsGroup.getFields().add(field);
        //fieldsMap.put(field, shortName);

        // Полное описание целей и задач
        fieldEntity = fieldDao.getByInternalName(FieldConstants.COMMUNITY_DESCRIPTION);
        field = fieldEntity.toDomain();
        field.setValue(descriptionCommunity);
        fieldsGroup.getFields().add(field);

        // Краткое описание целей объединения
        fieldEntity = fieldDao.getByInternalName(FieldConstants.COMMUNITY_BRIEF_DESCRIPTION);
        field = fieldEntity.toDomain();
        field.setValue(descriptionCommunity);
        fieldsGroup.getFields().add(field);

        // Установить поля с фактическим адресом создаваемого КУч
        // TODO
        String addressFieldsEncoded = batchVoting.getAdditionalData().get(BatchVotingConstants.ADDRESS_FIELDS_ATTR_NAME);
        fieldsGroup.getFields().addAll(commonVotingService.decodePlotAddress(addressFieldsEncoded));

        //--------------------------------------------------------------------------------------------------------------
        // Установить поля из ПО
        //--------------------------------------------------------------------------------------------------------------
        // Юр адрес из ПО копируем в КУч
        List<FieldsGroup> communityFieldGroups = community.getCommunityData().getFieldGroups();
        Field communityCharterDescField = null;
        //FieldsGroupEntity fieldsGroupEntity = fieldsGroupDao.getByInternalName(COMMUNITY_WITH_ORGANIZATION_LEGAL_ADDRESS_GROUP_NAME);
        if (communityFieldGroups != null) {
            for (FieldsGroup group : communityFieldGroups) {
                if (group != null) {
                    for (Field fieldItem : group.getFields()) {
                        if (COMMUNITY_WITH_ORGANIZATION_LEGAL_ADDRESS_GROUP_NAME.equals(group.getInternalName())) {
                            fieldsGroup.getFields().add(fieldItem);
                        }
                        if (COMMUNITY_CHARTER_DESCRIPTION_FIELD_NAME.equals(field.getInternalName())) {
                            communityCharterDescField = field;
                        }
                    }
                }

            }
        }

        // Прикреплённые документы к уставу в ПО устанавливаем как прикреплённые документы к уставу в КУч
        List<FieldFile> fieldFiles = null;
        if (communityCharterDescField != null) {
            fieldFiles = communityDomainService.getCommunityFieldFiles(communityId, communityCharterDescField.getId());
            communityCharterDescField.setValue("");
            fieldsGroup.getFields().add(communityCharterDescField);
        }
/*
        FieldValueEntity communityCharterDescription = community.getFieldValue(COMMUNITY_CHARTER_DESCRIPTION_FIELD_NAME);
        List<FieldFileEntity> communityCharterDescriptionFieldFiles = communityCharterDescription.getFieldFiles();
        FieldValueEntity subgroupCharterDescriptionFieldValue = subgroup.getFieldValue(COMMUNITY_CHARTER_DESCRIPTION_FIELD_NAME);
        if (subgroupCharterDescriptionFieldValue == null) {
            subgroupCharterDescriptionFieldValue = new FieldValueEntity();
            subgroupCharterDescriptionFieldValue.setObject(subgroup);
            subgroupCharterDescriptionFieldValue.setField(communityCharterDescription.getField());
            subgroupCharterDescriptionFieldValue.setStringValue("");
            fieldValueDao.saveOrUpdate(subgroupCharterDescriptionFieldValue);
        }
        for(FieldFileEntity fieldFile : communityCharterDescriptionFieldFiles) {
            FieldFileEntity subgroupFieldFile = new FieldFileEntity();
            subgroupFieldFile.setUrl(fieldFile.getUrl());
            subgroupFieldFile.setName(fieldFile.getName());
            subgroupFieldFile.setFieldValue(subgroupCharterDescriptionFieldValue);
            fieldFileDao.saveOrUpdate(subgroupFieldFile);
        }*/

        // Устанавливаем ИНН из ПО в КУч
        Field fieldInn = community.getCommunityData().getFieldByInternalName(FieldConstants.COMMUNITY_INN);
        if (fieldInn != null) {
            fieldsGroup.getFields().add(fieldInn);
        }
        /*String kuchInn = community.getInn();
        field = fieldDao.getByInternalName(COMMUNITY_INN_FIELD_NAME);
        if (field != null && kuchInn != null) {
            fieldsMap.put(field, kuchInn);
        }*/
        //--------------------------------------------------------------------------------------------------------------

        subgroup = communitiesService.createCommunity(subgroup, communityCreator, members, receiversOfEvent);
        //subgroup = fieldsService.saveFields(fieldsMap, subgroup);

        if (communityCharterDescField != null) {
            fieldsService.saveFieldFiles(
                    communityCharterDescField.getId(),
                    subgroup.getId(),
                    fieldFiles
            );
        }

        // Создать положение КУч
        Document document = cooperativePlotSovietDocumentsService.createDocumentStatusKuch(batchVoting, subgroup.getId(), new Date());
        // Установить положение КУч в виде устава в КУч ПО
        setKuchCharter(subgroup, document.getContent());

        return subgroup;
    }

    /**
     * Установить устав КУч ПО
     * @param kuchCommunity
     * @param kuchCharter
     */
    private void setKuchCharter(Community kuchCommunity, String kuchCharter) {
        Field field = kuchCommunity.getCommunityData().getFieldByInternalName(COMMUNITY_CHARTER_DESCRIPTION_FIELD_NAME);
        if (field == null) {
            FieldEntity fieldEntity = fieldDao.getByInternalName(COMMUNITY_CHARTER_DESCRIPTION_FIELD_NAME);
            field = fieldEntity.toDomain();
        }
        field.setValue(kuchCharter);
        kuchCommunity.getCommunityData().getFieldGroups().get(0).getFields().add(field);
        communitiesService.editCommunity(kuchCommunity, kuchCommunity.getCreator());
    }

    /**
     * Устанавливаем выбранного председателя и ревизора из собрания 2го этапа
     * @param secondBatchVoting
     * @param kuchCommunity
     * @param proxyDocumentForPresidentOfKuch - доверенность для председателя КУч
     * @param protocolOfSecondMeeting - протокол голосования собрания 2го этапа
     */
    private void setPresidentAndRevisor(BatchVoting secondBatchVoting, Community kuchCommunity, Document proxyDocumentForPresidentOfKuch, Document protocolOfSecondMeeting) {
        // 1й элемент голосования возвращает выбранного председателя
        User chairman = userDataService.getByIdFullData(Long.valueOf(secondBatchVoting.getVotings().get(CooperativeSecondPlotBatchVoting.VOTING_FOR_PRESIDENT_OF_SOCIAL_COMMUNITY_INDEX).getVotingItems().get(0).getValue()));
        CommunityMember chairmanMember = communityMemberDomainService.getByCommunityIdAndUserId(kuchCommunity.getId(), chairman.getId());

        List<CommunityPermission> permissions = communityPermissionDomainService.getByCommunityId(kuchCommunity.getId());
        CommunityPost communityPost = new CommunityPost();
        communityPost.setName(CommonCreateKuchSettings.getInstance().getPresidentOfKuchPostName());
        communityPost.setPosition(0);
        communityPost.setVacanciesCount(1);
        communityPost.setCommunity(kuchCommunity);
        communityPost.setPermissions(permissions);

        List<CommunityMember> postMembers = new ArrayList<>();
        postMembers.add(chairmanMember);
        communityPost.setMembers(postMembers);
        communityPostDomainService.save(communityPost);

        //--------------------------------------------------------------------------------------------------------------
        // Устанавливаем значение и прикрепляем документы к полю с председателем КУч
        //--------------------------------------------------------------------------------------------------------------
        FieldEntity presidentOfKuchSharerFieldEntity = fieldDao.getByInternalName(PRESIDENT_OF_COOPERATIVE_PLOT_FIELD_INTERNAL_NAME);
        FieldEntity presidentOfKuchSharerIdFieldEntity = fieldDao.getByInternalName(PRESIDENT_OF_COOPERATIVE_PLOT_FIELD_INTERNAL_NAME + "_ID");

        Field presidentOfKuchSharerField = presidentOfKuchSharerFieldEntity.toDomain();
        Field presidentOfKuchSharerIdField = presidentOfKuchSharerIdFieldEntity.toDomain();
        presidentOfKuchSharerField.setValue(chairman.getFullName());
        presidentOfKuchSharerIdField.setValue(String.valueOf(chairman.getId()));

        FieldsGroup fieldsGroup = kuchCommunity.getCommunityData().getFieldGroups().get(0);
        fieldsGroup.getFields().add(presidentOfKuchSharerField);
        fieldsGroup.getFields().add(presidentOfKuchSharerIdField);

        List<FieldFile> fieldFiles = new ArrayList<>();
        // Доверенность
        FieldFile fieldFile = new FieldFile();
        fieldFile.setName(proxyDocumentForPresidentOfKuch.getName());
        //fieldFile.setUrl(proxyDocumentForPresidentOfKuch.getPdfLink()); // TODO
        fieldFiles.add(fieldFile);


        // Протокол собрания пайщиков КУч о выборе председателя КУч
        fieldFile = new FieldFile();
        fieldFile.setName(protocolOfSecondMeeting.getName());
        //fieldFile.setUrl(protocolOfSecondMeeting.getPdfLink()); // TODO
        fieldFiles.add(fieldFile);

        // Доверенность
        /*FieldFileEntity fieldFile = new FieldFileEntity();
        fieldFile.setName(proxyDocumentForPresidentOfKuch.getName());
        fieldFile.setUrl(proxyDocumentForPresidentOfKuch.getPdfLink());
        fieldFile.setFieldValue(presidentOfKuchSharerFieldValue);
        fieldFileDao.saveOrUpdate(fieldFile);*/

        // Протокол собрания пайщиков КУч о выборе председателя КУч
        /*fieldFile = new FieldFileEntity();
        fieldFile.setName(protocolOfSecondMeeting.getName());
        fieldFile.setUrl(protocolOfSecondMeeting.getPdfLink());
        fieldFile.setFieldValue(presidentOfKuchSharerFieldValue);
        fieldFileDao.saveOrUpdate(fieldFile);*/
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------------------------------------------------------------------------------------
        // TODO Нестандартное назначение на пост председателя КУч.
        //--------------------------------------------------------------------------------------------------------------
        // Уведомление должно приходить председателю КУч, председателю ПО. А также всем участникам КУЧ.
        List<User> receivers = new ArrayList<>();
        // Председатель КУч
        receivers.add(commonVotingService.getPresidentOfKuch(secondBatchVoting));
        // Председатель ПО
        receivers.add(kuchCommunity.getParent().getCreator());
        for (RegisteredVoter voter : secondBatchVoting.getVotersAllowed()) {
            receivers.add(userDataService.getByIdFullData(voter.getVoterId()));
        }
        // Параметры уведомления
        Map<String, String> parameters = new HashMap<>();
        parameters.put("postName", communityPost.getName());
        parameters.put("candidateName", chairman.getName());
        parameters.put("candidateId", String.valueOf(chairman.getId()));
        blagosferaEventPublisher.publishEvent(new CommunityOtherEvent(this, CommunityEventType.MEMBER_VOTING_POST, kuchCommunity, receivers, parameters));

        // 2й элемент голосования возвращает выбранного ревизора
        User revisor = userDataService.getByIdFullData(Long.valueOf(secondBatchVoting.getVotings().get(CooperativeSecondPlotBatchVoting.VOTING_FOR_REVISOR_OF_SOCIAL_COMMUNITY_INDEX).getVotingItems().get(0).getValue()));
        CommunityMember revisorMember = communityMemberDomainService.getByCommunityIdAndUserId(kuchCommunity.getId(), revisor.getId());

        //communityPost = new CommunityPost(CommonCreateKuchSettings.getInstance().getRevisorOfKuchPostName(), 1, 1, kuchCommunity, permissions);
        communityPost = new CommunityPost();
        communityPost.setName(CommonCreateKuchSettings.getInstance().getRevisorOfKuchPostName());
        communityPost.setPosition(1);
        communityPost.setVacanciesCount(1);
        communityPost.setCommunity(kuchCommunity);
        communityPost.setPermissions(permissions);

        postMembers = new ArrayList<>();
        postMembers.add(revisorMember);
        communityPost.setMembers(postMembers);
        communityPostDomainService.save(communityPost);
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------------------------------------------------------------------------------------
        // Установить ревизора в поле КУч
        //--------------------------------------------------------------------------------------------------------------
        FieldEntity revisorOfKuchSharerFieldEntity = fieldDao.getByInternalName(REVISOR_OF_COOPERATIVE_PLOT_FIELD_INTERNAL_NAME);
        FieldEntity revisorOfKuchSharerIdFieldEntity = fieldDao.getByInternalName(REVISOR_OF_COOPERATIVE_PLOT_FIELD_INTERNAL_NAME + "_ID");

        Field revisorOfKuchSharerField = revisorOfKuchSharerFieldEntity.toDomain();
        Field revisorOfKuchSharerIdField = revisorOfKuchSharerIdFieldEntity.toDomain();
        revisorOfKuchSharerField.setValue(revisor.getFullName());
        revisorOfKuchSharerIdField.setValue(String.valueOf(revisor.getId()));

        fieldsGroup.getFields().add(presidentOfKuchSharerField);
        fieldsGroup.getFields().add(presidentOfKuchSharerIdField);
/*
        FieldValueEntity revisorOfKuchSharerFieldValue = kuchCommunity.getFieldValue(revisorOfKuchSharerField);
        FieldValueEntity revisorOfKuchSharerFieldIdValue = kuchCommunity.getFieldValue(revisorOfKuchSharerIdField);

        if (revisorOfKuchSharerFieldValue == null) {
            revisorOfKuchSharerFieldValue = new FieldValueEntity();
            revisorOfKuchSharerFieldValue.setObject(kuchCommunity);
            revisorOfKuchSharerFieldValue.setField(revisorOfKuchSharerField);
            revisorOfKuchSharerFieldValue.setStringValue(revisor.getFullName());
        } else {
            revisorOfKuchSharerFieldValue.setStringValue(revisor.getFullName());
        }
        if (revisorOfKuchSharerFieldIdValue == null) {
            revisorOfKuchSharerFieldIdValue = new FieldValueEntity();
            revisorOfKuchSharerFieldIdValue.setObject(kuchCommunity);
            revisorOfKuchSharerFieldIdValue.setField(revisorOfKuchSharerIdField);
            revisorOfKuchSharerFieldIdValue.setStringValue(String.valueOf(revisor.getId()));
        } else {
            revisorOfKuchSharerFieldIdValue.setStringValue(String.valueOf(revisor.getId()));
        }
        fieldValueDao.saveOrUpdate(revisorOfKuchSharerFieldValue);
        fieldValueDao.saveOrUpdate(revisorOfKuchSharerFieldIdValue);*/
        //--------------------------------------------------------------------------------------------------------------

        // Установливаем ИД бина поведения назначения на пост бухгалтера
        communityPost = new CommunityPost();
        communityPost.setName(CommonCreateKuchSettings.getInstance().getBuhgalterOfKuchPostName());
        communityPost.setPosition(1);
        communityPost.setVacanciesCount(1);
        communityPost.setCommunity(kuchCommunity);
        communityPost.setPermissions(permissions);
        communityPost.setAppointBehavior(PlotBuhgalterPostAppointBahavior.NAME);
        communityPostDomainService.save(communityPost);

        // Нужно отправить оповещение председателю КУч о том, что нужно предложить должность бухгалетра-кассира
        RameraTextEntity rameraText = rameraTextDao.getByCode(BUHGALTER_INSTRUCTION);
        String instruction = null;
        if (rameraText != null) {
            instruction = rameraText.getText();
        }

        kuchCommunity = communitiesService.editCommunity(kuchCommunity, kuchCommunity.getCreator());
        chairmanMember.setCommunity(kuchCommunity);
        blagosferaEventPublisher.publishEvent(new CommunityMemberAppointRequestEvent(this, CommunityEventType.NEED_APPOINT_MEMBER_TO_POST, chairmanMember, chairman, communityPost, instruction));
    }

    private void publishErrorMessage(String message, Long votingOwnerId) {
        if (message == null) return;
        if (votingOwnerId == null) return;

        User votingOwner = userDataService.getByIdFullData(votingOwnerId);

        if (votingOwner == null) return;

        blagosferaEventPublisher.publishEvent(new VoterErrorEvent(this, votingOwner, message));
    }
}

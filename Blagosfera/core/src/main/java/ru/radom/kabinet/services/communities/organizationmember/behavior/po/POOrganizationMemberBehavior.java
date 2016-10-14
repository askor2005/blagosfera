package ru.radom.kabinet.services.communities.organizationmember.behavior.po;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.askor.blagosfera.domain.community.OrganizationCommunityMember;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.community.OrganizationCommunityMemberEvent;
import ru.askor.blagosfera.domain.events.community.OrganizationCommunityMemberEventType;
import ru.askor.blagosfera.domain.events.document.FlowOfDocumentStateEvent;
import ru.askor.blagosfera.domain.events.document.FlowOfDocumentStateEventType;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.document.generator.CreateDocumentParameter;
import ru.radom.kabinet.document.generator.ParticipantCreateDocumentParameter;
import ru.radom.kabinet.document.generator.UserFieldValue;
import ru.radom.kabinet.document.generator.UserFieldValueBuilder;
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.document.services.DocumentService;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityException;
import ru.radom.kabinet.services.communities.organizationmember.OrganizationMemberDomainService;
import ru.radom.kabinet.services.communities.organizationmember.behavior.IOrganizationMemberBehavior;
import ru.radom.kabinet.services.communities.organizationmember.behavior.POAndKuchCommunityService;
import ru.radom.kabinet.services.communities.organizationmember.dto.ApproveOrganizationCommunityMembersDto;
import ru.radom.kabinet.services.communities.organizationmember.dto.LeaveOrganizationCommunityMembersDto;
import ru.radom.kabinet.services.communities.organizationmember.dto.OrganizationMembersHandleResult;
import ru.radom.kabinet.utils.VarUtils;

import java.util.*;

/**
 *
 * Created by vgusev on 20.10.2015.
 */
@Transactional
@Service
public class POOrganizationMemberBehavior implements IOrganizationMemberBehavior {

    private static final Logger logger = LoggerFactory.getLogger(POOrganizationMemberBehavior.class);

    //
    private static final String DOCUMENT_PARAMETER_DISCRIMINATOR_ATTR_NAME = "DOCUMENT_PARAMETER_DISCRIMINATOR";

    //
    private static final String MEMBER_ID_ATTR_NAME = "MEMBER_ID";

    //
    private static final String MEMBER_IDS_ATTR_NAME = "MEMBER_IDS";

    //
    private static final String STATEMENT_TO_JOIN_COMMUNITY_DOC_PARAMETERS_KEY = "STATEMENT_TO_JOIN_COMMUNITY_DOC_PARAMETERS";

    //
    private static final String PROTOCOL_TO_JOIN_COMMUNITY_DOC_PARAMETERS_KEY = "PROTOCOL_TO_JOIN_COMMUNITY_DOC_PARAMETERS";

    //
    private static final String STATEMENT_TO_EXCLUDE_COMMUNITY_DOC_PARAMETERS_KEY = "STATEMENT_TO_EXCLUDE_COMMUNITY_DOC_PARAMETERS";

    //
    private static final String PROTOCOL_TO_EXCLUDE_COMMUNITY_DOC_PARAMETERS_KEY = "PROTOCOL_TO_EXCLUDE_COMMUNITY_DOC_PARAMETERS";

    @Autowired
    private OrganizationMemberDomainService organizationMemberDomainService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private POAndKuchCommunityService poAndKuchCommunityService;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    public void checkPermission(Community community, Long userId, String permission, String errorMessage) {
        if (!communitiesService.hasPermission(community.getId(), userId, permission)) {
            throw new CommunityException(errorMessage);
        }
    }

    // Запрос на вступление через документы
    @Transactional
    @Override
    public OrganizationMembersHandleResult requestToJoinInCommunity(Community community, Community candidateToMember) {
        OrganizationCommunityMember organizationCommunityMember = new OrganizationCommunityMember();
        organizationCommunityMember.setCommunity(community);
        organizationCommunityMember.setOrganization(candidateToMember);
        organizationCommunityMember.setStatus(CommunityMemberStatus.CONDITION_NOT_DONE_REQUEST);
        organizationCommunityMember = organizationMemberDomainService.save(organizationCommunityMember);

        Document document = createJoinDocument(community, candidateToMember, organizationCommunityMember);
        organizationCommunityMember.setDocument(document);

        organizationMemberDomainService.save(organizationCommunityMember);

        return new OrganizationMembersHandleResult(document);
    }

    // Отказ на вступление в ПО от руководства ПО
    @Transactional
    @Override
    public OrganizationMembersHandleResult rejectRequests(Community community, List<OrganizationCommunityMember> organizationCommunityMembers, User currentUser) {
        // Проверить, что у текущего пользователя есть права на исключение пайщиков
        checkPermission(community, currentUser.getId(), "ROLE_APPROVE_SHARERS", "У Вас нет прав на вывод пайщиков из ПО");

        // Нужно удалить запросы
        for (OrganizationCommunityMember organizationCommunityMember : organizationCommunityMembers) {
            // Нужно вернуть заблокированные средства
            poAndKuchCommunityService.cancelAllBlockedFeesInJoinToPO(organizationCommunityMember.getId());

            // Нужно удалить запросы
            organizationMemberDomainService.delete(organizationCommunityMember.getId());

            // Событие отклонения запроса на вступление в ПО
            blagosferaEventPublisher.publishEvent(new OrganizationCommunityMemberEvent(this, OrganizationCommunityMemberEventType.REJECT_REQUEST_TO_PO, organizationCommunityMember.getOrganization(), community));
        }
        return new OrganizationMembersHandleResult();
    }

    // Отказ от вступления в ПО от руководства организации
    @Transactional
    @Override
    public OrganizationMembersHandleResult cancelRequest(OrganizationCommunityMember organizationCommunityMember) {
        // Нужно вернуть заблокированные средства
        poAndKuchCommunityService.cancelAllBlockedFeesInJoinToPO(organizationCommunityMember.getId());

        organizationMemberDomainService.delete(organizationCommunityMember.getId());
        return new OrganizationMembersHandleResult();
    }

    // Принять запрос на вступление от организаций
    @Transactional
    @Override
    public OrganizationMembersHandleResult acceptToJoinInCommunity(Community community, List<OrganizationCommunityMember> organizationCommunityMembers, User currentUser) {
        // Проверить права на принятие пайщиков
        checkPermission(community, currentUser.getId(), "ROLE_APPROVE_SHARERS", "У Вас нет прав на приём пайщиков в ПО");

        for (OrganizationCommunityMember organizationCommunityMember : organizationCommunityMembers) {
            // Принятие участников в процессе
            organizationCommunityMember.setStatus(CommunityMemberStatus.JOIN_IN_PROCESS);
            organizationMemberDomainService.save(organizationCommunityMember);
        }
        // Создаём протокол принятия юр лиц в ПО
        Document document = createAcceptToJoinDocument(community, organizationCommunityMembers);

        return new OrganizationMembersHandleResult(document);
    }

    // Запрос на выход из объединения от руководства организации
    @Transactional
    @Override
    public OrganizationMembersHandleResult requestFromOrganizationToExcludeFromCommunity(OrganizationCommunityMember organizationCommunityMember) {
        // Создаём заявление на выход из ПО
        Document document = createExcludeDocument(organizationCommunityMember);
        organizationCommunityMember.setDocument(document);

        organizationMemberDomainService.save(organizationCommunityMember);

        return new OrganizationMembersHandleResult(document);
    }

    // Запрос на выход из объединения от руководства объединения
    @Transactional
    @Override
    public OrganizationMembersHandleResult requestFromCommunityOwnerToExcludeFromCommunity(OrganizationCommunityMember organizationCommunityMember, User currentUser) {
        // Проверить права на вывод пайщиков
        checkPermission(organizationCommunityMember.getCommunity(), currentUser.getId(), "ROLE_APPROVE_SHARERS", "У Вас нет прав на вывод пайщиков из ПО");

        // Создать транзакции на вывод средств с паевой книжки
        poAndKuchCommunityService.createBlockedFeesInLeaveFromPO(organizationCommunityMember.getId());

        // Переводим юр лицо в статус ожидающего решения совета ПО и далее решения с протоколом
        organizationCommunityMember.setDocument(null);
        organizationCommunityMember.setStatus(CommunityMemberStatus.REQUEST_TO_LEAVE);
        organizationMemberDomainService.save(organizationCommunityMember);

        return new OrganizationMembersHandleResult();
    }

    // Принятие запроса на выход из объединения
    @Transactional
    @Override
    public OrganizationMembersHandleResult acceptExcludeFromCommunity(Community community, List<OrganizationCommunityMember> organizationCommunityMembers, User currentUser) {
        // Проверить права на вывод пайщиков
        checkPermission(community, currentUser.getId(), "ROLE_APPROVE_SHARERS", "У Вас нет прав на вывод пайщиков из ПО");

        // Изменить статус пайщиков на LEAVE_IN_PROCESS
        for (OrganizationCommunityMember organizationCommunityMember : organizationCommunityMembers) {
            organizationCommunityMember.setStatus(CommunityMemberStatus.LEAVE_IN_PROCESS);
            organizationMemberDomainService.save(organizationCommunityMember);
        }

        // Создать протокол на выход из ПО
        Document document = createAcceptToExcludeDocument(community, organizationCommunityMembers);

        return new OrganizationMembersHandleResult(document);
    }

    // Отмена запроса на выход из объедиения
    @Transactional
    @Override
    public OrganizationMembersHandleResult cancelExcludeRequest(OrganizationCommunityMember organizationCommunityMember) {
        // Отменить транзакции по возврату средств
        poAndKuchCommunityService.cancelAllBlockedFeesInLeaveFromPO(organizationCommunityMember.getId());

        organizationCommunityMember.setStatus(CommunityMemberStatus.MEMBER);
        organizationMemberDomainService.save(organizationCommunityMember);
        return new OrganizationMembersHandleResult();
    }

    @Override
    public LeaveOrganizationCommunityMembersDto getLeaveCommunityMembers(Community community, Long userId) {
        // Проверить права на вывод пайщиков
        checkPermission(community, userId, "ROLE_APPROVE_SHARERS", "У Вас нет прав на вывод пайщиков из ПО");
        List<OrganizationCommunityMember> members = organizationMemberDomainService.getByCommunityIdAndStatus(community.getId(), CommunityMemberStatus.REQUEST_TO_LEAVE);
        return new LeaveOrganizationCommunityMembersDto(members);
    }

    @Override
    public ApproveOrganizationCommunityMembersDto getApproveCommunityMembers(Community community, Long userId) {
        // Проверить права на ввод пайщиков
        checkPermission(community, userId, "ROLE_APPROVE_SHARERS", "У Вас нет прав на вывод пайщиков из ПО");
        List<OrganizationCommunityMember> members = organizationMemberDomainService.getByCommunityIdAndStatus(community.getId(), CommunityMemberStatus.CONDITION_DONE_REQUEST);
        return new ApproveOrganizationCommunityMembersDto(members);
    }

    @EventListener
    public void onBlagosferaEvent(FlowOfDocumentStateEvent event) {
        if (event.getParameters() == null || event.getParameters().size() == 0) {
            return;
        }
        try {
            switch (event.getStateEventType()) {
                case DOCUMENT_SIGNED: { // Документ подписан всеми
                    if (event.getParameters().containsKey(DOCUMENT_PARAMETER_DISCRIMINATOR_ATTR_NAME)) {
                        // Подписано заявление на вступление в ПО
                        if (event.getParameters().get(DOCUMENT_PARAMETER_DISCRIMINATOR_ATTR_NAME).equals(STATEMENT_TO_JOIN_COMMUNITY_DOC_PARAMETERS_KEY)) {
                            Long memberId = VarUtils.getLong(event.getParameters().get(MEMBER_ID_ATTR_NAME), -1l);
                            onSignedJoinDocument(memberId);
                        // Подписан протокол принятия в пайщики юр лиц
                        } else if (event.getParameters().get(DOCUMENT_PARAMETER_DISCRIMINATOR_ATTR_NAME).equals(PROTOCOL_TO_JOIN_COMMUNITY_DOC_PARAMETERS_KEY)) {
                            String membersStrIds = event.getParameters().get(MEMBER_IDS_ATTR_NAME);
                            String[] membersIds = membersStrIds.split(",");
                            List<Long> members = new ArrayList<>();
                            for (String strId : membersIds) {
                                members.add(VarUtils.getLong(strId, -1l));
                            }
                            onSignAcceptToJoinDocument(members);
                        // Подписано заявление на выход из ПО
                        } else if (event.getParameters().get(DOCUMENT_PARAMETER_DISCRIMINATOR_ATTR_NAME).equals(STATEMENT_TO_EXCLUDE_COMMUNITY_DOC_PARAMETERS_KEY)) {
                            Long memberId = VarUtils.getLong(event.getParameters().get(MEMBER_ID_ATTR_NAME), -1l);
                            onSignedExcludeDocument(memberId);
                        // Подписан протокол о выходе пайщика юр лицо из ПО
                        } else if (event.getParameters().get(DOCUMENT_PARAMETER_DISCRIMINATOR_ATTR_NAME).equals(PROTOCOL_TO_EXCLUDE_COMMUNITY_DOC_PARAMETERS_KEY)) {
                            String membersStrIds = event.getParameters().get(MEMBER_IDS_ATTR_NAME);
                            String[] membersIds = membersStrIds.split(",");
                            List<Long> members = new ArrayList<>();
                            for (String strId : membersIds) {
                                members.add(VarUtils.getLong(strId, -1l));
                            }
                            onSignAcceptToExcludeDocument(members);
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    //------------------------------------------------------------------------------------------------------------------
    /**
     * Создать заявление для вступления в ПО
     * @param community
     * @param candidateToMember
     * @param organizationCommunityMember
     * @return
     */
    @Transactional
    private Document createJoinDocument(Community community, Community candidateToMember, OrganizationCommunityMember organizationCommunityMember) {
        // Проверяем возможность вступления кандидата в ПО
        poAndKuchCommunityService.checkEntranceOrganizationToCooperative(community);

        String templateCode = POOrganizationMemberSettings.getInstance().getStatementJoinTemplateCode();
        String communityParticipantName = POOrganizationMemberSettings.getInstance().getStatementJoinCommunityParticipantName();
        String organizationParticipantName = POOrganizationMemberSettings.getInstance().getStatementJoinOrganizationParticipantName();

        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        //------------------------------------------------------------------------
        // ПО
        //------------------------------------------------------------------------
        ParticipantCreateDocumentParameter participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getId(), communityParticipantName);
        List<UserFieldValue> userFieldValueList = new ArrayList<>();
        CreateDocumentParameter createDocumentParameter = new CreateDocumentParameter(participantParameter, userFieldValueList);
        createDocumentParameters.add(createDocumentParameter);
        //------------------------------------------------------------------------

        //------------------------------------------------------------------------
        // Организация
        //------------------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), candidateToMember.getId(), organizationParticipantName);
        userFieldValueList = new ArrayList<>();
        createDocumentParameter = new CreateDocumentParameter(participantParameter, userFieldValueList);
        createDocumentParameters.add(createDocumentParameter);
        //------------------------------------------------------------------------

        // Создаём евент, который вызовется после подписания документа
        Map<String, String> parameters = new HashMap<>();
        parameters.put(DOCUMENT_PARAMETER_DISCRIMINATOR_ATTR_NAME, STATEMENT_TO_JOIN_COMMUNITY_DOC_PARAMETERS_KEY);
        parameters.put(MEMBER_ID_ATTR_NAME, String.valueOf(organizationCommunityMember.getId()));
        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);
        return documentService.createDocumentDomain(templateCode, createDocumentParameters, DocumentEntity.SYSTEM_CREATOR_ID, Collections.singletonList(stateEvent));
    }

    @Transactional
    private void onSignedJoinDocument(Long memberId) {
        OrganizationCommunityMember organizationCommunityMember = organizationMemberDomainService.getById(memberId);
        if (organizationCommunityMember != null && CommunityMemberStatus.CONDITION_NOT_DONE_REQUEST.equals(organizationCommunityMember.getStatus())) {
            // Создаём транзакции на вступление в ПО
            User organizationDirector = communitiesService.getCommunityDirector(organizationCommunityMember.getOrganization());
            poAndKuchCommunityService.createBlockedFeesInJoinToPO(memberId, organizationDirector);

            organizationCommunityMember.setStatus(CommunityMemberStatus.CONDITION_DONE_REQUEST);
            organizationMemberDomainService.save(organizationCommunityMember);

            // Событие запроса на вступление в ПО
            blagosferaEventPublisher.publishEvent(new OrganizationCommunityMemberEvent(this, OrganizationCommunityMemberEventType.REQUEST_TO_PO, organizationCommunityMember.getOrganization(), organizationCommunityMember.getCommunity()));
        }
    }
    //------------------------------------------------------------------------------------------------------------------


    //------------------------------------------------------------------------------------------------------------------
    // Протокол принятия новых пайщиков
    //------------------------------------------------------------------------------------------------------------------
    private Document createAcceptToJoinDocument(Community community, List<OrganizationCommunityMember> organizationCommunityMembers) {
        String templateCode = POOrganizationMemberSettings.getInstance().getProtocolJoinTemplateCode();
        String communityParticipantName = POOrganizationMemberSettings.getInstance().getProtocolJoinCommunityParticipantName();
        String organizationsParticipantName = POOrganizationMemberSettings.getInstance().getProtocolJoinOrganizationsParticipantName();
        String protocolJoinDocumentsUserField = POOrganizationMemberSettings.getInstance().getProtocolJoinOrganizationsDocumentsUserField();


        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        //------------------------------------------------------------------------
        // ПО
        //------------------------------------------------------------------------
        ParticipantCreateDocumentParameter participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getId(), communityParticipantName);
        List<UserFieldValue> userFieldValueList = new ArrayList<>();
        CreateDocumentParameter createDocumentParameter = new CreateDocumentParameter(participantParameter, userFieldValueList);
        createDocumentParameters.add(createDocumentParameter);
        //------------------------------------------------------------------------

        //------------------------------------------------------------------------
        // Организации
        //------------------------------------------------------------------------
        List<Long> organizationsIds = new ArrayList<>();
        List<Long> membersIds = new ArrayList<>();
        List<Long> documentIds = new ArrayList<>();
        for (OrganizationCommunityMember organizationCommunityMember : organizationCommunityMembers) {
            organizationsIds.add(organizationCommunityMember.getOrganization().getId());
            membersIds.add(organizationCommunityMember.getId());
            // Собираем ИД документов - заявлений
            if (organizationCommunityMember.getDocument() == null) {
                throw new RuntimeException("У организации " + organizationCommunityMember.getOrganization().getName() + " нет зявления в пайщики ПО");
            }
            documentIds.add(organizationCommunityMember.getDocument().getId());
        }
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION_LIST.getName(), organizationsIds, organizationsParticipantName);
        userFieldValueList = new ArrayList<>();
        UserFieldValue userFieldValue = UserFieldValueBuilder.createDocumentValue(protocolJoinDocumentsUserField, documentIds);
        userFieldValueList.add(userFieldValue);
        createDocumentParameter = new CreateDocumentParameter(participantParameter, userFieldValueList);
        createDocumentParameters.add(createDocumentParameter);
        //------------------------------------------------------------------------

        // Создаём евент, который вызовется после подписания документа
        Map<String, String> parameters = new HashMap<>();
        parameters.put(DOCUMENT_PARAMETER_DISCRIMINATOR_ATTR_NAME, PROTOCOL_TO_JOIN_COMMUNITY_DOC_PARAMETERS_KEY);
        parameters.put(MEMBER_IDS_ATTR_NAME, String.valueOf(StringUtils.join(membersIds, ",")));
        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);
        return documentService.createDocumentDomain(templateCode, createDocumentParameters, DocumentEntity.SYSTEM_CREATOR_ID, Collections.singletonList(stateEvent));
    }

    /**
     * Протокол принятия в пайщики юр лиц подписан
     * @param memberIds
     */
    @Transactional
    private void onSignAcceptToJoinDocument(List<Long> memberIds) {
        for (Long memberId : memberIds) {
            OrganizationCommunityMember organizationCommunityMember = organizationMemberDomainService.getById(memberId);
            organizationCommunityMember.setStatus(CommunityMemberStatus.MEMBER);
            organizationMemberDomainService.save(organizationCommunityMember);

            // Разблокировать транзакции
            poAndKuchCommunityService.acceptAllBlockedFeesInJoinToPO(memberId);

            // Событие - юр лицо принято в ПО
            blagosferaEventPublisher.publishEvent(new OrganizationCommunityMemberEvent(this, OrganizationCommunityMemberEventType.ACCEPT_TO_JOIN_IN_PO, organizationCommunityMember.getOrganization(), organizationCommunityMember.getCommunity()));
        }
    }
    //------------------------------------------------------------------------------------------------------------------


    //------------------------------------------------------------------------------------------------------------------
    /**
     * Создать заявление на выход из ПО
     * @param organizationCommunityMember
     * @return
     */
    @Transactional
    private Document createExcludeDocument(OrganizationCommunityMember organizationCommunityMember) {
        String templateCode = POOrganizationMemberSettings.getInstance().getStatementExcludeTemplateCode();
        String communityParticipantName = POOrganizationMemberSettings.getInstance().getStatementExcludeCommunityParticipantName();
        String organizationParticipantName = POOrganizationMemberSettings.getInstance().getStatementExcludeOrganizationParticipantName();

        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        //------------------------------------------------------------------------
        // ПО
        //------------------------------------------------------------------------
        ParticipantCreateDocumentParameter participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), organizationCommunityMember.getCommunity().getId(), communityParticipantName);
        List<UserFieldValue> userFieldValueList = new ArrayList<>();
        CreateDocumentParameter createDocumentParameter = new CreateDocumentParameter(participantParameter, userFieldValueList);
        createDocumentParameters.add(createDocumentParameter);
        //------------------------------------------------------------------------

        //------------------------------------------------------------------------
        // Организация
        //------------------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), organizationCommunityMember.getOrganization().getId(), organizationParticipantName);
        userFieldValueList = new ArrayList<>();
        createDocumentParameter = new CreateDocumentParameter(participantParameter, userFieldValueList);
        createDocumentParameters.add(createDocumentParameter);
        //------------------------------------------------------------------------

        // Создаём евент, который вызовется после подписания документа
        Map<String, String> parameters = new HashMap<>();
        parameters.put(DOCUMENT_PARAMETER_DISCRIMINATOR_ATTR_NAME, STATEMENT_TO_EXCLUDE_COMMUNITY_DOC_PARAMETERS_KEY);
        parameters.put(MEMBER_ID_ATTR_NAME, String.valueOf(organizationCommunityMember.getId()));
        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);
        return documentService.createDocumentDomain(templateCode, createDocumentParameters, DocumentEntity.SYSTEM_CREATOR_ID, Collections.singletonList(stateEvent));
    }

    @Transactional
    private void onSignedExcludeDocument(Long memberId) {
        OrganizationCommunityMember organizationCommunityMember = organizationMemberDomainService.getById(memberId);
        if (organizationCommunityMember != null) {
            // Создать транзакции на вывод средств с паевой книжки
            poAndKuchCommunityService.createBlockedFeesInLeaveFromPO(memberId);

            organizationCommunityMember.setStatus(CommunityMemberStatus.REQUEST_TO_LEAVE);
            organizationMemberDomainService.save(organizationCommunityMember);

            // Оповещение о том, что юр лицо создало запрос на выход из ПО
            blagosferaEventPublisher.publishEvent(new OrganizationCommunityMemberEvent(this, OrganizationCommunityMemberEventType.REQUEST_TO_EXCLUDE_FROM_PO, organizationCommunityMember.getOrganization(), organizationCommunityMember.getCommunity()));
        }
    }
    //------------------------------------------------------------------------------------------------------------------


    //------------------------------------------------------------------------------------------------------------------
    // Протокол выхода из ПО
    //------------------------------------------------------------------------------------------------------------------
    private Document createAcceptToExcludeDocument(Community community, List<OrganizationCommunityMember> organizationCommunityMembers) {
        String templateCode = POOrganizationMemberSettings.getInstance().getProtocolExcludeTemplateCode();
        String communityParticipantName = POOrganizationMemberSettings.getInstance().getProtocolExcludeCommunityParticipantName();
        String organizationsParticipantName = POOrganizationMemberSettings.getInstance().getProtocolExcludeOrganizationsParticipantName();
        String protocolExcludeDocumentsUserField = POOrganizationMemberSettings.getInstance().getProtocolExcludeOrganizationsDocumentsUserField();

        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        //------------------------------------------------------------------------
        // ПО
        //------------------------------------------------------------------------
        ParticipantCreateDocumentParameter participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getId(), communityParticipantName);
        List<UserFieldValue> userFieldValueList = new ArrayList<>();
        CreateDocumentParameter createDocumentParameter = new CreateDocumentParameter(participantParameter, userFieldValueList);
        createDocumentParameters.add(createDocumentParameter);
        //------------------------------------------------------------------------

        //------------------------------------------------------------------------
        // Организации
        //------------------------------------------------------------------------
        List<Long> organizationsIds = new ArrayList<>();
        List<Long> membersIds = new ArrayList<>();
        List<Long> documentIds = new ArrayList<>();
        for (OrganizationCommunityMember organizationCommunityMember : organizationCommunityMembers) {
            organizationsIds.add(organizationCommunityMember.getOrganization().getId());
            membersIds.add(organizationCommunityMember.getId());
            // Собираем ИД документов - заявлений
            // TODO Пайщика могут выгнать без заявления?
            /*if (organizationCommunityMember.getDocument() == null) {
                throw new RuntimeException("У организации " + organizationCommunityMember.getOrganization().getName() + " нет зявления на выход из ПО");
            }*/
            if (organizationCommunityMember.getDocument() != null) {
                documentIds.add(organizationCommunityMember.getDocument().getId());
            }
        }
        if (documentIds.size() == 0) { // Если заявлений нет (пайщиков выгоняет по своей инициативе руководитель ПО) то установливаем файовый ид документа
            documentIds.add(-1l);
        }
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION_LIST.getName(), organizationsIds, organizationsParticipantName);
        userFieldValueList = new ArrayList<>();
        UserFieldValue userFieldValue = UserFieldValueBuilder.createDocumentValue(protocolExcludeDocumentsUserField, documentIds);
        userFieldValueList.add(userFieldValue);
        createDocumentParameter = new CreateDocumentParameter(participantParameter, userFieldValueList);
        createDocumentParameters.add(createDocumentParameter);
        //------------------------------------------------------------------------

        // Создаём евент, который вызовется после подписания документа
        Map<String, String> parameters = new HashMap<>();
        parameters.put(DOCUMENT_PARAMETER_DISCRIMINATOR_ATTR_NAME, PROTOCOL_TO_EXCLUDE_COMMUNITY_DOC_PARAMETERS_KEY);
        parameters.put(MEMBER_IDS_ATTR_NAME, String.valueOf(StringUtils.join(membersIds, ",")));
        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);
        return documentService.createDocumentDomain(templateCode, createDocumentParameters, DocumentEntity.SYSTEM_CREATOR_ID, Collections.singletonList(stateEvent));
    }

    /**
     * Протокол выхода пайщиков юр лиц подписан
     * @param memberIds
     */
    @Transactional
    private void onSignAcceptToExcludeDocument(List<Long> memberIds) {
        for (Long memberId : memberIds) {
            OrganizationCommunityMember organizationCommunityMember = organizationMemberDomainService.getById(memberId);
            // Разблокировать транзакции вывода средств с паевой книжки
            poAndKuchCommunityService.acceptAllBlockedFeesInLeaveFromPO(memberId);

            organizationMemberDomainService.delete(memberId);

            // Оповещение о том, что юр лицо создало запрос на выход из ПО
            blagosferaEventPublisher.publishEvent(new OrganizationCommunityMemberEvent(this, OrganizationCommunityMemberEventType.ACCEPT_TO_EXCLUDE_FROM_PO, organizationCommunityMember.getOrganization(), organizationCommunityMember.getCommunity()));
        }
    }
    //------------------------------------------------------------------------------------------------------------------
}
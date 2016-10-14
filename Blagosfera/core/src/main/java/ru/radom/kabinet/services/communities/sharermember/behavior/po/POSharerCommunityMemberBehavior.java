package ru.radom.kabinet.services.communities.sharermember.behavior.po;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.askor.blagosfera.domain.events.community.CommunityMemberEvent;
import ru.askor.blagosfera.domain.events.document.FlowOfDocumentStateEvent;
import ru.askor.blagosfera.domain.events.document.FlowOfDocumentStateEventType;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.collections.CommunityMemberStatusList;
import ru.radom.kabinet.document.generator.CreateDocumentParameter;
import ru.radom.kabinet.document.generator.ParticipantCreateDocumentParameter;
import ru.radom.kabinet.document.generator.UserFieldValue;
import ru.radom.kabinet.document.generator.UserFieldValueBuilder;
import ru.radom.kabinet.document.services.DocumentDomainService;
import ru.radom.kabinet.document.services.DocumentService;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.communities.CommunityException;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.services.communities.sharermember.SharerCommunityMemberService;
import ru.radom.kabinet.services.communities.sharermember.behavior.ISharerCommunityMemberBehavior;
import ru.radom.kabinet.services.communities.sharermember.behavior.POAndKuchSharerCommunityService;
import ru.radom.kabinet.services.communities.sharermember.dto.ApproveCommunityMembersDto;
import ru.radom.kabinet.services.communities.sharermember.dto.CommunityMemberResponseDto;
import ru.radom.kabinet.services.communities.sharermember.dto.LeaveCommunityMembersDto;
import ru.radom.kabinet.services.letterOfAuthority.LetterOfAuthorityService;
import ru.radom.kabinet.services.sharer.UserSettingsService;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.HumansStringUtils;
import ru.radom.kabinet.utils.LetterOfAuthorityConstants;
import ru.radom.kabinet.utils.VarUtils;

import java.util.*;

/**
 *
 * Created by vgusev on 28.10.2015.
 */
@Transactional
@Service
public class POSharerCommunityMemberBehavior implements ISharerCommunityMemberBehavior {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentDomainService documentDomainService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Autowired
    private CommunityDataService communityDomainService;

    @Autowired
    private UserSettingsService userSettingsService;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private LetterOfAuthorityService letterOfAuthorityService;

    @Autowired
    private POAndKuchSharerCommunityService poAndKuchSharerCommunityService;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    public void checkPermission(Community community, User sharer, String permission, String errorMessage) {
        if (!communitiesService.hasPermission(community.getId(), sharer.getId(), permission)) {
            throw new CommunityException(errorMessage);
        }
    }

    /**
     * Принять инвайт
     * @param member участник которого приглашали
     * @param notifySignEvent флаг - нужно ли отправлять оповещение о том, что нужно подписать документ
     * @return
     */
    //+
    @Override
    public CommunityMemberResponseDto acceptInvite(CommunityMember member, boolean notifySignEvent) {

        member.setStatus(CommunityMemberStatus.CONDITION_NOT_DONE_REQUEST);
        Community community = communityDomainService.getByIdFullData(member.getCommunity().getId());
        member = communityMemberDomainService.save(member);

        member.setCommunity(community);

        // Создаём заявление пайщика
        Document flowOfDocument = createDocumentForEntranceSharerToCooperative(member, notifySignEvent);

        // Сумма списания со счета участника
        Double amount = poAndKuchSharerCommunityService.getEntranceSharerToCooperativeAmount(member.getCommunity());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("documentName", flowOfDocument.getName());
        parameters.put("link", flowOfDocument.getLink());
        parameters.put("linkDescription", "Перейти к подписанию документа");
        parameters.put("sharerName", member.getUser().getName());
        parameters.put("communityName", member.getCommunity().getName());
        parameters.put("amount", HumansStringUtils.money2numbersWithCurrencyNames(String.valueOf(amount), "RUR"));
        //parameters.put("member", member);
        parameters.put("responseType", "kuchPoName");

        CommunityMemberEvent event = new CommunityMemberEvent(this, CommunityEventType.CONDITION_NOT_DONE_REQUEST, member);

        return new CommunityMemberResponseDto(flowOfDocument, member, parameters, event);
    }

    /**
     * Создать запрос на вступление в ПО
     * @param community
     * @param requester
     * @param notifySignEvent
     * @return
     */
    //+
    @Override
    public CommunityMemberResponseDto request(Community community, User requester, boolean notifySignEvent) {
        CommunityMember member = new CommunityMember();
        member.setUser(requester);
        member.setCommunity(community);
        member.setStatus(CommunityMemberStatus.CONDITION_NOT_DONE_REQUEST);
        member.setRequestDate(new Date());

        // Все остальные действия такие же что и при принятии приглашения
        return acceptInvite(member, notifySignEvent);
    }

    /**
     * Принятие запросов от кандидатов в пайщики на вступление в ПО
     * @param members
     * @param accepter
     * @param notifySignEvent
     * @return
     */
    @Override
    public CommunityMemberResponseDto acceptRequests(List<CommunityMember> members, User accepter, boolean notifySignEvent) {
        Community community = members.get(0).getCommunity();
        // Ищем доверенность на принятие пайщиков в ПО
        boolean hasLOA = letterOfAuthorityService.checkLetterOfAuthority(LetterOfAuthorityConstants.ROLE_KEY_CASHBOX_ACCEPT_SHARERS, accepter, community);
        if (!hasLOA) {
            checkPermission(community, accepter, SharerCommunityMemberService.ROLE_APPROVE_SHARERS_PERMISSION, "У Вас нет прав напринятие пайщиков в ПО");
        }
        for (CommunityMember member : members) {
            SharerCommunityMemberService.check(!CommunityMemberStatus.CONDITION_DONE_REQUEST.equals(member.getStatus()), "Неверный статус запроса");
            member.setStatus(CommunityMemberStatus.JOIN_IN_PROCESS);
            communityMemberDomainService.save(member);
        }

        // Создаём протокол собрания о принятии пайщиков в ПО
        Document document = createProtocolForJoinSharersToCooperative(members, accepter, hasLOA, notifySignEvent);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("documentLink", document.getLink());
        parameters.put("documentName", document.getName());
        parameters.put("responseType", "kuchPoName");
        return new CommunityMemberResponseDto(document, parameters);
    }

    /**
     * Отклонить запросы на вступление в объединение от уполномоченного в объединении
     * @param members
     * @param rejecter
     * @return
     */
    @Override
    public CommunityMemberResponseDto rejectRequestsFromCommunityOwner(List<CommunityMember> members, User rejecter) {
        // Нужно вернуть заблокированные средства и удалить участников из объединения

        Community community = members.get(0).getCommunity();
        // Проверить, что у текущего пользователя есть права на исключение пайщиков
        checkPermission(community, rejecter, SharerCommunityMemberService.ROLE_APPROVE_SHARERS_PERMISSION, "У Вас нет прав на вывод пайщиков из ПО");

        List<CommunityMemberEvent> events = new ArrayList<>();
        for (CommunityMember member : members) {
            SharerCommunityMemberService.check(
                    !CommunityMemberStatus.CONDITION_DONE_REQUEST.equals(member.getStatus()) &&
                    !CommunityMemberStatus.JOIN_IN_PROCESS.equals(member.getStatus()),
                    "Неверный статус запроса"
            );

            // Возвращаем средства
            poAndKuchSharerCommunityService.cancelAllBlockedFeesInJoinToCommunity(member);
            // Удаляем участника из объединения
            communityMemberDomainService.delete(member.getId());
            member.setStatus(null);
            events.add(new CommunityMemberEvent(this, CommunityEventType.REJECT_REQUEST, member));
        }
        return new CommunityMemberResponseDto(events);
    }

    /**
     * Отмена запроса на вступление в объединение от участника
     * @param member
     * @param memberUser
     * @return
     */
    @Override
    public CommunityMemberResponseDto cancelRequestFromMember(CommunityMember member, User memberUser) {
        // Возвращаем средства
        poAndKuchSharerCommunityService.cancelAllBlockedFeesInJoinToCommunity(member);
        // Удаляем участника из объединения
        communityMemberDomainService.delete(member.getId());
        member.setStatus(null);
        return new CommunityMemberResponseDto(member, new CommunityMemberEvent(this, CommunityEventType.CANCEL_REQUEST, member));
    }

    /**
     * Запрос на исключение участника из объединения от уполномоченного в объединении
     * @param member
     * @param excluder уполномоченный участник объединения, который может исключать участников
     * @return
     */
    @Override
    public CommunityMemberResponseDto requestToExcludeFromCommunityOwner(CommunityMember member, User excluder) {
        // Проверить права на вывод пайщиков из ПО
        checkPermission(member.getCommunity(), excluder, SharerCommunityMemberService.ROLE_APPROVE_SHARERS_PERMISSION, "У Вас нет прав на вывод пайщиков из ПО");

        // Создать транзакции на вывод средств с паевой книжки
        poAndKuchSharerCommunityService.createLeaveFees(member);

        // Переводим физ лицо в статус ожидающего решения совета ПО и далее решения с протоколом
        member.setStatus(CommunityMemberStatus.REQUEST_TO_LEAVE);
        communityMemberDomainService.save(member);

        return new CommunityMemberResponseDto(member);
    }

    @Override
    public CommunityMemberResponseDto requestToExcludeFromMember(CommunityMember member, User leaver) {
        // Создаём заявление пайщика на выход из ПО
        Document flowOfDocument = createDocumentForLeaveSharerFromCooperative(member);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("responseType", "kuchPoRequest");
        parameters.put("documentName", flowOfDocument.getName());
        parameters.put("link", flowOfDocument.getLink());
        parameters.put("linkDescription", "Перейти к подписанию документа");
        parameters.put("sharerName", member.getUser().getName());
        parameters.put("communityName", member.getCommunity().getName());
        parameters.put("responseType", "kuchPoName");

        return new CommunityMemberResponseDto(flowOfDocument, parameters);
    }

    @Override
    public void cancelRequestToLeaveFromMember(CommunityMember member) {
        // Возвращаем средства
        poAndKuchSharerCommunityService.cancelAllBlockedFeesInLeaveFromCommunity(member);
        // Устанавливаем статус обратно в MEMBER
        member.setStatus(CommunityMemberStatus.MEMBER);
        communityMemberDomainService.save(member);
    }

    @Override
    public CommunityMemberResponseDto acceptRequestsToExcludeFromCommunity(List<CommunityMember> members, User excluder, boolean notifySignEvent) {
        // Ищем доверенность на принятие пайщиков в ПО
        /*boolean hasLOA = letterOfAuthorityService.checkLetterOfAuthority(LetterOfAuthorityConstants.ROLE_KEY_CASHBOX_ACCEPT_SHARERS, excluder, community);
        if (!hasLOA) {
            checkPermission(community, accepter, SharerCommunityMemberService.ROLE_APPROVE_SHARERS_PERMISSION, "У Вас нет прав на вывод пайщиков из ПО");
        }*/

        Community community = members.get(0).getCommunity();
        checkPermission(community, excluder, SharerCommunityMemberService.ROLE_APPROVE_SHARERS_PERMISSION, "У Вас нет прав на вывод пайщиков из ПО");

        for (CommunityMember member : members) {
            SharerCommunityMemberService.check(!CommunityMemberStatus.REQUEST_TO_LEAVE.equals(member.getStatus()), "Неверный статус запроса");
            member.setStatus(CommunityMemberStatus.LEAVE_IN_PROCESS);
            communityMemberDomainService.save(member);
        }

        boolean hasLOA = false; // TODO

        // Создаём протокол собрания о принятии пайщиков в ПО
        Document document = createProtocolForLeaveSharersFromCooperative(members, excluder, hasLOA, notifySignEvent);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("documentLink", document.getLink());
        parameters.put("documentName", document.getName());
        parameters.put("responseType", "kuchPoName");
        return new CommunityMemberResponseDto(document, parameters);
    }

    @Override
    public LeaveCommunityMembersDto getLeaveCommunityMembers(Community community, User excluder) {
        checkPermission(community, excluder, SharerCommunityMemberService.ROLE_APPROVE_SHARERS_PERMISSION, "У Вас нет прав на получение списка участников, которые выполнили услове на выход из ПО");
        int firstResult = 0;
        int maxResults = Integer.MAX_VALUE;
        CommunityMemberStatusList statusList = new CommunityMemberStatusList();
        statusList.add(CommunityMemberStatus.REQUEST_TO_LEAVE);
        List<CommunityMember> members = communityMemberDomainService.getList(community, statusList, firstResult, maxResults, null, null);

        Map<Long, String> mapSharerIdToDocumentLink = new HashMap<>();
        for(CommunityMember member : members) {
            String docLink = poAndKuchSharerCommunityService.getDocumentForLeaveSharerFromCooperativeLink(member);
            mapSharerIdToDocumentLink.put(member.getUser().getId(), docLink);
        }
        // Протоколы, которые подписаны не всеми
        List<Document> documents = documentDomainService.findByParameterAndParticipant(LEAVE_SHARERS_FROM_COMMUNITY_PARAM_NAME, ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getId());
        return new LeaveCommunityMembersDto(members, mapSharerIdToDocumentLink, documents);
    }

    @Override
    public ApproveCommunityMembersDto getApproveCommunityMembers(Community community, User approver) {
        checkPermission(community, approver, SharerCommunityMemberService.ROLE_APPROVE_SHARERS_PERMISSION, "У Вас нет прав на получение списка участников, которые выполнили услове входа в ПО");
        int firstResult = 0;
        int maxResults = Integer.MAX_VALUE;
        CommunityMemberStatusList statusList = new CommunityMemberStatusList();
        statusList.add(CommunityMemberStatus.CONDITION_DONE_REQUEST);
        List<CommunityMember> members = communityMemberDomainService.getList(community, statusList, firstResult, maxResults, null, null);

        Map<Long, String> mapSharerIdToDocumentLink = new HashMap<>();
        for(CommunityMember member : members) {
            String docLink = poAndKuchSharerCommunityService.getDocumentForEntranceSharerToCooperativeLink(member);
            mapSharerIdToDocumentLink.put(member.getUser().getId(), docLink);
        }
        // Протоколы, которые подписаны не всеми
        List<Document> documents = documentDomainService.findByParameterAndParticipant(JOIN_SHARER_TO_COMMUNITY_PARAM_NAME, ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getId());
        return new ApproveCommunityMembersDto(members, mapSharerIdToDocumentLink, documents);
    }

    //------------------------------------------------------------------------------------------
    // Параметры документов вход пайщиков в ПО
    //------------------------------------------------------------------------------------------

    // Параметр заявления пайщика о вступлении в ПО - тип документа "вступление пайщика в ПО"
    private static final String ENTRANCE_SHARER_TO_COMMUNITY_PARAM_NAME = "ENTRANCE_SHARER_TO_COMMUNITY_PARAM_NAME";

    // Параметр заявления пайщика о вступлении в ПО - ИД участника объединения, вступающего в ПО
    private static final String ENTRANCE_SHARER_TO_COMMUNITY_MEMBER_ID_PARAM_NAME = "ENTRANCE_SHARER_TO_COMMUNITY_MEMBER_ID_PARAM_NAME";

    // Параметр протокола собрания совета ПО - тип документа "вступление пайщика в ПО"
    private static final String JOIN_SHARER_TO_COMMUNITY_PARAM_NAME = "JOIN_SHARER_TO_COMMUNITY_PARAM_NAME";

    // Параметр протокола собрания совета ПО - ИДы участников, вступающих в ПО
    private static final String JOIN_SHARER_TO_COMMUNITY_MEMBER_IDS_PARAM_NAME = "JOIN_SHARER_TO_COMMUNITY_MEMBER_IDS_PARAM_NAME";

    //------------------------------------------------------------------------------------------


    //------------------------------------------------------------------------------------------
    // Параметры документов на выход из пайщиков
    //------------------------------------------------------------------------------------------

    // Параметр заявления пайщика о выходе из ПО - тип документа "выход пайщика из ПО"
    private static final String REQUEST_LEAVE_SHARER_FROM_COMMUNITY_PARAM_NAME = "REQUEST_LEAVE_SHARER_FROM_COMMUNITY_PARAM_NAME";

    // Параметр заявления пайщика о выходе из ПО - ИД пайщика, выходящего из ПО
    private static final String REQUEST_LEAVE_SHARER_FROM_COMMUNITY_MEMBER_ID_PARAM_NAME = "REQUEST_LEAVE_SHARER_FROM_COMMUNITY_MEMBER_ID_PARAM_NAME";

    // Параметр протокола собрания совета ПО - тип документа "выход пайщиков из ПО"
    private static final String LEAVE_SHARERS_FROM_COMMUNITY_PARAM_NAME = "LEAVE_SHARERS_FROM_COMMUNITY_PARAM_NAME";

    // Параметр протокола собрания совета ПО - ИДы участников, выходящих из ПО
    private static final String LEAVE_SHARERS_FROM_COMMUNITY_MEMBER_IDS_PARAM_NAME = "LEAVE_SHARERS_FROM_COMMUNITY_MEMBER_IDS_PARAM_NAME";

    //------------------------------------------------------------------------------------------

    /*@EventListener
    public void onBlagosferaEvent(FlowOfDocumentStateEvent event) {
        switch (event.getStateEventType()) {
            case DOCUMENT_SIGNED:
                // Заявление о принятии пайщика в ПО подписано пайщиком
                // Ищем параметры, которые были заложены в документ при его создании
                if (event.getParameters().containsKey(ENTRANCE_SHARER_TO_COMMUNITY_PARAM_NAME)) {
                    onSignedDeclarationToEntranceCooperative(event); // Действия после подписания заявления на принятие кандидата в пайщики
                } else if (event.getParameters().containsKey(JOIN_SHARER_TO_COMMUNITY_PARAM_NAME)) { // Протокол о принятии пайщиков подписан
                    onSignedProtocolToJoinSharersToCooperative(event);
                } else if (event.getParameters().containsKey(REQUEST_LEAVE_SHARER_FROM_COMMUNITY_PARAM_NAME)) { // Заявление на выход из ПО подписано пайщиком
                    onSignedDeclarationToLeaveCooperative(event);
                } else if (event.getParameters().containsKey(LEAVE_SHARERS_FROM_COMMUNITY_PARAM_NAME)) { // Протокол о выходе пайщиков подписан
                    onSignedProtocolForLeaveSharersFromCooperative(event);
                }
                break;
        }
    }*/

    /**
     * Проверка выхода участника из ПО
     * @param member
     */
    private void checkLeaveSharerFromCooperative(CommunityMember member) {
        // Проверяем, что участник не состоит в дочерних объединениях, потому что дочерние объединяния - КУч и из них
        // нужно выходить тоже c запросом средств
        Community community = communityDomainService.getByIdFullData(member.getCommunity().getId());
        for (Community childCommunity : community.getChildren()) {
            CommunityMember childMember = communityMemberDomainService.getByCommunityIdAndUserId(childCommunity.getId(), member.getUser().getId());
            SharerCommunityMemberService.check(childMember != null, "Нельзя выходить из ПО пока участник является пайщиком КУч ПО");
        }
    }

    /**
     * Создать заявление о вступлении пайщика в ПО
     * @param member
     * @param notifySignEvent - флаг нужно ли оповещать участника о подписании документа
     */
    private Document createDocumentForEntranceSharerToCooperative(CommunityMember member, boolean notifySignEvent) {
        String templateCode = POSharerCommunityMemberSettings.getInstance().getEntranceSharerToCommunityDocumentTemplateCode();
        String sharerParticipantName = POSharerCommunityMemberSettings.getInstance().getEntranceSharerToCommunitySharerParticipantName();
        String poParticipantName = POSharerCommunityMemberSettings.getInstance().getEntranceSharerToCommunityCommunityParticipantName();

        // Проверяем возможность вступления кандидата в ПО
        poAndKuchSharerCommunityService.checkEntranceSharerToCooperative(member.getCommunity());

        Map<String, String> parameters = new HashMap<>();
        parameters.put(ENTRANCE_SHARER_TO_COMMUNITY_PARAM_NAME, "true");
        parameters.put(ENTRANCE_SHARER_TO_COMMUNITY_MEMBER_ID_PARAM_NAME, String.valueOf(member.getId()));

        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);

        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        // Пайщик - физ лицо
        ParticipantCreateDocumentParameter participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(), member.getUser().getId(), sharerParticipantName);
        CreateDocumentParameter createDocumentParameter = new CreateDocumentParameter(participantCreateDocumentParameter, new ArrayList<>());
        createDocumentParameters.add(createDocumentParameter);

        // ПО
        participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), member.getCommunity().getId(), poParticipantName);
        createDocumentParameter = new CreateDocumentParameter(participantCreateDocumentParameter, new ArrayList<>());
        createDocumentParameters.add(createDocumentParameter);

        long documentOwnerId = member.getUser().getId(); // Костя сказал, что создатель документа - кандидат в пайщики

        Document document = documentService.createDocumentDomain(templateCode, createDocumentParameters, documentOwnerId, Collections.singletonList(stateEvent), notifySignEvent);

        // Сохраняем в настроках пользователя ИД документа
        userSettingsService.set(member.getUser(), POAndKuchSharerCommunityService.getEntranceDocumentIdSettingKey(member.getCommunity().getId()), String.valueOf(document.getId()));

        return document;
    }

    /**
     * Заявление на вступление в ПО подписано
     * @param event
     */
    private void onSignedDeclarationToEntranceCooperative(FlowOfDocumentStateEvent event) {
        Long memberId = VarUtils.getLong(event.getParameters().get(ENTRANCE_SHARER_TO_COMMUNITY_MEMBER_ID_PARAM_NAME), -1l);
        onSignedDeclarationToEntranceCooperative(memberId);
    }

    public CommunityMember onSignedDeclarationToEntranceCooperative(Long memberId) {
        CommunityMember member = communityMemberDomainService.getByIdFullData(memberId);
        SharerCommunityMemberService.check(memberId == -1l || member == null, "Не найден участник вступающий в ПО");
        SharerCommunityMemberService.check(!CommunityMemberStatus.CONDITION_NOT_DONE_REQUEST.equals(member.getStatus()), "Не правильный статус у участника, вступающего в ПО");

        // Создаём проводки с платежами
        poAndKuchSharerCommunityService.createJoinFees(member);

        // Выставляем статус - условие выполнено
        member.setStatus(CommunityMemberStatus.CONDITION_DONE_REQUEST);
        communityMemberDomainService.save(member);

        // Отправляем оповещение, о том, что условие вступления в сообщество выполнено
        blagosferaEventPublisher.publishEvent(new CommunityMemberEvent(this, CommunityEventType.CONDITION_DONE_REQUEST, member));

        return member;
    }

    /**
     * Создать протокол собрания о вступлении пайщиков в ПО
     * @param members
     * @param documentOwner
     * @param loa
     * @param notifySignEvent
     * @return
     */
    private Document createProtocolForJoinSharersToCooperative(List<CommunityMember> members, User documentOwner, boolean loa, boolean notifySignEvent) {
        String templateCode = POSharerCommunityMemberSettings.getInstance().getJoinSharerToCommunityDocumentTemplateCode();
        String loaTemplateCode = POSharerCommunityMemberSettings.getInstance().getJoinSharerToCommunityLoaDocumentTemplateCode();
        String sharersListParticipantName = POSharerCommunityMemberSettings.getInstance().getDocumentProtocolJoinSharersListParticipantName();
        String delegateParticipantName = POSharerCommunityMemberSettings.getInstance().getDocumentProtocolDelegateParticipantName();
        String poParticipantName = POSharerCommunityMemberSettings.getInstance().getDocumentProtocolJoinSharersListCooperativeParticipantName();
        String documentsUserFieldName = POSharerCommunityMemberSettings.getInstance().getSharersStatementDocumentListUserFieldName();
        String sharerParticipantName = POSharerCommunityMemberSettings.getInstance().getEntranceSharerToCommunitySharerParticipantName();

        List<Long> memberIds = new ArrayList<>();
        List<Long> sharerIds = new ArrayList<>();
        for (CommunityMember member : members) {
            memberIds.add(member.getId());
            sharerIds.add(member.getUser().getId());
        }

        // Создаём событие подписания документа
        Map<String, String> parameters = new HashMap<>();

        parameters.put(JOIN_SHARER_TO_COMMUNITY_PARAM_NAME, "true");
        parameters.put(JOIN_SHARER_TO_COMMUNITY_MEMBER_IDS_PARAM_NAME, StringUtils.join(memberIds, ","));

        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);

        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        // Пайщики - физ лица
        ParticipantCreateDocumentParameter participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL_LIST.getName(), sharerIds, sharersListParticipantName);
        CreateDocumentParameter createDocumentParameter = new CreateDocumentParameter(participantCreateDocumentParameter, new ArrayList<>());
        createDocumentParameters.add(createDocumentParameter);

        if (loa) {
            participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(), documentOwner.getId(), delegateParticipantName);
            createDocumentParameter = new CreateDocumentParameter(participantCreateDocumentParameter, new ArrayList<>());
            createDocumentParameters.add(createDocumentParameter);
        }

        if (members.size() == 1) {
            participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(), members.get(0).getUser().getId(), sharerParticipantName);
            createDocumentParameter = new CreateDocumentParameter(participantCreateDocumentParameter, new ArrayList<>());
            createDocumentParameters.add(createDocumentParameter);
        }

        //--------------------------------------------------------------------------
        // ПО
        //--------------------------------------------------------------------------
        List<UserFieldValue> userFieldValues = new ArrayList<>();

        // Нужно получить список заявлений от новых пайщиков
        //StringBuilder sharerDocumentsSb = new StringBuilder();
        Community community = null;
        List<String> sharerDocuments = new ArrayList<>();
        for (CommunityMember member : members) {
            if (community == null) {
                community = member.getCommunity();
            }
            User user = member.getUser();
            String documentIdStr = userSettingsService.get(user, POAndKuchSharerCommunityService.getEntranceDocumentIdSettingKey(member.getCommunity().getId()));
            Long documentId = VarUtils.getLong(documentIdStr, -1l);
            if (documentId > -1l) {
                Document document = documentDomainService.getById(documentId);
                sharerDocuments.add(document.getCode() + " от " + DateUtils.formatDate(document.getCreateDate(), DateUtils.Format.DATE));
            }
        }
        String sharersDocumentsStr = StringUtils.join(sharerDocuments, ",");

        userFieldValues.add(UserFieldValueBuilder.createStringValue(documentsUserFieldName, sharersDocumentsStr));

        participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getId(), poParticipantName);
        createDocumentParameter = new CreateDocumentParameter(participantCreateDocumentParameter, userFieldValues);
        createDocumentParameters.add(createDocumentParameter);
        //--------------------------------------------------------------------------

        return documentService.createDocumentDomain(loa ? loaTemplateCode : templateCode, createDocumentParameters, documentOwner.getId(), Collections.singletonList(stateEvent), notifySignEvent);
    }

    public List<CommunityMember> onSignedProtocolToJoinSharersToCooperative(List<Long> memberIds){
        List<CommunityMember> members = new ArrayList<>();

        // Получаем ИД пайщиков
        for (Long memberId : memberIds) {
            CommunityMember communityMember = communityMemberDomainService.getByIdFullData(memberId);
            SharerCommunityMemberService.check(memberId == -1l || communityMember == null, "Не правильный ИД кандидата в пайщики");
            SharerCommunityMemberService.check(!CommunityMemberStatus.JOIN_IN_PROCESS.equals(communityMember.getStatus()), "Не правильный статус у участника, вступающего в ПО");

            // Обновляем статус кандидата в пайщики
            communityMember.setStatus(CommunityMemberStatus.MEMBER);
            communityMemberDomainService.save(communityMember);
            members.add(communityMember);

            // Подтверждаем блокированные транзакции
            poAndKuchSharerCommunityService.finishJoinSharerToCommunityTransactions(communityMember);
        }

        // Отправляем событие вступления участников в объединение
        for (CommunityMember communityMember : members) {
            blagosferaEventPublisher.publishEvent(new CommunityMemberEvent(this, CommunityEventType.ACCEPT_REQUEST_TO_COOPERATIVE, communityMember));
        }
        return members;
    }

    /**
     * Обработка действий после подписания протокола о принятии в пайщики ПО
     * @param event
     */
    private void onSignedProtocolToJoinSharersToCooperative(FlowOfDocumentStateEvent event){
        String memberIdsStr = event.getParameters().get(JOIN_SHARER_TO_COMMUNITY_MEMBER_IDS_PARAM_NAME);
        String[] memberIds = memberIdsStr.split(",");

        List<Long> ids = new ArrayList<>();
        for (String memberIdStr : memberIds) {
            Long id = VarUtils.getLong(memberIdStr, null);
            if (id != null) {
                ids.add(id);
            }
        }
        onSignedProtocolToJoinSharersToCooperative(ids);
    }

    /**
     * Создать заявление о выходе пайщика из ПО
     * @param member
     */
    private Document createDocumentForLeaveSharerFromCooperative(CommunityMember member) {
        String templateCode = POSharerCommunityMemberSettings.getInstance().getRequestLeaveSharerFromCommunityDocumentTemplateCode();
        String sharerParticipantName = POSharerCommunityMemberSettings.getInstance().getLeaveStatementDocumentSharerParticipantName();
        String poParticipantName = POSharerCommunityMemberSettings.getInstance().getLeaveStatementDocumentCommunityParticipantName();

        // Проверяем возможность выхода пайщика из ПО
        // TODO отличие от КУч
        checkLeaveSharerFromCooperative(member);

        Map<String, String> parameters = new HashMap<>();

        parameters.put(REQUEST_LEAVE_SHARER_FROM_COMMUNITY_PARAM_NAME, "true");
        parameters.put(REQUEST_LEAVE_SHARER_FROM_COMMUNITY_MEMBER_ID_PARAM_NAME, String.valueOf(member.getId()));

        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);

        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        // Пайщик - физ лицо
        ParticipantCreateDocumentParameter participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(), member.getUser().getId(), sharerParticipantName);
        CreateDocumentParameter createDocumentParameter = new CreateDocumentParameter(participantCreateDocumentParameter, new ArrayList<>());
        createDocumentParameters.add(createDocumentParameter);

        // ПО
        participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), member.getCommunity().getId(), poParticipantName);
        createDocumentParameter = new CreateDocumentParameter(participantCreateDocumentParameter, new ArrayList<>());
        createDocumentParameters.add(createDocumentParameter);

        long documentOwnerId = member.getUser().getId(); // Костя сказал, что создатель документа - кандидат в пайщики
        Document document = documentService.createDocumentDomain(templateCode, createDocumentParameters, documentOwnerId, Collections.singletonList(stateEvent));

        // Сохраняем в настроках пользователя ИД документа
        userSettingsService.set(member.getUser(), POAndKuchSharerCommunityService.getLeaveDocumentIdSettingsKey(member.getCommunity().getId()), String.valueOf(document.getId()));

        return document;
    }

    /**
     * Заявление на выход из ПО подписано пайщиком
     * @param event
     */
    private void onSignedDeclarationToLeaveCooperative(FlowOfDocumentStateEvent event) {
        Long memberId = VarUtils.getLong(event.getParameters().get(REQUEST_LEAVE_SHARER_FROM_COMMUNITY_MEMBER_ID_PARAM_NAME), null);
        CommunityMember member = communityMemberDomainService.getByIdFullData(memberId);
        onSignedDeclarationToLeaveCooperative(member);
    }

    public void onSignedDeclarationToLeaveCooperative(CommunityMember member) {
        SharerCommunityMemberService.check(member == null || member.getId() == null, "Не найден участник выходящий из ПО");
        SharerCommunityMemberService.check(!CommunityMemberStatus.MEMBER.equals(member.getStatus()), "Не правильный статус у участника, выходящего из ПО");

        // Проверяем возможность выхода пайщика из ПО
        checkLeaveSharerFromCooperative(member);

        // Создаём транзакции платежей для выхода из ПО
        poAndKuchSharerCommunityService.createLeaveFees(member);

        // Выставляем статус - условие выполнено
        member.setStatus(CommunityMemberStatus.REQUEST_TO_LEAVE);
        communityMemberDomainService.save(member);

        // Отправляем оповещение, о том, что участник валит прихватив трактор из ПО
        blagosferaEventPublisher.publishEvent(new CommunityMemberEvent(this, CommunityEventType.REQUEST_TO_LEAVE, member));
    }

    /**
     * Создать протокол собрания о выходе пайщиков из ПО
     * @param members
     * @param documentOwner
     * @return
     */
    private Document createProtocolForLeaveSharersFromCooperative(List<CommunityMember> members, User documentOwner, boolean hasLOA, boolean notifySignEvent) {
        String templateCode = POSharerCommunityMemberSettings.getInstance().getLeaveSharersFromCommunityDocumentTemplateCode();
        String sharersParticipantName = POSharerCommunityMemberSettings.getInstance().getDocumentProtocolLeaveSharersListParticipantName();
        String poParticipantName = POSharerCommunityMemberSettings.getInstance().getDocumentProtocolLeaveSharersListCooperativeParticipantName();
        String documentsUserFieldName = POSharerCommunityMemberSettings.getInstance().getSharersStatementToLeaveDocumentListUserFieldName();

        List<Long> membersIds = new ArrayList<>();
        List<Long> sharerIds = new ArrayList<>();
        Community community = null;
        for (CommunityMember member : members) {
            // Проверяем возможность выхода пайщика из ПО
            checkLeaveSharerFromCooperative(member);
            if (community == null) {
                community = member.getCommunity();
            }
            sharerIds.add(member.getUser().getId());
            membersIds.add(member.getId());
        }

        // Создаём событие подписания документа
        Map<String, String> parameters = new HashMap<>();

        parameters.put(LEAVE_SHARERS_FROM_COMMUNITY_PARAM_NAME, "true");
        parameters.put(LEAVE_SHARERS_FROM_COMMUNITY_MEMBER_IDS_PARAM_NAME, StringUtils.join(membersIds, ","));

        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);

        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        // Пайщики - физ лица
        ParticipantCreateDocumentParameter participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL_LIST.getName(), sharerIds, sharersParticipantName);
        CreateDocumentParameter createDocumentParameter = new CreateDocumentParameter(participantCreateDocumentParameter, new ArrayList<>());
        createDocumentParameters.add(createDocumentParameter);

        //--------------------------------------------------------------------------
        // ПО
        //--------------------------------------------------------------------------
        List<UserFieldValue> userFieldValues = new ArrayList<>();

        // Нужно получить список заявлений пайщиков ожидающих выход из ПО
        List<String> sharerDocuments = new ArrayList<>();
        for (CommunityMember member : members) {
            String documentIdStr = userSettingsService.get(member.getUser(), POAndKuchSharerCommunityService.getLeaveDocumentIdSettingsKey(community.getId()));
            Long documentId = VarUtils.getLong(documentIdStr, -1l);
            if (documentId > -1l) {
                Document document = documentDomainService.getById(documentId);
                sharerDocuments.add(document.getCode() + " от " + DateUtils.formatDate(document.getCreateDate(), DateUtils.Format.DATE));
            }
        }
        String sharersDocumentsStr = StringUtils.join(sharerDocuments, ",");

        userFieldValues.add(UserFieldValueBuilder.createStringValue(documentsUserFieldName, sharersDocumentsStr));

        participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getId(), poParticipantName);
        createDocumentParameter = new CreateDocumentParameter(participantCreateDocumentParameter, userFieldValues);
        createDocumentParameters.add(createDocumentParameter);
        //--------------------------------------------------------------------------

        return documentService.createDocumentDomain(templateCode, createDocumentParameters, documentOwner.getId(), Collections.singletonList(stateEvent), notifySignEvent);
    }

    /**
     * Обработка действий после подписания протокола о выходе пайщиков из ПО
     * @param event
     */
    private void onSignedProtocolForLeaveSharersFromCooperative(FlowOfDocumentStateEvent event) {
        // ИД участников выходящих из ПО
        String memberIdsStr = event.getParameters().get(LEAVE_SHARERS_FROM_COMMUNITY_MEMBER_IDS_PARAM_NAME);
        String[] memberIds = memberIdsStr.split(",");

        List<Long> members = new ArrayList<>();
        for (String memberIdStr : memberIds) {
            Long memberId = VarUtils.getLong(memberIdStr, -1l);
            members.add(memberId);
        }
        onSignedProtocolForLeaveSharersFromCooperative(members);
    }

    public List<Long> onSignedProtocolForLeaveSharersFromCooperative(List<Long> memberIds){
        List<CommunityMember> members = new ArrayList<>();
        List<Long> result = new ArrayList<>();
        // Получаем ИД пайщиков
        for (Long memberId : memberIds) {
            CommunityMember communityMember = communityMemberDomainService.getByIdFullData(memberId);
            result.add(communityMember.getUser().getId());
            // Проверяем возможность выхода пайщика из ПО
            checkLeaveSharerFromCooperative(communityMember);

            SharerCommunityMemberService.check(memberId == -1l || communityMember == null, "Не правильный ИД кандидата на выход из ПО");
            SharerCommunityMemberService.check(!CommunityMemberStatus.LEAVE_IN_PROCESS.equals(communityMember.getStatus()), "Не правильный статус у участника, выходящего из ПО");

            // Списываем с баланса объединения блокированные средства бывшему пайщику
            poAndKuchSharerCommunityService.finishLeaveSharerFromCommunityTransactions(communityMember);

            // Удаляем участника из объединения
            communityMemberDomainService.delete(communityMember.getId());
            communityMember.setStatus(null);
            members.add(communityMember);
        }

        // Отправляем событие выхода участников из объединения
        for (CommunityMember communityMember : members) {
            blagosferaEventPublisher.publishEvent(new CommunityMemberEvent(this, CommunityEventType.LEAVE, communityMember));
        }
        return result;
    }
}

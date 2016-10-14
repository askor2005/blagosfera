package ru.radom.kabinet.services.communities.sharermember.behavior.kuch;

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
import ru.radom.kabinet.document.dao.FlowOfDocumentDao;
import ru.radom.kabinet.document.generator.CreateDocumentParameter;
import ru.radom.kabinet.document.generator.ParticipantCreateDocumentParameter;
import ru.radom.kabinet.document.generator.UserFieldValue;
import ru.radom.kabinet.document.generator.UserFieldValueBuilder;
import ru.radom.kabinet.document.model.DocumentEntity;
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
public class KuchSharerCommunityMemberBehavior implements ISharerCommunityMemberBehavior {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentDomainService documentDomainService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    /*@Autowired
    private SharerSettingDao sharerSettingDao;*/

    @Autowired
    private UserSettingsService userSettingsService;

    @Autowired
    private FlowOfDocumentDao flowOfDocumentDao;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private LetterOfAuthorityService letterOfAuthorityService;

    @Autowired
    private POAndKuchSharerCommunityService poAndKuchSharerCommunityService;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    @Autowired
    private CommunityDataService communityDataService;

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
    @Transactional
    @Override
    public CommunityMemberResponseDto acceptInvite(CommunityMember member, boolean notifySignEvent) {

        member.setStatus(CommunityMemberStatus.CONDITION_NOT_DONE_REQUEST);
        communityMemberDomainService.save(member);

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
        parameters.put("responseType", "kuchPoName");

        CommunityMemberEvent event = new CommunityMemberEvent(this, CommunityEventType.CONDITION_NOT_DONE_REQUEST, member);

        return new CommunityMemberResponseDto(flowOfDocument, member, parameters, event);
    }

    /**
     * Создать запрос на вступление в КУч ПО
     * @param community
     * @param requester
     * @param notifySignEvent
     * @return
     */
    @Transactional
    @Override
    public CommunityMemberResponseDto request(Community community, User requester, boolean notifySignEvent) {
        CommunityMember member = new CommunityMember();
        member.setUser(requester);
        member.setCommunity(community);
        member.setStatus(CommunityMemberStatus.CONDITION_NOT_DONE_REQUEST);
        member.setRequestDate(new Date());

        communityMemberDomainService.save(member);

        // Все остальные действия такие же что и при принятии приглашения
        return acceptInvite(member, notifySignEvent);
    }

    /**
     * Принятие запросов от кандидатов в пайщики на вступление в КУч ПО
     * @param members
     * @param accepter
     * @param notifySignEvent
     * @return
     */
    @Transactional
    @Override
    public CommunityMemberResponseDto acceptRequests(List<CommunityMember> members, User accepter, boolean notifySignEvent) {
        Community community = members.get(0).getCommunity();
        // Ищем доверенность на принятие пайщиков в КУч ПО
        // TODO Правильная ли доверенность?
        boolean hasLOA = letterOfAuthorityService.checkLetterOfAuthority(LetterOfAuthorityConstants.ROLE_KEY_CASHBOX_ACCEPT_SHARERS, accepter, community);
        if (!hasLOA) {
            checkPermission(community, accepter, SharerCommunityMemberService.ROLE_APPROVE_SHARERS_PERMISSION, "У Вас нет прав напринятие пайщиков в КУч ПО");
        }
        for (CommunityMember member : members) {
            SharerCommunityMemberService.check(!CommunityMemberStatus.CONDITION_DONE_REQUEST.equals(member.getStatus()), "Неверный статус запроса");
            member.setStatus(CommunityMemberStatus.JOIN_IN_PROCESS);
            communityMemberDomainService.save(member);
        }

        // Создаём протокол собрания о принятии пайщиков в КУч ПО
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
    @Transactional
    @Override
    public CommunityMemberResponseDto rejectRequestsFromCommunityOwner(List<CommunityMember> members, User rejecter) {
        // Нужно вернуть заблокированные средства и удалить участников из объединения

        Community community = members.get(0).getCommunity();
        // Проверить, что у текущего пользователя есть права на исключение пайщиков
        checkPermission(community, rejecter, SharerCommunityMemberService.ROLE_APPROVE_SHARERS_PERMISSION, "У Вас нет прав на вывод пайщиков из КУч ПО");

        List<CommunityMemberEvent> events = new ArrayList<>();
        for (CommunityMember member : members) {
            SharerCommunityMemberService.check(!CommunityMemberStatus.CONDITION_DONE_REQUEST.equals(member.getStatus()), "Неверный статус запроса");

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
     * @param memberShare
     * @return
     */
    @Transactional
    @Override
    public CommunityMemberResponseDto cancelRequestFromMember(CommunityMember member, User memberShare) {
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
    @Transactional
    @Override
    public CommunityMemberResponseDto requestToExcludeFromCommunityOwner(CommunityMember member, User excluder) {
        // Проверить права на вывод пайщиков из КУч ПО
        checkPermission(member.getCommunity(), excluder, SharerCommunityMemberService.ROLE_APPROVE_SHARERS_PERMISSION, "У Вас нет прав на вывод пайщиков из КУч ПО");

        // Создать транзакции на вывод средств с паевой книжки
        poAndKuchSharerCommunityService.createLeaveFees(member);

        // Переводим физ лицо в статус ожидающего решения совета КУч ПО и далее решения с протоколом
        member.setStatus(CommunityMemberStatus.REQUEST_TO_LEAVE);
        communityMemberDomainService.save(member);

        return new CommunityMemberResponseDto(member);
    }

    @Transactional
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

        return new CommunityMemberResponseDto(member, parameters);
    }

    @Transactional
    @Override
    public void cancelRequestToLeaveFromMember(CommunityMember member) {
        // Возвращаем средства
        poAndKuchSharerCommunityService.cancelAllBlockedFeesInLeaveFromCommunity(member);
        // Устанавливаем статус обратно в MEMBER
        member.setStatus(CommunityMemberStatus.MEMBER);
        communityMemberDomainService.save(member);
    }

    @Transactional
    @Override
    public CommunityMemberResponseDto acceptRequestsToExcludeFromCommunity(List<CommunityMember> members, User excluder, boolean notifySignEvent) {
        // Ищем доверенность на принятие пайщиков в ПО
        /*boolean hasLOA = letterOfAuthorityService.checkLetterOfAuthority(LetterOfAuthorityConstants.ROLE_KEY_CASHBOX_ACCEPT_SHARERS, excluder, community);
        if (!hasLOA) {
            checkPermission(community, accepter, SharerCommunityMemberService.ROLE_APPROVE_SHARERS_PERMISSION, "У Вас нет прав напринятие пайщиков в КУч ПО");
        }*/

        Community community = members.get(0).getCommunity();
        checkPermission(community, excluder, SharerCommunityMemberService.ROLE_APPROVE_SHARERS_PERMISSION, "У Вас нет прав на вывод пайщиков из КУч ПО");

        for (CommunityMember member : members) {
            SharerCommunityMemberService.check(!CommunityMemberStatus.REQUEST_TO_LEAVE.equals(member.getStatus()), "Неверный статус запроса");
            member.setStatus(CommunityMemberStatus.LEAVE_IN_PROCESS);
            communityMemberDomainService.save(member);
        }

        boolean hasLOA = false; // TODO

        // Создаём протокол собрания о принятии пайщиков в КУч ПО
        Document document = createProtocolForLeaveSharersFromCooperative(members, excluder, hasLOA, notifySignEvent);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("documentLink", document.getLink());
        parameters.put("documentName", document.getName());
        parameters.put("responseType", "kuchPoName");
        return new CommunityMemberResponseDto(document, parameters);
    }

    @Override
    public LeaveCommunityMembersDto getLeaveCommunityMembers(Community community, User excluder) {
        checkPermission(community, excluder, SharerCommunityMemberService.ROLE_APPROVE_SHARERS_PERMISSION, "У Вас нет прав на получение списка участников, которые выполнили услове на выход из КУч ПО");
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
        List<Document> documents = documentDomainService.findByParameterAndParticipant(KUCH_LEAVE_SHARERS_FROM_COMMUNITY_PARAM_NAME, ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getId());
        return new LeaveCommunityMembersDto(members, mapSharerIdToDocumentLink, documents);
    }

    @Override
    public ApproveCommunityMembersDto getApproveCommunityMembers(Community community, User approver) {
        checkPermission(community, approver, SharerCommunityMemberService.ROLE_APPROVE_SHARERS_PERMISSION, "У Вас нет прав на получение списка участников, которые выполнили услове входа в КУч ПО");
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
        List<Document> documents = documentDomainService.findByParameterAndParticipant(KUCH_JOIN_SHARER_TO_COMMUNITY_PARAM_NAME, ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getId());
        return new ApproveCommunityMembersDto(members, mapSharerIdToDocumentLink, documents);
    }

    //------------------------------------------------------------------------------------------
    // Параметры документов вход пайщиков в ПО
    //------------------------------------------------------------------------------------------

    // Параметр заявления пайщика о вступлении в ПО - тип документа "вступление пайщика в КУч ПО"
    private static final String KUCH_ENTRANCE_SHARER_TO_COMMUNITY_PARAM_NAME = "KUCH_ENTRANCE_SHARER_TO_COMMUNITY_PARAM_NAME";

    // Параметр заявления пайщика о вступлении в ПО - ИД участника объединения, вступающего в КУч ПО
    private static final String KUCH_ENTRANCE_SHARER_TO_COMMUNITY_MEMBER_ID_PARAM_NAME = "KUCH_ENTRANCE_SHARER_TO_COMMUNITY_MEMBER_ID_PARAM_NAME";

    // Параметр протокола собрания совета ПО - тип документа "вступление пайщика в КУч ПО"
    private static final String KUCH_JOIN_SHARER_TO_COMMUNITY_PARAM_NAME = "KUCH_JOIN_SHARER_TO_COMMUNITY_PARAM_NAME";

    // Параметр протокола собрания совета ПО - ИДы участников, вступающих в КУч ПО
    private static final String KUCH_JOIN_SHARER_TO_COMMUNITY_MEMBER_IDS_PARAM_NAME = "KUCH_JOIN_SHARER_TO_COMMUNITY_MEMBER_IDS_PARAM_NAME";

    //------------------------------------------------------------------------------------------


    //------------------------------------------------------------------------------------------
    // Параметры документов на выход из пайщиков
    //------------------------------------------------------------------------------------------

    // Параметр заявления пайщика о выходе из КУч ПО - тип документа "выход пайщика из КУч ПО"
    private static final String KUCH_REQUEST_LEAVE_SHARER_FROM_COMMUNITY_PARAM_NAME = "KUCH_REQUEST_LEAVE_SHARER_FROM_COMMUNITY_PARAM_NAME";

    // Параметр заявления пайщика о выходе из КУч ПО - ИД пайщика, выходящего из КУч ПО
    private static final String KUCH_REQUEST_LEAVE_SHARER_FROM_COMMUNITY_MEMBER_ID_PARAM_NAME = "KUCH_REQUEST_LEAVE_SHARER_FROM_COMMUNITY_MEMBER_ID_PARAM_NAME";

    // Параметр протокола собрания совета КУч ПО - тип документа "выход пайщиков из КУч ПО"
    private static final String KUCH_LEAVE_SHARERS_FROM_COMMUNITY_PARAM_NAME = "KUCH_LEAVE_SHARERS_FROM_COMMUNITY_PARAM_NAME";

    // Параметр протокола собрания совета КУч ПО - ИДы участников, выходящих из КУч ПО
    private static final String KUCH_LEAVE_SHARERS_FROM_COMMUNITY_MEMBER_IDS_PARAM_NAME = "KUCH_LEAVE_SHARERS_FROM_COMMUNITY_MEMBER_IDS_PARAM_NAME";

    //------------------------------------------------------------------------------------------

    @EventListener
    public void onFlowOfDocumentStateEvent(FlowOfDocumentStateEvent event) {
        switch (event.getStateEventType()) {
            case DOCUMENT_SIGNED:
                // Заявление о принятии пайщика в КУч ПО подписано пайщиком
                // Ищем параметры, которые были заложены в документ при его создании
                if (event.getParameters().containsKey(KUCH_ENTRANCE_SHARER_TO_COMMUNITY_PARAM_NAME)) {
                    onSignedDeclarationToEntranceCooperative(event); // Действия после подписания заявления на принятие кандидата в пайщики
                } else if (event.getParameters().containsKey(KUCH_JOIN_SHARER_TO_COMMUNITY_PARAM_NAME)) { // Протокол о принятии пайщиков подписан
                    onSignedProtocolToJoinSharersToCooperative(event);
                } else if (event.getParameters().containsKey(KUCH_REQUEST_LEAVE_SHARER_FROM_COMMUNITY_PARAM_NAME)) { // Заявление на выход из КУч ПО подписано пайщиком
                    onSignedDeclarationToLeaveCooperative(event);
                } else if (event.getParameters().containsKey(KUCH_LEAVE_SHARERS_FROM_COMMUNITY_PARAM_NAME)) { // Протокол о выходе пайщиков подписан
                    onSignedProtocolForLeaveSharersFromCooperative(event);
                }
                break;
        }
    }

    /**
     * Проверка вступления кандидата в КУч ПО
     * @param community
     * @param user
     */
    private void checkEntranceSharerToCooperative(Community community, User user) {
        // Общая проверка
        poAndKuchSharerCommunityService.checkEntranceSharerToCooperative(community);
        // Проверяем, что КУч является дочерней группой к ПО
        SharerCommunityMemberService.check(community.getParent() == null, "КУч должен быть дочерней группой к ПО");
        // Проверяем, что участник состоит в родительском объединении - в ПО
        CommunityMember parentMember = communityMemberDomainService.getByCommunityIdAndUserId(community.getParent().getId(), user.getId());
        SharerCommunityMemberService.check(parentMember == null, "Участник должен состоять в родительском ПО");
    }

    /**
     * Проверка выхода участника из КУч ПО
     * @param member
     */
    private void checkLeaveSharerFromCooperative(CommunityMember member) {
        // ?
    }

    /**
     * Создать заявление о вступлении пайщика в КУч ПО
     * @param member
     * @param notifySignEvent - флаг нужно ли оповещать участника о подписании документа
     */
    @Transactional
    private Document createDocumentForEntranceSharerToCooperative(CommunityMember member, boolean notifySignEvent) {
        String templateCode = KuchSharerCommunityMemberSettings.getInstance().getEntranceSharerToCommunityDocumentTemplateCode();
        String sharerParticipantName = KuchSharerCommunityMemberSettings.getInstance().getEntranceSharerToCommunitySharerParticipantName();
        String kuchParticipantName = KuchSharerCommunityMemberSettings.getInstance().getEntranceSharerToCommunityCommunityParticipantName();
        String poParticipantName = KuchSharerCommunityMemberSettings.getInstance().getEntranceSharerToCommunityParentCommunityParticipantName();

        // Проверяем возможность вступления кандидата в КУч ПО
        checkEntranceSharerToCooperative(member.getCommunity(), member.getUser());

        Map<String, String> parameters = new HashMap<>();
        parameters.put(KUCH_ENTRANCE_SHARER_TO_COMMUNITY_PARAM_NAME, "true");
        parameters.put(KUCH_ENTRANCE_SHARER_TO_COMMUNITY_MEMBER_ID_PARAM_NAME, String.valueOf(member.getId()));

        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);

        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        // Пайщик - физ лицо
        ParticipantCreateDocumentParameter participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(), member.getUser().getId(), sharerParticipantName);
        CreateDocumentParameter createDocumentParameter = new CreateDocumentParameter(participantCreateDocumentParameter, new ArrayList<>());
        createDocumentParameters.add(createDocumentParameter);

        // КУч ПО
        participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), member.getCommunity().getId(), kuchParticipantName);
        createDocumentParameter = new CreateDocumentParameter(participantCreateDocumentParameter, new ArrayList<>());
        createDocumentParameters.add(createDocumentParameter);

        // ПО
        participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), member.getCommunity().getParent().getId(), poParticipantName);
        createDocumentParameter = new CreateDocumentParameter(participantCreateDocumentParameter, new ArrayList<>());
        createDocumentParameters.add(createDocumentParameter);

        long documentOwnerId = member.getUser().getId(); // Костя сказал, что создатель документа - кандидат в пайщики

        Document document = documentService.createDocumentDomain(templateCode, createDocumentParameters, documentOwnerId, Collections.singletonList(stateEvent), notifySignEvent);

        // Сохраняем в настроках пользователя ИД документа
        userSettingsService.set(member.getUser(), POAndKuchSharerCommunityService.getEntranceDocumentIdSettingKey(member.getCommunity().getId()), String.valueOf(document.getId()));

        return document;
    }

    /**
     * Заявление на вступление в КУч ПО подписано
     * @param event
     */
    @Transactional
    private void onSignedDeclarationToEntranceCooperative(FlowOfDocumentStateEvent event) {
        Long memberId = VarUtils.getLong(event.getParameters().get(KUCH_ENTRANCE_SHARER_TO_COMMUNITY_MEMBER_ID_PARAM_NAME), -1l);
        CommunityMember member = communityMemberDomainService.getByIdFullData(memberId);
        SharerCommunityMemberService.check(memberId == -1l || member == null, "Не найден участник вступающий в КУч ПО");
        SharerCommunityMemberService.check(!CommunityMemberStatus.CONDITION_NOT_DONE_REQUEST.equals(member.getStatus()), "Не правильный статус у участника, вступающего в КУч ПО");

        // Создаём проводки с платежами
        poAndKuchSharerCommunityService.createJoinFees(member);

        // Выставляем статус - условие выполнено
        member.setStatus(CommunityMemberStatus.CONDITION_DONE_REQUEST);
        communityMemberDomainService.save(member);

        // Отправляем оповещение, о том, что условие вступления в сообщество выполнено
        blagosferaEventPublisher.publishEvent(new CommunityMemberEvent(this, CommunityEventType.CONDITION_DONE_REQUEST, member));
    }

    /**
     * Создать протокол собрания о вступлении пайщиков в КУч ПО
     * @param members
     * @param documentOwner
     * @param loa
     * @param notifySignEvent
     * @return
     */
    @Transactional
    private Document createProtocolForJoinSharersToCooperative(List<CommunityMember> members, User documentOwner, boolean loa, boolean notifySignEvent) {
        String templateCode = KuchSharerCommunityMemberSettings.getInstance().getJoinSharerToCommunityDocumentTemplateCode();
        String loaTemplateCode = KuchSharerCommunityMemberSettings.getInstance().getJoinSharerToCommunityLoaDocumentTemplateCode();
        String sharersListParticipantName = KuchSharerCommunityMemberSettings.getInstance().getDocumentProtocolJoinSharersListParticipantName();
        String delegateParticipantName = KuchSharerCommunityMemberSettings.getInstance().getDocumentProtocolDelegateParticipantName();
        String kuchParticipantName = KuchSharerCommunityMemberSettings.getInstance().getDocumentProtocolJoinSharersListCooperativeParticipantName();
        String poParticipantName = KuchSharerCommunityMemberSettings.getInstance().getDocumentProtocolJoinSharersListParentCooperativeParticipantName();
        String documentsUserFieldName = KuchSharerCommunityMemberSettings.getInstance().getSharersStatementDocumentListUserFieldName();

        List<Long> memberIds = new ArrayList<>();
        List<Long> sharerIds = new ArrayList<>();
        for (CommunityMember member : members) {
            memberIds.add(member.getId());
            sharerIds.add(member.getUser().getId());
        }

        // Создаём событие подписания документа
        Map<String, String> parameters = new HashMap<>();

        parameters.put(KUCH_JOIN_SHARER_TO_COMMUNITY_PARAM_NAME, "true");
        parameters.put(KUCH_JOIN_SHARER_TO_COMMUNITY_MEMBER_IDS_PARAM_NAME, StringUtils.join(memberIds, ","));

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

        //--------------------------------------------------------------------------
        // КУч ПО
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

        participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getId(), kuchParticipantName);
        createDocumentParameter = new CreateDocumentParameter(participantCreateDocumentParameter, userFieldValues);
        createDocumentParameters.add(createDocumentParameter);
        //--------------------------------------------------------------------------

        // ПО
        participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getParent().getId(), poParticipantName);
        createDocumentParameter = new CreateDocumentParameter(participantCreateDocumentParameter, new ArrayList<>());
        createDocumentParameters.add(createDocumentParameter);

        return documentService.createDocumentDomain(loa ? loaTemplateCode : templateCode, createDocumentParameters, documentOwner.getId(), Collections.singletonList(stateEvent), notifySignEvent);
    }

    /**
     * Обработка действий после подписания протокола о принятии в пайщики КУч ПО
     * @param event
     */
    @Transactional
    private void onSignedProtocolToJoinSharersToCooperative(FlowOfDocumentStateEvent event){
        String memberIdsStr = event.getParameters().get(KUCH_JOIN_SHARER_TO_COMMUNITY_MEMBER_IDS_PARAM_NAME);
        String[] memberIds = memberIdsStr.split(",");

        List<CommunityMember> members = new ArrayList<>();

        // Получаем ИД пайщиков
        for (String memberIdStr : memberIds) {
            Long memberId = VarUtils.getLong(memberIdStr, -1l);
            CommunityMember communityMember = communityMemberDomainService.getByIdFullData(memberId);
            SharerCommunityMemberService.check(memberId == -1l || communityMember == null, "Не правильный ИД кандидата в пайщики");
            SharerCommunityMemberService.check(!CommunityMemberStatus.JOIN_IN_PROCESS.equals(communityMember.getStatus()), "Не правильный статус у участника, вступающего в КУч ПО");

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
    }

    /**
     * Создать заявление о выходе пайщика из КУч ПО
     * @param member
     */
    @Transactional
    private Document createDocumentForLeaveSharerFromCooperative(CommunityMember member) {
        String templateCode = KuchSharerCommunityMemberSettings.getInstance().getRequestLeaveSharerFromCommunityDocumentTemplateCode();
        String sharerParticipantName = KuchSharerCommunityMemberSettings.getInstance().getLeaveStatementDocumentSharerParticipantName();
        String kuchParticipantName = KuchSharerCommunityMemberSettings.getInstance().getLeaveStatementDocumentCommunityParticipantName();
        String poParticipantName = KuchSharerCommunityMemberSettings.getInstance().getLeaveStatementDocumentParentCommunityParticipantName();

        Community community = communityDataService.getByIdFullData(member.getCommunity().getId());

        // Проверяем возможность выхода пайщика из КУч ПО
        checkLeaveSharerFromCooperative(member);

        Map<String, String> parameters = new HashMap<>();

        parameters.put(KUCH_REQUEST_LEAVE_SHARER_FROM_COMMUNITY_PARAM_NAME, "true");
        parameters.put(KUCH_REQUEST_LEAVE_SHARER_FROM_COMMUNITY_MEMBER_ID_PARAM_NAME, String.valueOf(member.getId()));

        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);

        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        // Пайщик - физ лицо
        ParticipantCreateDocumentParameter participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(), member.getUser().getId(), sharerParticipantName);
        CreateDocumentParameter createDocumentParameter = new CreateDocumentParameter(participantCreateDocumentParameter, new ArrayList<>());
        createDocumentParameters.add(createDocumentParameter);

        // КУч ПО
        participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getId(), kuchParticipantName);
        createDocumentParameter = new CreateDocumentParameter(participantCreateDocumentParameter, new ArrayList<>());
        createDocumentParameters.add(createDocumentParameter);

        // ПО
        participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getParent().getId(), poParticipantName);
        createDocumentParameter = new CreateDocumentParameter(participantCreateDocumentParameter, new ArrayList<>());
        createDocumentParameters.add(createDocumentParameter);


        long documentOwnerId = member.getUser().getId(); // Костя сказал, что создатель документа - кандидат в пайщики
        Document document = documentService.createDocumentDomain(templateCode, createDocumentParameters, documentOwnerId, Collections.singletonList(stateEvent));

        // Сохраняем в настроках пользователя ИД документа
        userSettingsService.set(member.getUser(), POAndKuchSharerCommunityService.getLeaveDocumentIdSettingsKey(community.getId()), String.valueOf(document.getId()));

        return document;
    }

    /**
     * Заявление на выход из КУч ПО подписано пайщиком
     * @param event
     */
    @Transactional
    private void onSignedDeclarationToLeaveCooperative(FlowOfDocumentStateEvent event) {
        Long memberId = VarUtils.getLong(event.getParameters().get(KUCH_REQUEST_LEAVE_SHARER_FROM_COMMUNITY_MEMBER_ID_PARAM_NAME), -1l);
        CommunityMember member = communityMemberDomainService.getByIdFullData(memberId);
        SharerCommunityMemberService.check(memberId == -1l || member == null, "Не найден участник выходящий из ПО");
        SharerCommunityMemberService.check(!CommunityMemberStatus.MEMBER.equals(member.getStatus()), "Не правильный статус у участника, выходящего из КУч ПО");

        // Проверяем возможность выхода пайщика из КУч ПО
        checkLeaveSharerFromCooperative(member);

        // Создаём транзакции платежей для выхода из КУч ПО
        poAndKuchSharerCommunityService.createLeaveFees(member);

        // Выставляем статус - условие выполнено
        member.setStatus(CommunityMemberStatus.REQUEST_TO_LEAVE);
        communityMemberDomainService.save(member);

        // Отправляем оповещение, о том, что участник валит прихватив трактор из КУч ПО
        blagosferaEventPublisher.publishEvent(new CommunityMemberEvent(this, CommunityEventType.REQUEST_TO_LEAVE, member));
    }

    /**
     * Создать протокол собрания о выходе пайщиков из КУч ПО
     * @param members
     * @param documentOwner
     * @return
     */
    @Transactional
    private Document createProtocolForLeaveSharersFromCooperative(List<CommunityMember> members, User documentOwner, boolean hasLOA, boolean notifySignEvent) {
        String templateCode = KuchSharerCommunityMemberSettings.getInstance().getLeaveSharersFromCommunityDocumentTemplateCode();
        String sharersParticipantName = KuchSharerCommunityMemberSettings.getInstance().getDocumentProtocolLeaveSharersListParticipantName();
        String kuchParticipantName = KuchSharerCommunityMemberSettings.getInstance().getDocumentProtocolLeaveSharersListCooperativeParticipantName();
        String poParticipantName = KuchSharerCommunityMemberSettings.getInstance().getDocumentProtocolLeaveSharersListParentCooperativeParticipantName();
        String documentsUserFieldName = KuchSharerCommunityMemberSettings.getInstance().getSharersStatementToLeaveDocumentListUserFieldName();



        List<Long> membersIds = new ArrayList<>();
        List<Long> sharerIds = new ArrayList<>();
        Community community = null;
        for (CommunityMember member : members) {
            // Проверяем возможность выхода пайщика из ПО
            checkLeaveSharerFromCooperative(member);
            if (community == null) {
                community = communityDataService.getByIdFullData(member.getCommunity().getId());
            }
            sharerIds.add(member.getUser().getId());
            membersIds.add(member.getId());
        }

        // Создаём событие подписания документа
        Map<String, String> parameters = new HashMap<>();

        parameters.put(KUCH_LEAVE_SHARERS_FROM_COMMUNITY_PARAM_NAME, "true");
        parameters.put(KUCH_LEAVE_SHARERS_FROM_COMMUNITY_MEMBER_IDS_PARAM_NAME, StringUtils.join(membersIds, ","));

        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);

        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        // Пайщики - физ лица
        ParticipantCreateDocumentParameter participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL_LIST.getName(), sharerIds, sharersParticipantName);
        CreateDocumentParameter createDocumentParameter = new CreateDocumentParameter(participantCreateDocumentParameter, new ArrayList<>());
        createDocumentParameters.add(createDocumentParameter);

        //--------------------------------------------------------------------------
        // КУч ПО
        //--------------------------------------------------------------------------
        List<UserFieldValue> userFieldValues = new ArrayList<>();

        // Нужно получить список заявлений пайщиков ожидающих выход из ПО
        List<String> sharerDocuments = new ArrayList<>();
        for (CommunityMember member : members) {
            String documentIdStr = userSettingsService.get(member.getUser(), POAndKuchSharerCommunityService.getLeaveDocumentIdSettingsKey(community.getId()));
            Long documentId = VarUtils.getLong(documentIdStr, -1l);
            if (documentId > -1l) {
                DocumentEntity document = flowOfDocumentDao.getById(documentId);
                sharerDocuments.add(document.getCode() + " от " + DateUtils.formatDate(document.getCreateDate(), DateUtils.Format.DATE));
            }
        }
        String sharersDocumentsStr = StringUtils.join(sharerDocuments, ",");

        userFieldValues.add(UserFieldValueBuilder.createStringValue(documentsUserFieldName, sharersDocumentsStr));

        participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getId(), kuchParticipantName);
        createDocumentParameter = new CreateDocumentParameter(participantCreateDocumentParameter, userFieldValues);
        createDocumentParameters.add(createDocumentParameter);
        //--------------------------------------------------------------------------

        // ПО
        participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getParent().getId(), poParticipantName);
        createDocumentParameter = new CreateDocumentParameter(participantCreateDocumentParameter, new ArrayList<>());
        createDocumentParameters.add(createDocumentParameter);

        return documentService.createDocumentDomain(templateCode, createDocumentParameters, documentOwner.getId(), Collections.singletonList(stateEvent), notifySignEvent);
    }

    /**
     * Обработка действий после подписания протокола о выходе пайщиков из КУч ПО
     * @param event
     */
    @Transactional
    private void onSignedProtocolForLeaveSharersFromCooperative(FlowOfDocumentStateEvent event){
        // ИД участников выходящих из КУч ПО
        String memberIdsStr = event.getParameters().get(KUCH_LEAVE_SHARERS_FROM_COMMUNITY_MEMBER_IDS_PARAM_NAME);
        String[] memberIds = memberIdsStr.split(",");

        List<CommunityMember> members = new ArrayList<>();

        // Получаем ИД пайщиков
        for (String memberIdStr : memberIds) {
            Long memberId = VarUtils.getLong(memberIdStr, -1l);
            CommunityMember communityMember = communityMemberDomainService.getByIdFullData(memberId);
            // Проверяем возможность выхода пайщика из КУч ПО
            checkLeaveSharerFromCooperative(communityMember);

            SharerCommunityMemberService.check(memberId == -1l || communityMember == null, "Не правильный ИД кандидата на выход из КУч ПО");
            SharerCommunityMemberService.check(!CommunityMemberStatus.LEAVE_IN_PROCESS.equals(communityMember.getStatus()), "Не правильный статус у участника, выходящего из КУч ПО");

            // Списываем с баланса объединения блокированные средства бывшему пайщику
            poAndKuchSharerCommunityService.finishLeaveSharerFromCommunityTransactions(communityMember);

            // Удаляем участника из объединения
            communityMemberDomainService.delete(communityMember.getId());
            communityMember.setStatus(null);
        }

        // Отправляем событие выхода участников из объединения
        for (CommunityMember communityMember : members) {
            blagosferaEventPublisher.publishEvent(new CommunityMemberEvent(this, CommunityEventType.LEAVE, communityMember));
        }
    }
}

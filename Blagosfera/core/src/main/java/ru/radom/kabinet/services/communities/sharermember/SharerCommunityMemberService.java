package ru.radom.kabinet.services.communities.sharermember;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateSetting;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.bpm.BpmRaiseSignalEvent;
import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.askor.blagosfera.domain.events.bpm.BpmRaiseSignalsEvent;
import ru.askor.blagosfera.domain.events.community.CommunityMemberEvent;
import ru.askor.blagosfera.domain.events.community.PublishCommunityMemberEventsCallback;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.SharerService;
import ru.radom.kabinet.module.rameralisteditor.service.ListEditorItemDomainService;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.communities.CommunityException;
import ru.radom.kabinet.services.communities.sharermember.behavior.SharerCommunityMemberBehaviorResolver;
import ru.radom.kabinet.services.communities.sharermember.dto.ApproveCommunityMembersDto;
import ru.radom.kabinet.services.communities.sharermember.dto.CommunityMemberResponseDto;
import ru.radom.kabinet.services.communities.sharermember.dto.LeaveCommunityMembersDto;
import ru.radom.kabinet.services.sharer.UserDataService;

import java.util.*;

/**
 * Сервис для управления участниками физ лицами объединения
 * В сервисе производятся общие проверки для участников объединения
 * Created by vgusev on 28.10.2015.
 */
@Service
@Transactional
public class SharerCommunityMemberService implements PublishCommunityMemberEventsCallback {

    public static final String INVITES_PERMISSION = "INVITES";

    public static final String REQUESTS_PERMISSION = "REQUESTS";

    public static final String EXCLUDE_PERMISSION = "EXCLUDE";

    public static final String ROLE_APPROVE_SHARERS_PERMISSION = "ROLE_APPROVE_SHARERS";

    @Autowired
    private SharerCommunityMemberBehaviorResolver sharerCommunityMemberBehaviorResolver;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SharerService sharerService;

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private CommunityDataService communityDataService;

    @Autowired
    private UserDataService userDataService;
    
    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    @Autowired
    private ListEditorItemDomainService listEditorItemDomainService;

    public static void check(boolean condition, String message) {
        if (condition) {
            throw new CommunityException(message);
        }
    }

    private void checkParentMember(Community community, User user) {
        if (!community.isRoot()) {
            check(!communityMemberDomainService.exists(community.getParent().getId(), user.getId(), CommunityMemberStatus.MEMBER), "Для выполнения данного действия участник должен состоять в родительском объединении");
        }
    }

    public void checkPermission(Community community, User user, String permission, String errorMessage) {
        if (!communitiesService.hasPermission(community.getId(), user.getId(), permission)) {
            throw new CommunityException(errorMessage);
        }
    }

    /**
     * Пригласить пользователя в сообщество
     * <ul>
     *     <li><b>sharer</b>, в котором лежит id, ikp или объект у которого есть поле id</li>
     *     <li><b>inviter</b>, в котором лежит id, ikp или объект у которого есть поле id</li>
     *     <li><b>community</b>, id, seolink или объект у которого есть поле id</li>
     * </ul>
     */

    // ?TODO Переделать на BPMHandler
    /*@RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "core.community.invite.sharer", durable = "true"),
        exchange = @Exchange(value = "task-exchange", durable = "true"),
        key = "core.community.invite.sharer"
    ))
    public void inviteMemberWorker(Message message) {
        BPMBlagosferaUtils.commonRabbitTaskExecutorWithConverter(rabbitTemplate, message, (Map<String, Object> data) -> {
            User user = sharerService.tryGetUser(data.get("sharer"));
            User inviter = sharerService.tryGetUser(data.get("inviter"));
            Community community = communitiesService.tryGetCommunity(data.get("community"));
            CommunityMemberResponseDto responseDto = inviteMember(community, user, inviter);
            return serializeService.toPrimitiveObject(responseDto);
        });
    }*/

    /**
     * Выгнать пользователя из сообщества
     * <ul>
     *     <li><b>memberId</b> id участника</li>
     *     <li><b>sharer</b>, в котором лежит id, ikp или объект у которого есть поле id</li>
     *     <li><b>excluder</b>, в котором лежит id, ikp или объект у которого есть поле id</li>
     *     <li><b>community</b>, id, seolink или объект у которого есть поле id</li>
     * </ul>
     */

    // ?TODO Переделать на BPMHandler
    /*@RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "core.community.exclude.sharer", durable = "true"),
        exchange = @Exchange(value = "task-exchange", durable = "true"),
        key = "core.community.exclude.sharer"
    ))
    public void excludeFromMemberWorker(Message message) {
        BPMBlagosferaUtils.commonRabbitTaskExecutorWithConverter(rabbitTemplate, message, (Map<String, Object> data) -> {
            Long memberId = MapUtils.getLong(data, "memberId");
            User user = null;
            if(memberId == null) {
                user = sharerService.tryGetUser(data.get("sharer"));
                Community community = communitiesService.tryGetCommunity(data.get("community"));
                CommunityMember member = communityMemberDomainService.getByCommunityIdAndUserId(community.getId(), user.getId());
                memberId = member.getId();
            }
            User excluder = sharerService.tryGetUser(data.get("excluder"));
            CommunityMemberResponseDto responseDto;
            if(excluder != null) {
                responseDto = requestToExcludeFromCommunityOwner(memberId, excluder);
            } else {
                if (user == null) {
                    user = sharerService.tryGetUser(data.get("sharer"));
                }
                if (user == null) {
                    user = communityMemberDomainService.getByIdFullData(memberId).getUser();
                }
                responseDto = requestToExcludeFromMember(memberId, user);
            }
            return serializeService.toPrimitiveObject(responseDto);
        });
    }*/

    private void sendSignalForMember(Community community, User user, User ownerOfCommunity, String requestEventType) {
        sendSignalForMember("joinToCommunityEvent", community, user, ownerOfCommunity, requestEventType);
    }

    private void sendSignalForMember(String signal, Community community, User user, User ownerOfCommunity, String requestEventType) {
        String associationFormCode = null;
        if (community.getAssociationForm() != null) {
            ListEditorItem listEditorItem = listEditorItemDomainService.getById(community.getAssociationForm().getId());
            associationFormCode = listEditorItem.getCode();
        }
        List<Long> documentTemplateSettingsIds = new ArrayList<>();
        if (community.isNeedCreateDocuments() && community.getDocumentTemplateSettings() != null && !community.getDocumentTemplateSettings().isEmpty()) {
            for (DocumentTemplateSetting documentTemplateSetting : community.getDocumentTemplateSettings()) {
                documentTemplateSettingsIds.add(documentTemplateSetting.getId());
            }
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("user", serializeService.toPrimitiveObject(user));
        payload.put("community", serializeService.toPrimitiveObject(community));
        payload.put("documentTemplateSettingsIds", documentTemplateSettingsIds);
        payload.put("communityOwner", serializeService.toPrimitiveObject(ownerOfCommunity));
        payload.put("associationFormCode", associationFormCode);
        payload.put("requestEventType", requestEventType);
        BpmRaiseSignalEvent bpmRaiseSignalEvent = new BpmRaiseSignalEvent(this, signal, payload);
        blagosferaEventPublisher.publishEvent(bpmRaiseSignalEvent);
    }

    /**
     * Пригласить участника в объединение
     * @param communityId
     * @param userId
     * @param inviter
     * @return
     */
    public CommunityMemberResponseDto inviteMember(Long communityId, Long userId, User inviter) {
        return inviteMember(communityDataService.getByIdFullData(communityId), userDataService.getByIdFullData(userId), inviter);
    }

    public CommunityMemberResponseDto inviteMember(Community community, User user, User inviter) {
        // Поведение отправки инвайта для всех видов объединений одинаковый
        checkParentMember(community, user);
        checkPermission(community, inviter, INVITES_PERMISSION, "У Вас нет прав на приглашение участников");

        sendSignalForMember(community, user, inviter, "inviteMemberEvent");

        /*check(communityMemberDomainService.exists(community.getId(), user.getId()), "Участник уже подал заявку / приглашен / состоит в объединении");
        CommunityMember member = new CommunityMember();
        member.setCommunity(community);
        member.setUser(user);
        member.setInviter(inviter);
        member.setStatus(CommunityMemberStatus.INVITE);
        member.setRequestDate(new Date());
        member = communityMemberDomainService.save(member);
        CommunityMemberEvent event = new CommunityMemberEvent(this, CommunityEventType.INVITE, member);
        CommunityMemberResponseDto responseDto = new CommunityMemberResponseDto(member, event);
        Map<String, Object> payload = new HashMap<>();
        payload.put("invite", serializeService.toPrimitiveObject(responseDto));

        BpmRaiseSignalsEvent bpmRaiseSignalsEvent = new BpmRaiseSignalsEvent(this);
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_invited_to_community", payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_invited_to_community_" + community.getId(), payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_" + user.getId() + "_invited_to_community_" + community.getId(), payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_" + user.getId() + "_invited_to_community", payload));

        blagosferaEventPublisher.publishEvent(bpmRaiseSignalsEvent);
        publishCommunityMemberEventsAfterTransactionCommit(responseDto.getEvents());
        return responseDto;*/

        return null;
    }

    /**
     * Принять инвайт на вступление в объединение
     * @param memberId
     * @param accepter участник которого приглашали
     * @param notifySignEvent
     * @return
     */
    //acceptInvite
    
    public CommunityMemberResponseDto acceptInvite(Long memberId, User accepter, boolean notifySignEvent) {
        CommunityMember member = communityMemberDomainService.getByIdFullData(memberId);
        check(member == null, "Приглашение не найдено");
        member.setCommunity(communityDataService.getByIdFullData(member.getCommunity().getId()));
        check(!member.getUser().getId().equals(accepter.getId()), "У Вас нет прав на управление этим приглашением");
        check(!CommunityMemberStatus.INVITE.equals(member.getStatus()), "Неверный статус приглашения");

        Map<String, Object> payload = new HashMap<>();
        BpmRaiseSignalEvent bpmRaiseSignalEvent = new BpmRaiseSignalEvent(this, "member_" + memberId + "_accept_invite", payload);
        blagosferaEventPublisher.publishEvent(bpmRaiseSignalEvent);

        /*CommunityMemberResponseDto responseDto = sharerCommunityMemberBehaviorResolver.getBehavior(member.getCommunity().getId()).acceptInvite(member, notifySignEvent);

        Map<String, Object> payload = new HashMap<>();
        payload.put("acceptInvite", serializeService.toPrimitiveObject(responseDto));

        BpmRaiseSignalsEvent bpmRaiseSignalsEvent = new BpmRaiseSignalsEvent(this);
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_accept_invite_to_community", payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_accept_invite_to_community_" + member.getCommunity().getId(), payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_" + accepter.getId() + "_accept_invite_to_community_" + member.getCommunity().getId(), payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_" + accepter.getId() + "_accept_invite_to_community", payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "member_" + memberId + "_accept_invite", payload));

        blagosferaEventPublisher.publishEvent(bpmRaiseSignalsEvent);
        //publishCommunityMemberEventsAfterTransactionCommit(responseDto.getEvents());
        return responseDto;*/

        return null;
    }

    /**
     * Отклонить инвайт на вступление в объединение
     * @param memberId
     * @param rejecter участник которого приглашали
     * @return
     */
    //rejectInvite
    
    public CommunityMemberResponseDto rejectInvite(Long memberId, User rejecter) {
        CommunityMember member = communityMemberDomainService.getByIdFullData(memberId);
        // Поведение отклонения инвайта для всех видов объединений одинаковый
        check(member == null, "Приглашение не найдено");
        check(!member.getUser().getId().equals(rejecter.getId()), "У Вас нет прав на управление этим приглашением");
        check(!CommunityMemberStatus.INVITE.equals(member.getStatus()), "Неверный статус приглашения");

        Map<String, Object> payload = new HashMap<>();
        BpmRaiseSignalEvent bpmRaiseSignalEvent = new BpmRaiseSignalEvent(this, "member_" + memberId + "_reject_invite", payload);
        blagosferaEventPublisher.publishEvent(bpmRaiseSignalEvent);

        return null;
        /*communityMemberDomainService.delete(member.getId());
        member.setStatus(null);
        CommunityMemberEvent event = new CommunityMemberEvent(this, CommunityEventType.REJECT_INVITE, member);
        CommunityMemberResponseDto responseDto = new CommunityMemberResponseDto(member, event);

        Map<String, Object> payload = new HashMap<>();
        payload.put("rejectInvite", serializeService.toPrimitiveObject(responseDto));

        BpmRaiseSignalsEvent bpmRaiseSignalsEvent = new BpmRaiseSignalsEvent(this);
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_reject_invite_to_community", payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_reject_invite_to_community_" + member.getCommunity().getId(), payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_" + rejecter.getId() + "_reject_invite_to_community_" + member.getCommunity().getId(), payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_" + rejecter.getId() + "_reject_invite_to_community", payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "member_" + memberId + "_reject_invite", payload));

        blagosferaEventPublisher.publishEvent(bpmRaiseSignalsEvent);
        publishCommunityMemberEventsAfterTransactionCommit(responseDto.getEvents());
        return responseDto;*/
    }

    /**
     * Отмена приглашения участника в объединение уполномоченным участником объединения
     * @param memberId
     * @param canceller
     * @return
     */
    //cancelInvite
    
    public CommunityMemberResponseDto cancelInvite(Long memberId, User canceller) {
        CommunityMember member = communityMemberDomainService.getByIdFullData(memberId);
        // Отмена приглашения для всех видов объединений одинаковый
        check(member == null, "Приглашение не найдено");
        check(!CommunityMemberStatus.INVITE.equals(member.getStatus()), "Приглешение уже принято");
        checkPermission(member.getCommunity(), canceller, SharerCommunityMemberService.INVITES_PERMISSION, "У Вас нет прав на управление этим приглашением");

        Map<String, Object> payload = new HashMap<>();
        BpmRaiseSignalEvent bpmRaiseSignalEvent = new BpmRaiseSignalEvent(this, "member_" + memberId + "_cancel_invite", payload);
        blagosferaEventPublisher.publishEvent(bpmRaiseSignalEvent);

        return null;

        /*communityMemberDomainService.delete(member.getId());
        member.setStatus(null);
        CommunityMemberEvent event = new CommunityMemberEvent(this, CommunityEventType.CANCEL_INVITE, member);
        CommunityMemberResponseDto responseDto = new CommunityMemberResponseDto(member, event);

        Map<String, Object> payload = new HashMap<>();
        payload.put("canceledInvite", serializeService.toPrimitiveObject(responseDto));

        BpmRaiseSignalsEvent bpmRaiseSignalsEvent = new BpmRaiseSignalsEvent(this);
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_canceled_invite_to_community", payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_canceled_invite_to_community_" + member.getCommunity().getId(), payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_" + member.getUser().getId() + "_canceled_invite_to_community_" + member.getCommunity().getId(), payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "sharer_" + member.getUser().getId() + "_canceled_invite_to_community", payload));
        bpmRaiseSignalsEvent.getEvents().add(new BpmRaiseSignalEvent(this, "member_" + memberId + "_canceled_invite", payload));

        blagosferaEventPublisher.publishEvent(bpmRaiseSignalsEvent);
        publishCommunityMemberEventsAfterTransactionCommit(responseDto.getEvents());
        return responseDto;*/
    }

    /**
     * Запрос участника вступить в объединение
     * @param communityId
     * @param requesterId
     * @return
     */
    // Теперь вступление в отрытое объединение происходит тоже через этот метод
    //request
    //join
    public CommunityMemberResponseDto request(Long communityId, Long requesterId, boolean notifySignEvent) {
        Community community = communityDataService.getByIdFullData(communityId);
        User requester = userDataService.getByIdMinData(requesterId);

        /*String associationFormCode = null;
        if (community.getAssociationForm() != null) {
            ListEditorItem listEditorItem = listEditorItemDomainService.getById(community.getAssociationForm().getId());
            associationFormCode = listEditorItem.getCode();
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("user", serializeService.toPrimitiveObject(requester));
        payload.put("community", serializeService.toPrimitiveObject(community));
        payload.put("communityOwner", serializeService.toPrimitiveObject(community.getCreator()));
        payload.put("associationFormCode", associationFormCode);
        payload.put("requestEventType", "requestToJoinEvent");
        BpmRaiseSignalEvent bpmRaiseSignalEvent = new BpmRaiseSignalEvent(this, "joinToCommunityEvent", payload);
        blagosferaEventPublisher.publishEvent(bpmRaiseSignalEvent);*/

        sendSignalForMember(community, requester, community.getCreator(), "requestToJoinEvent");

        //requestToJoinEvent

        /*check(!requester.isVerified() && ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.equals(community.getCommunityType()),
                "Вам необходимо пройти идентификацию прежде чем Вы сможете вступать в объединения");

        checkParentMember(community, requester);

        check(communityMemberDomainService.exists(community.getId(), requester.getId()),
                "Вы уже подали заявку / приглашены / состоите в объединении");

        CommunityMemberResponseDto responseDto = sharerCommunityMemberBehaviorResolver.getBehavior(community.getId())
                .request(community, requester, notifySignEvent);

        //publishCommunityMemberEventsAfterTransactionCommit(responseDto.getEvents());
        return responseDto;*/

        return null;
    }

    /**
     * Принять запросы на вступление в объединение
     * @param memberIds
     * @param accepterUserId
     * @param notifySignEvent
     * @return
     */
    //acceptRequest
    public CommunityMemberResponseDto acceptRequests(List<Long> memberIds, Long accepterUserId, boolean notifySignEvent) {
        List<CommunityMember> members = communityMemberDomainService.getByIds(memberIds, true, true, true, true);
        check(members == null || members.isEmpty(), "Не переданы участники");
        User accepter = userDataService.getByIdFullData(accepterUserId);

        checkPermission(members.get(0).getCommunity(), accepter, SharerCommunityMemberService.REQUESTS_PERMISSION, "У Вас нет прав на управление этим запросом");
        for (CommunityMember communityMember : members) {
            BpmRaiseSignalEvent bpmRaiseSignalEvent = new BpmRaiseSignalEvent(
                    this,
                    "request_" + communityMember.getId() + "_accepted",
                    Collections.emptyMap()
            );
            blagosferaEventPublisher.publishEvent(bpmRaiseSignalEvent);
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("memberIds", memberIds);
        parameters.put("accepter", accepter);
        BpmRaiseSignalEvent bpmRaiseSignalEvent = new BpmRaiseSignalEvent(
                this,
                "requests_join_community_accepted_" + members.get(0).getCommunity().getId(),
                parameters
        );
        blagosferaEventPublisher.publishEvent(bpmRaiseSignalEvent);


        /*Map<String, Object> payload = new HashMap<>();
        payload.put("user", serializeService.toPrimitiveObject(requester));
        payload.put("community", serializeService.toPrimitiveObject(community));
        payload.put("communityOwner", serializeService.toPrimitiveObject(community.getCreator()));
        BpmRaiseSignalEvent bpmRaiseSignalEvent = new BpmRaiseSignalEvent(this, "requestToJoinEvent", payload);
        blagosferaEventPublisher.publishEvent(bpmRaiseSignalEvent);*/


        return null;
        /*check(members == null || members.size() == 0, "Не переданы участники");
        Community community = members.get(0).getCommunity();
        CommunityMemberResponseDto responseDto = sharerCommunityMemberBehaviorResolver.getBehavior(community.getId()).acceptRequests(members, accepter, notifySignEvent);
        //publishCommunityMemberEventsAfterTransactionCommit(responseDto.getEvents());
        return responseDto;*/
    }

    /**
     * Отклонить запросы на вступление в объединение от уполномоченного в объединении
     * @param memberIds
     * @param rejecter
     * @return
     */
    //rejectRequest
    
    public CommunityMemberResponseDto rejectRequestsFromCommunityOwner(List<Long> memberIds, User rejecter) {
        List<CommunityMember> members = communityMemberDomainService.getByIds(memberIds, true, true, true, true);
        check(members == null || members.isEmpty(), "Не переданы участники");
        Community community = members.get(0).getCommunity();

        checkPermission(community, rejecter, SharerCommunityMemberService.REQUESTS_PERMISSION, "У Вас нет прав на управление этим запросом");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rejecter", serializeService.toPrimitiveObject(rejecter));
        for (CommunityMember communityMember : members) {
            BpmRaiseSignalEvent bpmRaiseSignalEvent = new BpmRaiseSignalEvent(
                    this,
                    "request_" + communityMember.getId() + "_rejected",
                    Collections.emptyMap()
            );
            blagosferaEventPublisher.publishEvent(bpmRaiseSignalEvent);
        }


        /*CommunityMemberResponseDto responseDto = sharerCommunityMemberBehaviorResolver.getBehavior(community.getId()).rejectRequestsFromCommunityOwner(members, rejecter);
        //publishCommunityMemberEventsAfterTransactionCommit(responseDto.getEvents());
        return responseDto;*/



        return null;
    }

    /**
     * Отмена запроса на вступление в объединение от участника
     * @param memberId
     * @param memberUser участник который создавал запрос на вступление
     * @return
     */
    //cancelRequest
    
    public CommunityMemberResponseDto cancelRequestFromMember(Long memberId, User memberUser) {
        CommunityMember member = communityMemberDomainService.getByIdFullData(memberId);
        check(member == null, "Запрос не найден");
        SharerCommunityMemberService.check(!member.getUser().getId().equals(memberUser.getId()), "Запрос Вам не принадлежит");

        BpmRaiseSignalEvent bpmRaiseSignalEvent = new BpmRaiseSignalEvent(
                this,
                "request_" + member.getId() + "_cancel",
                Collections.emptyMap()
        );
        blagosferaEventPublisher.publishEvent(bpmRaiseSignalEvent);

        /*CommunityMemberResponseDto responseDto = sharerCommunityMemberBehaviorResolver.getBehavior(member.getCommunity().getId()).cancelRequestFromMember(member, memberUser);
        //publishCommunityMemberEventsAfterTransactionCommit(responseDto.getEvents());
        return responseDto;*/

        return null;
    }

    /**
     * Запрос на исключение участника из объединения от уполномоченного в объединении
     * @param memberId
     * @param excluder уполномоченный участник объединения, который может исключать участников
     * @return
     */
    public CommunityMemberResponseDto requestToExcludeFromCommunityOwner(Long memberId, User excluder) {
        CommunityMember member = communityMemberDomainService.getByIdFullData(memberId);
        check(member == null, "Участник не найден");
        SharerCommunityMemberService.check(member.getCommunity().isRoot() && member.isCreator(), "Создатель объединения не может быть исключен");

        Community community = communityDataService.getByIdFullData(member.getCommunity().getId());
        User user = userDataService.getByIdMinData(member.getUser().getId());

        sendSignalForMember("exitFromCommunityEvent", community, user, excluder, "requestToExcludeFromCommunityOwnerEvent");

        return null;

        /*CommunityMemberResponseDto responseDto = sharerCommunityMemberBehaviorResolver.getBehavior(member.getCommunity().getId()).requestToExcludeFromCommunityOwner(member, excluder);
        //publishCommunityMemberEventsAfterTransactionCommit(responseDto.getEvents());
        return responseDto;*/
    }

    /**
     * Запрос на выход из объединения от участника объединения
     * @param memberId
     * @param leaver
     * @return
     */
    //leave
    
    public CommunityMemberResponseDto requestToExcludeFromMember(Long memberId, User leaver) {
        CommunityMember member = communityMemberDomainService.getByIdFullData(memberId);
        check(member == null, "Участник не найден");
        check(!member.getUser().getId().equals(leaver.getId()), "Доступ запрещен");
        check(member.getCommunity().isRoot() && member.isCreator(), "Вы создали это объединение и не можете из него выйти");

        Community community = communityDataService.getByIdFullData(member.getCommunity().getId());

        sendSignalForMember("exitFromCommunityEvent", community, leaver, community.getCreator(), "requestToExcludeFromMemberEvent");

        return null;

        /*CommunityMemberResponseDto responseDto = sharerCommunityMemberBehaviorResolver.getBehavior(member.getCommunity().getId()).requestToExcludeFromMember(member, leaver);
        //publishCommunityMemberEventsAfterTransactionCommit(responseDto.getEvents());
        return responseDto;*/
    }

    /**
     * Принятие запроса на выход из объединения от уполномоченного в объединении
     * @param memberIds
     * @param excluderUserId
     * @param notifySignEvent
     * @return
     */
    public CommunityMemberResponseDto acceptRequestsToExcludeFromCommunity(List<Long> memberIds, Long excluderUserId, boolean notifySignEvent) {
        List<CommunityMember> members = communityMemberDomainService.getByIds(memberIds, true, true, true, true);
        User excluderUser = userDataService.getByIdFullData(excluderUserId);
        check(members == null || members.size() == 0, "Не переданы участники");
        Community community = members.get(0).getCommunity();
        //CommunityMemberResponseDto responseDto = sharerCommunityMemberBehaviorResolver.getBehavior(community.getId()).acceptRequestsToExcludeFromCommunity(members, excluderUser, notifySignEvent);
        //publishCommunityMemberEventsAfterTransactionCommit(responseDto.getEvents());
        //return responseDto;


        Map<String, Object> parameters = new HashMap<>();
        parameters.put("memberIds", memberIds);
        parameters.put("excluder", excluderUser);
        BpmRaiseSignalEvent bpmRaiseSignalEvent = new BpmRaiseSignalEvent(
                this,
                "requests_exclude_community_accepted_" + community.getId(),
                parameters
        );
        blagosferaEventPublisher.publishEvent(bpmRaiseSignalEvent);
        return null;
    }

    /**
     * Отмена запроса на выход из объединения
     * @param memberId
     * @param canceler
     * @return
     */
    
    public CommunityMemberResponseDto cancelRequestToLeave(Long memberId, User canceler) {
        CommunityMember member = communityMemberDomainService.getByIdFullData(memberId);
        check(member == null, "Участник не найден");
        check(!member.getUser().getId().equals(canceler.getId()), "Доступ запрещен");
        check(member.getStatus() != CommunityMemberStatus.REQUEST_TO_LEAVE/* && member.getStatus() != CommunityMemberStatus.LEAVE_IN_PROCESS*/, "Не правильный статус у запроса на выход");
        //sharerCommunityMemberBehaviorResolver.getBehavior(member.getCommunity().getId()).cancelRequestToLeaveFromMember(member);
        /*CommunityMemberResponseDto responseDto = new CommunityMemberResponseDto(member, new CommunityMemberEvent(this, CommunityEventType.CANCEL_REQUEST_LEAVE, member));
        publishCommunityMemberEventsAfterTransactionCommit(responseDto.getEvents());
        return responseDto;*/

        BpmRaiseSignalEvent bpmRaiseSignalEvent = new BpmRaiseSignalEvent(
                this,
                "request_exclude_" + member.getId() + "_cancel",
                Collections.emptyMap()
        );
        blagosferaEventPublisher.publishEvent(bpmRaiseSignalEvent);

        return null;
    }

    /**
     * Кандидаты на выход из объединения
     * @param communityId
     * @param excluderUserId
     * @return
     */
    public LeaveCommunityMembersDto getLeaveCommunityMembers(Long communityId, Long excluderUserId) {
        Community community = communityDataService.getByIdFullData(communityId);
        User excluderUser = userDataService.getByIdFullData(excluderUserId);
        return sharerCommunityMemberBehaviorResolver.getBehavior(communityId).getLeaveCommunityMembers(community, excluderUser);
    }

    /**
     * Кандидаты на вход в объединение
     * @param communityId
     * @param includerUserId
     * @return
     */
    public ApproveCommunityMembersDto getApproveCommunityMembers(Long communityId, Long includerUserId) {
        Community community = communityDataService.getByIdFullData(communityId);
        User includerUser = userDataService.getByIdFullData(includerUserId);
        return sharerCommunityMemberBehaviorResolver.getBehavior(communityId).getApproveCommunityMembers(community, includerUser);
    }

    /**
     * Опубликовать события членов сообщества после комита
     */
    public void publishCommunityMemberEventsAfterTransactionCommit(List<CommunityMemberEvent> events) {
        if (CollectionUtils.isEmpty(events)) return;
        //blagosferaEventPublisher.publishEvent(new PublishCommunityMemberEventsCallbackEvent(this, events, this));
        for (CommunityMemberEvent event : events) {
            blagosferaEventPublisher.publishEvent(event);
        }
    }

    @Override
    public void publishCommunityMemberEvents(List<CommunityMemberEvent> events) {
        for (CommunityMemberEvent event : events) {
            blagosferaEventPublisher.publishEvent(event);
        }
    }
}

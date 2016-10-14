package ru.radom.kabinet.services.communities.sharermember.behavior;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.*;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateSetting;
import ru.askor.blagosfera.domain.events.community.CommunityMemberEvent;
import ru.askor.blagosfera.domain.events.document.FlowOfDocumentStateEvent;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.document.services.DocumentDomainService;
import ru.radom.kabinet.document.services.DocumentService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.communities.CommunityDocumentCustomSourceHandler;
import ru.radom.kabinet.services.communities.CommunityDocumentRequestService;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.services.communities.sharermember.dto.ApproveCommunityMembersDto;
import ru.radom.kabinet.services.communities.sharermember.dto.CommunityMemberResponseDto;
import ru.radom.kabinet.services.communities.sharermember.dto.LeaveCommunityMembersDto;
import ru.radom.kabinet.services.document.DocumentTemplateSettingService;
import ru.radom.kabinet.utils.SpringUtils;
import ru.radom.kabinet.utils.VarUtils;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.util.*;

/**
 * Реализация поведения для вступления в объединение в котором необходимо подписывать документы
 * Created by vgusev on 15.07.2016.
 */
@Service("documentsSharerCommunityMemberBehavior")
@Transactional
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class DocumentsSharerCommunityMemberBehavior implements ISharerCommunityMemberBehavior {

    private static final String IS_COMMUNITY_DOCUMENT_REQUEST_PARAMETER = "COMMUNITY_DOCUMENT_REQUEST";
    private static final String COMMUNITY_ID_PARAMETER = "COMMUNITY_ID";
    private static final String USER_ID_PARAMETER = "USER_ID";
    private static final String REQUEST_ID_PARAMETER = "COMMUNITY_DOCUMENT_REQUEST_ID";
    //private static final String DELEGATE_METHOD_PARAMETER = "DELEGATE_METHOD_PARAMETER";

    private ISharerCommunityMemberBehavior delegatedBehavior;

    @Autowired
    private CommunityDocumentRequestService communityDocumentRequestService;

    //@Autowired
    //private CommunityDocumentCustomSourceHandler communityDocumentCustomSourceHandler;

    @Autowired
    private DocumentTemplateSettingService documentTemplateSettingService;

    @Autowired
    private CommunityDataService communityDataService;

    @Autowired
    private DocumentDomainService documentDomainService;

    /*@Autowired
    private SharerCommunityMemberBehaviorResolver sharerCommunityMemberBehaviorResolver;*/

    @Autowired
    private DocumentService documentService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = {CommunityDocumentSharerException.class})
    public void onFlowOfDocumentStateEvent(FlowOfDocumentStateEvent event) throws Throwable {

        if (event.getParameters() != null && event.getParameters().containsKey(IS_COMMUNITY_DOCUMENT_REQUEST_PARAMETER)) {
            Long communityId = VarUtils.getLong(event.getParameters().get(COMMUNITY_ID_PARAMETER), null);
            ExceptionUtils.check(communityId == null, "Не установлен ИД объединения");
            Community community = communityDataService.getByIdMinData(communityId);

            Long requestId = VarUtils.getLong(event.getParameters().get(REQUEST_ID_PARAMETER), null);
            CommunityDocumentRequest communityDocumentRequest = communityDocumentRequestService.getById(requestId);

            switch (event.getStateEventType()) {
                case DOCUMENT_SIGNED: { // Документ подписан всеми
                    if (!community.isNeedCreateDocuments()) {
                        if (communityDocumentRequest != null) {
                            documentService.setActiveDocument(event.getDocument().getId(), false);
                            communityDocumentRequestService.deleteRequestAndMember(communityId, communityDocumentRequest.getUser().getId());
                        }
                        throw new CommunityDocumentSharerException("Руководство объединения отменило подписание документов для вступления. Вам необходимо вновь выполнить запрос на вступление в объединение.");
                    }

                    boolean isSignedAllDocuments = true;
                    for (Document document : communityDocumentRequest.getDocuments()) {
                        if (!documentService.isSignedDocument(document.getId()) && !event.getDocument().getId().equals(document.getId())) {
                            isSignedAllDocuments = false;
                            break;
                        }
                    }
                    if (isSignedAllDocuments) {
                        CommunityMember communityMember = communityMemberDomainService.getByCommunityIdAndUserId(
                                communityDocumentRequest.getCommunity().getId(),
                                communityDocumentRequest.getUser().getId()
                        );
                        if (communityMember == null) {
                            communityMember = new CommunityMember();
                            communityMember.setCommunity(communityDocumentRequest.getCommunity());
                            communityMember.setUser(communityDocumentRequest.getUser());
                        }
                        communityMember.setStatus(CommunityMemberStatus.MEMBER);
                        communityMemberDomainService.save(communityMember);
                        communityDocumentRequestService.delete(communityDocumentRequest.getId());
                    }

                    break;
                }
                case DOCUMENT_UNSIGNED: {
                    if (communityDocumentRequest != null && communityDocumentRequest.getDocuments() != null) {
                        for (Document doc : communityDocumentRequest.getDocuments()) {
                            if (!event.getDocument().getId().equals(doc.getId()) && doc.isActive()) {
                                documentService.setActiveDocument(doc.getId(), false);
                            }
                        }
                        communityDocumentRequestService.deleteRequestAndMember(
                                communityDocumentRequest.getCommunity().getId(),
                                communityDocumentRequest.getUser().getId()
                        );
                    }
                    break;
                }
            }
        }
    }

    public DocumentsSharerCommunityMemberBehavior() {}

    public DocumentsSharerCommunityMemberBehavior(ISharerCommunityMemberBehavior delegatedBehavior) {
        this.delegatedBehavior = delegatedBehavior;
    }

    private CommunityDocumentRequest createDocuments(Community community, User user) {
        CommunityDocumentCustomSourceHandler communityDocumentCustomSourceHandler = SpringUtils.getBean("communityDocumentCustomSourceHandler", user.getId(), community.getId());

        CommunityDocumentRequest communityDocumentRequest = new CommunityDocumentRequest();
        communityDocumentRequest.setCommunity(community);
        communityDocumentRequest.setUser(user);

        communityDocumentRequest = communityDocumentRequestService.save(communityDocumentRequest);

        Map<String, String> documentParameters = new HashMap<>();

        documentParameters.put(IS_COMMUNITY_DOCUMENT_REQUEST_PARAMETER, Boolean.TRUE.toString());
        documentParameters.put(COMMUNITY_ID_PARAMETER, String.valueOf(community.getId()));
        documentParameters.put(USER_ID_PARAMETER, String.valueOf(user.getId()));
        documentParameters.put(REQUEST_ID_PARAMETER, String.valueOf(communityDocumentRequest.getId()));

        List<DocumentTemplateSetting> documentTemplateSettings = community.getDocumentTemplateSettings();
        List<Document> documents = new ArrayList<>();
        for (DocumentTemplateSetting documentTemplateSetting : documentTemplateSettings) {
            Document document = documentTemplateSettingService.createDocument(
                    documentTemplateSetting,
                    community.getCreator().getId(),
                    communityDocumentCustomSourceHandler,
                    documentParameters
            );
            documents.add(document);
        }
        communityDocumentRequest.getDocuments().addAll(documents);
        return communityDocumentRequestService.save(communityDocumentRequest);
    }

    private void deleteRequestAndDocuments(Community community, User user) {
        CommunityDocumentRequest communityDocumentRequest = communityDocumentRequestService.getByCommunityAndUser(community.getId(), user.getId());
        if (communityDocumentRequest != null) {
            for (Document document : communityDocumentRequest.getDocuments()) {
                document.setActive(false);
                documentDomainService.save(document);
            }
            communityDocumentRequestService.delete(communityDocumentRequest.getId());
        }
    }

    private CommunityMemberResponseDto createDocumentRequestOrGetMessage(Community community, User user, CommunityEventType communityEventType) {
        community = communityDataService.getByIdFullData(community.getId());
        CommunityDocumentRequest communityDocumentRequest = communityDocumentRequestService.getByCommunityAndUser(community.getId(), user.getId());
        Map<String, Object> parameters = new HashMap<>();
        if (communityDocumentRequest != null) {
            // выдать сообщение с ссылкой на страницу с документами
            parameters.put("responseType", "existsDocumentRequest");
        } else {
            communityDocumentRequest = createDocuments(community, user);
            parameters.put("responseType", "documentRequest");
        }

        CommunityMember communityMember = communityMemberDomainService.getByCommunityIdAndUserId(community.getId(), user.getId());

        if (communityMember == null) {
            communityMember = new CommunityMember();
            communityMember.setCommunity(community);
            communityMember.setUser(user);
        }

        parameters.put("sharerName", user.getFullName());
        parameters.put("link", "/groups/documentrequests/" + communityDocumentRequest.getId());
        parameters.put("linkDescription", "Открыть страницу пакета документов");

        return new CommunityMemberResponseDto(null, communityMember, parameters, new CommunityMemberEvent(this, communityEventType, communityMember));
    }

    @Override
    public CommunityMemberResponseDto acceptInvite(CommunityMember member, boolean notifySignEvent) {
        //Принять приглашение на вступление в объединение.
        // Проверить наличие запроса с созданными документами на вступление - если есть - то выдать ошибку
        // Создать пачку документов для подписания

        CommunityMemberResponseDto result = delegatedBehavior.acceptInvite(member, notifySignEvent);
        if (CommunityMemberStatus.MEMBER.equals(result.getMember().getStatus())) {
            CommunityMember communityMember = result.getMember();
            communityMember.setStatus(CommunityMemberStatus.CONDITION_NOT_DONE_REQUEST);
            communityMemberDomainService.save(communityMember);
            result = createDocumentRequestOrGetMessage(member.getCommunity(), member.getUser(), CommunityEventType.CONDITION_NOT_DONE_REQUEST);
        }
        return result;
    }

    @Override
    public CommunityMemberResponseDto request(Community community, User requester, boolean notifySignEvent) {
        // Проверить наличие запроса с созданными документами на вступление - если есть - то выдать ошибку
        // Создать пачку документов для подписания
        CommunityMemberResponseDto result = delegatedBehavior.request(community, requester, notifySignEvent);
        if (CommunityMemberStatus.MEMBER.equals(result.getMember().getStatus())) {
            CommunityMember communityMember = result.getMember();
            communityMember.setStatus(CommunityMemberStatus.CONDITION_NOT_DONE_REQUEST);
            communityMemberDomainService.save(communityMember);
            result = createDocumentRequestOrGetMessage(community, requester, CommunityEventType.CONDITION_NOT_DONE_REQUEST);
        }
        return result;
    }

    @Override
    public CommunityMemberResponseDto acceptRequests(List<CommunityMember> members, User accepter, boolean notifySignEvent) {
        CommunityMemberResponseDto result = delegatedBehavior.acceptRequests(members, accepter, notifySignEvent);
        List<CommunityMember> communityMembers = null;
        if (result.getEvents() != null && result.getEvents().size() > 1) {
            communityMembers = new ArrayList<>();
            for (CommunityMemberEvent communityMemberEvent : result.getEvents()) {
                communityMembers.add(communityMemberEvent.getMember());
            }
        } else if (result.getMember() != null) {
            communityMembers = Collections.singletonList(result.getMember());
        }
        if (communityMembers != null) {
            for (CommunityMember communityMember : communityMembers) {
                communityMember.setStatus(CommunityMemberStatus.CONDITION_NOT_DONE_REQUEST);
                communityMemberDomainService.save(communityMember);
                createDocumentRequestOrGetMessage(communityMember.getCommunity(), communityMember.getUser(), CommunityEventType.CONDITION_NOT_DONE_REQUEST);
            }
        }
        return result;
    }

    @Override
    public CommunityMemberResponseDto rejectRequestsFromCommunityOwner(List<CommunityMember> members, User rejecter) {
        // вызвать метод делегата
        return delegatedBehavior.rejectRequestsFromCommunityOwner(members, rejecter);
    }

    @Override
    public CommunityMemberResponseDto cancelRequestFromMember(CommunityMember member, User memberUser) {
        deleteRequestAndDocuments(member.getCommunity(), memberUser);
        member.setStatus(CommunityMemberStatus.REQUEST);
        return delegatedBehavior.cancelRequestFromMember(member, memberUser);
    }

    @Override
    public CommunityMemberResponseDto requestToExcludeFromCommunityOwner(CommunityMember member, User excluder) {
        return delegatedBehavior.requestToExcludeFromCommunityOwner(member, excluder);
    }

    @Override
    public CommunityMemberResponseDto requestToExcludeFromMember(CommunityMember member, User leaver) {
        return delegatedBehavior.requestToExcludeFromMember(member, leaver);
    }

    @Override
    public void cancelRequestToLeaveFromMember(CommunityMember member) {
        delegatedBehavior.cancelRequestToLeaveFromMember(member);
    }

    @Override
    public CommunityMemberResponseDto acceptRequestsToExcludeFromCommunity(List<CommunityMember> members, User excluder, boolean notifySignEvent) {
        return delegatedBehavior.acceptRequestsToExcludeFromCommunity(members, excluder, notifySignEvent);
    }

    @Override
    public LeaveCommunityMembersDto getLeaveCommunityMembers(Community community, User excluder) {
        return delegatedBehavior.getLeaveCommunityMembers(community, excluder);
    }

    @Override
    public ApproveCommunityMembersDto getApproveCommunityMembers(Community community, User approver) {
        return delegatedBehavior.getApproveCommunityMembers(community, approver);
    }
/*
    public void setDelegatedBehavior(ISharerCommunityMemberBehavior delegatedBehavior) {
        this.delegatedBehavior = delegatedBehavior;
    }*/
}

package ru.radom.kabinet.web.communities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.domain.community.*;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.collections.CommunityMemberStatusList;
import ru.radom.kabinet.dao.communities.CommunityPostRequestDao;
import ru.radom.kabinet.dto.CommonResponseDto;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.dto.community.CommunityUserPost;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.communities.CommunityPermissionRequired;
import ru.radom.kabinet.security.context.RequestContext;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.communities.CommunityPostDomainService;
import ru.radom.kabinet.services.communities.CommunityPostRequestDomainService;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.CommonConstants;
import ru.radom.kabinet.utils.CommunityPermissions;
import ru.radom.kabinet.web.communities.dto.*;

import java.util.List;

/**
 * Контроллер для обработки запросов на должности участников
 * Created by vgusev on 28.08.2015.
 */
@Controller("communitiesPostRequestController")
public class CommunitiesSeoPostRequestController {

    @Autowired
    private CommunityPostRequestDao communityPostRequestDao;

    @Autowired
    private CommunityPostRequestDomainService communityPostRequestDomainService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Autowired
    private CommunityDataService communityDomainService;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private RequestContext radomRequestContext;

    @Autowired
    private CommunityPostDomainService communityPostDomainService;

    /**
     * Страница назначения долженостей
     * @param seoLink
     * @return
     */
    @CommunityPermissionRequired(CommunityPermissions.MEMBERS_APPOINTS)
    @RequestMapping(value = "/group/{seolink}/settings/members", method = RequestMethod.GET)
    public String showMembersSettingsPage(@PathVariable("seolink") String seoLink) {
        return "communitySettingsMembers";
    }

    /**
     * Данные для страницы назначения на должность
     * @param seoLink
     * @return
     */
    @CommunityPermissionRequired(CommunityPermissions.MEMBERS_APPOINTS)
    @RequestMapping(value = "/group/{seolink}/post_request_page_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommunityPostRequestPageDataDto getPostRequestPageData(@PathVariable("seolink") String seoLink) {
        Community community = communityDomainService.getByIdFullData(radomRequestContext.getCommunityId());
        CommunityMember selfMember = communityMemberDomainService.getByCommunityIdAndUserId(radomRequestContext.getCommunityId(), SecurityUtils.getUser().getId());
        List<Community> children = community.getChildren();
        CommunityMemberStatusList communityMemberStatuses = new CommunityMemberStatusList();
        communityMemberStatuses.add(CommunityMemberStatus.MEMBER);
        List<CommunityMember> possibleCandidates = communityMemberDomainService.getList(community.getId(), communityMemberStatuses);
        return new CommunityPostRequestPageDataDto(community, selfMember, children, possibleCandidates);
    }

    /**
     * Получить массив с параметрами для таблицы с должностями объединения
     * @param seoLink
     * @param start
     * @param limit
     * @return
     */
    @CommunityPermissionRequired(CommunityPermissions.MEMBERS_APPOINTS)
    @RequestMapping(value = "/group/{seolink}/load_members_posts.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommunityPostListDto loadMembersPosts(@PathVariable("seolink") String seoLink,
                                                 //@RequestParam(value = "page", required = false) int page,
                                                 @RequestParam(value = "start", required = false) int start,
                                                 @RequestParam(value = "limit", required = false) int limit) {
        CommunityPostListDto result;
        try {
            // TODO Нужно будет сделать фильтрацию
            // TODO Надо будет добавить проверку на то, что нельзя уволить руководство
            List<CommunityUserPost> communityUserPosts = communityPostDomainService.getCommunityUserPosts(radomRequestContext.getCommunityId(), start, limit);
            int count = communityPostDomainService.getCommunityUserPostsCount(radomRequestContext.getCommunityId());
            result = new CommunityPostListDto(communityUserPosts, count);
        } catch (Exception e) {
            e.printStackTrace();
            result = CommunityPostListDto.toErrorDto();
        }
        return result;
    }

    /**
     * Уволить участника с должности
     * @param seoLink
     * @param userId
     * @param communityPostId
     * @return
     */
    @CommunityPermissionRequired(CommunityPermissions.MEMBERS_APPOINTS)
    @RequestMapping(value = "/group/{seolink}/fire_member_from_post.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto fireMemberFromPost(@PathVariable("seolink") String seoLink,
                                                 @RequestParam(value = "user_id", required = false) Long userId,
                                                 @RequestParam(value = "post_id", required = false) Long communityPostId) {
        CommunityPost communityPost = communityPostDomainService.getByIdFullData(communityPostId);
        Community community = communityPost.getCommunity();
        CommunityMember member = communityMemberDomainService.getByCommunityIdAndUserId(community.getId(), userId);
        communitiesService.disapoint(SecurityUtils.getUser(), member, communityPost);
        return SuccessResponseDto.get();
    }

    /**
     * Запрос на назначение в должности
     * @param userId
     * @param communityPostId
     * @return
     */
    @CommunityPermissionRequired(CommunityPermissions.MEMBERS_APPOINTS)
    @RequestMapping(value = "/group/{seolink}/request_appoint_member_post.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto appointMemberPost(@PathVariable("seolink") String seoLink,
                                                   @RequestParam(value = "user_id", required = true) Long userId,
                                                   @RequestParam(value = "post_id", required = true) Long communityPostId) {
        // Сделать запрос участнику о том, что его хотят назначить на должность
        User user = userDataService.getByIdMinData(userId);
        CommunityPost communityPost = communityPostDomainService.getByIdFullData(communityPostId);
        CommunityMember member = communityMemberDomainService.getByCommunityIdAndUserId(communityPost.getCommunity().getId(), userId);
        if (member == null) {
            throw new RuntimeException("Участник " + user.getName() + " не является членом объединения " + communityPost.getCommunity().getName());
        }
        communitiesService.requestToAppoint(SecurityUtils.getUser(), member, communityPost);
        return SuccessResponseDto.get();
    }

    /**
     * Получить список должностей по объединению
     * @param seoLink
     * @param communityId
     * @return
     */
    @CommunityPermissionRequired(CommunityPermissions.MEMBERS_APPOINTS)
    @RequestMapping(value = "/group/{seolink}/load_posts_by_community.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public List<CommunityPostDto> loadPosts(
            @PathVariable("seolink") String seoLink,
            @RequestParam(value = "communityId", required = true) Long communityId) {
        // Загружаем должности с схемой и ролями
        return CommunityPostDto.toListDto(communityPostDomainService.getByCommunityId(communityId, false, false, true, true));
    }

    /**
     * Страница приглашения на работу
     * @param model
     * @return
     */
    @RequestMapping(value = "/communities/requests/appoint", method = RequestMethod.GET)
    public String showPostRequestsPage(Model model, @RequestParam(value = "request_id", required = true) Long requestId) {
        // Ссылка на подтверждение должности
        /*model.addAttribute("approveRequestLink", "/communities/requests/approve_post_appoint.json");
        model.addAttribute("cancelRequestLink", "/communities/requests/cancel_post_appoint.json");

        CommunityPostRequestEntity communityPostRequest = communityPostRequestDao.getById(requestId);
        if (communityPostRequest == null) {
            model.addAttribute("errorMessage", "Приглашение на работу не существует!");
        } else if (!communityPostRequest.getReceiver().getUser().getId().equals(SecurityUtils.getUser().getId())) {
            model.addAttribute("errorMessage", "Приглашение на работу Вам не принадлежит!");
        } else if (communityPostRequest.getStatus() != CommunityPostRequestStatus.NEW) {
            model.addAttribute("errorMessage", "Принятие на должность в процессе!");
        }

        model.addAttribute("communityPostRequest", communityPostRequest);*/
        return "postRequestPage";
    }

    /**
     * Данные для страницы принятия должности
     * @param requestId
     * @return
     */
    @RequestMapping(value = "/communities/requests/appoint_page_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommunityPostRequestAppointPageDataDto getPostRequestsPageData(@RequestParam(value = "request_id", required = true) Long requestId) {
        CommunityPostRequestAppointPageDataDto result;
        CommunityPostRequest communityPostRequest = communityPostRequestDomainService.getById(requestId);
        String errorMessage = null;
        if (communityPostRequest == null) {
            errorMessage = "Приглашение на работу не существует!";
        } else if (!communityPostRequest.getReceiver().getUser().getId().equals(SecurityUtils.getUser().getId())) {
            errorMessage = "Приглашение на работу Вам не принадлежит!";
        } else if (communityPostRequest.getStatus() != CommunityPostRequestStatus.NEW) {
            errorMessage = "Принятие на должность в процессе!";
        }
        if (errorMessage != null) {
            result = new CommunityPostRequestAppointPageDataDto(errorMessage);
        } else {
            Community community = communityDomainService.getByIdFullData(communityPostRequest.getCommunity().getId());
            result = new CommunityPostRequestAppointPageDataDto(communityPostRequest, community);
        }

        return result;
    }

    /**
     * Подтвердить должность
     * @param requestId
     * @return
     */
    @RequestMapping(value = "/communities/requests/approve_post_appoint.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto approvePostsRequest(@RequestParam(value = "request_id", required = true) Long requestId) {
        // Согласиться на должность
        communitiesService.approveAppointRequest(requestId, SecurityUtils.getUser().getId());
        return SuccessResponseDto.get();
    }

    /**
     * Отказаться от должности
     * @param requestId
     * @return
     */
    @RequestMapping(value = "/communities/requests/cancel_post_appoint.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto cancelPostsRequest(@RequestParam(value = "request_id", required = true) Long requestId) {
        communitiesService.cancelPostAppoint(requestId, SecurityUtils.getUser().getId());
        return SuccessResponseDto.get();
    }
}


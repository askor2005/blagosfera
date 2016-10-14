package ru.radom.kabinet.web.communities;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.core.services.account.AccountService;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.data.jpa.entities.account.SharebookEntity;
import ru.askor.blagosfera.data.jpa.services.account.AccountDataService;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.account.Account;
import ru.askor.blagosfera.domain.community.*;
import ru.askor.blagosfera.domain.field.FieldsGroup;
import ru.radom.kabinet.dao.OkvedDao;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.dao.communities.CommunityLogEventDao;
import ru.radom.kabinet.dao.communities.CommunityMemberDao;
import ru.radom.kabinet.dao.communities.schema.CommunitySchemaUnitDao;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.dao.rameralisteditor.RameraListEditorItemDAO;
import ru.radom.kabinet.dto.CommonResponseDto;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.json.SerializationManager;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.model.communities.dto.CommunityFillingDto;
import ru.radom.kabinet.module.rameralisteditor.service.ListEditorItemDomainService;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.bio.TokenProtected;
import ru.radom.kabinet.services.SharebookService;
import ru.radom.kabinet.services.batchVoting.BatchVotingTemplateService;
import ru.radom.kabinet.services.communities.*;
import ru.radom.kabinet.services.communities.organizationmember.OrganizationCommunityMemberService;
import ru.radom.kabinet.services.communities.organizationmember.OrganizationMemberDomainService;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.services.communities.sharermember.SharerCommunityMemberService;
import ru.radom.kabinet.services.communities.sharermember.dto.CommunityMemberResponseDto;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.*;
import ru.radom.kabinet.web.communities.dto.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller("communitiesController")
@RequestMapping("/communities")
public class CommunitiesController {
    private static final Logger logger = LoggerFactory.getLogger(CommunitiesController.class);

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private CommunityMemberDao communityMemberDao;

    @Autowired
    private OrganizationCommunityMemberService organizationCommunityMemberService;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private SharebookService sharebookService;

    @Autowired
    private SerializationManager serializationManager;

    @Autowired
    private OkvedDao okvedDao;

    /**
     * ДАО класс для работы с элементами универсальных списков.
     */
    @Autowired
    private RameraListEditorItemDAO rameraListEditorItemDAO;

    @Autowired
    private CommunityLogEventDao communityLogEventDao;

    @Autowired
    private CommunitySchemaUnitDao communitySchemaUnitDao;

    @Autowired
    private CommunityInventoryService communityInventoryService;

    @Autowired
    private FieldDao fieldDao;

    @Autowired
    private FieldsService fieldsService;

    @Autowired
    private SharerCommunityMemberService sharerCommunityMemberService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private CommunityFillingService communityFillingService;

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private CommunityDataService communityDataService;

    @Autowired
    private OrganizationMemberDomainService organizationMemberDomainService;

    @Autowired
    private ListEditorItemDomainService listEditorItemService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountDataService accountDataService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Autowired
    private BatchVotingTemplateService batchVotingTemplateService;

    @Autowired
    private SettingsManager settingsManager;

    @RequestMapping(value = "/create.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CreateCommunityResponseDto create(@RequestBody CommunityFullDataDto communityFullDataDto) {
        CreateCommunityResponseDto result;
        try {
            Community community = communityFullDataDto.toDomain();
            community = communitiesService.createCommunity(community, SecurityUtils.getUser(), null);
            result = new CreateCommunityResponseDto(community);
        } catch (CommunityException e) {
            if (e.getMap() != null) {
                result = new CreateCommunityResponseDto(e.getMessage(), e.getMap());
            } else {
                result = new CreateCommunityResponseDto(e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = new CreateCommunityResponseDto(e.getMessage());
        }
        return result;
    }
    @RequestMapping(value = "/top.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public List<Community> top(@RequestParam("page") Long page,@RequestParam(value = "name",required = false,defaultValue = "") String name) {
       return communityDataService.getTopVisitForUser(SecurityUtils.getUser().getId(),page, name);
    }

	@RequestMapping(value = "/delete.json", method = RequestMethod.POST)
    @TokenProtected
    @ResponseBody
	public Object delete(@RequestParam(value = "community_id") Long communityId, @RequestParam(value = "comment", required = false) String comment) {
		try {
			Community community = communitiesService.deleteCommunity(communityId, comment, SecurityUtils.getUser());
			CommunityMemberEntity communityMember = communityMemberDao.get(community.getId(), SecurityUtils.getUser().getId());

			return new CommunityListItemDto(community,
                    communityMember != null ? communityMember.toDomain() : null,
                    communitiesService.canDeleteCommunity(community, SecurityUtils.getUserDetails()),
                    communitiesService.canRestoreCommunity(community, SecurityUtils.getUserDetails()));
		} catch (CommunityException e) {
			logger.error(e.getMessage(), e);
			return JsonUtils.getErrorJson(e.getMessage());
		}
	}

	@RequestMapping(value = "/restore.json", method = RequestMethod.POST)
    @ResponseBody
	public Object restore(@RequestParam(value = "community_id") Long communityId) {
		try {
			Community community = communitiesService.restoreCommunity(communityId);
			CommunityMemberEntity communityMember = communityMemberDao.get(community.getId(), SecurityUtils.getUser().getId());

            return new CommunityListItemDto(community,
                    communityMember != null ? communityMember.toDomain() : null,
                    communitiesService.canDeleteCommunity(community, SecurityUtils.getUserDetails()),
                    communitiesService.canRestoreCommunity(community, SecurityUtils.getUserDetails()));
		} catch (CommunityException e) {
			logger.error(e.getMessage(), e);
			return JsonUtils.getErrorJson(e.getMessage());
		}
	}

    /**
     * Запрос участника вступления в объединение
     *
     * @param communityId
     * @return
     */
    @RequestMapping(value = "/request.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto request(@RequestParam("community_id") Long communityId) {
        //return createMemberJsonFromDto(sharerCommunityMemberService.request(communityId, SecurityUtils.getUser().getId(), true));
        sharerCommunityMemberService.request(communityId, SecurityUtils.getUser().getId(), true);
        return SuccessResponseDto.get();
    }

    /**
     * Вступить в открытое объединение
     *
     * @param community
     * @return
     */
    @RequestMapping(value = "/join.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto join(@RequestParam("community_id") CommunityEntity community) {
        //return createMemberJsonFromDto(sharerCommunityMemberService.request(community.getId(), SecurityUtils.getUser().getId(), true));
        sharerCommunityMemberService.request(community.getId(), SecurityUtils.getUser().getId(), true);
        return SuccessResponseDto.get();
    }

    /**
     * Принять запрос на вступление
     *
     * @param memberId
     * @return
     */
    @RequestMapping(value = "/accept_request.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto acceptRequest(@RequestParam("member_id") Long memberId) {
        //return createMemberJsonFromDto(sharerCommunityMemberService.acceptRequests(Collections.singletonList(memberId), SecurityUtils.getUser().getId(), true));
        sharerCommunityMemberService.acceptRequests(Collections.singletonList(memberId), SecurityUtils.getUser().getId(), true);
        return SuccessResponseDto.get();
    }

    @RequestMapping(value = "/reject_request.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto rejectRequest(@RequestParam("member_id") Long memberId) {
        //return createMemberJsonFromDto(sharerCommunityMemberService.rejectRequestsFromCommunityOwner(Collections.singletonList(memberId), userDataService.getByIdFullData(SecurityUtils.getUser().getId())));
        sharerCommunityMemberService.rejectRequestsFromCommunityOwner(Collections.singletonList(memberId), userDataService.getByIdFullData(SecurityUtils.getUser().getId()));
        return SuccessResponseDto.get();
    }

    /**
     * Отклонить запрос на вступление
     * @param memberId
     * @return
     */
    //rejectRequestsFromCommunityOwner
    // TODO Переделать
	/*
	@RequestMapping(value = "/reject_request.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	public @ResponseBody String rejectRequest(@RequestParam("member_id") Long memberId) {
		return createMemberJsonFromDto(sharerCommunityMemberService.rejectRequestsFromCommunityOwner(Collections.singletonList(memberId), radomRequestContext.getCurrentSharer()));
	}*/

    /**
     * Отклонить несколько запросов на вступление
     */
    //rejectRequestsFromCommunityOwner
    // TODO Переделать
	/*
	@RequestMapping(value = "/reject_requests.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	public @ResponseBody String rejectRequests(@RequestBody List<Long> memberIds) {
		return createMemberJsonFromDto(sharerCommunityMemberService.rejectRequestsFromCommunityOwner(memberIds, radomRequestContext.getCurrentSharer()));
	}*/

    /**
     * Пригласить участника в объединение
     * @param communityId
     * @param userId
     * @return
     */
    @RequestMapping(value = "/invite.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto invite(@RequestParam("community_id") Long communityId, @RequestParam("user_id") Long userId) {
        //return createMemberJsonFromDto(sharerCommunityMemberService.inviteMember(communityId, userId, SecurityUtils.getUser()));
        sharerCommunityMemberService.inviteMember(communityId, userId, SecurityUtils.getUser());
        return SuccessResponseDto.get();
    }

    @RequestMapping(value = "/accept_invite.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
    public CommonResponseDto acceptInvite(@RequestParam("member_id") Long memberId) {
		//return createMemberJsonFromDto(sharerCommunityMemberService.acceptInvite(memberId, SecurityUtils.getUser(), true));
        sharerCommunityMemberService.acceptInvite(memberId, SecurityUtils.getUser(), true);
        return SuccessResponseDto.get();
	}

    @RequestMapping(value = "/reject_invite.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
    public CommonResponseDto rejectInvite(@RequestParam("member_id") Long memberId) {
		//return createMemberJsonFromDto(sharerCommunityMemberService.rejectInvite(memberId, SecurityUtils.getUser()));
        sharerCommunityMemberService.rejectInvite(memberId, SecurityUtils.getUser());
        return SuccessResponseDto.get();
	}

    @RequestMapping(value = "/cancel_invite.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto cancelInvite(@RequestParam("member_id") Long memberId) {
        //return createMemberJsonFromDto(sharerCommunityMemberService.cancelInvite(memberId, SecurityUtils.getUser()));
        sharerCommunityMemberService.cancelInvite(memberId, SecurityUtils.getUser());
        return SuccessResponseDto.get();
    }

    @RequestMapping(value = "/exclude_member.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
	public CommonResponseDto exclude(@RequestParam("member_id") Long memberId) {
		//return createMemberJsonFromDto(sharerCommunityMemberService.requestToExcludeFromCommunityOwner(memberId, SecurityUtils.getUser()));
        sharerCommunityMemberService.requestToExcludeFromCommunityOwner(memberId, SecurityUtils.getUser());
        return SuccessResponseDto.get();
	}

    @RequestMapping(value = "/leave.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto leave(@RequestParam("member_id") Long memberId) {
        //return createMemberJsonFromDto(sharerCommunityMemberService.requestToExcludeFromMember(memberId, SecurityUtils.getUser()));
        sharerCommunityMemberService.requestToExcludeFromMember(memberId, SecurityUtils.getUser());
        return SuccessResponseDto.get();
    }

    @RequestMapping(value = "/cancel_request.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
    public CommonResponseDto cancelRequest(@RequestParam("member_id") Long memberId) {
		//return createMemberJsonFromDto(sharerCommunityMemberService.cancelRequestFromMember(memberId, SecurityUtils.getUser()));
        sharerCommunityMemberService.cancelRequestFromMember(memberId, SecurityUtils.getUser());
        return SuccessResponseDto.get();
	}



    /**
     * Отмена запроса на выход из объединения
     */
    @RequestMapping(value = "/cancel_request_to_leave.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
    public CommonResponseDto cancelRequestToLeave(@RequestParam("member_id") Long memberId) {
		//return createMemberJsonFromDto(sharerCommunityMemberService.cancelRequestToLeave(memberId, SecurityUtils.getUser()));
        sharerCommunityMemberService.cancelRequestToLeave(memberId, SecurityUtils.getUser());
        return SuccessResponseDto.get();
	}

	// TODO Переделать
	/*@RequestMapping(value = "/members.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	public @ResponseBody String members(@RequestParam("community_id") CommunityEntity community,
										@RequestParam(value = "status_list[]", required = false) CommunityMemberStatusList statusList,
										@RequestParam(value = "page", defaultValue = "1") int page,
										@RequestParam(value = "per_page", defaultValue = "20") int perPage,
										@RequestParam(value = "query", required = false) String query,
										@RequestParam(value = "exclude_sharer_ids[]", required = false) SharersList excludeSharersList) {
		int firstResult = (page - 1) * perPage;
		List<CommunityMemberEntity> members = communityMemberDao.getAccounts(community, statusList, firstResult, perPage > 0 ? perPage : Integer.MAX_VALUE, query, excludeSharersList);
		return serializationManager.serializeCollection(members).toString();
	}*/

	@RequestMapping(value = "/requests.json", method = RequestMethod.POST)
    @ResponseBody
	public List<CommunityMemberDto> requests(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "per_page", defaultValue = "20") int perPage) {
        List<CommunityMember> communityMembers = communityMemberDomainService.getByCommunityCreator(
                SecurityUtils.getUser().getId(),
                Collections.singletonList(CommunityMemberStatus.REQUEST),
                page - 1,
                perPage
        );

		/*try {
			int firstResult = (page - 1) * perPage;
			List<CommunityMemberEntity> members = communityMemberDao.getRequests(SecurityUtils.getUser().getId(), firstResult, perPage);

			JSONArray array = new JSONArray();
			for (CommunityMemberEntity member : members) {
				JSONObject memberJson = serializationManager.serialize(member);
				memberJson.put("community", serializationManager.serialize(member.getCommunity()));
				array.put(memberJson);
			}
			return array.toString();
		} catch (CommunityException e) {
			logger.error(e.getMessage(), e);
			return JsonUtils.getErrorJson(e.getMessage()).toString();
		}*/
        return CommunityMemberDto.toDtoList(communityMembers);
	}

    @RequestMapping(value = "/list.json", method = {RequestMethod.POST, RequestMethod.GET}, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommunitiesListDto list(
            @RequestParam(value = "status", required = false) CommunityMemberStatus status,
            @RequestParam(value = "creator", required = false) Boolean creator,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "per_page", defaultValue = "20") int perPage,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "access_type", required = false) CommunityAccessType accessType,
            @RequestParam(value = "community_type", required = false) String communityType,
            @RequestParam(value = "activity_scope_id", required = false) Long activityScopeId,
            @RequestParam(value = "order_by", defaultValue = "name") String orderBy,
            @RequestParam(value = "asc", defaultValue = "true") boolean asc,
            @RequestParam(value = "parent_id", required = false) CommunityEntity parent,
            @RequestParam(value = "check_parent", defaultValue = "true") boolean checkParent,
            @RequestParam(value = "deleted", required = false) Boolean deleted
    ) {
        int firstResult = (page - 1) * perPage;

        if (!SecurityUtils.getUserDetails().hasRole("ROLE_ADMIN")) {
            deleted = false;
        }

        boolean isAdmin = SecurityUtils.getUserDetails().hasRole(Roles.ROLE_ADMIN);
        List<CommunityMemberStatus> statusList = Arrays.asList(status, CommunityMemberStatus.REQUEST_TO_LEAVE);
        List<Community> communities = communityDataService.getList(
                SecurityUtils.getUser().getId(), isAdmin, statusList, creator,
                firstResult, perPage, query, accessType, communityType, activityScopeId, parent,
                checkParent, deleted, orderBy, asc
        );
        long count = communityDataService.getListCount(
                SecurityUtils.getUser().getId(), isAdmin, statusList,
                creator, query, accessType, communityType,
                activityScopeId, parent, checkParent, deleted);

        Map<Long, Boolean> canDeleteCommunitiesMap = new HashMap<>();
        Map<Long, Boolean> canRestoreCommunitiesMap = new HashMap<>();
        List<Long> communitiesIds = new ArrayList<>();
        if (communities != null) {
            for (Community community : communities) {
                canDeleteCommunitiesMap.put(community.getId(), communitiesService.canDeleteCommunity(community, SecurityUtils.getUserDetails()));
                canRestoreCommunitiesMap.put(community.getId(), communitiesService.canRestoreCommunity(community, SecurityUtils.getUserDetails()));
                communitiesIds.add(community.getId());
            }
        }
        List<CommunityMember> communityMembers = communityDataService.getByCommunityIdsAndUserId(communitiesIds, SecurityUtils.getUser().getId());

        return CommunitiesListDto.toDto(communities, count, communityMembers, canDeleteCommunitiesMap, canRestoreCommunitiesMap);


		/*try {
			int firstResult = (page - 1) * perPage;
			if (!SecurityUtils.hasRole("ROLE_ADMIN")) {
				deleted = false;
			}

			Sharer sharer = radomRequestContext.getCurrentSharer();
			boolean isAdmin = SecurityUtils.hasRole(Roles.ROLE_ADMIN);

			List<CommunityEntity> communities = communityDao.getAccounts(sharer, isAdmin, Arrays.asList(status, CommunityMemberStatus.REQUEST_TO_LEAVE), creator, firstResult, perPage, query, accessType, communityType, activityScope, parent, checkParent, deleted, orderBy, asc);
			Long count = (StringUtils.hasLength(query) || creator != null || accessType != null) ? communityDao.getListCount(sharer, isAdmin, Arrays.asList(status, CommunityMemberStatus.REQUEST_TO_LEAVE), creator, query, accessType, communityType, activityScope, parent, checkParent, deleted) : null;

			List<CommunityDto> list = new ArrayList<>();
			for (CommunityEntity community : communities) {
				list.add(new CommunityDto(community, radomRequestContext.getCurrentSharer()));
			}
			StringObjectHashMap payload = new StringObjectHashMap();
			payload.put("list", list);
			payload.put("count", count);

			return serializationManager.serialize(payload).toString();
		} catch (CommunityException e) {
			logger.error(e.getMessage(), e);
			return JsonUtils.getErrorJson(e.getMessage()).toString();
		}*/
    }

    @RequestMapping(value = "/search.json", method = {RequestMethod.POST, RequestMethod.GET}, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommunitiesListDto search(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "per_page", defaultValue = "20") int perPage,
            @RequestParam(value = "query", defaultValue = "") String query,
            @RequestParam(value = "order_by", defaultValue = "name") String orderBy,
            @RequestParam(value = "asc", defaultValue = "true") boolean asc,
            @RequestParam(value = "access_type", required = false) CommunityAccessType accessType,
            @RequestParam(value = "community_type", required = false) String communityType,
            @RequestParam(value = "activity_scope_id", required = false) Long activityScopeId,
            @RequestParam(value = "deleted", required = false) Boolean deleted,
            @RequestParam(value = "check_parent", required = false, defaultValue = "true") boolean checkParent
    ) {
        int firstResult = (page - 1) * perPage;
        if (!SecurityUtils.getUserDetails().hasRole("ROLE_ADMIN")) {
            deleted = false;
        }

        boolean isAdmin = SecurityUtils.getUserDetails().hasRole(Roles.ROLE_ADMIN);
        List<Community> communities = communityDataService.getList(
                null, isAdmin, null, null,
                firstResult, perPage, query, accessType, communityType, activityScopeId, null,
                checkParent, deleted, orderBy, asc
        );
        long count = communityDataService.getListCount(
                null, isAdmin, null,
                null, query, accessType, communityType,
                activityScopeId, null, checkParent, deleted);

        Map<Long, Boolean> canDeleteCommunitiesMap = new HashMap<>();
        Map<Long, Boolean> canRestoreCommunitiesMap = new HashMap<>();
        List<Long> communitiesIds = new ArrayList<>();
        if (communities != null) {
            for (Community community : communities) {
                canDeleteCommunitiesMap.put(community.getId(), communitiesService.canDeleteCommunity(community, SecurityUtils.getUserDetails()));
                canRestoreCommunitiesMap.put(community.getId(), communitiesService.canRestoreCommunity(community, SecurityUtils.getUserDetails()));
                communitiesIds.add(community.getId());
            }
        }
        List<CommunityMember> communityMembers = communityDataService.getByCommunityIdsAndUserId(communitiesIds, SecurityUtils.getUser().getId());

        return CommunitiesListDto.toDto(communities, count, communityMembers, canDeleteCommunitiesMap, canRestoreCommunitiesMap);


		/*try {
			int firstResult = (page - 1) * perPage;

			boolean isAdmin = SecurityUtils.hasRole(Roles.ROLE_ADMIN);
			if (!isAdmin) {
				deleted = false;
			}

			List<CommunityEntity> communities = communityDao.getAccounts(null, isAdmin, null, null, firstResult, perPage, query, accessType, communityType, activityScopeId, null, checkParent, deleted, orderBy, asc);
			List<CommunityDto> list = new ArrayList<>();
			for (CommunityEntity community : communities) {
				list.add(new CommunityDto(community, radomRequestContext.getCurrentSharer()));
			}

			Long count = (StringUtils.hasLength(query)) ? communityDao.getListCount(null, isAdmin, null, null, query, accessType, communityType, activityScopeId, null, true, deleted) : null;

			StringObjectHashMap payload = new StringObjectHashMap();
			payload.put("list", list);
			payload.put("count", count);
			return serializationManager.serialize(payload).toString();
		} catch (CommunityException e) {
			logger.error(e.getMessage(), e);
			return JsonUtils.getErrorJson(e.getMessage()).toString();
		}*/
    }

    // TODO  Переделать
	/*@RequestMapping(value = "/members_page.json", method = RequestMethod.GET)
	public @ResponseBody String getMembersPage(@RequestParam(value = "community_id", defaultValue = "-1") CommunityEntity community, @RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "per_page", defaultValue = "6") int perPage) {
		List<CommunityMemberEntity> members = communityMemberDao.getPage(community, perPage, page);
		if (members.isEmpty()) {
			page = 1;
			members = communityMemberDao.getPage(community, perPage, page);
		}
		StringObjectHashMap payload = new StringObjectHashMap();
		payload.put("page", page);
		payload.put("pagesCount", communityMemberDao.getPagesCount(community, perPage));
		payload.put("members", members);
		payload.put("membersCount", community.getMembersCount());
		return serializationManager.serialize(payload).toString();
	}

	@RequestMapping(value = "/members_next_page.json", method = RequestMethod.GET)
	public @ResponseBody String getNextMembersPage(@RequestParam(value = "community_id", defaultValue = "-1") CommunityEntity community, @RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "per_page", defaultValue = "6") int perPage) {
		int nextPage = page + 1;
		List<CommunityMemberEntity> members = communityMemberDao.getPage(community, perPage, nextPage);
		if (members.isEmpty()) {
			nextPage = 1;
			members = communityMemberDao.getPage(community, perPage, nextPage);
		}
		StringObjectHashMap payload = new StringObjectHashMap();
		payload.put("page", nextPage);
		payload.put("pagesCount", communityMemberDao.getPagesCount(community, perPage));
		payload.put("members", members);
		payload.put("membersCount", community.getMembersCount());
		return serializationManager.serialize(payload).toString();
	}

	@RequestMapping(value = "/members_previous_page.json", method = RequestMethod.GET)
	public @ResponseBody String getPreviousMembersPage(@RequestParam(value = "community_id", defaultValue = "-1") CommunityEntity community, @RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "per_page", defaultValue = "6") int perPage) {
		int previousPage = page - 1;
		if (previousPage <= 0) {
			previousPage = communityMemberDao.getPagesCount(community, perPage);
		}
		List<CommunityMemberEntity> members = communityMemberDao.getPage(community, perPage, previousPage);
		StringObjectHashMap payload = new StringObjectHashMap();
		payload.put("page", previousPage);
		payload.put("pagesCount", communityMemberDao.getPagesCount(community, perPage));
		payload.put("members", members);
		payload.put("membersCount", community.getMembersCount());
		return serializationManager.serialize(payload).toString();
	}
*/
    // TODO Переделать
	/*@RequestMapping(value = "/filter.json", method = RequestMethod.GET)
	@ResponseBody
	public String filter(@RequestParam(value = "okveds[]") List<OkvedEntity> okveds) {
		final List<CommunityEntity> communities = communityDao.findByOkved(okveds);
		return serializationManager.serializeCollection(communities).toString();
	}*/


    /**
     * Список подгрупп объединения
     *
     * @param communityId
     * @param page
     * @param perPage
     * @return
     */
    @RequestMapping(value = "/get_children_hierarchy_list.json", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public CommunitiesListDto getChildrenHierarchyList(
            @RequestParam(value = "community_id", required = true) Long communityId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "per_page", defaultValue = "20") int perPage) {
        List<Community> communities = communityDataService.getByParentId(communityId, page - 1, perPage);
        List<Long> communitiesIds = new ArrayList<>();
        Map<Long, Boolean> canDeleteCommunitiesMap = new HashMap<>();
        Map<Long, Boolean> canRestoreCommunitiesMap = new HashMap<>();
        if (communities != null) {
            for (Community community : communities) {
                canDeleteCommunitiesMap.put(community.getId(), communitiesService.canDeleteCommunity(community, SecurityUtils.getUserDetails()));
                canRestoreCommunitiesMap.put(community.getId(), communitiesService.canRestoreCommunity(community, SecurityUtils.getUserDetails()));
            }
            communitiesIds = communities.stream().map(Community::getId).collect(Collectors.toList());
        }
        List<CommunityMember> communityMembers = communityDataService.getByCommunityIdsAndUserId(communitiesIds, SecurityUtils.getUser().getId());
        return CommunitiesListDto.toDto(communities, 0, communityMembers, canDeleteCommunitiesMap, canRestoreCommunitiesMap);
    }

    /**
     * Создать json ответ из параметров
     *
     * @param memberResponseDto
     * @return
     */
    private Object createMemberJsonFromDto(CommunityMemberResponseDto memberResponseDto) {
        Object result;

        if (memberResponseDto.getMember() != null) {
            Community community = communityDataService.getByIdFullData(memberResponseDto.getMember().getCommunity().getId());

            memberResponseDto.getParameters().put("member", new CommunityMemberDto(memberResponseDto.getMember()));

            memberResponseDto.getParameters().put("community",
                    new CommunityListItemDto(community,
                    memberResponseDto.getMember() != null ? memberResponseDto.getMember() : null,
                    communitiesService.canDeleteCommunity(community, SecurityUtils.getUserDetails()),
                    communitiesService.canRestoreCommunity(community, SecurityUtils.getUserDetails())));
        }

        result = memberResponseDto.getParameters();

        if (result == null) {
            result = SuccessResponseDto.get();
        }

        return result;
    }

    /**
     * Получить все объединения текущего пользователя которые могут стать членами объединения в котором находимся
     *
     * @param communityId
     * @param query
     * @param page
     * @return
     */
    @RequestMapping(value = "/getPossibleOrganizationsMembersToCommunity.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public PossibleOrganizationsMembersGridDto getPossibleOrganizationsMembersToCommunity(
            @RequestParam(value = "communityId", required = true) Long communityId,
            @RequestParam(value = "name", required = true) String query,
            @RequestParam(value = "page", defaultValue = "1") int page) {
        PossibleOrganizationsMembersGridDto result;
        try {
            int perPage = 25;
            List<Community> organizations = communityDataService.getPossibleCommunitiesMembers(SecurityUtils.getUser().getId(), communityId, query, (page - 1) * perPage, perPage);
            Long count = communityDataService.getPossibleCommunitiesMembersCount(SecurityUtils.getUser().getId(), communityId, query);

            List<OrganizationCommunityMember> findMembers = new ArrayList<>();
            for (Community organization : organizations) {
                OrganizationCommunityMember organizationCommunityMember = organizationMemberDomainService.getByCommunityIdAndOrganizationId(communityId, organization.getId());
                findMembers.add(organizationCommunityMember);
            }

            result = PossibleOrganizationsMembersGridDto.successDtoFromDomain(count.intValue(), organizations, findMembers);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = PossibleOrganizationsMembersGridDto.failDto();
        }
        return result;


    }

	/*private Map<String, Object> createResponseFromMap(Map<String, Object> parameters) {
		Map<String, Object> result = new HashMap<>();
		result.putAll(parameters);
		result.put("result", "success");
		return result;
	}*/

    /**
     * Создать запрос на вступление в объединение от объединения
     *
     * @param communityId
     * @param candidateCommunityId
     * @return
     */
    @RequestMapping(value = "/createRequestToOrganizationMember.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto createRequestToOrganizationMember(
            @RequestParam(value = "communityId", required = true) Long communityId,
            @RequestParam(value = "candidateCommunityId", required = true) Long candidateCommunityId) {
        Community community = communityDataService.getByIdFullData(communityId);
        Community candidateCommunity = communityDataService.getByIdFullData(candidateCommunityId);
        organizationCommunityMemberService.requestToJoinInCommunity(candidateCommunity, community, SecurityUtils.getUser());
        return SuccessResponseDto.get();
    }

    /**
     * Принять организацию в объединение
     *
     * @param memberId
     * @return
     */
    @RequestMapping(value = "/accept_organization_request.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto acceptJoinOrganizationMember(
            @RequestParam(value = "member_id", required = true) Long memberId) {
        organizationCommunityMemberService.acceptToJoinInCommunity(memberId, SecurityUtils.getUser());
        return SuccessResponseDto.get();
    }

    /**
     * Запрос на исключение организации из объединения от руководства организации
     *
     * @param memberId
     * @return
     */
    @RequestMapping(value = "/request_from_organization_to_exclude_organization_member.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto requestFromOrganizationToExcludeFromCommunity(
            @RequestParam(value = "member_id", required = true) Long memberId) {
        organizationCommunityMemberService.requestFromOrganizationToExcludeFromCommunity(memberId, SecurityUtils.getUser());
        return SuccessResponseDto.get();
    }
/*
	// Принять организацию в объединение
	@RequestMapping(value = "/accept_organization_request.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public Map<String, Object> acceptJoinOrganizationMember(
			@RequestParam(value = "member_id", required = true) Long memberId) {
		User user = radomRequestContext.getCurrentUser();
		return createResponseFromMap(organizationCommunityMemberService.acceptToJoinInCommunity(memberId, radomRequestContext.getCurrentUser()));
	}

	// Запрос на исключение организации из объединения от руководства объединения
	@RequestMapping(value = "/request_from_community_to_exclude_organization_member.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public Map<String, Object> requestFromCommunityOwnerToExcludeFromCommunity(
			@RequestParam(value = "member_id", required = true) Long memberId) {
		return createResponseFromMap(organizationCommunityMemberService.requestFromCommunityOwnerToExcludeFromCommunity(memberId, radomRequestContext.getCurrentUser()));
	}



	// Отказ в вступлении в объединение
	@RequestMapping(value = "/reject_organization_request.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public Map<String, Object> rejectRequestToJoinOrganizationMembers(
			@RequestParam(value = "member_id", required = true) Long memberId) {
		return createResponseFromMap(organizationCommunityMemberService.rejectRequest(memberId, radomRequestContext.getCurrentUser()));
	}

	// Отказ в вступлении в объединение
	@RequestMapping(value = "/reject_organization_requests.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public Map<String, Object> rejectRequestsToJoinOrganizationMembers(@RequestBody List<Long> memberIds) {
		return createResponseFromMap(organizationCommunityMemberService.rejectRequests(memberIds, radomRequestContext.getCurrentUser()));
	}

	// Отмена вступления в объединение
	@RequestMapping(value = "/cancel_organization_request.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public Map<String, Object> cancelRequestToJoinOrganizationMembers(
			@RequestParam(value = "member_id", required = true) Long memberId) {
		return createResponseFromMap(organizationCommunityMemberService.cancelRequest(memberId, radomRequestContext.getCurrentUser()));
	}

	// Отмена запроса на выход из объединения
	@RequestMapping(value = "/cancel_exclude_organization_request.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public Map<String, Object> cancelExcludeRequestOrganizationMember(
			@RequestParam(value = "member_id", required = true) Long memberId) {
		return createResponseFromMap(organizationCommunityMemberService.cancelExcludeRequest(memberId, radomRequestContext.getCurrentUser()));
	}*/

    // Получить данные по заполненности юр лица
    @RequestMapping(value = "/get_community_filling.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public CommunityFillingDto getCommunityFilling(@RequestParam(value = "community_id") Long communityId) {
        Community community = communityDataService.getByIdFullData(communityId);
        return communityFillingService.getCommunityFilling(community);
    }

    /**
     * Данные объединения для главной страницы
     *
     * @param communityId
     * @return
     */
    @RequestMapping(value = "/news_page_data.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public CommunityNewsPageDataDto getNewsPageData(@RequestParam(value = "community_id") Long communityId) {
        Community community = communityDataService.getByIdMediumData(
                communityId,
                Arrays.asList(
                        FieldConstants.COMMUNITY_DESCRIPTION,
                        FieldConstants.COMMUNITY_GEO_POSITION,
                        FieldConstants.COMMUNITY_GEO_LOCATION,
                        FieldConstants.COMMUNITY_LEGAL_GEO_POSITION,
                        FieldConstants.COMMUNITY_LEGAL_GEO_LOCATION
                )
        );
        //boolean isCurrentSharerMember = communityDomainService.isSharerMember(communityId, radomRequestContext.getCurrentSharer().getId());
        CommunityNewsPageDomain communityNewsPageDomain = communitiesService.getCommunityNewsPageDomain(communityId, SecurityUtils.getUserDetails());
        return CommunityNewsPageDataDto.toDto(CommunityNewsPageDto.toDto(community, communityNewsPageDomain.getCommunityMember()), communityNewsPageDomain);
    }

    /**
     * Данные объединения для страниц где загрузка всех данных не нужна
     *
     * @param communityId
     * @return
     */
    @RequestMapping(value = "/any_page_data.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public CommunityAnyPageDataDto getAnyPageData(@RequestParam(value = "community_id") Long communityId) {
        Community community = communityDataService.getByIdMediumData(
                communityId,
                Arrays.asList(
                        FieldConstants.COMMUNITY_DESCRIPTION,
                        FieldConstants.COMMUNITY_GEO_POSITION,
                        FieldConstants.COMMUNITY_GEO_LOCATION,
                        FieldConstants.COMMUNITY_LEGAL_GEO_POSITION,
                        FieldConstants.COMMUNITY_LEGAL_GEO_LOCATION
                )
        );
        CommunityMember selfMember = communityMemberDomainService.getByCommunityIdAndUserId(communityId, SecurityUtils.getUser().getId());
        return CommunityAnyPageDataDto.toDto(CommunityAnyPageDto.toDto(community, selfMember));
    }

    /**
     * Загрузка данных объединения для страницы просмотра подробной информации
     *
     * @param communityId
     * @return
     */
    @RequestMapping(value = "/info_page_data.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public CommunityInfoPageDataDto getInfoPageData(@RequestParam(value = "community_id") Long communityId) {
        CommunityMember selfMember = communityMemberDomainService.getByCommunityIdAndUserId(communityId, SecurityUtils.getUser().getId());
        Community community = communityDataService.getByIdFullData(communityId);
        return new CommunityInfoPageDataDto(community, selfMember);
    }

    /**
     * Получить данные списка участников по ИД участников
     *
     * @param participantIds
     * @return
     */
    @RequestMapping(value = "/participants_list.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public List<ParticipantsListDto> getParticipantsList(@RequestParam(value = "participant_ids[]") List<Long> participantIds) {
        return ParticipantsListDto.toListDto(userDataService.getByIds(participantIds));
    }

    /**
     * @param listItemIds
     * @return
     */
    @RequestMapping(value = "/universal_list.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public List<ListEditorItemDto> getUniversalListValue(@RequestParam(value = "list_item_ids[]") List<Long> listItemIds) {
        return ListEditorItemDto.toListDto(listEditorItemService.getByIds(listItemIds));
    }

    @RequestMapping(value = "/menu_data.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public CommunityMenuDto getMenu(@RequestParam(value = "community_id") Long communityId,
                                    @RequestParam(value = "community_type") ParticipantsTypes communityType) {
        List<CommunitySectionDomain> communitySections = communityDataService.getAllCommunitySections();
        List<Long> visibleIds = communitiesService.getVisibleSectionsForUser(communityId, SecurityUtils.getUser().getId(), communitySections);
        List<Account> accounts = null;
        BigDecimal communityBookAccountsBalance = null;
        SharebookEntity sharebook = null;
        boolean isConsumerSociety = false;

        if (ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.equals(communityType)) {
            CommunityMember communityMember = communityMemberDomainService.getByCommunityIdAndUserId(communityId, SecurityUtils.getUser().getId());

            if (communityMember != null) {
                accounts = communitiesService.getCommunityAccounts(communityId);
                communityBookAccountsBalance = sharebookService.getCommunitySharebooksTotalBalance(communityId);
                sharebook = accountService.getSharebook(SecurityUtils.getUser(), communityId);
            }

            isConsumerSociety = communityDataService.isConsumerSociety(communityId);
        }

        BigDecimal sharerBookAccountBalance = sharebook != null ? sharebook.getBalance() : null;
        return new CommunityMenuDto(communitySections, visibleIds, accounts, sharerBookAccountBalance, communityBookAccountsBalance, isConsumerSociety);
    }

    /**
     * Данные для страницы создания объединения
     *
     * @return
     */
    @RequestMapping(value = "/create_page_data.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public CreateCommunityPageDataDto getCreateCommunityData(@RequestParam(value = "with_organization", defaultValue = "true") boolean withOrganization) {
        List<FieldsGroup> fieldsGroups = fieldsService.getFieldGroupsCommunity(withOrganization);
        return new CreateCommunityPageDataDto(fieldsGroups);
    }
}
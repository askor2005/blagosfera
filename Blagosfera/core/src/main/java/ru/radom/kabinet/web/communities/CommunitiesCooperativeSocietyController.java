package ru.radom.kabinet.web.communities;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.askor.voting.business.services.BatchVotingService;
import ru.askor.voting.domain.BatchVoting;
import ru.radom.kabinet.module.rameralisteditor.service.ListEditorItemDomainService;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.communities.CommunityPermissionRequired;
import ru.radom.kabinet.security.context.RequestContext;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.communities.CooperativeFirstMeetingService;
import ru.radom.kabinet.services.communities.organizationmember.OrganizationCommunityMemberService;
import ru.radom.kabinet.services.communities.organizationmember.dto.ApproveOrganizationCommunityMembersDto;
import ru.radom.kabinet.services.communities.organizationmember.dto.LeaveOrganizationCommunityMembersDto;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.services.communities.sharermember.SharerCommunityMemberService;
import ru.radom.kabinet.services.communities.sharermember.dto.ApproveCommunityMembersDto;
import ru.radom.kabinet.services.communities.sharermember.dto.LeaveCommunityMembersDto;
import ru.radom.kabinet.utils.*;
import ru.radom.kabinet.voting.CooperativeFirstPlotBatchVoting;
import ru.radom.kabinet.web.communities.dto.ApproveNewMembersToPOResponseDto;
import ru.radom.kabinet.web.communities.dto.CommunityCreateKuchPageDataDto;
import ru.radom.kabinet.web.communities.dto.CreateKuchResponseDto;
import ru.radom.kabinet.web.communities.dto.LeaveMembersFromPOResponseDto;
import ru.radom.kabinet.web.voting.VotingPageController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 29.07.2015.
 */
@Controller
public class CommunitiesCooperativeSocietyController {

    /*@Autowired
    private RameraListEditorItemDAO rameraListEditorItemDAO;

    @Autowired
    private SectionDao sectionDao;

    @Autowired
    private CommunitySectionDao communitySectionDao;*/

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private BatchVotingService batchVotingService;

    @Autowired
    private CooperativeFirstMeetingService cooperativeFirstMeetingService;

    @Autowired
    private VotingPageController votingPageController;

    @Autowired
    private CooperativeFirstPlotBatchVoting cooperativePlotBatchVoting;

    /*@Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private FieldsGroupDao fieldsGroupDao;*/

    @Autowired
    private CommunityDataService communityDataService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Autowired
    private RequestContext radomRequestContext;

    @Autowired
    private SharerCommunityMemberService sharerCommunityMemberService;

    @Autowired
    private OrganizationCommunityMemberService organizationCommunityMemberService;

    @Autowired
    private ListEditorItemDomainService listEditorItemDomainService;

    @Autowired
    private CommunitiesService communitiesService;

    // Ссылка на страницу по созданию протокола на принятие пайщиков в ПО
    public static final String APPROVE_SHARERS_LINK = "/approve_sharers";

    // Ссылка на страницу по созданию протокола на выход пайщиков из ПО
    public static final String LEAVE_SHARERS_LINK = "/leave_sharers";

    @PostConstruct
    public void init() {
        batchVotingService.registerBatchVotingBehavior(CooperativeFirstPlotBatchVoting.NAME, cooperativePlotBatchVoting);
    }

    // Ссылка на создание КУч
    private static final String CREATE_MEETING_LINK = "/create_meeting";

    private static final String MIN_COUNT_PARTICIPANTS_FOR_CREATE_KUCH_SYS_SETTINGS = "kuch.min.count.participants";

    @RequestMapping(value = "/group/{seolink}" + CREATE_MEETING_LINK, method = RequestMethod.GET)
    public String createMeetingPage(@PathVariable("seolink") String seoLink, Model model) {
        /*model.addAttribute("currentPageTitle", "Создание Кооперативного участка");
        String result = null;
        // TODO Переделать
        CommunityEntity community = radomRequestContext.getCommunity();

        RameraListEditorItem rameraListEditorItem = rameraListEditorItemDAO.getByCode(CommunityEntity.COOPERATIVE_SOCIETY_LIST_ITEM_CODE);
        // Проверяем, что это потреб сообщество
        if (community.getRameraAssociationFormId() != null && community.getRameraAssociationFormId().longValue() == rameraListEditorItem.getId().longValue()) {
            result = "createMeeting";

            String seolink = community.getSeoLink();
            if (seolink == null || seolink.equals("")) {
                seolink = community.getId() + "";
            }

            // Считываем минимальное количество участников для создания КУЧ
            model.addAttribute("minCountParticipants", getMinCountParticipants());
            // Необходимые требования для создания КУЧ
            model.addAttribute("requirementsForCreateMeeting", getRequirementsForCreateMeeting(community));
            // Текущее время сервера
            model.addAttribute("nowDate", LocalDateTime.now().toDate().getTime());

            // Загружаем поля фактического адреса
            //COMMUNITY_WITH_ORGANIZATION_LEGAL_F_ADDRESS
            FieldsGroupEntity fieldsGroup = fieldsGroupDao.getByInternalName(COMMUNITY_WITH_ORGANIZATION_LEGAL_F_ADDRESS_FIELDS_GROUP_NAME);
            model.addAttribute("fieldsGroups", Arrays.asList(fieldsGroup));

            //
            model.addAttribute("votingTypes", CommonVotingService.VOTING_TYPES);

            CommunitySection communitySection = communitySectionDao.getByLink(CREATE_MEETING_LINK);

            Section section = sectionDao.getByName("ramera");
            model.addAttribute("currentSection", section);
            model.addAttribute("breadcrumb", new Breadcrumb()
                    .add(section.getTitle(), section.getLink())
                    .addItem(communitiesService.getBreadcrumbCommonItems())
                    .add(community.getName(), "/group/" + seolink)
                    .add(communitySection.getTitle(), "/group/" + seolink + CREATE_MEETING_LINK));

        } else {
            result = "error403";
        }*/
        return "createMeeting";
    }

    @RequestMapping(value = "/group/{seolink}/create_kuch_page_data.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public CommunityCreateKuchPageDataDto getCreateKuchPageData(@PathVariable("seolink") String seoLink) {
        CommunityCreateKuchPageDataDto result;
        boolean isMember = communityDataService.isSharerMember(radomRequestContext.getCommunityId(), SecurityUtils.getUser().getId());
        Community community = communityDataService.getByIdFullData(radomRequestContext.getCommunityId());
        boolean votersNeedBeVerified = settingsManager.getSystemSettingAsBool(SystemSettingsConstants.VOTING_VOTERS_NEED_BE_VERIFIED, false);
        List<String> requirements = getRequirementsForCreateMeeting(community);
        CommunityMember selfMember = communityMemberDomainService.getByCommunityIdAndUserId(radomRequestContext.getCommunityId(), SecurityUtils.getUser().getId());
        if (requirements != null && !requirements.isEmpty()) {
            result = new CommunityCreateKuchPageDataDto(requirements, community, selfMember);
        } else {
            List<CommunityMember> communityMembers = communityMemberDomainService.getByCommunityIdAndStatus(radomRequestContext.getCommunityId(), CommunityMemberStatus.MEMBER);
            result = new CommunityCreateKuchPageDataDto(getMinCountParticipants(), community, selfMember, communityMembers, votersNeedBeVerified);
        }
        return result;
    }

    /**
     * Минимальное количество участников для создания КУЧ
     * @return
     */
    private int getMinCountParticipants(){
        int result = 5;
        String resultStr = settingsManager.getSystemSetting(MIN_COUNT_PARTICIPANTS_FOR_CREATE_KUCH_SYS_SETTINGS, String.valueOf(result));
        return VarUtils.getInt(resultStr, result);
    }

    /**
     * Получить требования которые нужно выполнить, чтобы можно было создать КУЧ для текущего ПО
     * @return
     */
    private List<String> getRequirementsForCreateMeeting(Community community){
        List<String> result = new ArrayList<>();
        Long presidentOfSovietId = -1l;

        Field field = community.getCommunityData().getFieldByInternalName(FieldConstants.PRESIDENT_OF_SOVIET_ID_FIELD_NAME);
        if (field != null && field.getValue() != null && !field.getValue().equals("")) {
            presidentOfSovietId = VarUtils.getLong(field.getValue(), presidentOfSovietId);
        }
        if (presidentOfSovietId == -1l){
            result.add("Необходимо назначить Председателя Совета");
        }
        boolean isMembersOfSovietExists = false;
        field = community.getCommunityData().getFieldByInternalName(FieldConstants.MEMBERES_OF_SOVIET_ID_FIELD_NAME);
        if (field != null && field.getValue() != null && !field.getValue().equals("")) {
            isMembersOfSovietExists = true;
        }
        if (!isMembersOfSovietExists){
            result.add("Необходимо назначить Членов Совета");
        }
        Long presidentOfBoardId = -1l;
        field = community.getCommunityData().getFieldByInternalName(FieldConstants.PRESIDENT_OF_BOARD_ID_FIELD_NAME);
        if (field != null && field.getValue() != null && !field.getValue().equals("")) {
            presidentOfBoardId = VarUtils.getLong(field.getValue(), presidentOfBoardId);
        }
        if (presidentOfBoardId == -1l){
            result.add("Необходимо назначить Председателя Правления");
        }
        boolean communityIsCooperative = false;
        ListEditorItem listEditorItem = listEditorItemDomainService.getByCode(Community.COOPERATIVE_SOCIETY_LIST_ITEM_CODE);
        if (community.getAssociationForm() != null && community.getAssociationForm().getId() != null) {
            if (community.getAssociationForm().getId().equals(listEditorItem.getId())) {
                communityIsCooperative = true;
            }
        }
        if (!communityIsCooperative){
            result.add("Необходимо установить форму объединения сообщества - \"" + listEditorItem.getText() + "\"");
        }
        if (!communitiesService.hasPermission(community.getId(), SecurityUtils.getUser().getId(), "SUBGROUP_CREATE")) {
            result.add("У Вас нет прав на создание подгрупп в данном объединении");
        }
        return result;
    }

    /**
     * Метод создания собрания для создания КУЧ
     * @param requestBody тело запроса
     * @return отображение
     */
    @RequestMapping(value = "/group/{seolink}/save_meeting.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public CreateKuchResponseDto saveMeeting(@RequestBody String requestBody) {
        Community community = communityDataService.getByIdFullData(radomRequestContext.getCommunityId());
        List<String> requirements = getRequirementsForCreateMeeting(community);
        if (requirements != null && requirements.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Собрание для создания Кооперативного участка не создано. Необходимо выполнить следующие требования: ");
            sb.append(StringUtils.join(requirements, ", "));
            throw new RuntimeException(sb.toString());
        }

        // Наименование КУЧ
        String name = WebUtils.getValueOfParameter(requestBody, "name");
        // Цели КУЧ
        String meetingTargets = WebUtils.getValueOfParameter(requestBody, "meetingTargets");
        // Наименование темы собрания
        //String meetingTheme = WebUtils.getValueOfParameter(requestBody, "meetingTheme");
        // Дата начала собрания
        String dateStartValueStr = WebUtils.getValueOfParameter(requestBody, "dateStartValue");
        // Дата окончания собрания
        String dateEndValueStr = WebUtils.getValueOfParameter(requestBody, "dateEndValue");
        // Дата окончания регистрации в собрании
        String votersRegistrationEndDateStr = WebUtils.getValueOfParameter(requestBody, "votersRegistrationEndDate");
        // Участники собрания
        String votersStr = WebUtils.getValueOfParameter(requestBody, "voters");
        // Поля с аддресом кодированные urlencoding
        String addressFields = WebUtils.getValueOfParameter(requestBody, "addressFields");

        //
        String votingType = WebUtils.getValueOfParameter(requestBody, "votingType");

        BatchVoting batchVoting = cooperativeFirstMeetingService.createCooperativeMeeting(
                name, meetingTargets, dateStartValueStr, dateEndValueStr, votersRegistrationEndDateStr,
                votersStr, addressFields, votingType,
                community, SecurityUtils.getUser());


        return new CreateKuchResponseDto(batchVoting.getId());
    }

    //------------------------------------------------------------------------------------------------------------------
    // Методы для работы со страницей - приём пайщиков
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Страница с принятием кандидатов в пайщики в ПО.
     * @param seoLink
     * @return
     */
    @CommunityPermissionRequired("ROLE_APPROVE_SHARERS")
    @RequestMapping(value = "/group/{seolink}" + APPROVE_SHARERS_LINK, method = RequestMethod.GET)
    public String approveSharersToCooperativePage(@PathVariable("seolink") String seoLink) {
        return "approveSharersToCooperativePage";
    }

    /**
     * Загрузка данных кандидатов физ лиц для входа в ПО
     * @param seoLink
     * @return
     */
    @CommunityPermissionRequired("ROLE_APPROVE_SHARERS")
	@RequestMapping(value = "/group/{seolink}/get_sharers_candidates_to_join_in_community.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
	public ApproveCommunityMembersDto getSharersJoinCandidates(@PathVariable("seolink") String seoLink) {
		return sharerCommunityMemberService.getApproveCommunityMembers(radomRequestContext.getCommunityId(), SecurityUtils.getUser().getId());
	}

    /**
     * Загрузка данных кандидатов юр лиц для входа в ПО
     * @param seoLink
     * @return
     */
    @CommunityPermissionRequired("ROLE_APPROVE_SHARERS")
    @RequestMapping(value = "/group/{seolink}/get_organizations_candidates_join_in_community.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public ApproveOrganizationCommunityMembersDto getOrganizationsJoinCandidates(@PathVariable("seolink") String seoLink) {
        return organizationCommunityMemberService.getApproveCommunityMembers(radomRequestContext.getCommunityId(), SecurityUtils.getUser().getId());
    }

    /**
     * Принять несколько запросов на вступление от новых пайщиков
     * @param memberIds
     * @return
     */
    @CommunityPermissionRequired("ROLE_APPROVE_SHARERS")
	@RequestMapping(value = "/group/{seolink}/accept_requests.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
	public ApproveNewMembersToPOResponseDto acceptRequests(@PathVariable("seolink") String seoLink, @RequestBody List<Long> memberIds) {
        return new ApproveNewMembersToPOResponseDto(sharerCommunityMemberService.acceptRequests(memberIds, SecurityUtils.getUser().getId(), true));
	}

    /**
     * Принять несколько объединений в объединение
     * @param seoLink
     * @param memberIds
     * @return
     */
    @CommunityPermissionRequired("ROLE_APPROVE_SHARERS")
    @RequestMapping(value = "/group/{seolink}/accept_organization_requests.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public ApproveNewMembersToPOResponseDto acceptJoinOrganizationMembers(@PathVariable("seolink") String seoLink, @RequestBody List<Long> memberIds) {
        return new ApproveNewMembersToPOResponseDto(organizationCommunityMemberService.acceptToJoinInCommunity(memberIds, SecurityUtils.getUser().getId()));
    }

    //------------------------------------------------------------------------------------------------------------------
    // Методы для работы со страницей - вывод пайщиков
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Страница со списком участников, которые хотят выйти из ПО
     * @param seoLink
     * @return
     */
    @CommunityPermissionRequired("ROLE_APPROVE_SHARERS")
    @RequestMapping(value = "/group/{seolink}" + LEAVE_SHARERS_LINK, method = RequestMethod.GET)
    public String leaveSharersFromCooperativePage(@PathVariable("seolink") String seoLink) {
        return "leaveSharersFromCooperativePage";
    }

    /**
     * Загрузка данных кандидатов физ лиц для выхода из объединения
     * @param seoLink
     * @return
     */
    @CommunityPermissionRequired("ROLE_APPROVE_SHARERS")
	@RequestMapping(value = "/group/{seolink}/get_sharers_candidates_to_leave_from_community.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public LeaveCommunityMembersDto getSharersLeavesCandidates(@PathVariable("seolink") String seoLink) {
		return sharerCommunityMemberService.getLeaveCommunityMembers(radomRequestContext.getCommunityId(), SecurityUtils.getUser().getId());
	}

    /**
     * Загрузка данных кандидатов юр лиц для выхода из ПО
     * @param seoLink
     * @return
     */
    @CommunityPermissionRequired("ROLE_APPROVE_SHARERS")
    @RequestMapping(value = "/group/{seolink}/get_organizations_candidates_to_leave_from_community.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public LeaveOrganizationCommunityMembersDto getOrganizationsLeavesCandidates(@PathVariable("seolink") String seoLink) {
        return organizationCommunityMemberService.getLeaveCommunityMembers(radomRequestContext.getCommunityId(), SecurityUtils.getUser().getId());
    }

    /**
     * Подтверждение выхода участников из объединения
     * @param memberIds
     * @return
     */
    @CommunityPermissionRequired("ROLE_APPROVE_SHARERS")
	@RequestMapping(value = "/group/{seolink}/accept_exclude.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
	public LeaveMembersFromPOResponseDto acceptExclude(@PathVariable("seolink") String seoLink, @RequestBody List<Long> memberIds) {
		return new LeaveMembersFromPOResponseDto(sharerCommunityMemberService.acceptRequestsToExcludeFromCommunity(memberIds, SecurityUtils.getUser().getId(), true));
	}

    /**
     * Принять выход из объединения от нескольких организаций
     * @param memberIds
     * @return
     */
    @CommunityPermissionRequired("ROLE_APPROVE_SHARERS")
    @RequestMapping(value = "/group/{seolink}/accept_exclude_organization_requests.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public LeaveMembersFromPOResponseDto acceptExcludeOrganizationMembers(@PathVariable("seolink") String seoLink, @RequestBody List<Long> memberIds) {
        return new LeaveMembersFromPOResponseDto(organizationCommunityMemberService.acceptExcludeFromCommunity(memberIds, SecurityUtils.getUser().getId()));
    }

    //------------------------------------------------------------------------------------------------------------------
}
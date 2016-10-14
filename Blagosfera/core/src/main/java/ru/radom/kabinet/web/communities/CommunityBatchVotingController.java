package ru.radom.kabinet.web.communities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.data.jpa.repositories.RameraTextsRepository;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.business.services.BatchVotingService;
import ru.askor.voting.domain.BatchVoting;
import ru.askor.voting.domain.BatchVotingState;
import ru.askor.voting.domain.exception.VotingSystemException;
import ru.radom.kabinet.dto.CommonResponseDto;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.model.RameraTextEntity;
import ru.radom.kabinet.model.votingtemplate.BatchVotingTemplateEntity;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.communities.CommunityMembershipRequired;
import ru.radom.kabinet.security.communities.CommunityPermissionRequired;
import ru.radom.kabinet.security.context.RequestContext;
import ru.radom.kabinet.services.batchVoting.BatchVotingConstructorService;
import ru.radom.kabinet.services.batchVoting.CommunityBatchVotingTemplateService;
import ru.radom.kabinet.services.batchVoting.dto.*;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.communities.CommunitiesPermissions;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.*;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.voting.BatchVotingConstants;
import ru.radom.kabinet.web.communities.dto.*;
import ru.radom.kabinet.web.voting.VotingPageController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Контроллер для создания собрания в ПО
 * Created by vgusev on 09.10.2015.
 */
@Controller
public class CommunityBatchVotingController {

    @Autowired
    private BatchVotingConstructorService batchVotingConstructorService;

    @Autowired
    private CommunityBatchVotingTemplateService batchVotingTemplateService;

    @Autowired
    private BatchVotingService batchVotingService;

    @Autowired
    private CommunityDataService communityDomainService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private VotingPageController votingPageController;

    @Autowired
    private RequestContext radomRequestContext;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private RameraTextsRepository rameraTextsRepository;

    private static final String BATCH_VOTING_DESCRIPTION_ATTR_NAME = "description";

    private static final String COMMUNITY_WITH_ORGANIZATION_LEGAL_F_ADDRESS_FIELDS_GROUP_NAME = "COMMUNITY_WITH_ORGANIZATION_LEGAL_F_ADDRESS";

    private static final String CONSTRCTOR_DRAFT_SETTINGS_PREFFIX = "constructor.batch.voting.template";

    /**
     * Страница создания собрания
     *
     * @param model модель
     * @return представление модели
     */
    @CommunityPermissionRequired(CommunitiesPermissions.VOTINGS_ADMIN)
    @RequestMapping(value = "/group/{seolink}/batchVotingConstructor.html", method = RequestMethod.GET)
    public String getConstructorBatchVotingPage(Model model, @PathVariable("seolink") String seolink,
                                                @RequestParam(value = "templateId", required = false) Long templateId) {
        model.addAttribute("seoLink", seolink);
        return "constructorBatchVotingPage";
    }

    @CommunityPermissionRequired(CommunitiesPermissions.VOTINGS_ADMIN)
    @RequestMapping(value = "/group/{seolink}/constructor_batch_voting_page_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommunityConstructorBatchVotingPageDataDto getConstructorBatchVotingPageData(
            @PathVariable("seolink") String seolink,
            @RequestParam(value = "template_id", required = false) Long templateId) {
        Long communityId = radomRequestContext.getCommunityId();

        Community community = communityDomainService.getByIdMediumData(communityId,
                Arrays.asList(FieldConstants.COMMUNITY_DESCRIPTION,
                        FieldConstants.COMMUNITY_GEO_POSITION,
                        FieldConstants.COMMUNITY_GEO_LOCATION,
                        FieldConstants.COMMUNITY_LEGAL_GEO_POSITION,
                        FieldConstants.COMMUNITY_LEGAL_GEO_LOCATION));

        boolean votersNeedBeVerified = settingsManager.getSystemSettingAsBool(SystemSettingsConstants.VOTING_VOTERS_NEED_BE_VERIFIED, false);
        List<CommunityMember> communityMembers = communityMemberDomainService.getByCommunityIdAndStatus(communityId, CommunityMemberStatus.MEMBER);
        List<CommunityMember> communityMembersChildren = communityMemberDomainService.getByCommunityIdChildren(communityId, CommunityMemberStatus.MEMBER);
        BatchVotingTemplateDto batchVotingTemplateDto = null;

        if (templateId != null) {
            BatchVotingTemplateEntity batchVotingTemplate = batchVotingTemplateService.getById(templateId);

            if (batchVotingTemplate != null) {
                batchVotingTemplateDto = new BatchVotingTemplateDto(batchVotingTemplate);
            }
        } else {
            String settingsKey = CONSTRCTOR_DRAFT_SETTINGS_PREFFIX + communityId;
            batchVotingTemplateDto = settingsManager.getUserSettingAsObject(settingsKey, SecurityUtils.getUser().getId(), BatchVotingTemplateDto.class, null);
        }

        VotingTemplateHelpTextsSettingsDto votingTemplateHelpTextsSettings = settingsManager.getSystemSettingsAsObject(SystemSettingsConstants.VOTING_TEMPLATE_HELP_TEXTS, VotingTemplateHelpTextsSettingsDto.class, VotingTemplateHelpTextsSettingsDto.DEFAULT_INSTANCE);
        List<String> codes = Arrays.asList(
                votingTemplateHelpTextsSettings.getVotingSentenceHelpTextCode(),
                votingTemplateHelpTextsSettings.getVotingSuccessDecreeHelpTextCode(),
                votingTemplateHelpTextsSettings.getVotingFailDecreeHelpTextCode()
        );
        List<RameraTextEntity> rameraTextEntities = rameraTextsRepository.findByCodes(codes);

        String sentenceHelpText = null;
        String successDecreeHelpText = null;
        String failDecreeHelpText = null;
        for (RameraTextEntity rameraTextEntity : rameraTextEntities) {
            if (votingTemplateHelpTextsSettings.getVotingSentenceHelpTextCode().equals(rameraTextEntity.getCode())){
                sentenceHelpText = rameraTextEntity.getText();
            } else if (votingTemplateHelpTextsSettings.getVotingSuccessDecreeHelpTextCode().equals(rameraTextEntity.getCode())){
                successDecreeHelpText = rameraTextEntity.getText();
            } else if (votingTemplateHelpTextsSettings.getVotingFailDecreeHelpTextCode().equals(rameraTextEntity.getCode())){
                failDecreeHelpText = rameraTextEntity.getText();
            }
        }

        CommunityMember selfMember = communityMemberDomainService.getByCommunityIdAndUserId(communityId, SecurityUtils.getUser().getId());

        return new CommunityConstructorBatchVotingPageDataDto(
                CommunityAnyPageDto.toDto(community, selfMember), communityMembers, communityMembersChildren,
                SecurityUtils.getUser().getId(), batchVotingTemplateDto, votersNeedBeVerified, sentenceHelpText, successDecreeHelpText, failDecreeHelpText);
    }

    /**
     * Страница списка шаблонов собраний
     *
     * @param model модель
     * @return представление модели
     */
    @CommunityPermissionRequired(CommunitiesPermissions.VOTINGS_ADMIN)
    @RequestMapping(value = "/group/{seolink}/batchVotingTemplates.html", method = RequestMethod.GET)
    public String batchVotingTemplates(Model model, @PathVariable("seolink") String seolink) {
        return "batchVotingTemplatesPage";
    }

    private BatchVotingTemplateEntity saveBatchVotingTemplate(BatchVotingTemplateDto createBatchVotingDto) {
        BatchVotingTemplateEntity batchVotingTemplate = batchVotingTemplateService.getBatchVotingTemplate(createBatchVotingDto, radomRequestContext.getCommunityId());
        batchVotingTemplateService.save(batchVotingTemplate);
        return batchVotingTemplate;
    }

    /**
     * Запустить собрание без сохранения
     *
     * @param createBatchVotingDto
     * @return
     */
    @CommunityPermissionRequired(CommunitiesPermissions.VOTINGS_ADMIN)
    @RequestMapping(value = "/group/{seolink}/startBatchVoting.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public BatchVotingTemplateResponseDto startBatchVoting(@PathVariable("seolink") String seolink, @RequestBody BatchVotingTemplateDto createBatchVotingDto) {
        BatchVotingTemplateEntity batchVotingTemplate = batchVotingTemplateService.getBatchVotingTemplate(createBatchVotingDto, radomRequestContext.getCommunityId());
        BatchVoting batchVoting = batchVotingConstructorService.startBatchVoting(
                batchVotingTemplate, SecurityUtils.getUser().getId(), radomRequestContext.getCommunityId());
        BatchVotingTemplateResponseDto result = new BatchVotingTemplateResponseDto();
        result.setBatchVotingId(batchVoting.getId());
        return result;
    }

    /**
     * Запустить собрание из шаблона
     *
     * @param templateId
     * @return
     */
    @CommunityPermissionRequired(CommunitiesPermissions.VOTINGS_ADMIN)
    @RequestMapping(value = "/group/{seolink}/startBatchVotingFromTemplate.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public BatchVotingTemplateResponseDto startBatchVotingFromTemplate(@PathVariable("seolink") String seolink, @RequestParam(value = "template_id", required = true) Long templateId) {
        BatchVotingTemplateEntity batchVotingTemplate = batchVotingTemplateService.getById(templateId);
        BatchVoting batchVoting = batchVotingConstructorService.startBatchVoting(
                batchVotingTemplate, SecurityUtils.getUser().getId(), radomRequestContext.getCommunityId());
        BatchVotingTemplateResponseDto result = new BatchVotingTemplateResponseDto();
        result.setBatchVotingId(batchVoting.getId());
        return result;
    }

    /**
     * Сохранить собрание в шаблон и запустить
     *
     * @param seolink
     * @param createBatchVotingDto
     * @return
     */
    @CommunityPermissionRequired(CommunitiesPermissions.VOTINGS_ADMIN)
    @RequestMapping(value = "/group/{seolink}/saveAndStartBatchVotingTemplate.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public BatchVotingTemplateResponseDto saveAndStartBatchVotingTemplate(@PathVariable("seolink") String seolink, @RequestBody BatchVotingTemplateDto createBatchVotingDto) {
        BatchVotingTemplateEntity batchVotingTemplate = saveBatchVotingTemplate(createBatchVotingDto);
        BatchVoting batchVoting = batchVotingConstructorService.startBatchVoting(
                batchVotingTemplate, SecurityUtils.getUser().getId(), radomRequestContext.getCommunityId());
        BatchVotingTemplateResponseDto result = new BatchVotingTemplateResponseDto();
        result.setBatchVotingId(batchVoting.getId());
        result.setBatchVotingTemplateId(batchVotingTemplate.getId());
        return result;
    }

    /**
     * Сохранить собрание в шаблон
     *
     * @param seolink
     * @param createBatchVotingDto
     * @return
     */
    @CommunityPermissionRequired(CommunitiesPermissions.VOTINGS_ADMIN)
    @RequestMapping(value = "/group/{seolink}/saveBatchVotingTemplate.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public BatchVotingTemplateResponseDto saveBatchVotingTemplate(@PathVariable("seolink") String seolink, @RequestBody BatchVotingTemplateDto createBatchVotingDto) {
        BatchVotingTemplateEntity batchVotingTemplate = saveBatchVotingTemplate(createBatchVotingDto);
        BatchVotingTemplateResponseDto result = new BatchVotingTemplateResponseDto();
        result.setBatchVotingTemplateId(batchVotingTemplate.getId());
        return result;
    }

    /**
     * Удалить шаблон собрания
     *
     * @param seolink
     * @param templateId
     * @return
     */
    @CommunityPermissionRequired(CommunitiesPermissions.VOTINGS_ADMIN)
    @RequestMapping(value = "/group/{seolink}/deleteBatchVotingTemplate.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto deleteBatchVotingTemplate(@PathVariable("seolink") String seolink, @RequestParam(value = "template_id", required = true) Long templateId) {
        batchVotingTemplateService.deleteById(templateId);
        return SuccessResponseDto.get();
    }

    /**
     * Данные для грида
     *
     * @param seolink
     * @param subject
     * @param page
     * @return
     */
    @CommunityPermissionRequired(CommunitiesPermissions.VOTINGS_ADMIN)
    @RequestMapping(value = "/group/{seolink}/getBatchVotingTemplates.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public BatchVotingTemplatesGridDto getBatchVotingTemplates(@PathVariable("seolink") String seolink,
                                                               @RequestParam(value = "name", required = true) String subject,
                                                               @RequestParam(value = "page", defaultValue = "1") int page) {
        BatchVotingTemplatesGridDto result;

        try {
            int count = batchVotingTemplateService.getCountBySubject(subject, radomRequestContext.getCommunityId());
            List<BatchVotingTemplateEntity> batchVotingTemplates = batchVotingTemplateService.findBySubject(subject, page - 1, radomRequestContext.getCommunityId());
            List<BatchVotingTemplateDto> batchVotingTemplateDtos =
                    batchVotingTemplates.stream().map(batchVotingTemplate ->
                            new BatchVotingTemplateDto(batchVotingTemplate)).collect(Collectors.toList());
            result = BatchVotingTemplatesGridDto.successDtoFromDomain(count, batchVotingTemplateDtos);
        } catch (Exception e) {
            e.printStackTrace();
            result = BatchVotingTemplatesGridDto.failDto();
        }

        return result;
    }

    @CommunityMembershipRequired
    @RequestMapping(value = "/group/{seolink}/getBatchVotingList.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public BatchVotingsByTemplateGridDto getBatchVotingTemplates(@PathVariable("seolink") String seolink,
                                                                 @RequestParam(value = "templateId", required = true) Long templateId,
                                                                 @RequestParam(value = "page", defaultValue = "1") int page) {
        BatchVotingsByTemplateGridDto result;

        try {
            result = batchVotingTemplateService.getBatchVotingsByTemplate(templateId, page);
        } catch (Exception e) {
            e.printStackTrace();
            result = BatchVotingsByTemplateGridDto.failDto();
        }

        return result;
    }

    /**
     * Страница со списками собраний объединения
     *
     * @param seoLink
     * @param model
     * @return
     */
    @CommunityMembershipRequired
    @RequestMapping(value = "/group/{seolink}/batchvotings", method = RequestMethod.GET)
    public String communityBatchVotingsPage(@PathVariable("seolink") String seoLink, Model model) {
        return "communityBatchVotingsPage";
    }

    /**
     * Страница со списками голосований объединения
     *
     * @param seoLink
     * @param model
     * @return
     */
    @CommunityMembershipRequired
    @RequestMapping(value = "/group/{seolink}/votings", method = RequestMethod.GET)
    public String communityVotingsPage(@PathVariable("seolink") String seoLink, Model model) {
        return "communityVotingsPage";
    }

    @CommunityMembershipRequired
    @RequestMapping(value = "/group/{seolink}/batch_votings_page_grid_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public BatchVotingsGridDto getBatchVotingsPageData(
            @PathVariable("seolink") String seoLink,
            @RequestParam(value = "startDateStart", required = false) String startDateStartStr,
            @RequestParam(value = "startDateEnd", required = false) String startDateEndStr,
            @RequestParam(value = "endDateStart", required = false) String endDateStartStr,
            @RequestParam(value = "endDateEnd", required = false) String endDateEndStr,
            @RequestParam(value = "ownerId", required = false) Long ownerId,
            @RequestParam(value = "subject", required = false) String subject,
            @RequestParam(value = "state", required = false) BatchVotingState state,
            @RequestParam(value = "showMyBatchVotings", required = false) boolean showMyBatchVotings,
            @RequestParam(value = "page", defaultValue = "1") int page) {
        BatchVotingsGridDto result;

        try {
            Date startDateStart = DateUtils.parseDate(startDateStartStr, null);
            Date startDateEnd = DateUtils.parseDate(startDateEndStr, null);
            Date endDateStart = DateUtils.parseDate(endDateStartStr, null);
            Date endDateEnd = DateUtils.parseDate(endDateEndStr, null);
            page = page - 1;
            boolean votingsViewer = communitiesService.hasPermission(radomRequestContext.getCommunityId(),
                    SecurityUtils.getUser().getId(), CommunitiesPermissions.VOTINGS_VIEW);
            Long voterId;

            if (votingsViewer && showMyBatchVotings) {
                voterId = SecurityUtils.getUser().getId();
            } else if (votingsViewer) {
                voterId = null;
            } else {
                voterId = SecurityUtils.getUser().getId();
            }

            //SecurityUtils.getUserDetails().hasRole(Roles.ROLE_ADMIN);
            BatchVotingsPageResultDto batchVotingsPageResultDto = batchVotingTemplateService.filterBatchVotings(
                    ownerId, voterId, startDateStart, startDateEnd, endDateStart, endDateEnd, null, state, subject, page,
                    radomRequestContext.getCommunityId(), SecurityUtils.getUser().getId());

            List<Long> userIds = new ArrayList<>();

            if (batchVotingsPageResultDto.getBatchVotings() != null) {
                for (BatchVoting batchVoting : batchVotingsPageResultDto.getBatchVotings()) {
                    userIds.add(batchVoting.getOwnerId());
                }
            }

            List<User> users = null;

            if (!userIds.isEmpty()) {
                users = userDataService.getByIds(userIds);
            }

            result = BatchVotingsGridDto.successDtoFromDomain(batchVotingsPageResultDto, users);
        } catch (Exception e) {
            e.printStackTrace();
            result = BatchVotingsGridDto.failDto();
        }

        return result;
    }

    @CommunityMembershipRequired
    @RequestMapping(value = "/group/{seolink}/batch_voting_voters_grid_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public BatchVotingVotersGridDto getBatchVotingsPageData(@PathVariable("seolink") String seoLink,
                                                            @RequestParam(value = "batchVotingId", required = false) Long batchVotingId,
                                                            @RequestParam(value = "page", defaultValue = "1") int page) {
        BatchVotingVotersGridDto result;

        try {
            page = page - 1;
            BatchVotingVotersPageResultDto batchVotingVotersPageResultDto = batchVotingTemplateService.filterBatchVotingVoters(batchVotingId, null, null, page);
            result = BatchVotingVotersGridDto.successDto(batchVotingVotersPageResultDto);
        } catch (Exception e) {
            e.printStackTrace();
            result = BatchVotingVotersGridDto.failDto();
        }

        return result;
    }

    @CommunityMembershipRequired
    @RequestMapping(value = "/group/{seolink}/batch_votings_page_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommunityBatchVotingsPageDataDto getBatchVotingsPageData(@PathVariable("seolink") String seoLink) {
        boolean votingsViewer = communitiesService.hasPermission(
                radomRequestContext.getCommunityId(),
                SecurityUtils.getUser().getId(),
                CommunitiesPermissions.VOTINGS_VIEW);

        return new CommunityBatchVotingsPageDataDto(votingsViewer);
    }

    @CommunityMembershipRequired
    @RequestMapping(value = "/group/{seolink}/votings_page_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommunityVotingsPageDataDto getVotingsPageData(@PathVariable("seolink") String seoLink) {
        Long communityId = radomRequestContext.getCommunityId();
        User user = SecurityUtils.getUser();
        Map<String, String> parameters = new HashMap<>();
        parameters.put(BatchVotingConstants.COMMUNITY_ID_ATTR_NAME, String.valueOf(communityId));
        List<BatchVoting> batchVotings = getBatchVotings(user.getId(), parameters);
        setBatchVotingsSubject(batchVotings);
        Map<Long, User> owners = new HashMap<>();

        if (batchVotings != null) {
            for (BatchVoting batchVoting : batchVotings) {
                User owner = userDataService.getByIdMinData(batchVoting.getOwnerId());
                owners.put(batchVoting.getId(), owner);
            }
        }

        return new CommunityVotingsPageDataDto(batchVotings, owners, "/group/" + communityId + "/voting/");
    }

    private List<BatchVoting> getBatchVotings(Long userId, Map<String, String> parameters) {
        List<BatchVoting> result = null;

        try {
            result = batchVotingService.getBatchVotings(userId, parameters);
        } catch (VotingSystemException e) {
            ExceptionUtils.check(true, e.getMessage());
        }

        return result;
    }

    /**
     * Установить описание у собраний, на основе анализа доп параметров.
     *
     * @param batchVotings
     */
    private void setBatchVotingsSubject(List<BatchVoting> batchVotings) {
        for (BatchVoting batchVoting : batchVotings) {
            if (batchVoting.getAdditionalData().containsKey(BatchVotingConstants.COOPERATIVE_PLOT_NAME_ATTR_NAME)) {
                batchVoting.getAdditionalData().put(BATCH_VOTING_DESCRIPTION_ATTR_NAME, batchVoting.getSubject() + " " + "\"" + batchVoting.getAdditionalData().get(BatchVotingConstants.COOPERATIVE_PLOT_NAME_ATTR_NAME) + "\"");
            } else {
                batchVoting.getAdditionalData().put(BATCH_VOTING_DESCRIPTION_ATTR_NAME, batchVoting.getSubject());
            }
        }
    }

    /**
     * Страница со списками голосований объединения
     *
     * @param seoLink
     * @param model
     * @return
     */
    @CommunityMembershipRequired
    @RequestMapping(value = "/group/{seolink}/voting/{votingId}", method = RequestMethod.GET)
    public String communityVotingPage(Model model, @PathVariable("seolink") String seoLink, @PathVariable("votingId") String votingId) {
        BatchVoting batchVoting;

        try {
            Long votingIdLong = VarUtils.getLong(votingId, -1l);
            Long currentCommunityId = radomRequestContext.getCommunityId();

            // Ишем пачку голосований по еденичному голосованию
            batchVoting = batchVotingService.getBatchVotingByVotingId(votingIdLong, true, true);

            if (batchVoting == null) {
                throw new RuntimeException("Собрание не найдено");
            }

            String communityIdStr = batchVoting.getAdditionalData().get(BatchVotingConstants.COMMUNITY_ID_ATTR_NAME);
            Long communityId = VarUtils.getLong(communityIdStr, -1l);
            // Проверить, что собрание относится к группе

            if (!communityId.equals(currentCommunityId)) {
                throw new RuntimeException("В объединении нет такого собрания");
            }

            votingPageController.getVotingPage(model, votingIdLong);
            model.addAttribute("baseLink", "/group/" + seoLink + "/voting/");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("communityErrorMessage", e.getMessage());
        }

        return "communityVotingPage";
    }

    @CommunityPermissionRequired(CommunitiesPermissions.VOTINGS_ADMIN)
    @RequestMapping(value = "/group/{seolink}/save_batch_voting_template_draft.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto saveBatchVotingTemplateDraft(@PathVariable("seolink") String seoLink, @RequestBody BatchVotingTemplateDto batchVotingTemplateDto) {
        String settingsKey = CONSTRCTOR_DRAFT_SETTINGS_PREFFIX + radomRequestContext.getCommunityId();
        settingsManager.setUserSettingObject(settingsKey, batchVotingTemplateDto, SecurityUtils.getUser());
        return SuccessResponseDto.get();
    }
}

package ru.radom.kabinet.web.communities;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.core.services.account.AccountService;
import ru.askor.blagosfera.core.services.community.log.CommunityVisitLogService;
import ru.askor.blagosfera.core.services.invite.InvitationDataService;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.data.jpa.services.account.AccountDataService;
import ru.askor.blagosfera.data.jpa.services.settings.SystemSettingService;
import ru.askor.blagosfera.domain.account.Account;
import ru.askor.blagosfera.domain.account.Transaction;
import ru.askor.blagosfera.domain.account.TransactionDetail;
import ru.askor.blagosfera.domain.community.*;
import ru.askor.blagosfera.domain.community.schema.CommunitySchema;
import ru.askor.blagosfera.domain.community.schema.CommunitySchemaUnitType;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.document.DocumentParticipant;
import ru.askor.blagosfera.domain.document.DocumentTemplate;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateSetting;
import ru.askor.blagosfera.domain.document.templatesettings.dto.DocumentTemplateSettingDto;
import ru.radom.kabinet.collections.CommunityMemberStatusList;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.account.AccountTypeDao;
import ru.radom.kabinet.dao.applications.SharerApplicationDao;
import ru.radom.kabinet.dao.communities.*;
import ru.radom.kabinet.dao.communities.inventory.CommunityInventoryUnitTypeDao;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.dao.fields.FieldFileDao;
import ru.radom.kabinet.dao.fields.FieldsGroupDao;
import ru.radom.kabinet.dao.news.NewsDao;
import ru.radom.kabinet.dao.rameralisteditor.RameraListEditorItemDAO;
import ru.radom.kabinet.document.services.DocumentParticipantService;
import ru.radom.kabinet.document.services.DocumentTemplateDataService;
import ru.radom.kabinet.dto.CommonResponseDto;
import ru.radom.kabinet.dto.ErrorResponseDto;
import ru.radom.kabinet.dto.StringObjectHashMap;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.json.SerializationManager;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldStates;
import ru.radom.kabinet.model.fields.FieldValueEntity;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.communities.CommunityPermissionRequired;
import ru.radom.kabinet.security.context.RequestContext;
import ru.radom.kabinet.services.communities.*;
import ru.radom.kabinet.services.communities.log.CommunityLogEventDomainService;
import ru.radom.kabinet.services.communities.organizationmember.OrganizationMemberDomainService;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.services.discuss.DiscussionService;
import ru.radom.kabinet.services.document.DocumentTemplateSettingService;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.utils.*;
import ru.radom.kabinet.web.communities.dto.*;
import ru.radom.kabinet.web.utils.Breadcrumb;

import java.util.*;
import java.util.stream.Collectors;

@Controller("communitiesSeoController")
public class CommunitiesSeoController {
	private final static Logger logger = LoggerFactory.getLogger(CommunitiesSeoController.class);
	@Autowired
	private CommunityVisitLogService communityVisitLogService;
	@Autowired
	private SystemSettingService systemSettingService;
	@Autowired
	private SharerDao sharerDao;

	@Autowired
	private CommunityDao communityDao;

	@Autowired
	private CommunityTypeDao communityTypeDao;

	@Autowired
	private CommunityMemberDao communityMemberDao;

	@Autowired
	private SerializationManager serializationManager;

	@Autowired
	private DiscussionService discussionService;

	@Autowired
	private CommunitySchemaService communitySchemaService;
    @Autowired
	private InvitationDataService invitationDataService;
	@Autowired
	private NewsDao newsDao;

	@Autowired
	private CommunityPostDao communityPostDao;

	@Autowired
	private CommunityPermissionDao communityPermissionDao;

	@Autowired
	private CommunitiesService communitiesService;

	@Autowired
	private SharerApplicationDao sharerApplicationDao;

	@Autowired
	private CommunityInventoryUnitTypeDao communityInventoryUnitTypeDao;

	@Autowired
    private CommunityInventoryService communityInventoryService;

	@Autowired
	private FieldsGroupDao fieldsGroupDao;

	@Autowired
	private FieldsService fieldsService;

	@Autowired
	private RameraListEditorItemDAO rameraListEditorItemDAO;

	@Autowired
	private SettingsManager settingsManager;

	@Autowired
	private FieldDao fieldDao;

	@Autowired
	private AccountTypeDao accountTypeDao;

	@Autowired
	private AccountService accountService;

	@Autowired
	private FieldFileDao fieldFileDao;

	@Autowired
	private RequestContext radomRequestContext;

	@Autowired
	private CommunityDataService communityDataService;

	@Autowired
	private CommunityPostDomainService communityPostDomainService;

	@Autowired
	private CommunityPermissionDomainService communityPermissionDomainService;

	@Autowired
	private CommunitySchemaUnitDomainService communitySchemaUnitDomainService;

	@Autowired
	private CommunityInventoryDomainService communityInventoryDomainService;

	@Autowired
	private CommunityMemberDomainService communityMemberDomainService;

	@Autowired
	private OrganizationMemberDomainService organizationMemberDomainService;

	@Autowired
	private CommunityLogEventDomainService communityLogEventDomainService;

	@Autowired
	private AccountDataService accountDataService;

	@Autowired
	private DocumentTemplateDataService documentTemplateDataService;

	@Autowired
	private DocumentTemplateSettingService documentTemplateSettingService;

	@Autowired
	private CommunityDocumentRequestService communityDocumentRequestService;

	@Autowired
	private DocumentParticipantService documentParticipantService;

	@RequestMapping(value = "/groups", method = RequestMethod.GET)
	public String showCommunitiesPage(Model model) {
		int count = communityMemberDao.count(SecurityUtils.getUser().getId(), CommunityMemberStatus.MEMBER);
		model.addAttribute("count", count);
		model.addAttribute("countDeclension", StringUtils.getDeclension(count, "объединении", "объединениях", "объединениях"));

		model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Объединения с моим участием", "/groups"));

		return "communitiesListMember";
	}

	@RequestMapping(value = "/groups/creator", method = RequestMethod.GET)
	public String showCreatorCommunitiesPage(Model model) {
		int count = communityMemberDao.count(SecurityUtils.getUser().getId(), CommunityMemberStatus.MEMBER, true);
		model.addAttribute("count", count);
		model.addAttribute("countDeclension", StringUtils.getDeclension(count, "объединение", "объединения", "объединений"));

		model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Мои Объединения", "/groups/creator"));

		return "communitiesListCreator";
	}

	@RequestMapping(value = "/groups/my_requests", method = RequestMethod.GET)
	public String showMyRequestsPage(Model model) {
		//model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Мои запросы", "/group/my_requests"));
		return "communitiesListMyRequests";
	}

	@RequestMapping(value = "/groups/invites", method = RequestMethod.GET)
	public String showInvitesPage(Model model) {
		model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Приглашения", "/group/invites"));
		return "communitiesListInvites";
	}

	@RequestMapping(value = "/groups/all", method = RequestMethod.GET)
	public String showAllCommunitiesPage(Model model, @RequestParam(value = "activity_scope_id", required = false) RameraListEditorItem rameraActivityScope) {
		model.addAttribute("activityScope", rameraActivityScope);
		model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Объединения", "/groups/all"));
		return "communitiesAll";
	}

	@RequestMapping(value = "/groups/requests", method = RequestMethod.GET)
	public String showRequestsPage(Model model) {
		return "communitiesRequests";
	}

	/**
	 * Глааная страница объединения
	 * @param seoLink
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/group/{seolink}", method = RequestMethod.GET)
	public String showCommunityNewsPage(@PathVariable("seolink") String seoLink, Model model) {
		communityVisitLogService.createCommunityVisitLog(seoLink,SecurityUtils.getUser().getId());
		return "communityNews";
	}

	//------------------------------------------------------------------------------------------------------------------
	// Методы для страницы редактирования объединения
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * Страница редактирования объединения
	 * @param seoLink
	 * @return
	 */
	@CommunityPermissionRequired(CommunityPermissions.SETTINGS_COMMON)
	@RequestMapping(value = "/group/{seolink}/edit", method = RequestMethod.GET)
	public String showCommunityEditPage(@PathVariable("seolink") String seoLink) {
		return "communityEdit";
	}

	/**
	 * Загрузка данных объединения для страницы редактирования
	 * @param seoLink
	 * @return
	 */
	@CommunityPermissionRequired(CommunityPermissions.SETTINGS_COMMON)
	@RequestMapping(value = "/group/{seolink}/edit_page_data.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public CommunityEditPageDataDto getEditPageData(@PathVariable("seolink") String seoLink) {
		Long communityId = radomRequestContext.getCommunityId();
		CommunityMember selfMember = communityMemberDomainService.getByCommunityIdAndUserId(communityId, SecurityUtils.getUser().getId());
		Community community = communityDataService.getByIdFullData(communityId);
		return new CommunityEditPageDataDto(community, selfMember);
	}

	/**
	 * Сохранить данные объединения
	 * @param communityFullDataDto
	 * @return
	 */
	@CommunityPermissionRequired(CommunityPermissions.SETTINGS_COMMON)
	@RequestMapping(value = "/group/{seolink}/edit.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public CommonResponseDto edit(
			@PathVariable("seolink") String seoLink,
			@RequestBody CommunityFullDataDto communityFullDataDto) {
		CommonResponseDto result;
		try {
			Community community = communityFullDataDto.toDomain();
			communitiesService.editCommunity(community, SecurityUtils.getUser());
			result = SuccessResponseDto.get();
		} catch (CommunityException e) {
			if (e.getMap() != null) {
				result = new ErrorResponseDto(e.getMessage(), e.getMap());
			} else {
				result = new ErrorResponseDto(e.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = new ErrorResponseDto(e.getMessage());
		}
		return result;
	}

	//------------------------------------------------------------------------------------------------------------------

	/**
	 * Страница с подробной информацией об объединении
	 * @param seoLink
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/group/{seolink}/info", method = RequestMethod.GET)
	public String showCommunityInfoPage(@PathVariable("seolink") String seoLink, Model model) {
		return "communityInfo";
	}

	// инициализация адресных полей на странице информации юр лица
	private void initOfficeApartmentFields(String rentOfficeTypeName, FieldValueEntity value, String rentPeriodFieldName, Map<FieldEntity, FieldStates> fieldsStates) {
		Long listItemId = VarUtils.getLong(FieldsService.getFieldStringValue(value), -1l);
		RameraListEditorItem listItem = rameraListEditorItemDAO.getById(listItemId);
		String listItemCode = null;

		if (listItem != null) {
			value.setStringValue(listItem.getText());
			listItemCode = listItem.getMnemoCode();
		}
		FieldEntity officeRentPeriodField = fieldDao.getByInternalName(rentPeriodFieldName);
		if (rentOfficeTypeName.equals(listItemCode)) {
			fieldsStates.get(officeRentPeriodField).setVisible(true);
		} else {
			fieldsStates.get(officeRentPeriodField).setVisible(false);
		}
	}

	@CommunityPermissionRequired("NEWS_MODERATE")
	@RequestMapping(value = "/group/{seolink}/moderation/news", method = RequestMethod.GET)
	public String showCommunityNewsModerate(@PathVariable("seolink") String seoLink, Model model) {
		/*
		CommunityEntity community = radomRequestContext.getCommunity();
		model.addAttribute("communityMenuActiveItem", "moderation");
		model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").addItem(communitiesService.getBreadcrumbCommonItems()).add(community.getName(), community.getLink()).add("Модерация новостей", community.getLink() + "/moderation/news"));
		*/
		return "communityModerateNews";
	}

	//------------------------------------------------------------------------------------------------------------------
	// Методы для страниц создания объединения
	//------------------------------------------------------------------------------------------------------------------

	@RequestMapping(value = "/groups/create", method = RequestMethod.GET)
	public String showCreatePage(Model model) {
		// Если пользователь не сертифицирован, то ему нельзя создавать объединения
		/*if (!radomRequestContext.getCurrentSharer().isVerified()) {
			return "notVerifiedAccessToCreateCommunity";
		}*/

		//model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Мои Объединения", "/groups/creator").add("Создать", "/group/create"));
		return "communityCreate";
	}

	@RequestMapping(value = "/groups/create/with_organization_intro", method = RequestMethod.GET)
	public String showCreateWithoutOrganizationIntroPage(Model model) {
		return "communityCreateWithOrganizationIntro";
	}

	@RequestMapping(value = "/groups/create/without_organization", method = RequestMethod.GET)
	public String showCreateWithoutOrganizationPage(Model model) {
		return "communityCreateWithoutOrganization";
	}

	@RequestMapping(value = "/groups/create/with_organization", method = RequestMethod.GET)
	public String showCreateWithOrganizationPage(Model model) {
		// Если пользователь не сертифицирован, то ему нельзя создавать объединения
		if (!SecurityUtils.getUser().isVerified()) {
			return "notVerifiedAccessToCreateCommunity";
		}
		return "communityCreateWithOrganization";
	}

	/**
	 * Страница с созданием юр лица
	 * @return
	 */
	@RequestMapping(value = "/groups/create/organization", method = RequestMethod.GET)
	public String getCreateOrganizationPage() {
		return "createOrganization";
	}

	//------------------------------------------------------------------------------------------------------------------


	@RequestMapping(value = "/group/{seolink}/discussions/list")
	public String showGroupDiscussionList(@PathVariable("seolink") String seoLink, Model model) {
		/*
		CommunityEntity community = radomRequestContext.getCommunity();
		model.addAttribute("communityMenuActiveItem", "discussion");
		model.addAttribute("discussions", discussionService.getDiscussionsForScope(community));
		model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").addItem(communitiesService.getBreadcrumbCommonItems()).add(community.getName(), community.getLink()).add("Обсуждения", community.getLink() + "/discussions/list"));
		*/
		return "communityDiscussionsList";
	}

	@CommunityPermissionRequired("DISCUSSIONS_CREATE")
	@RequestMapping(value = "/group/{seolink}/discussions/create")
	public String createGroupDiscussionPage(@PathVariable("seolink") String seoLink, Model model) {
		/*
		CommunityEntity community = radomRequestContext.getCommunity();
		model.addAttribute("forCommunity", true);
		model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").addItem(communitiesService.getBreadcrumbCommonItems()).add(community.getName(), community.getLink()).add("Обсуждения", community.getLink() + "/discussions/list").add("Создать", community.getLink() + "/discussions/create"));

		final Map<String, String> sharers = new HashMap<>();
		for (CommunityMemberEntity communityMember : community.getMembers()) {
			Sharer sharer = communityMember.getUser();
			if (CommunityMemberStatus.MEMBER.equals(communityMember.getStatus())) {
				sharers.put(sharer.getId().toString(), sharer.getFullName());
			}
		}
		model.addAttribute("members", sharers);
		model.addAttribute("communityMenuActiveItem", "discussion");
		model.addAttribute("discussionForm", new DiscussionForm());
		*/

		return "communityDiscussionsCreate";
	}

	// TODO Переделать
	/*@CommunityPermissionRequired("DISCUSSIONS_CREATE")
	@RequestMapping(value = "/group/{seolink}/discussions/create", method = RequestMethod.POST)
	public String createGroupDiscussion(@PathVariable("seolink") String seoLink, @ModelAttribute DiscussionForm discussionForm, BindingResult bindingResult, Sharer sharer) {
		final CommunityEntity community = radomRequestContext.getCommunity();
		final Discussion discussion = discussionService.createDiscussion(discussionForm, sharer, community);
		return "redirect:/discuss/view/" + discussion.getId().toString();
	}*/


	@RequestMapping(value = "/communities/loadschema.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
	public Object loadCommunitySchema(@RequestParam("community_id") Long communityId) {
		try {
			communitiesService.checkPermission(communityId, SecurityUtils.getUser(), "SETTINGS_SCHEMA", "У Вас нет прав на редактирование структуры");
            Community community = communityDataService.getByIdFullData(communityId);
			StringObjectHashMap payload = new StringObjectHashMap();
			payload.put("schema", community.getSchema());
			payload.put("connectionTypes", communitySchemaService.getConnectionTypes());
			payload.put("fullName", community.getCreator().getFullName());
			return payload;
		} catch (CommunityException e) {
			return JsonUtils.getErrorJson(e.getMessage()).toString();
		} catch (Exception e) {
			logger.error("Failed to load community schema. Exception: {}", e.getMessage(), e);
			return JsonUtils.getErrorJson("Ошибка загрузки структуры").toString();
		}
	}

	@CommunityPermissionRequired("SETTINGS_SCHEMA")
	@RequestMapping(value = "/group/{seolink}/saveschema.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
	public CommonResponseDto create(@PathVariable("seolink") String seoLink, @RequestBody CommunitySchema schema) {
		Long communityId = radomRequestContext.getCommunityId();
		//communitiesService.checkPermission(communityId, SecurityUtils.getUser(), "SETTINGS_SCHEMA", "У Вас нет прав на редактирование структуры");
		//CommunitySchema schema = objectMapper.readValue(data, CommunitySchema.class);
		communitySchemaService.saveSchema(communityId, schema);
		return SuccessResponseDto.get();
	}

	// TODO Переделать
	/*@RequestMapping(value = "/communities/schema/possible_members.json", method = RequestMethod.POST)
	public @ResponseBody String possibleSchemaMembers(
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "per_page", defaultValue = "20") int perPage,
			@RequestParam(value = "query", defaultValue = "") String query,
			@RequestParam(value = "ikps", required = false) String ikps,
			@RequestParam(value = "ikpsOnly", required = false) Boolean ikpsOnly) {
		try {
			int firstResult = (page - 1) * perPage;
			int maxResults = perPage;
			List<String> ikpList = null;
			if (ikps != null)
				ikpList = new ObjectMapper().readValue(ikps, List.class);
			List<Sharer> sharers = sharerDao.search(query, firstResult, maxResults > 0 ? maxResults : Integer.MAX_VALUE, true, ikpList, ikpsOnly);
			return serializationManager.serializeCollection(sharers).toString();
		} catch (Exception e) {
			logger.error("Failed load sharers. Exception: {}", e.getMessage(), e);
			return JsonUtils.getErrorJson("Ошибка загрузки").toString();
		}
	}*/

	/*@CommunityPermissionRequired("SETTINGS_DOCUMENT_TEMPLATES")
	@RequestMapping(value = "/group/{seolink}/settings/document_templates", method = RequestMethod.GET)
	public String showDocumentTemplatesPage(@PathVariable("seolink") String seoLink, Model model) {
		CommunityEntity community = radomRequestContext.getCommunity();
		model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").addItem(getBreadcrumbCommonItems()).add(community.getName(), community.getLink()).add("Настройки", community.getLink() + "/settings").add("Шаблоны документов", community.getLink() + "/settings/document_templates"));
		model.addAttribute("templates", documentTemplateDao.getByScope(community));
		model.addAttribute("documentTemplateTypes", community.getAssociationForm().getDocumentTemplateTypes());
		return "communitySettingsDocumentTemplates";
	}*/

	@RequestMapping(value = "/group/{seolink}/conditions", method = RequestMethod.GET)
	public String showConditionsPage(@PathVariable("seolink") String seoLink, Model model) {
		return "communityConditions";
	}

	// TODO Переделать
	/*@RequestMapping(value = "/group/{seolink}/application/start/{id}", method = RequestMethod.GET)
	public String showApplicationStartPage(@PathVariable("seolink") String seoLink, @PathVariable("id") Application application, Model model, HttpServletResponse response) throws IOException {
		SharerApplication sharerApplication = sharerApplicationDao.get(radomRequestContext.getCurrentSharer(), application);
		if (sharerApplication == null || !sharerApplication.isInstalled()) {
			response.sendError(404);
			return null;
		} else {
			model.addAttribute("application", application);
			model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").addItem(communitiesService.getBreadcrumbCommonItems()).add(radomRequestContext.getCommunity().getName(), radomRequestContext.getCommunity().getLink()).add("Приложения", "#").add(application.getName(), radomRequestContext.getCommunity().getLink() + application.getStartLink()));
			return "communityApplicationStart";
		}
	}*/

	@RequestMapping(value = "/group/{seolink}/create_subgroup", method = RequestMethod.GET)
	public String showSubgroupCreatePage(@PathVariable("seolink") String seoLink, Model model) {
		return "communityCreateSubgroup";
	}

	// TODO Переделать

	@RequestMapping(value = "/group/{seolink}/subgroups", method = RequestMethod.GET)
	public String showSubgroupsPage(@PathVariable("seolink") String seoLink, Model model) {
		/*
		model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").addItem(communitiesService.getBreadcrumbCommonItems()).add(radomRequestContext.getCommunity().getName(), radomRequestContext.getCommunity().getLink()).add("Подгруппы", radomRequestContext.getCommunity().getLink() + "/subgroups"));
		List<StringObjectHashMap> children = new ArrayList<>();
		Map<CommunityEntity, Integer> map = communitiesService.getChildMap(radomRequestContext.getCommunity());
		for (CommunityEntity child : map.keySet()) {
			StringObjectHashMap item = new StringObjectHashMap();
			item.put("child", child);
			item.put("member", communityMemberDao.get(child, radomRequestContext.getCurrentSharer()));
			item.put("level", map.get(child));
			children.add(item);
		}
		model.addAttribute("children", serializationManager.serializeCollection(children));*/

		return "communitySubgroups";
	}

	@CommunityPermissionRequired(CommunityPermissions.SETTINGS_COMMON)
	@RequestMapping(value = "/group/{seolink}/account", method = RequestMethod.GET)
	public String showAccountPage(@PathVariable("seolink") String seoLink, Model model) {
		return "communityAccount";
	}

	/**
	 * Список с транзакциями объедиинения
	 * @param seoLink
	 * @param accountTypeId
	 * @param page
	 * @param perPage
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	@CommunityPermissionRequired(CommunityPermissions.SETTINGS_COMMON)
	@RequestMapping(value = "/group/{seolink}/transactions.json", method = RequestMethod.GET, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public List<CommunityTransactionDto> getTransactionsList(
			@PathVariable("seolink") String seoLink,
			@RequestParam(value = "account_type_id", required = false) Long accountTypeId,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "per_page", defaultValue = "20") int perPage,
			@RequestParam(value = "from_date", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date fromDate,
			@RequestParam(value = "to_date", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date toDate) {
		page = page - 1;
		List<Long> communityAccountIds = accountService.getCommunityAccountIds(radomRequestContext.getCommunityId());
		List<Transaction> transactions = accountService.getTransactions(communityAccountIds, page, perPage, fromDate, toDate, accountTypeId);
		List<Long> accountIds = new ArrayList<>();
		if (transactions != null) {
			for (Transaction transaction : transactions) {
				if (transaction.getDetails() != null) {
					for (TransactionDetail transactionDetail : transaction.getDetails()) {
						accountIds.add(transactionDetail.getAccountId());
					}
				}
			}
		}
		List<Account> accounts = accountDataService.getAccountsByIds(accountIds);

		return CommunityTransactionDto.toListDto(transactions, accounts, communityAccountIds);
	}

	/**
	 * Данные для страницы с транзакциями объединения
	 * @param seoLink
	 * @return
     */
	@CommunityPermissionRequired(CommunityPermissions.SETTINGS_COMMON)
	@RequestMapping(value = "/group/{seolink}/account_page_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public CommunityAccountPageDataDto getAccountPageData(@PathVariable("seolink") String seoLink) {
		Community community = communityDataService.getByIdMediumData(
				radomRequestContext.getCommunityId(),
				Arrays.asList(
						FieldConstants.COMMUNITY_DESCRIPTION,
						FieldConstants.COMMUNITY_GEO_POSITION,
						FieldConstants.COMMUNITY_GEO_LOCATION,
						FieldConstants.COMMUNITY_LEGAL_GEO_POSITION,
						FieldConstants.COMMUNITY_LEGAL_GEO_LOCATION
				)
		);
		List<Account> accounts = accountService.getCommunityAccounts(radomRequestContext.getCommunityId());
		CommunityMember selfMember = communityMemberDomainService.getByCommunityIdAndUserId(radomRequestContext.getCommunityId(), SecurityUtils.getUser().getId());
		return new CommunityAccountPageDataDto(
				CommunityAnyPageDto.toDto(community, selfMember),
				accounts
		);
	}
	
	@RequestMapping(value = "/group/{seolink}/deleted", method = RequestMethod.GET)
	public String showDeletedPage(@PathVariable("seolink") String seoLink, Model model) {
		//model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").addItem(communitiesService.getBreadcrumbCommonItems()).add(radomRequestContext.getCommunity().getName(), radomRequestContext.getCommunity().getLink()));
		return "communityDeleted";
	}


	
	/*@RequestMapping(value = "/communities/{id}", method = RequestMethod.GET)
	public String showCommunityPage(@PathVariable("id") CommunityEntity community) {
		return "redirect:" + community.getLink();
	}*/

	// TODO Переделать
	/*
	@RequestMapping(value = "/communities/fields_group.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	public @ResponseBody String getFieldsGroupByRameraListEditorItem(@RequestParam(value = "rameraListEditorItemId", defaultValue = "-1") RameraListEditorItem rameraListEditorItem) {
		try {
			Sharer sharer = radomRequestContext.getCurrentSharer();
			List<FieldsGroupEntity> fieldsGroups = fieldsGroupDao.getByRameraListEditorItem(rameraListEditorItem);

			//сортируем группы по их позиции
			Collections.sort(fieldsGroups, new Comparator<FieldsGroupEntity>() {
				public int compare(FieldsGroupEntity obj1, FieldsGroupEntity obj2) {
					return ComparisonChain.start().compare(obj1.getPosition(), obj2.getPosition()).result();
				}
			});

			JSONObject jsonObject = JsonUtils.getSuccessJson();
			JSONArray jsonArray = new JSONArray();
			for (FieldsGroupEntity fieldsGroup : fieldsGroups) {
				jsonArray.put(serializationManager.serialize(fieldsGroup));
			}
			jsonObject.put("groups", jsonArray);

			jsonArray = new JSONArray();
			Map<FieldEntity, FieldStates> fieldsStates = fieldsService.getFieldsStatesMap(fieldsGroups, sharer, sharer);

			//сортируем поля по их позиции
			List<FieldEntity> list = new ArrayList<>(fieldsStates.keySet());
			Collections.sort(list, new Comparator<FieldEntity>() {
				public int compare(FieldEntity obj1, FieldEntity obj2) {
					return ComparisonChain.start()
							.compare(obj1.getFieldsGroup().getPosition(), obj2.getFieldsGroup().getPosition())
							.compare(obj1.getPosition(), obj2.getPosition())
							.result();
				}
			});

			for (FieldEntity field : list) {
				jsonArray.put(serializationManager.serialize(field));
			}
			jsonObject.put("fields", jsonArray);
			return jsonObject.toString();
		} catch (Exception e) {
			return JsonUtils.getErrorJson(e.getMessage()).toString();
		}
	}*/

	/**
	 * Изменить видимость у поля объединения
	 * @param seoLink
	 * @param fieldId
	 * @param hidden
	 * @return
	 */
	@CommunityPermissionRequired(CommunityPermissions.SETTINGS_COMMON)
	@RequestMapping(value = "/group/{seolink}/setFieldVisible.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public CommonResponseDto setFieldValueHidden(
			@PathVariable("seolink") String seoLink,
			@RequestParam(value = "fieldId", required = true) Long fieldId,
			@RequestParam(value = "hidden", required = true) boolean hidden) {
		communityDataService.setFieldValueHidden(radomRequestContext.getCommunityId(), fieldId, hidden);
		return SuccessResponseDto.get();
	}

	/**
	 * Изменить видимость у группы полей объединения
	 * @param seoLink
	 * @param fieldsGroupId
	 * @param hidden
	 * @return
	 */
	@CommunityPermissionRequired(CommunityPermissions.SETTINGS_COMMON)
	@RequestMapping(value = "/group/{seolink}/setFieldGroupVisible.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public CommonResponseDto setFieldValuesGroupHidden(
			@PathVariable("seolink") String seoLink,
			@RequestParam(value = "fieldsGroupId", required = true) Long fieldsGroupId,
			@RequestParam(value = "hidden", required = true) boolean hidden) {
		communityDataService.setFieldValuesGroupHidden(radomRequestContext.getCommunityId(), fieldsGroupId, hidden);
		return SuccessResponseDto.get();
	}

	/**
	 * Сохранить прикреплённые файлы к полю объединения
	 * @param seoLink
	 * @param fieldId
	 * @param communityFieldFiles
	 * @return
	 */
	@RequestMapping(value = "/group/{seolink}/{fieldId}/saveFieldFiles.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
	@ResponseBody
	public CommonResponseDto saveFieldFiles(@PathVariable("seolink") String seoLink,
											   @PathVariable("fieldId") Long fieldId,
											   @RequestBody List<CommunityFieldFileDto> communityFieldFiles) {
		fieldsService.saveFieldFiles(fieldId, radomRequestContext.getCommunityId(), CommunityFieldFileDto.toDomainList(communityFieldFiles));
		return SuccessResponseDto.get();
	}

	/**
	 * Загрузить список файлов поля объединения
	 * @param seoLink
	 * @param fieldId
	 * @return
	 */
	@RequestMapping(value = "/group/{seolink}/{fieldId}/fieldFiles.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
	@ResponseBody
	public List<CommunityFieldFileDto> getFieldFiles(
			@PathVariable("seolink") String seoLink,
			@PathVariable("fieldId") Long fieldId) {
		// TODO Добавить проверку на доступ к данным
		return CommunityFieldFileDto.toDtoList(communityDataService.getCommunityFieldFiles(radomRequestContext.getCommunityId(), fieldId));
	}

	// возвращает список объединений только с именами и айдишниками(чтобы избежать утечки остальных данных)
	// TODO Переделать
	// TODO Нужен ли метод
	/*
	@RequestMapping("/communities.json")
	public @ResponseBody String getCommunities() {
		List<CommunityEntity> communities = communityDao.findAllOrderByIndexAsc();
		Collections.sort(communities, new Comparator<CommunityEntity>() {
			@Override
			public int compare(CommunityEntity o1, CommunityEntity o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		List<StringObjectHashMap> result = new ArrayList<>(communities.size());
		for(CommunityEntity community: communities) {
			StringObjectHashMap payload = new StringObjectHashMap();
			payload.put("id", community.getId().toString());
			payload.put("name", community.getName());
			result.add(payload);
		}
		return serializationManager.serializeCollection(result).toString();
	}*/

	// возвращает список мемберов объединения только с именами и айдишниками(чтобы избежать утечки остальных данных)
	// TODO Переделать
	/*@RequestMapping("/members.json")
	public @ResponseBody String getCommunityMembers(@RequestParam(value = "id", required = true) CommunityEntity community) {
		if(community == null) {
			return JsonUtils.getErrorJson("Необходимо указать существующее объединение").toString();
		}

		List<Sharer> sharers = new ArrayList<>(community.getMembers().size());
		for (CommunityMemberEntity communityMember : community.getMembers()) {
			Sharer sharer = communityMember.getUser();
			if (CommunityMemberStatus.MEMBER.equals(communityMember.getStatus())) {
				sharers.add(sharer);
			}
		}

		Collections.sort(sharers, new Comparator<Sharer>() {
			@Override
			public int compare(Sharer o1, Sharer o2) {
				return o1.getFullName().compareTo(o2.getFullName());
			}
		});

		List<StringObjectHashMap> result = new ArrayList<>(sharers.size());
		for(Sharer sharer: sharers) {
			StringObjectHashMap payload = new StringObjectHashMap();
			payload.put("id", sharer.getId().toString());
			payload.put("fullName", sharer.getFullName());
			result.add(payload);
		}

		return serializationManager.serializeCollection(result).toString();
	}*/

	// TODO Переделать
	/*@RequestMapping(value = "/group/{seolink}/files", method = RequestMethod.GET)
	public String showCommunityFilesPage(@PathVariable("seolink") String seoLink, Model model) {
		CommunityEntity community = radomRequestContext.getCommunity();
		model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/")
				.addItem(communitiesService.getBreadcrumbCommonItems())
					.add(community.getName(), community.getLink()).add("Файлы объединения", ""));
		return "communityFiles";
	}*/

	//------------------------------------------------------------------------------------------------------------------
	// Методы для работы на странице редактирования должностей
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * Страница с должностями объединения
	 * @param seoLink
	 * @return
	 */
	@CommunityPermissionRequired("SETTINGS_POSTS")
	@RequestMapping(value = "/group/{seolink}/settings/posts", method = RequestMethod.GET)
	public String showPostsSettingsPage(@PathVariable("seolink") String seoLink) {
		return "communitySettingsPosts";
	}

	@CommunityPermissionRequired("SETTINGS_POSTS")
	@RequestMapping(value = "/group/{seolink}/settings/posts/documentTemplates.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public List<DocumentTemplateForPostDto> getTemplatesForPost(@PathVariable("seolink") String seoLink, @RequestParam(value = "search_string", required = false) String searchString) {
		List<DocumentTemplate> list = documentTemplateDataService.getFilteredTemplate(searchString, null, 0, 10);
		return DocumentTemplateForPostDto.toListDto(list);
	}

	/**
	 * Данные для формирования страницы с должностями
	 * @param seoLink
	 * @return
	 */
	@CommunityPermissionRequired("SETTINGS_POSTS")
	@RequestMapping(value = "/group/{seolink}/posts_page_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public CommunityPostsPageDataDto getPostsPageData(@PathVariable("seolink") String seoLink) {
		List<CommunityPost> communityPosts = communityPostDomainService.getByCommunityId(
				radomRequestContext.getCommunityId(),
				false, false, false ,true
		);
		List<CommunityPermission> communityPermissions = communityPermissionDomainService.getByCommunityId(radomRequestContext.getCommunityId());
		return new CommunityPostsPageDataDto(communityPosts, communityPermissions);
	}

	/**
	 * Загрузить данные должности со списком прав
	 * @param seoLink
	 * @return
	 */
	@CommunityPermissionRequired("SETTINGS_POSTS")
	@RequestMapping(value = "/group/{seolink}/get_post.json", method = RequestMethod.GET, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public CommunityPostDto getPost(
			@PathVariable("seolink") String seoLink,
			@RequestParam(value = "post_id", required = true) Long postId
	) {
		return new CommunityPostDto(communityPostDomainService.getById(postId, false, false, true, true));
	}

	/**
	 * Загрузить подрозделения объединения
	 * @param seoLink
	 * @return
	 */
	@CommunityPermissionRequired("SETTINGS_POSTS")
	@RequestMapping(value = "/group/{seolink}/get_schema_units.json", method = RequestMethod.GET, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public List<CommunityPostPageSchemaUnitDto> getSchemaUnits(@PathVariable("seolink") String seoLink) {
		return CommunityPostPageSchemaUnitDto.toDtoList(
				communitySchemaUnitDomainService.getByCommunityId(radomRequestContext.getCommunityId(), CommunitySchemaUnitType.DEPARTMENT)
		);
	}

	/**
	 * Скопировать должность в объединении
	 * @param seoLink
	 * @param postId
	 * @return
	 */
	@CommunityPermissionRequired("SETTINGS_POSTS")
	@RequestMapping(value = "/group/{seolink}/copy_post.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public CommunityPostDto copyPost(
			@PathVariable("seolink") String seoLink,
			@RequestParam(value = "post_id", required = true) Long postId) {
		return new CommunityPostDto(communitiesService.copyPost(SecurityUtils.getUser(), postId, radomRequestContext.getCommunityId()));
	}

	/**
	 * Удалить должность в объединении
	 * @param postId
	 * @return
	 */
	@CommunityPermissionRequired("SETTINGS_POSTS")
	@RequestMapping(value = "/group/{seolink}/delete_post.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public CommunityPostDto deletePost(
			@PathVariable("seolink") String seoLink,
			@RequestParam(value = "post_id", required = true) Long postId) {
		return new CommunityPostDto(communitiesService.deletePost(SecurityUtils.getUser(), postId, radomRequestContext.getCommunityId()));
	}

	@CommunityPermissionRequired("SETTINGS_POSTS")
	@RequestMapping(value = "/group/{seolink}/save_post.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public CommunityPostDto savePost(@PathVariable("seolink") String seoLink, @RequestBody CommunityPostServiceDto communityPostServiceDto) {
		return new CommunityPostDto(
				communitiesService.savePost(SecurityUtils.getUser(), communityPostServiceDto.toDomain(), radomRequestContext.getCommunityId())
		);
	}

	//------------------------------------------------------------------------------------------------------------------

	//------------------------------------------------------------------------------------------------------------------
	// Методы для работы со страницей "Инвентаризация"
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * Контроллер страницы "Инвентаризация"
	 * @param seoLink
	 * @return
	 */
	@CommunityPermissionRequired("SETTINGS_INVENTORY")
	@RequestMapping(value = "/group/{seolink}/inventory", method = RequestMethod.GET)
	public String showInventoryPage(@PathVariable("seolink") String seoLink) {
		return "communityInventory";
	}

	/**
	 * Данные для страницы "инвенторизация"
	 * @param seoLink
	 * @return
	 */
	@CommunityPermissionRequired("SETTINGS_INVENTORY")
	@RequestMapping(value = "/group/{seolink}/inventory_page_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public CommunityInventoryPageDataDto getInventoryPageData(@PathVariable("seolink") String seoLink) {
		Community community = communityDataService.getByIdMediumData(
				radomRequestContext.getCommunityId(),
				Arrays.asList(
						FieldConstants.COMMUNITY_DESCRIPTION,
						FieldConstants.COMMUNITY_GEO_POSITION,
						FieldConstants.COMMUNITY_GEO_LOCATION,
						FieldConstants.COMMUNITY_LEGAL_GEO_POSITION,
						FieldConstants.COMMUNITY_LEGAL_GEO_LOCATION
				)
		);
		CommunityMember selfMember = communityMemberDomainService.getByCommunityIdAndUserId(radomRequestContext.getCommunityId(), SecurityUtils.getUser().getId());
		return new CommunityInventoryPageDataDto(
				CommunityInventoryUnit.DEFAULT_PHOTO,
				communityInventoryDomainService.getAllTypes(),
				community,
				selfMember
		);
	}

	/**
	 * Получить список передаваемых предметов
	 * @param query
	 * @param communityInventoryUnitTypeId
	 * @return
	 */
	@CommunityPermissionRequired("SETTINGS_INVENTORY")
	@RequestMapping(value = "/group/{seolink}/inventory/list.json", method = RequestMethod.GET, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public List<CommunityInventoryUnitDto> getInventoryUnitsList(
			@PathVariable("seolink") String seoLink,
			@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "type_id", required = false) Long communityInventoryUnitTypeId
	) {
		return CommunityInventoryUnitDto.toDtoList(
				communityInventoryDomainService.getList(radomRequestContext.getCommunityId(), communityInventoryUnitTypeId, query)
		);
	}

	/**
	 * Загрузка предмета
	 * @param id
	 * @return
	 */
	@CommunityPermissionRequired("SETTINGS_INVENTORY")
	@RequestMapping(value = "/group/{seolink}/inventory/get.json", method = RequestMethod.GET, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public CommunityInventoryUnitDto getInventoryUnit(
			@PathVariable("seolink") String seoLink,
			@RequestParam("unit_id") Long id
	) {
		return new CommunityInventoryUnitDto(communityInventoryDomainService.getById(id));
	}

	/**
	 * Удаление предмета
	 * @param seoLink
	 * @param unitId
	 * @return
	 */
	@CommunityPermissionRequired("SETTINGS_INVENTORY")
	@RequestMapping(value = "/group/{seolink}/inventory/delete.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public CommunityInventoryUnitDto deleteInventoryUnit(
			@PathVariable("seolink") String seoLink,
			@RequestParam("unit_id") Long unitId) {
		return new CommunityInventoryUnitDto(
				communityInventoryService.deleteUnit(unitId, radomRequestContext.getCommunityId())
		);
	}

	/**
	 * Сохранить предмет
	 * @param seoLink
	 * @param communityInventoryUnitSaveDto
	 * @return
	 */
	@CommunityPermissionRequired("SETTINGS_INVENTORY")
	@RequestMapping(value = "/group/{seolink}/inventory/save.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public CommunityInventoryUnitDto saveInventoryUnit(
			@PathVariable("seolink") String seoLink,
			@RequestBody CommunityInventoryUnitSaveDto communityInventoryUnitSaveDto
	) {
		return new CommunityInventoryUnitDto(
				communityInventoryService.saveUnit(communityInventoryUnitSaveDto.toDomain(), radomRequestContext.getCommunityId())
		);
	}

	//------------------------------------------------------------------------------------------------------------------

	//------------------------------------------------------------------------------------------------------------------
	// Страница со списком участников объединения
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * Страница участников объединения
	 * @param seoLink
	 * @return
	 */
	@RequestMapping(value = "/group/{seolink}/members", method = RequestMethod.GET)
	public String showCommunityMembersPage(@PathVariable("seolink") String seoLink) {
		return "communityMembers";
	}

	/**
	 * Данные для страницы пользователей объединения
	 * @param seoLink
	 * @return
	 */
	@RequestMapping(value = "/group/{seolink}/members_page_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public CommunityMembersPageDataDto getMembersPageDataDto(@PathVariable("seolink") String seoLink) {
		Community community = communityDataService.getByIdMediumData(
				radomRequestContext.getCommunityId(),
				Arrays.asList(
						FieldConstants.COMMUNITY_DESCRIPTION,
						FieldConstants.COMMUNITY_GEO_POSITION,
						FieldConstants.COMMUNITY_GEO_LOCATION,
						FieldConstants.COMMUNITY_LEGAL_GEO_POSITION,
						FieldConstants.COMMUNITY_LEGAL_GEO_LOCATION
				)
		);
		boolean hasRightInvites = communitiesService.hasPermission(radomRequestContext.getCommunityId(), SecurityUtils.getUser().getId(), "INVITES");
		boolean hasRightRequests = communitiesService.hasPermission(radomRequestContext.getCommunityId(), SecurityUtils.getUser().getId(), "REQUESTS");
		boolean hasRightExclude = communitiesService.hasPermission(radomRequestContext.getCommunityId(), SecurityUtils.getUser().getId(), "EXCLUDE");
		boolean isCreator = community.getCreator().getId().equals(SecurityUtils.getUser().getId());

		int membersCount = communityMemberDomainService.getMembersCount(
				radomRequestContext.getCommunityId(),
				Arrays.asList(
						CommunityMemberStatus.MEMBER,
						CommunityMemberStatus.REQUEST_TO_LEAVE,
						CommunityMemberStatus.LEAVE_IN_PROCESS
				)
		);

		int organizationMembersCount = organizationMemberDomainService.getMembersCount(radomRequestContext.getCommunityId(), Arrays.asList(
				CommunityMemberStatus.MEMBER,
				CommunityMemberStatus.REQUEST_TO_LEAVE,
				CommunityMemberStatus.LEAVE_IN_PROCESS
		));

		CommunityMember selfMember = communityMemberDomainService.getByCommunityIdAndUserId(radomRequestContext.getCommunityId(), SecurityUtils.getUser().getId());
		return new CommunityMembersPageDataDto(
				community,
				selfMember,
				membersCount,
				organizationMembersCount,
				hasRightInvites,
				hasRightRequests,
				hasRightExclude,
				isCreator
		);
	}
	//------------------------------------------------------------------------------------------------------------------

	//------------------------------------------------------------------------------------------------------------------
	// Поиск участников объединения
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * Поиск участников объединения
	 * @param seoLink
	 * @param query
	 * @param includeContextUser
	 * @param page
	 * @param perPage
	 * @return
	 */
	// TODO Нужно сделать проверку доступа к объединению - aop с проверкой текущиго пользователя что он участник
	@RequestMapping(value = "/group/{seolink}/search_members.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public List<CommunityUserMemberDataDto> searchCommunityMembers(
			@PathVariable("seolink") String seoLink,
			@RequestParam(value = "query", defaultValue = "") String query,
			@RequestParam(value = "include_context_user", defaultValue = "false") boolean includeContextUser,
			@RequestParam(value = "excluded_user_ids[]", required = false) List<Long> excludedUserIds,
			@RequestParam(value = "page", defaultValue = "1", required = false) int page,
			@RequestParam(value = "per_page", defaultValue = "20", required = false) int perPage) {
		excludedUserIds = excludedUserIds == null ? new ArrayList<>() : excludedUserIds;
		CommunityMemberStatusList statusList = new CommunityMemberStatusList();
		statusList.addAll(
				Arrays.asList(
						CommunityMemberStatus.MEMBER,
						CommunityMemberStatus.REQUEST_TO_LEAVE,
						CommunityMemberStatus.LEAVE_IN_PROCESS
				)
		);
		int firstResult = (page - 1) * perPage;
		int maxResults = perPage;
		if (!includeContextUser) {
			excludedUserIds.add(SecurityUtils.getUser().getId());
		}
		return CommunityUserMemberDataDto.toDtoList(
			communityMemberDomainService.getList(radomRequestContext.getCommunityId(), statusList, firstResult, maxResults, query, excludedUserIds)
		);
	}


	/**
	 * Поиск участников объединения - юр лиц
	 * @param seoLink
	 * @param page
	 * @param perPage
	 * @param query
	 * @return
	 */
	// TODO какие права должны быть для того чтобы получить данные по участникам юр лицам?
	@RequestMapping(value = "/group/{seolink}/search_organization_members.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public List<CommunityOrganizationMemberDataDto> organizationMembers(
			@PathVariable("seolink") String seoLink,
			@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "per_page", defaultValue = "20") int perPage) {
		int firstResult = (page - 1) * perPage;
		int maxResults = perPage;
		List<OrganizationCommunityMember> members =
		organizationMemberDomainService.find(
				radomRequestContext.getCommunityId(),
				Arrays.asList(
						CommunityMemberStatus.MEMBER,
						CommunityMemberStatus.REQUEST_TO_LEAVE,
						CommunityMemberStatus.LEAVE_IN_PROCESS
				),
				query,
				firstResult,
				maxResults
		);
		return CommunityOrganizationMemberDataDto.toDtoList(members);
	}
	//------------------------------------------------------------------------------------------------------------------

	//------------------------------------------------------------------------------------------------------------------
	// Отправленные приглашения
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * Страница с приглашениями
	 * @param seoLink
	 * @return
	 */
	@CommunityPermissionRequired("INVITES")
	@RequestMapping(value = "/group/{seolink}/requests/outgoing", method = RequestMethod.GET)
	public String showCommunityOutgoingRequests(@PathVariable("seolink") String seoLink) {
		return "communityOutgoingRequests";
	}

	/**
	 * Поиск участников на странице приглашений
	 * @param seoLink
	 * @param query
	 * @param page
	 * @param perPage
	 * @return
	 */
	@RequestMapping(value = "/group/{seolink}/search_invite_members.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	@CommunityPermissionRequired("INVITES")
	public List<CommunityUserMemberDataDto> searchInviteCommunityMembers(
			@PathVariable("seolink") String seoLink,
			@RequestParam(value = "query", defaultValue = "") String query,
			@RequestParam(value = "page", defaultValue = "1", required = false) int page,
			@RequestParam(value = "per_page", defaultValue = "20", required = false) int perPage) {

		CommunityMemberStatusList statusList = new CommunityMemberStatusList();
		statusList.addAll(
				Arrays.asList(
						CommunityMemberStatus.INVITE
				)
		);
		int firstResult = (page - 1) * perPage;
		int maxResults = perPage;

		return CommunityUserMemberDataDto.toDtoList(
				communityMemberDomainService.getList(radomRequestContext.getCommunityId(), statusList, firstResult, maxResults, query, null)
		);
	}
	//------------------------------------------------------------------------------------------------------------------

	//------------------------------------------------------------------------------------------------------------------
	// Методы для работы со страницей
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * Страница с запросами на вступление в объединение
	 * @param seoLink
	 * @return
	 */
	@CommunityPermissionRequired("REQUESTS")
	@RequestMapping(value = "/group/{seolink}/requests/incoming", method = RequestMethod.GET)
	public String showCommunityIncomingRequests(@PathVariable("seolink") String seoLink) {
		return "communityIncomingRequests";
	}

	/**
	 * Поиск участников объединения со статусом REQUEST
	 * @param seoLink
	 * @param query
	 * @param page
	 * @param perPage
	 * @return
	 */
	@RequestMapping(value = "/group/{seolink}/search_request_members.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	@CommunityPermissionRequired("REQUESTS")
	public List<CommunityUserMemberDataDto> searchRequestCommunityMembers(
			@PathVariable("seolink") String seoLink,
			@RequestParam(value = "query", defaultValue = "") String query,
			@RequestParam(value = "page", defaultValue = "1", required = false) int page,
			@RequestParam(value = "per_page", defaultValue = "20", required = false) int perPage) {

		CommunityMemberStatusList statusList = new CommunityMemberStatusList();
		statusList.addAll(
				Arrays.asList(
						CommunityMemberStatus.REQUEST
				)
		);
		int firstResult = (page - 1) * perPage;
		int maxResults = perPage;

		return CommunityUserMemberDataDto.toDtoList(
				communityMemberDomainService.getList(radomRequestContext.getCommunityId(), statusList, firstResult, maxResults, query, null)
		);
	}

	/**
	 * Поиск участников - организаций со статусом REQUEST
	 * @param seoLink
	 * @param query
	 * @param page
	 * @param perPage
	 * @return
	 */
	@RequestMapping(value = "/group/{seolink}/search_request_organization_members.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	@CommunityPermissionRequired("REQUESTS")
	public List<CommunityOrganizationMemberDataDto> searchRequestOrganizationMembers(
			@PathVariable("seolink") String seoLink,
			@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "per_page", defaultValue = "20") int perPage) {
		int firstResult = (page - 1) * perPage;
		int maxResults = perPage;
		List<OrganizationCommunityMember> members =
				organizationMemberDomainService.find(
						radomRequestContext.getCommunityId(),
						Arrays.asList(
								CommunityMemberStatus.REQUEST
						),
						query,
						firstResult,
						maxResults
				);
		return CommunityOrganizationMemberDataDto.toDtoList(members);
	}

	//------------------------------------------------------------------------------------------------------------------

	//------------------------------------------------------------------------------------------------------------------
	// Пригласить участников
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * Страница приглашения участников
	 * @param seoLink
	 * @return
	 */
	@CommunityPermissionRequired("INVITES")
	@RequestMapping(value = "/group/{seolink}/invite", method = RequestMethod.GET)
	public String showCommunityInvite(@PathVariable("seolink") String seoLink) {
		return "communityInvite";
	}

	/**
	 * Поиск возможных участников объединения
	 * @param seoLink
	 * @param page
	 * @param perPage
	 * @param query
	 * @return
	 */
	@CommunityPermissionRequired("INVITES")
	@RequestMapping(value = "/group/{seolink}/possible_members.json", method = RequestMethod.POST)
	@ResponseBody
	public List<CommunityUserMemberDataDto>  searchPossibleMembers(
			@PathVariable("seolink") String seoLink,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "per_page", defaultValue = "20") int perPage,
			@RequestParam(value = "query", defaultValue = "") String query) {
		int firstResult = (page - 1) * perPage;
		return CommunityUserMemberDataDto.toDtoList(
				communitiesService.getPossibleMembers(radomRequestContext.getCommunityId(), firstResult, perPage,
						query,false)
		);
	}
	/**
	 * Поиск возможных участников объединения
	 * @param page
	 * @param perPage
	 * @param query
	 * @return
	 */
	@CommunityPermissionRequired("INVITES")
	@RequestMapping(value = "/group/{seolink}/possible_members_with_verified_count.json", method = RequestMethod.POST)
	@ResponseBody
	public List<PossibleCommunityMemberDto>  searchPossibleMembersWithVerifiedCount(
			@PathVariable("seolink") Long id,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "per_page", defaultValue = "20") int perPage,
			@RequestParam(value = "query", defaultValue = "") String query) {
		String groupsOnlyVerifiedSetting =  systemSettingService.getSystemSetting("groups_members_verified_only_seolink");
		List<String> groupsOnlyVerified = Arrays.asList(groupsOnlyVerifiedSetting.split(","));
		boolean onlyVerifiedUsers = groupsOnlyVerified.contains(communityDataService.getByIdMediumData(id).getSeoLink());
		int firstResult = (page - 1) * perPage;
		return CommunityUserMemberDataDto.toDtoList(
				communitiesService.getPossibleMembers(radomRequestContext.getCommunityId(), firstResult, perPage,
						query,onlyVerifiedUsers)
		).stream().map(communityUserMemberDataDto -> {
			PossibleCommunityMemberDto possibleCommunityMemberDto = new PossibleCommunityMemberDto();
			possibleCommunityMemberDto.setCommunityMember(communityUserMemberDataDto);
			if (onlyVerifiedUsers) {
				possibleCommunityMemberDto.setCountVerified(invitationDataService.getInviteCountData(communityUserMemberDataDto.getId()).getCountVerified());
			}
			return possibleCommunityMemberDto;
		}).collect(Collectors.toList());
	}

	//------------------------------------------------------------------------------------------------------------------

	//------------------------------------------------------------------------------------------------------------------
	// Методы для работы с журналом активности
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * Страница журнала активности
	 * @param seoLink
	 * @return
	 */
	@CommunityPermissionRequired("MEMBERS_ACTIVITY_JOURNAL")
	@RequestMapping(value = "/group/{seolink}/activity_journal", method = RequestMethod.GET)
	public String showActivityJournalPage(@PathVariable("seolink") String seoLink) {
		return "communityActivityJournal";
	}

	/**
	 * Список событий журнала объединения
	 * @param seoLink
	 * @param userId
	 * @param type
	 * @param fromDate
	 * @param toDate
	 * @param page
	 * @param perPage
	 * @return
	 */
	@CommunityPermissionRequired("MEMBERS_ACTIVITY_JOURNAL")
	@RequestMapping(value = "/group/{seolink}/log_events.json", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public List<CommunityActivityJournalDto> logEvents(
			@PathVariable("seolink") String seoLink,
			@RequestParam(value = "user_id", required = false) Long userId,
			@RequestParam(value = "type", required = false) CommunityEventType type,
			@RequestParam(value = "from_date", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date fromDate,
			@RequestParam(value = "to_date", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date toDate,
			@RequestParam(value = "page", required = false, defaultValue = "1") int page,
			@RequestParam(value = "per_page", defaultValue = "20") int perPage) {
		int firstResult = (page - 1) * perPage;
		return CommunityActivityJournalDto.toDtoList(
				communityLogEventDomainService.find(radomRequestContext.getCommunityId(), userId, fromDate, toDate, type, firstResult, perPage)
		);
	}

	/**
	 * Данные объединения для страницы журнала активности
	 * @param seoLink
	 * @return
	 */
	@CommunityPermissionRequired("MEMBERS_ACTIVITY_JOURNAL")
	@RequestMapping(value = "/group/{seolink}/activity_journal_page_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public CommunityActivityJournalPageDataDto getActivityJournalPageDataDto(@PathVariable("seolink") String seoLink) {
		Community community = communityDataService.getByIdMediumData(
				radomRequestContext.getCommunityId(),
				Arrays.asList(
						FieldConstants.COMMUNITY_DESCRIPTION,
						FieldConstants.COMMUNITY_GEO_POSITION,
						FieldConstants.COMMUNITY_GEO_LOCATION,
						FieldConstants.COMMUNITY_LEGAL_GEO_POSITION,
						FieldConstants.COMMUNITY_LEGAL_GEO_LOCATION
				)
		);
		CommunityMember selfMember = communityMemberDomainService.getByCommunityIdAndUserId(radomRequestContext.getCommunityId(), SecurityUtils.getUser().getId());

		Date toDate = new Date();

		Calendar startDateCalendar = Calendar.getInstance();
		startDateCalendar.setTime(toDate);
		startDateCalendar.add(Calendar.MONTH, -1);
		Date fromDate = startDateCalendar.getTime();
		return new CommunityActivityJournalPageDataDto(community, selfMember, fromDate, toDate);
	}
	//------------------------------------------------------------------------------------------------------------------

	@CommunityPermissionRequired(CommunityPermissions.SETTINGS_COMMON)
	@RequestMapping(value = "/group/{seolink}/save_document_templates_settings.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public List<DocumentTemplateSettingDto> saveDocumentTemplateSettings(@RequestBody CommunitySaveInputMembersSettingsDto request) {
		List<DocumentTemplateSetting> documentTemplateSettings = communityDataService.saveDocumentTemplateSettings(
				radomRequestContext.getCommunityId(),
				DocumentTemplateSettingDto.toDomainList(request.getDocumentTemplateSettings()),
				request.isNeedCreateDocuments()
		);
		List<DocumentTemplateSettingDto> result = DocumentTemplateSettingDto.toDtoList(documentTemplateSettings);
		return result == null ? Collections.emptyList() : result;
	}


	@CommunityPermissionRequired(CommunityPermissions.SETTINGS_COMMON)
	@RequestMapping(value = "/group/{seolink}/load_document_templates_settings_page_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public CommunityInputMembersPageDataDto loadDocumentTemplateSettings() {
		Community community = communityDataService.getByIdMediumData(
				radomRequestContext.getCommunityId(),
				Arrays.asList(
						FieldConstants.COMMUNITY_DESCRIPTION,
						FieldConstants.COMMUNITY_GEO_POSITION,
						FieldConstants.COMMUNITY_GEO_LOCATION,
						FieldConstants.COMMUNITY_LEGAL_GEO_POSITION,
						FieldConstants.COMMUNITY_LEGAL_GEO_LOCATION
				)
		);
		CommunityMember selfMember = communityMemberDomainService.getByCommunityIdAndUserId(radomRequestContext.getCommunityId(), SecurityUtils.getUser().getId());

		List<DocumentTemplateSetting> documentTemplateSettings =
				communityDataService.getByIdFullData(radomRequestContext.getCommunityId())
						.getDocumentTemplateSettings();
		List<DocumentTemplateSettingDto> dtoList = DocumentTemplateSettingDto.toDtoList(documentTemplateSettings);
		dtoList = dtoList == null ? Collections.emptyList() : dtoList;
		return new CommunityInputMembersPageDataDto(
				CommunityAnyPageDto.toDto(community, selfMember),
				dtoList,
				community.isNeedCreateDocuments()
		);
	}

	// Страница запросов вступления в объединение с документами
	@RequestMapping(value = "/groups/documentrequests", method = RequestMethod.GET)
	public String getGroupsRequests() {
		return "groupsRequests";
	}

	@RequestMapping(value = "/groups/documentrequests/requests_grid_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public CommunityDocumentRequestGridDto groupsRequestsGridData(@RequestParam(value = "page", defaultValue = "1") int page) {
		CommunityDocumentRequestGridDto result;
		try {
			page = page - 1;
			int perPage = 15;
			CommunityDocumentRequestsPage communityDocumentRequestsPage = communityDocumentRequestService.getByUserIdPage(SecurityUtils.getUser().getId(), page, perPage);
			result = CommunityDocumentRequestGridDto.toSuccessDto(communityDocumentRequestsPage.getCommunityDocumentRequests(), communityDocumentRequestsPage.getCount());
		} catch (Exception e) {
			e.printStackTrace();
			result = CommunityDocumentRequestGridDto.toFailDto();
		}
		return result;
	}

	@RequestMapping(value = "/groups/documentrequests/remove_request.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public CommonResponseDto removeDocumentRequest(@RequestParam(value = "id", defaultValue = "1") Long id) {
		communityDocumentRequestService.deleteRequestAndMember(id);
		return SuccessResponseDto.get();
	}

	@RequestMapping(value = "/groups/documentrequests/{documentRequestId}", method = RequestMethod.GET)
	public String documentsRequestPage(@PathVariable("documentRequestId") Long documentRequestId) {
		CommunityDocumentRequest communityDocumentRequest = communityDocumentRequestService.getById(documentRequestId);
		String result;
		if (communityDocumentRequest == null) {
			result = "error404";
		} else if (communityDocumentRequest.getUser().getId().equals(SecurityUtils.getUser().getId())) {
			result = "groupsRequest";
		} else {
			result = "error403";
		}
		return result;
	}

	@RequestMapping(value = "/groups/documentrequests/request_grid_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public List<DocumentFromCommunityRequestDto> groupsRequestGridData(@RequestParam(value = "id", defaultValue = "1") Long id) {
		CommunityDocumentRequest communityDocumentRequest = communityDocumentRequestService.getById(id);
		List<DocumentFromCommunityRequestDto> result;
		if (communityDocumentRequest != null && communityDocumentRequest.getDocuments() != null && !communityDocumentRequest.getDocuments().isEmpty()) {
			result = new ArrayList<>();
			for (Document document : communityDocumentRequest.getDocuments()) {
				boolean isDocumentSigned = documentParticipantService.isDocumentSignedByUser(document, SecurityUtils.getUser());
				DocumentFromCommunityRequestDto documentFromCommunityRequestDto = new DocumentFromCommunityRequestDto(document, isDocumentSigned);
				result.add(documentFromCommunityRequestDto);
			}
		} else {
			result = Collections.emptyList();
		}
		return result;
	}

	@CommunityPermissionRequired(CommunityPermissions.SETTINGS_COMMON)
	@RequestMapping(value = "/group/{seolink}/input_member_settings", method = RequestMethod.GET)
	public String showInputMemberSettingsPage(@PathVariable("seolink") String seoLink) {
		return "inputMemberSettings";
	}

}
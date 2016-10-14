package ru.radom.kabinet.interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import ru.askor.blagosfera.core.services.account.AccountService;
import ru.radom.kabinet.dao.OkvedDao;
import ru.radom.kabinet.dao.account.AccountDao;
import ru.radom.kabinet.dao.account.AccountTypeDao;
import ru.radom.kabinet.dao.applications.ApplicationDao;
import ru.radom.kabinet.dao.applications.SharerApplicationDao;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.dao.communities.CommunityMemberDao;
import ru.radom.kabinet.dao.rameralisteditor.RameraListEditorItemDAO;
import ru.radom.kabinet.dao.web.CommunitySectionDao;
import ru.radom.kabinet.dao.web.SectionDao;
import ru.radom.kabinet.json.SerializationManager;
import ru.radom.kabinet.model.web.CommunitySection;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.context.RequestContext;
import ru.radom.kabinet.services.SharebookService;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityDataService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.Map;

public class CommunityInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private AccountService accountService;

	@Autowired
	private CommunitiesService communitiesService;

	@Autowired
	private CommunityDataService communityDomainService;

	@Autowired
	private CommunityDao communityDao;

	@Autowired
	private CommunityMemberDao communityMemberDao;

	@Autowired
	private SectionDao sectionDao;

	@Autowired
	private SerializationManager serializationManager;

	@Autowired
	private OkvedDao okvedDao;

	@Autowired
	private CommunitySectionDao communitySectionDao;

	@Autowired
	private ApplicationDao applicationDao;

	@Autowired
	private SharerApplicationDao sharerApplicationDao;

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private AccountTypeDao accountTypeDao;

	@Autowired
	private RameraListEditorItemDAO rameraListEditorItemDAO;

	@Autowired
	private SharebookService sharebookService;

	@Autowired
	private RequestContext radomRequestContext;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (pathVariables == null) return true;

		String seoLink = (String) pathVariables.get("seolink");

		seoLink = URLDecoder.decode(seoLink, "UTF-8");
		seoLink = URLDecoder.decode(seoLink, "UTF-8"); // 2 раза декодится для получения ссылки для подгруппы

		Long communityId = communityDomainService.findCommunityId(seoLink);

		if (communityId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return false;
		}
		radomRequestContext.setCommunityId(communityId);

		/*CommunityEntity community = communityDao.getBySeoLink(seoLink);
		if (community == null) {
			try {
				community = communityDao.getById(Long.valueOf(seoLink));
				community.setSeoLink(seoLink);
			} catch (Exception e) {
				//do nothing
			}
		}

		if (community == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return false;
		}

		String uri = URLDecoder.decode(request.getRequestURI(), "UTF-8").substring(community.getLink().length());

		if (community.isDeleted() && !"/deleted".equals(uri)) {
			response.sendRedirect("/group/" + URLEncoder.encode(seoLink, "UTF-8") + "/deleted");
			return false;
		}


		radomRequestContext.setCommunity(community);

		CommunityMemberEntity member = communityMemberDao.get(community, radomRequestContext.getCurrentSharer());


		if (member != null && member.isCreator()) {
			CommunityPostEntity ceoPost = communitiesService.getCeoPost(community);
		}

		if (member != null) {
			radomRequestContext.setCommunity(community);
			radomRequestContext.setCommunityMember(member);
		}

		// Проверяем права доступа к странице
		// Нужно получить ссылку на страницу, и запросить права на неё
		CommunitySection communitySection = communitySectionDao.getByLink(uri);
		if (communitySection != null) {
			// Проверяем права доступа участника к разделу в объединении
			boolean sharerHasRightsToPage = true;
			if (communitySection.getPermission() != null) {
				sharerHasRightsToPage = communitiesService.hasPermission(community, radomRequestContext.getCurrentSharer(), communitySection.getPermission());
			}

			if (!sharerHasRightsToPage) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return false;
			}

			// Проверяем можно ли не участникам объединения просматривать раздел
			boolean guestAccess = communitySection.getGuestAccess();
			if (member == null && !guestAccess) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return false;
			}
		}*/

		return true;
	}

	private void setSectionVisible(CommunitySection section) {
		boolean result = false;

		if (section.getPermission() != null && !communitiesService.hasPermission(radomRequestContext.getCommunity(), SecurityUtils.getUser().getId(), section.getPermission())) {
			result = false;
		} else {
			result = true;
		}

		// TODO Переделать на права community_permissions (Заменить старую форму объединения на новую)
		// Производим фильтрацию по форме объединения
		/*RameraListEditorItem rameraListEditorItem = null;
		if (section.getCommunityAssociationFormId() != null) {
			rameraListEditorItem = rameraListEditorItemDAO.getById(section.getCommunityAssociationFormId());
		}
		if (rameraListEditorItem != null) {
			if (radomRequestContext.getCommunity().getRameraAssociationFormId() != null &&
					radomRequestContext.getCommunity().getRameraAssociationFormId().longValue() == rameraListEditorItem.getId().longValue()) {
				result = result && true;
			} else {
				result = false;
			}
		}*/

		section.setVisible(result);
		for (CommunitySection child : section.getChildren()) {
			setSectionVisible(child);
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
		String uri = URLDecoder.decode(URLDecoder.decode(request.getRequestURI(), "UTF-8"), "UTF-8");
		if (modelAndView == null) {
			return;
		}
		ModelMap model = modelAndView.getModelMap();
		model.addAttribute("communityId", radomRequestContext.getCommunityId());
		/*model.addAttribute("community", radomRequestContext.getCommunity());
		model.addAttribute("selfMember", radomRequestContext.getCommunityMember());

		model.addAttribute("currentSection", sectionDao.getByName(radomRequestContext.isActiveCommunityMember() ? "communityMy" : "community"));
		model.addAttribute("membersCount", radomRequestContext.getCommunity().getMembersCount());

		model.addAttribute("rightSidebarMembersFirstPage", serializationManager.serializeCollection(communityMemberDao.getPage(radomRequestContext.getCommunity(), 6, 1)));
		model.addAttribute("rightSidebarMembersPagesCount", communityMemberDao.getPagesCount(radomRequestContext.getCommunity(), 6));
		final List<Long> okvedIds = new LinkedList<>();
		for (OkvedEntity o : radomRequestContext.getCommunity().getOkveds()) {
			okvedIds.add(o.getId());
		}
		// Основной вид деятельности
		if (radomRequestContext.getCommunity().getMainOkved() != null) {
			model.addAttribute("communityMainOkved", radomRequestContext.getCommunity().getMainOkved().getId());
		}
		// Дополнительные виды деятельности
		model.addAttribute("communityAdditionalOkveds", StringUtils.join(okvedIds, ";"));
		if (communityMemberDao.exists(radomRequestContext.getCommunity(), radomRequestContext.getCurrentSharer())) {
			model.addAttribute("leftSidebarActiveItem", "/group");
		}

		List<CommunitySection> roots = communitySectionDao.getRoots();
		for (CommunitySection root : roots) {
			setSectionVisible(root);
		}
		for (CommunitySection root : roots) {
			boolean visibleChildrenExists = false;
			for (CommunitySection child : root.getChildren()) {
				if (child.isVisible()) {
					visibleChildrenExists = true;
					break;
				}
			}
			if (StringUtils.isBlank(root.getLink()) && root.getChildren().size() > 0 && !visibleChildrenExists) {
				root.setVisible(false);
			}
		}

		//radomRequestContext.getCommunity().setSeoLink(null);
		if (radomRequestContext.getCommunity().getSeoLink() == null || radomRequestContext.getCommunity().getSeoLink().equals("")) {
			radomRequestContext.getCommunity().setSeoLink(radomRequestContext.getCommunity().getId().toString());
		}
		int length = radomRequestContext.getCommunity().getLink().length();
		String subUri = "/";
		if (uri.length() > length) {
			subUri = uri.substring(length);
			if (subUri.length() == 0) {
				subUri = "/";
			}
		}

		CommunitySection activeCommunitySection = communitySectionDao.getByLink(subUri);
		List<CommunitySection> communitySectionsHierarchy = communitySectionDao.getHierarchy(activeCommunitySection);

		List<Application> applications = applicationDao.getByCommunity(radomRequestContext.getCommunity());
		Map<Application, SharerApplication> applicationsMap = sharerApplicationDao.get(radomRequestContext.getCurrentSharer(), applications);

		CommunitySection applicationsRootSection = new CommunitySection();
		applicationsRootSection.setId(-1L);
		applicationsRootSection.setTitle("Приложения");
		applicationsRootSection.setChildren(new ArrayList<>());
		long id = -1L;
		boolean applicationsRootVisible = false;
		for (Application application : applications) {
			CommunitySection applicationSection = new CommunitySection();
			applicationSection.setId(--id);
			applicationSection.setTitle(application.getName());
			applicationSection.setParent(applicationsRootSection);
			applicationSection.setLink(application.getStartLink());
			SharerApplication sharerApplication = applicationsMap.get(application);
			boolean visible = sharerApplication != null && sharerApplication.isInstalled() && sharerApplication.isShowInMenu();
			applicationSection.setVisible(visible);
			if (visible) {
				applicationsRootVisible = true;
			}
			applicationsRootSection.getChildren().add(applicationSection);

			if (applicationSection.getLink().equals(uri)) {
				activeCommunitySection = applicationSection;
				communitySectionsHierarchy.clear();
				communitySectionsHierarchy.add(applicationsRootSection);
				communitySectionsHierarchy.add(applicationSection);
			}

		}
		applicationsRootSection.setVisible(applicationsRootVisible);
		roots.add(applicationsRootSection);
		model.addAttribute("communityRootSections", roots);

		model.addAttribute("activeCommunitySection", activeCommunitySection);
		model.addAttribute("communitySectionsHierarchy", communitySectionsHierarchy);

		modelAndView.getModel().put("communityAccountTypes", accountTypeDao.getAccounts(CommunityEntity.class));
		modelAndView.getModel().put("communityAccountsMap", accountDao.getAccountMap(radomRequestContext.getCommunity()));
		modelAndView.getModel().put("sharerBookAccount", accountService.getSharebook(radomRequestContext.getCurrentSharer(), radomRequestContext.getCommunity()));
		modelAndView.getModel().put("communityBookAccountsBalance", sharebookService.getCommunitySharebooksTotalBalance(radomRequestContext.getCommunity().getId()));

		boolean isConsumerSociety = false;
		RameraListEditorItem poAssociationForm = rameraListEditorItemDAO.getByCode(CommunityEntity.COOPERATIVE_SOCIETY_LIST_ITEM_CODE);
		RameraListEditorItem kuchAssociationForm = rameraListEditorItemDAO.getByCode(CommunityEntity.COOPERATIVE_PLOT_ASSOCIATION_FORM_CODE);
		Long associationFormId = radomRequestContext.getCommunity().getRameraAssociationFormId();
		if (associationFormId != null && (associationFormId.equals(poAssociationForm.getId()) || associationFormId.equals(kuchAssociationForm.getId()))) {
			isConsumerSociety = true;
		}
		modelAndView.getModel().put("isConsumerSociety", isConsumerSociety);

		// Если объединение - в рамках юр лица и пользователь не сертифицированн - то показать страницу с запретом доступа
		if (radomRequestContext.getCommunity().isWithOrganization() && !radomRequestContext.getCurrentSharer().isVerified()) {
			modelAndView.setViewName("notVerifiedAccessToCommunity");
		}*/
	}
}
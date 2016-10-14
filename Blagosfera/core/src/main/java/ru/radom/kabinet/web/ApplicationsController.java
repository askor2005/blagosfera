package ru.radom.kabinet.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.data.jpa.entities.account.AccountTypeEntity;
import ru.radom.kabinet.dao.applications.ApplicationDao;
import ru.radom.kabinet.dao.applications.SharerApplicationDao;
import ru.radom.kabinet.dao.web.SectionDao;
import ru.radom.kabinet.json.SerializationManager;
import ru.radom.kabinet.model.applications.Application;
import ru.radom.kabinet.model.applications.SharerApplication;
import ru.radom.kabinet.model.web.Section;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.ApplicationException;
import ru.radom.kabinet.services.ApplicationsService;
import ru.radom.kabinet.utils.JsonUtils;
import ru.radom.kabinet.utils.StringUtils;
import ru.radom.kabinet.web.utils.Breadcrumb;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class ApplicationsController {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationsController.class);

	@Autowired
	private ApplicationsService applicationsService;

	@Autowired
	private SectionDao sectionDao;

	@Autowired
	private ApplicationDao applicationDao;

	@Autowired
	private SharerApplicationDao sharerApplicationDao;

	@Autowired
	private SerializationManager serializationManager;

	@RequestMapping(value = "/apps/download.json", method = RequestMethod.POST)
	public @ResponseBody String download(@RequestParam("application_id") Application application) {
		try {
			SharerApplication sharerApplication = applicationsService.downloadApplication(SecurityUtils.getUser().getId(), application);
			return serializationManager.serialize(sharerApplication.getApplication()).toString();
		} catch (ApplicationException e) {
			return JsonUtils.getErrorJson(e.getMessage()).toString();
		}
	}

	@RequestMapping(value = "/apps/buy.json", method = RequestMethod.POST)
	public @ResponseBody String buy(@RequestParam("application_id") Application application, @RequestParam("account_type_id") AccountTypeEntity accountType) {
		try {
			SharerApplication sharerApplication = applicationsService.buyApplication(SecurityUtils.getUser().getId(), application, accountType);
			return serializationManager.serialize(sharerApplication.getApplication()).toString();
		} catch (ApplicationException e) {
			return JsonUtils.getErrorJson(e.getMessage()).toString();
		}
	}

	@RequestMapping(value = "/apps/install.json", method = RequestMethod.POST)
	public @ResponseBody String install(@RequestParam("application_id") Application application) {
		try {
			SharerApplication sharerApplication = applicationsService.installApplication(SecurityUtils.getUser().getId(), application);
			return serializationManager.serialize(sharerApplication.getApplication()).toString();
		} catch (ApplicationException e) {
			return JsonUtils.getErrorJson(e.getMessage()).toString();
		}
	}

	@RequestMapping(value = "/apps/uninstall.json", method = RequestMethod.POST)
	public @ResponseBody String uninstall(@RequestParam("application_id") Application application) {
		try {
			SharerApplication sharerApplication = applicationsService.uninstallApplication(SecurityUtils.getUser().getId(), application);
			return serializationManager.serialize(sharerApplication.getApplication()).toString();
		} catch (ApplicationException e) {
			return JsonUtils.getErrorJson(e.getMessage()).toString();
		}
	}

	@RequestMapping(value = "/apps/show_in_menu.json", method = RequestMethod.POST)
	public @ResponseBody String showInMenu(@RequestParam("application_id") Application application) {
		try {
			SharerApplication sharerApplication = applicationsService.showApplicationInMenu(SecurityUtils.getUser().getId(), application);
			return serializationManager.serialize(sharerApplication.getApplication()).toString();
		} catch (ApplicationException e) {
			return JsonUtils.getErrorJson(e.getMessage()).toString();
		}
	}

	@RequestMapping(value = "/apps/hide_in_menu.json", method = RequestMethod.POST)
	public @ResponseBody String hideInMenu(@RequestParam("application_id") Application application) {
		try {
			SharerApplication sharerApplication = applicationsService.hideApplicationInMenu(SecurityUtils.getUser().getId(), application);
			return serializationManager.serialize(sharerApplication.getApplication()).toString();
		} catch (ApplicationException e) {
			return JsonUtils.getErrorJson(e.getMessage()).toString();
		}
	}

	@RequestMapping(value = "/apps/list.json", method = RequestMethod.GET)
	public @ResponseBody String list(
			@RequestParam(value = "section_id", required = false) Section section,
			@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "per_page", defaultValue = "20") int perPage,
			@RequestParam(value = "include_not_sharers", defaultValue = "true") boolean includeNotSharers,
			@RequestParam(value = "include_downloaded", defaultValue = "true") boolean includeDownloaded,
			@RequestParam(value = "include_bought", defaultValue = "true") boolean includeBought,
			@RequestParam(value = "include_installed", defaultValue = "true") boolean includeInstalled) {
		List<Application> applications = applicationDao.search(section, query, includeNotSharers, includeDownloaded, includeBought, includeInstalled, SecurityUtils.getUser().getId(), (page - 1) * perPage, perPage);
		return serializationManager.serializeCollection(applications).toString();
	}

	@RequestMapping("/application/start/{id}")
	public String showApplicationStartPage(@PathVariable("id") Application application, Model model, HttpServletResponse response) throws IOException {

		SharerApplication sharerApplication = sharerApplicationDao.get(SecurityUtils.getUser().getId(), application);
		if (sharerApplication == null || !sharerApplication.isInstalled()) {
			response.sendError(404);
			return null;
		} else {
			model.addAttribute("application", application);

			Section applicationSection = sectionDao.getByApplication(application);
			if (applicationSection != null) {
				model.addAttribute("currentSection", applicationSection);
			} else {
				Section currentSection = application.getFeaturesLibrarySection();
				model.addAttribute("currentSection", currentSection);
				List<Section> sectionsHierarchy = sectionDao.getHierarchy(currentSection);
				Breadcrumb breadcrumb = new Breadcrumb();
				for (Section section : sectionsHierarchy) {
					if (StringUtils.hasLength(section.getTitle())) {
						breadcrumb.add(section.getTitle(), StringUtils.hasLength(section.getLink()) ? section.getLink() : "#");
					}
				}
				breadcrumb.add("Приложение " + application.getName(), application.getInfoLink());
				model.addAttribute("breadcrumb", breadcrumb);
			}
			return "applicationStart";
		}
	}

	@RequestMapping("/application/info/{id}")
	public String showApplicationInfoPage(@PathVariable("id") Application application, Model model, HttpServletResponse response) throws IOException {
		model.addAttribute("application", application);
		Section currentSection = application.getFeaturesLibrarySection();
		List<Section> sectionsHierarchy = sectionDao.getHierarchy(currentSection);
		model.addAttribute("currentSection", currentSection);
		Breadcrumb breadcrumb = new Breadcrumb();
		for (Section section : sectionsHierarchy) {
			if (StringUtils.hasLength(section.getTitle())) {
				breadcrumb.add(section.getTitle(), StringUtils.hasLength(section.getLink()) ? section.getLink() : "#");
			}
		}
		breadcrumb.add("Информация о приложении " + application.getName(), application.getInfoLink());
		model.addAttribute("breadcrumb", breadcrumb);
		
		SharerApplication sharerApplication = sharerApplicationDao.get(SecurityUtils.getUser().getId(), application);
		model.addAttribute("sharerApplication", sharerApplication);
		Section applicationSection = sectionDao.getByApplication(application);
		model.addAttribute("applicationSection", applicationSection);
		return "applicationInfo";
	}

}

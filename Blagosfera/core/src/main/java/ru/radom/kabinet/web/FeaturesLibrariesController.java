package ru.radom.kabinet.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.radom.kabinet.dao.applications.ApplicationDao;
import ru.radom.kabinet.dao.applications.SharerApplicationDao;
import ru.radom.kabinet.dao.news.NewsDao;
import ru.radom.kabinet.dao.web.SectionDao;
import ru.radom.kabinet.json.SerializationManager;
import ru.radom.kabinet.model.web.Section;

import javax.servlet.http.HttpServletRequest;

@Controller("featuresLibrariesController")
public class FeaturesLibrariesController {

	@Autowired
	private NewsDao newsDao;

	@Autowired
	private SerializationManager serializationManager;

	@Autowired
	private ApplicationDao applicationDao;
	
	@Autowired
	private SharerApplicationDao sharerApplicationDao;
	
	@Autowired
	private SectionDao sectionDao;
	
	private Logger logger = LoggerFactory.getLogger(FeaturesLibrariesController.class);

	@RequestMapping(value = { "/features_library/**" }, method = RequestMethod.GET)
	public String showRadomPage(Model model, HttpServletRequest request) {
		String uri = request.getRequestURI();
		Section section = sectionDao.getByLink(uri);
		model.addAttribute("applicationCount", applicationDao.getCountBySection(section));
//		List<Application> applications = applicationDao.getBySection(section);
//		model.addAttribute("applications", applications);
//		Map<Application, SharerApplication> applicationsMap = sharerApplicationDao.get(radomRequestContext.getCurrentSharer(), applications);
		
//		model.addAttribute("applicationsMap", applicationsMap);
//		Map<Application, Section> applicationsSectionsMap = sectionDao.getApplicationsSectionsMap(applications);
//		model.addAttribute("applicationsSectionsMap", applicationsSectionsMap);
		return "featuresLibrary";
	}

}

package ru.radom.kabinet.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.dao.web.SectionDao;
import ru.radom.kabinet.security.context.RequestContext;
import ru.radom.kabinet.services.section.SectionService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.Map;

public class LayoutInterceptor extends HandlerInterceptorAdapter {

	private static final Logger logger = LoggerFactory.getLogger(LayoutInterceptor.class);

	@Autowired
	private SectionDao sectionDao;

	@Autowired
	private SectionService sectionService;
	
	@Autowired
	private SharerDao sharerDao;
	
	@Autowired
	private CommunityDao communityDao;

    @Autowired
    private SettingsManager settingsManager;

	@Autowired
	private RequestContext radomRequestContext;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		String uri = URLDecoder.decode(URLDecoder.decode(request.getRequestURI(), "UTF-8"), "UTF-8");
		if (request.getQueryString() != null) {
			uri = uri + "?" + request.getQueryString();
		}


		if (modelAndView == null) {
			return;
		}
		
		Map<String, Object> model = modelAndView.getModel();
		
		if (model == null) {
			return;
		}
		
		model.put("uri", uri);
		/*model.put("rootSections", sectionService.getRoots());
		SectionDomain currentSection;
		if (radomRequestContext.getCommunityId() != null) {
			currentSection = sectionService.getByName("community");
		} else {
			currentSection = sectionService.getByLink(uri);
		}
		//}
		if (currentSection == null) {
			String activeSubportal = WebUtils.getCookie(request.getCookies(), "RADOM_ACTIVE_SUBPORTAL", "blagosferaNews");
			currentSection = sectionService.getByName(activeSubportal);
		}
		model.put("currentSection", currentSection);
		List<SectionDomain> sectionsHierarchy = sectionService.getHierarchy(currentSection != null ? currentSection.getId() : null);
		if (sectionsHierarchy != null && sectionsHierarchy.size() > 0) {
			Cookie subportalCookie = new Cookie("RADOM_ACTIVE_SUBPORTAL", sectionsHierarchy.get(0).getName());
			subportalCookie.setPath("/");
			response.addCookie(subportalCookie);
		}
		model.put("sectionsHierarchy", sectionsHierarchy);*/

		/*Breadcrumb breadcrumb = (Breadcrumb) model.get("breadcrumb");
		if (breadcrumb == null) {
			breadcrumb = new Breadcrumb();
			if (sectionsHierarchy != null) {
				for (SectionDomain section : sectionsHierarchy) {
					if (StringUtils.hasLength(section.getTitle())) {
						breadcrumb.add(section.getTitle(), StringUtils.hasLength(section.getLink()) ? section.getLink() : "#");
					}
				}
			}
			model.put("breadcrumb", breadcrumb);
		}*/

		if (request.getAttribute("start") != null) {
			long end = System.currentTimeMillis();
			long duration = end - (Long) request.getAttribute("start");
			model.put("requestProcessingDuration", duration);
		} else {
			model.put("requestProcessingDuration", -1);
		}
		
		model.put("sharersTotalCount", sharerDao.getTotalCount());
		model.put("rootCommunitiesTotalCount", communityDao.getTotalRootCount());

		model.put("fingerScanTimeout", settingsManager.getSystemSettingAsInt("bio.finger.scan-timeout", 30));
		model.put("fingerSensorAutoselectTimeout", settingsManager.getSystemSettingAsInt("bio.finger.sensor-autoselect-timeout", 30));
	}
}

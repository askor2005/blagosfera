package ru.radom.kabinet.web.pages;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import ru.askor.blagosfera.domain.section.SectionAccessType;
import ru.askor.blagosfera.domain.section.SectionDomain;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.SystemSettingsService;
import ru.radom.kabinet.services.section.SectionDomainService;
import ru.radom.kabinet.utils.Roles;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

public class PagesController extends AbstractController {
	@Autowired
	private SectionDomainService sectionDomainService;

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String uri = URLDecoder.decode(request.getRequestURI(), "UTF-8");
		SectionDomain section = sectionDomainService.getByLink(uri);

		if (section == null || (!section.isEditable() && section.getParentId() != null)) {
			response.sendError(404);
			return null;
		}

		SectionAccessType sectionAccessType = section.getAccessType();
		if(sectionAccessType != null) {
			User user = SecurityUtils.getUser();
			switch(sectionAccessType) {
				case REGISTERED:
					if(user == null) {
						response.sendError(404);
						return null;
					}
					break;
				case VERIFIED:
					if(user != null && !user.isVerified()) {
						response.sendError(404);
						return null;
					}
					break;
			}
		}

		boolean isAdmin = SecurityUtils.getUserDetails() != null && SecurityUtils.getUserDetails().hasRole(Roles.ROLE_ADMIN);
		boolean isBlagosferaEditor =
						SecurityUtils.getUserDetails() != null &&
						(SecurityUtils.getUserDetails().hasRole(Roles.ROLE_BLAGOSFERA_PAGES_EDITOR) &&
								sectionDomainService.getHierarchy(section.getId()).get(0).getName().equals("blagosfera"));

		if (!section.isPublished() && !isAdmin && !isBlagosferaEditor) {
			response.sendError(404);
			return null;
		}

		ModelAndView modelAndView;

		if (!StringUtils.isBlank(section.getForwardUrl()) && !section.getForwardUrl().equals(section.getLink())) {
			modelAndView = new ModelAndView("forward:" + section.getForwardUrl());
		} else if (section.getParentId() == null && section.getPageId() == null){ // Если это корневой раздел и у него не установлена статичная страница, то страница по умолчанию
			modelAndView = new ModelAndView(section.getName());
			modelAndView.getModel().put("sectionName",section.getName());
		} else {
			/*PageEntity page = pagesService.getPage(section);

			modelAndView = new ModelAndView();
			modelAndView.setViewName("page");
			modelAndView.getModel().put("page", page);
			modelAndView.getModel().put("content", page.getContent());
			modelAndView.getModel().put("currentPageTitle", page.getTitle());
			modelAndView.getModel().put("currentPageDescription", page.getDescription());
			modelAndView.getModel().put("currentPageKeywords", page.getKeywords());
			if (isAdmin) {
				modelAndView.getModel().put("pageEditLink", "/admin/page/" + page.getId() + "/edit");
			} else if (isBlagosferaEditor) {
				modelAndView.getModel().put("pageEditLink", "/Благосфера/редактор/страница/" + page.getId());
			}
			modelAndView.getModel().put("hasEditPermission", isAdmin || isBlagosferaEditor);

			Date date = new Date();
			if (page.getCurrentEditor() != null && page.getCurrentEditorEditDate() != null) {
				// Страницу можно редактировать
				if ((date.getTime() - page.getCurrentEditorEditDate().getTime() > PageEntity.MILLISECONDS_FOR_RELEASE_PAGE) ||
						(SecurityUtils.getUserDetails() != null && page.getCurrentEditor().getId().equals(SecurityUtils.getUser().getId()))) {
					// do nothing
				} else { // Страница ещё редактируется
					modelAndView.getModel().put("currentEditor", page.getCurrentEditor());
				}
			}*/
			modelAndView = new ModelAndView();
			modelAndView.setViewName("page");
			modelAndView.getModel().put("pageId",section.getPageId());
		}

		return modelAndView;
	}

}

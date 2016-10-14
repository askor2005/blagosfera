package ru.radom.kabinet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.core.services.cms.PagesService;
import ru.askor.blagosfera.core.web.pageedition.services.PageEditionService;
import ru.askor.blagosfera.domain.cms.Page;
import ru.askor.blagosfera.domain.section.SectionDomain;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.SharerService;
import ru.radom.kabinet.dao.web.SectionDao;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.expressions.Functions;
import ru.radom.kabinet.model.web.Section;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.section.SectionDomainService;
import ru.radom.kabinet.web.admin.dto.PageDto;
import ru.radom.kabinet.web.admin.dto.PageEditionDto;
import ru.radom.kabinet.web.admin.dto.PageResponseDto;

import java.util.stream.Collectors;

@Controller("blagosferaController")
public class BlagosferaController {
	@Autowired
	private SectionDao sectionDao;
    @Autowired
	private PagesService pagesService;
	@Autowired
	private SectionDomainService sectionDomainService;
	@Autowired
	private PageEditionService pageEditionService;
	@Autowired
	private SharerService sharerService;
	@RequestMapping("/Благосфера/редактор/страница/{page_id}")
	public String getPageEditorPage(Model model, @PathVariable("page_id") Long pageId) {
		SectionDomain section = sectionDomainService.getByPageId(pageId);
		//TODO неправильное имя секции, пока не исправил тк может меняться
		if (!sectionDomainService.getHierarchy(section.getId()).get(0).getName().equals("blagosfera")) {
			throw new AccessDeniedException("Страница не относится к порталу Благосфера");
		}
		//model.addAttribute("currentSection", section);
		model.addAttribute("pageId", pageId);
		return "blagosferaPageEdit";
	}

	@RequestMapping("/blagosfera/editor/page/{page_id}/edit.json")
	public @ResponseBody SuccessResponseDto savePage (@PathVariable("page_id") Long pageId, @RequestParam(value = "title", required = false) String title,
													 @RequestParam(value = "description", required = false) String description,
													 @RequestParam(value = "keywords", required = false) String keywords,
													 @RequestParam(value = "content", required = false) String content) throws Exception{
		SectionDomain section = sectionDomainService.getByPageId(pageId);
		//TODO неправильное имя секции, пока не исправил тк может меняться
		if (!sectionDomainService.getHierarchy(section.getId()).get(0).getName().equals("blagosfera")) {
			throw new AccessDeniedException("Страница не относится к порталу Благосфера");
		}
		pagesService.editPage(pageId, SecurityUtils.getUser().getId(), title, description, keywords, content, null);
		return SuccessResponseDto.get();
	}
	@RequestMapping("/blagosfera/editor/page/{page_id}/get.json")
	public @ResponseBody PageResponseDto getPageForEditJson(@PathVariable("page_id") Long pageId)
	{
		SectionDomain section = sectionDomainService.getByPageId(pageId);
		//TODO неправильное имя секции, пока не исправил тк может меняться
		if (!sectionDomainService.getHierarchy(section.getId()).get(0).getName().equals("blagosfera")) {
			throw new AccessDeniedException("Страница не относится к порталу Благосфера");
		}
		Page page = pagesService.getById(pageId);
		if (page == null) {
			throw new RuntimeException("Страница с таким id не найдена!");
		}
		PageResponseDto response = new PageResponseDto();
		response.setPage(PageDto.createFromPage(page));
		User currentEditor = pagesService.getCurrentEditor(pageId);
		response.setCurrentEditor(currentEditor != null ? Functions.getSharerPadeg(currentEditor, 5) : null);//имя пользователя нужно в падеже
		return response;
	}

	@RequestMapping("/Благосфера/редактор/меню")
	public String getMenuEditorPage(Model model) {
		Section root = sectionDao.getByName("blagosferaNews");
		model.addAttribute("root", root);
		return "adminMenuEdit";
	}
	@RequestMapping(value = "/blagosfera/page/static/get/{page_id}", method = RequestMethod.GET)
	public
	@ResponseBody
	PageResponseDto getStaticPage(@PathVariable("page_id") Long pageId) {
		Page page = pagesService.getById(pageId);
		if (page == null) {
			throw new RuntimeException("Страница с таким id не найдена!");
		}
		PageResponseDto response = new PageResponseDto();
		response.setPage(PageDto.createFromPage(page));
		User currentEditor = pagesService.getCurrentEditor(pageId);
		response.setCurrentEditor(currentEditor != null ? currentEditor.getName() : null);
		response.setHasEditPermission(pagesService.isAllowedEditStaticPage(pageId));
		response.setPageEditLink(pagesService.getPageEditLink(pageId));
		response.setTimesWord(Functions.getDeclension(page.getEditionsCount(), "раз", "раза", "раз"));
		response.setEditions(pageEditionService.getByPage(pageId).stream().map(
                pageEditionDomain -> new PageEditionDto(pageEditionDomain.getDate(),
                sharerService.getByIdMinData(pageEditionDomain.getEditorId()).getShortName())
        ).collect(Collectors.toList()));
		return response;
	}

}

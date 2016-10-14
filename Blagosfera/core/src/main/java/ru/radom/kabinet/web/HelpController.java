package ru.radom.kabinet.web;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.askor.blagosfera.core.services.cms.HelpSectionService;
import ru.askor.blagosfera.core.services.cms.PagesService;
import ru.askor.blagosfera.core.web.pageedition.services.PageEditionService;
import ru.askor.blagosfera.domain.cms.Page;
import ru.askor.blagosfera.domain.section.HelpSectionDomain;
import ru.radom.kabinet.SharerService;
import ru.radom.kabinet.expressions.Functions;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.utils.Roles;
import ru.radom.kabinet.web.admin.dto.HelpSectionDto;
import ru.radom.kabinet.web.admin.dto.HelpSectionResponseDto;
import ru.radom.kabinet.web.admin.dto.PageDto;
import ru.radom.kabinet.web.admin.dto.PageEditionDto;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HelpController {
	private static final Logger logger = LoggerFactory.createLogger(HelpController.class);
	@Autowired
	private SharerService sharerService;
	@Autowired
	private PageEditionService pageEditionService;
    @Autowired
	private HelpSectionService helpSectionService;
	@Autowired
	private PagesService pagesService;
	@RequestMapping("/help/{name}/get.json")
	public @ResponseBody
	HelpSectionResponseDto getHelpPage(@PathVariable("name") String name, @RequestParam(value = "create",required = false) Boolean create) {
		HelpSectionDomain helpSectionDomain = helpSectionService.getByName(name);
		if (helpSectionDomain == null) {
			if (SecurityUtils.getUserDetails().hasRole(Roles.ROLE_ADMIN)) {
				helpSectionDomain = helpSectionService.createHelpSection(null,name);
			} else {
				throw new ResourceNotFoundException();
			}
		}
		HelpSectionResponseDto helpSectionResponseDto = new HelpSectionResponseDto();
		helpSectionResponseDto.setCurrentHelpSection(HelpSectionDto.toDto(helpSectionDomain));
		Page page = pagesService.getById(helpSectionDomain.getPageId());
		helpSectionResponseDto.setEditionsCount(page.getEditionsCount());
		boolean isAdmin = SecurityUtils.getUserDetails().hasRole(Roles.ROLE_ADMIN);
		if ((isAdmin) || (helpSectionDomain.isPublished())) {
			helpSectionResponseDto.setPage(PageDto.createFromPage(page));
		}
		helpSectionResponseDto.setAdmin(isAdmin);
		if (isAdmin) {
			helpSectionResponseDto.setEditions(pageEditionService.getByPage(page.getId()).stream().map(pageEditionDomain -> new PageEditionDto(pageEditionDomain.getDate(), sharerService.getByIdMinData(pageEditionDomain.getEditorId()).getShortName())).collect(Collectors.toList()));
		}
		helpSectionResponseDto.setTimesWord(Functions.getDeclension(page.getEditionsCount(), "раз", "раза", "раз"));
		List<HelpSectionDomain> children = isAdmin ? helpSectionService.getChildren(helpSectionDomain) : helpSectionService.getChildren(helpSectionDomain,true);
		helpSectionResponseDto.setChildren(children.stream().map(hs -> HelpSectionDto.toDto(hs)).collect(Collectors.toList()));
		return helpSectionResponseDto;
	}

	@RequestMapping("/help/{name}")
	public String showHelpPage(@PathVariable("name") String name, Model model) {
		HelpSectionDomain helpSectionDomain = helpSectionService.getByName(name);
		if (helpSectionDomain == null) {
			if (!SecurityUtils.getUserDetails().hasRole(Roles.ROLE_ADMIN)) {
				throw new ResourceNotFoundException();
			}
		}
		model.addAttribute("helpName", name);
		return "help";
	}

	@RequestMapping("/help/popup/{name}")
	public String showHelpPagePupup(@PathVariable("name") String name, Model model) {
		HelpSectionDomain helpSection = helpSectionService.getByName(name);
		Page page = pagesService.getById(helpSection.getPageId());
		model.addAttribute("page",page);
		return "helpWindowPopup";
	}

}

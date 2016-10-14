package ru.radom.kabinet.web.section;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.askor.blagosfera.core.services.cms.HelpSectionService;
import ru.askor.blagosfera.core.services.cms.PagesService;
import ru.askor.blagosfera.domain.cms.Page;
import ru.askor.blagosfera.domain.section.HelpSectionDomain;
import ru.askor.blagosfera.domain.section.SectionDomain;
import ru.radom.kabinet.dao.web.SectionDao;
import ru.radom.kabinet.json.SerializationManager;
import ru.radom.kabinet.model.web.Section;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.section.SectionService;
import ru.radom.kabinet.utils.CommonConstants;
import ru.radom.kabinet.web.section.dto.SectionDto;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SectionsController {
	private static final Logger logger = LoggerFactory.createLogger(SectionsController.class);

	@Autowired
	private SectionDao sectionDao;

	@Autowired
	private PagesService pagesService;
	
	@Autowired
	private SerializationManager serializationManager;

	@Autowired
	private SectionService sectionService;

	@Autowired
	private HelpSectionService helpSectionService;

	@RequestMapping("/sections/list.json")
	public @ResponseBody String list(@RequestParam(value = "parent_id", required = false) Section parent, @RequestParam(value = "query", required = false) String query) {
		return serializationManager.serializeCollection(sectionDao.getList(parent, query)).toString();
	}
	@RequestMapping("/section/popup/Благосфера/Руководство Пользователя")
	public String showUserGuidePagePupup(Model model)  {
		SectionDomain section = sectionService.getByLink("/Благосфера/Руководство Пользователя");
		Page page = pagesService.getById(section.getPageId());
		model.addAttribute("page", page);
		return "helpWindowPopup";
	}

	@RequestMapping(value = "/sections/current_sections.json", method = {RequestMethod.GET, RequestMethod.POST},  produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public List<SectionDto> getCurrentSections(@RequestParam(value = "link", required = true) String link) {
		List<HelpSectionDomain> helpSections = helpSectionService.getAllHelpSections();
		List<SectionDomain> rootSections = sectionService.getRoots((SecurityUtils.getUser() != null) ? SecurityUtils.getUser().getId() : null);
		List<SectionDomain> rootSectionsAll = sectionService.getRootsAll((SecurityUtils.getUser() != null) ? SecurityUtils.getUser().getId() : null);
		List<SectionDto> result = new ArrayList<>();
		SectionDomain activeSection = sectionService.findSectionByLink(link, SecurityUtils.getUser() != null ? SecurityUtils.getUser().getId() : null);
		Long rootActiveSectionId = getRootActiveSectionId(rootSectionsAll, activeSection.getId());


		for (SectionDomain section : rootSections) {
			SectionDto sectionDto = SectionDto.toDto(section, false);
			if (sectionDto.getId().equals(rootActiveSectionId)) {
				sectionDto.setActive(true);
				if (section.getChildren() != null) { // Устанавливаем активную ветку
					List<SectionDto> children = SectionDto.toDtoList(section.getChildren(), true);
					setActiveHierarchy(children, activeSection);
					sectionDto.setChildren(children);
				}
			} else {
				sectionDto.setActive(false);
			}
			result.add(sectionDto);
		}
		setHelpData(result, helpSections);
		return result;
	}

	private void setActiveHierarchy(List<SectionDto> sections, SectionDomain activeSection) {
		for (SectionDto sectionDto : sections) {
			if (sectionDto.getId().equals(activeSection.getId())) {
				sectionDto.setActive(true);
			} else if (sectionDto.getChildren() != null) {
				List<SectionDto> children = sectionDto.getChildren();
				for (SectionDto child : children) {
					if (child.getId().equals(activeSection.getId())) {
						sectionDto.setActive(true);
						child.setActive(true);
						break;
					}
				}
			}
			if (sectionDto.isActive()) {
				break;
			}
		}
	}

	private void setHelpData(List<SectionDto> sections, List<HelpSectionDomain> helpSections) {
		for (SectionDto sectionDto : sections) {
			boolean helpExists = false;
			boolean helpPublished = false;

			for (HelpSectionDomain helpSection : helpSections) {
				if (sectionDto.getHelpLink() != null && sectionDto.getHelpLink().equals(helpSection.getName())) {
					helpExists = true;
					helpPublished = helpSection.isPublished();
					break;
				}
			}
			sectionDto.setHelpExists(helpExists);
			sectionDto.setHelpPublished(helpPublished);

			if (sectionDto.getChildren() != null) {
				setHelpData(sectionDto.getChildren(), helpSections);
			}
		}
	}

	private Long getRootActiveSectionId(List<SectionDomain> rootSections, Long childActiveSectionId) {
		Long result = null;
		if (rootSections != null) {
			for (SectionDomain section : rootSections) {
				if (childActiveSectionId.equals(section.getId())) {
					result = section.getId();
					break;
				}
				if (section.getChildren() != null) {
					Long childActiveId = getRootActiveSectionId(section.getChildren(), childActiveSectionId);
					if (childActiveId != null) {
						result = section.getId();
					}
				}
				if (result != null) {
					break;
				}
			}
		}
		return result;
	}
}

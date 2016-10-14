package ru.radom.kabinet.web.admin;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.core.services.cms.PagesDataService;
import ru.askor.blagosfera.core.services.cms.PagesService;
import ru.askor.blagosfera.domain.cms.Page;
import ru.askor.blagosfera.domain.section.SectionDomain;
import ru.askor.blagosfera.domain.section.SectionType;
import ru.radom.kabinet.model.applications.Application;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.section.SectionDomainService;
import ru.radom.kabinet.services.web.sections.MenuEditService;
import ru.radom.kabinet.utils.CommonConstants;
import ru.radom.kabinet.web.admin.dto.SectionTreeNodeDto;
import ru.radom.kabinet.web.admin.dto.SectionsTreeDto;
import ru.radom.kabinet.web.section.dto.SectionDto;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/menu_edit")
public class MenuEditController {
	private static final Logger logger = LoggerFactory.createLogger(MenuEditController.class);
	@Autowired
	private PagesDataService pagesDataService;

	private enum Position {
		firstChild, lastChild, before, after
	}

	@Autowired
	private MenuEditService menuEditService;

	@Autowired
	private SectionDomainService sectionDomainService;

	@RequestMapping(value = "/{subportal}", method = RequestMethod.GET)
	public String showPage(Model model, @PathVariable("subportal") String subportalName) {
		model.addAttribute("sectionName", subportalName);
		return "adminMenuEdit";
	}

    @PreAuthorize("hasAnyRole('ADMIN', 'BLAGOSFERA_MENU_EDITOR')")
	@RequestMapping(value = "/save.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public SectionTreeNodeDto saveSection(
		@RequestParam(value = "section_id", required = false) Long sectionId,
		@RequestParam(value = "root_id", required = true) Long rootId,
		@RequestParam(value = "parent_id", required = false) Long parentSectionId,
		@RequestParam(value = "title", required = false) String title,
		@RequestParam(value = "position", required = false) Position position,
		@RequestParam(value = "related_id", required = false) Long relatedSectionId
	) {
		SectionDomain section = null;
		SectionDomain root = null;
		SectionDomain relatedSection = null;
		if (sectionId != null) {
			section = sectionDomainService.getById(sectionId);
		}
		if (rootId != null) {
			root = sectionDomainService.getById(rootId);
		}
		if (relatedSectionId != null) {
			relatedSection = sectionDomainService.getById(relatedSectionId);
		}

		if (section == null) {
			section = new SectionDomain();
			section.setPublished(true);
			section.setType(SectionType.EDITABLE);
			section.setLink(null);
			Page page = new Page();
			pagesDataService.savePage(page);
			section.setPageId(page.getId());

		}
		section.setParentId((parentSectionId == null || parentSectionId == 0l) ? rootId : parentSectionId);
		section.setTitle(title);
		determinePosition(section, relatedSection, position);
		section = menuEditService.saveSection(section, root, SecurityUtils.getUser());
		return getSectionTreeNode(section);
	}

    @PreAuthorize("hasAnyRole('ADMIN', 'BLAGOSFERA_MENU_EDITOR')")
	@RequestMapping(value = "/move.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public SectionDto moveSection(
			@RequestParam("section_id") Long sectionId,
			@RequestParam("root_id") Long rootId,
			@RequestParam("position") Position position,
			@RequestParam("related_id") Long relatedSectionId) {

		SectionDomain section = sectionDomainService.getById(sectionId);
		SectionDomain root = sectionDomainService.getById(rootId);
		SectionDomain relatedSection = sectionDomainService.getById(relatedSectionId);

		determinePosition(section, relatedSection, position);
		section = menuEditService.saveSection(section, root, SecurityUtils.getUser());
		return SectionDto.toDto(section, false);
	}

    @PreAuthorize("hasAnyRole('ADMIN', 'BLAGOSFERA_MENU_EDITOR')")
	@RequestMapping(value = "/edit.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public SectionDto editSection(
		@RequestParam("section_id") Long sectionId,
		@RequestParam("root_id") Long rootId,
		@RequestParam(value = "title", required = false) String title,
		@RequestParam(value = "link", required = false) String link,
		@RequestParam(value = "icon", required = false) String icon,
		@RequestParam(value = "help_link", required = false) String helpLink,
		@RequestParam(value = "type", required = true) SectionType type,
		@RequestParam(value = "application_id", required = false) Application application,
		@RequestParam(value = "published", defaultValue = "false") boolean published,
		@RequestParam(value = "openInNewLink", defaultValue = "false") boolean openInNewLink,
		@RequestParam(value = "disabled", defaultValue = "false")   boolean disabled,
		@RequestParam(value = "show_to_admin_users_only",defaultValue = "false") boolean showToAdminUsersOnly,
		@RequestParam(value = "show_to_verified_users_only",defaultValue = "false") boolean showToVerifiedUsersOnly,
		@RequestParam(value = "show_to_authorized_users_only",defaultValue = "false") boolean showToAuthorizedUsersOnly,
		@RequestParam(value = "min_registrator_level_to_show", required = false) Integer minRegistratorLevelToShow
	) {
		SectionDomain section = sectionDomainService.getById(sectionId);
		SectionDomain root = sectionDomainService.getById(rootId);
        section.setDisabled(disabled);
		section.setMinRegistratorLevelToShow(minRegistratorLevelToShow);
		section.setShowToVerfiedUsersOnly(showToVerifiedUsersOnly);
		section.setShowToAdminUsersOnly(showToAdminUsersOnly);
		section.setTitle(title);
		section.setOpenInNewLink(openInNewLink);
		section.setLink(link);
		section.setIcon(icon);
		section.setHelpLink(helpLink);
		section.setPublished(published);
		section.setType(type);
		section.setShowToAuthorizedUsersOnly(showToAuthorizedUsersOnly);
		//section.setApplication(application);

		section = menuEditService.editSection(section, root, SecurityUtils.getUser());
		return SectionDto.toDto(section, false);
	}

	@RequestMapping(value = "/load.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public SectionDto loadSection(@RequestParam("section_id") Long sectionId) {
		SectionDomain section = sectionDomainService.getById(sectionId);
		return SectionDto.toDto(section, false);
	}

	@RequestMapping(value = "/loadByName.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public SectionDto loadSectionByName(@RequestParam("section_name") String sectionName) {
		SectionDomain section = sectionDomainService.getByName(sectionName);
		return SectionDto.toDto(section, false);
	}

    @PreAuthorize("hasAnyRole('ADMIN', 'BLAGOSFERA_MENU_EDITOR')")
	@RequestMapping(value = "/delete.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
	@ResponseBody
	public SectionDto deleteSection(
		@RequestParam(value = "section_id", required = false) Long sectionId,
		@RequestParam(value = "root_id", required = false) Long rootId
	) {
		SectionDomain section = sectionDomainService.getById(sectionId);
		SectionDomain root = sectionDomainService.getById(rootId);
		section = menuEditService.deleteSection(section, root, SecurityUtils.getUser());
		return SectionDto.toDto(section, false);
	}

	@RequestMapping("/tree.json")
	@ResponseBody
	public SectionsTreeDto tree(
			@RequestParam("id") Long parentId,
			@RequestParam(value = "root_id", required = false) Long rootId) {
		SectionsTreeDto result;
		if (parentId == null || parentId == 0l) {
			result = getSectionsList(rootId);
		} else {
			result = getSectionsList(parentId);
		}
		return result;
	}

	private SectionTreeNodeDto getSectionTreeNode(SectionDomain section) {
		return new SectionTreeNodeDto(section, sectionDomainService.getHierarchy(section.getId()).size() - 1);
	}

	private SectionsTreeDto getSectionsList(Long parentId) {
		List<SectionTreeNodeDto> treeSections = new ArrayList<>();
		SectionDomain parentSection = sectionDomainService.getById(parentId);
		List<SectionDomain> sections = parentSection.getChildren();
		for (SectionDomain section : sections) {
			treeSections.add(getSectionTreeNode(section));
		}
		return new SectionsTreeDto(treeSections);
	}

	private void determinePosition(SectionDomain section, SectionDomain relatedSection, Position position) {
		if (relatedSection != null) {
			switch (position) {
			case after:
				section.setParentId(relatedSection.getParentId());
				section.setPosition(relatedSection.getPosition() + 1);
				break;
			case before:
				section.setParentId(relatedSection.getParentId());
				section.setPosition(relatedSection.getPosition() - 1);
				break;
			case firstChild:
				section.setParentId(relatedSection.getId());
				section.setPosition(sectionDomainService.determineFirstPosition(relatedSection.getId()));
				break;
			case lastChild:
				section.setParentId(relatedSection.getId());
				section.setPosition(sectionDomainService.determineLastPosition(relatedSection.getId()));
				break;
			default:
				break;
			}
		}
	}

}

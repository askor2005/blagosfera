package ru.radom.kabinet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.askor.blagosfera.core.services.contacts.ContactsGroupService;
import ru.askor.blagosfera.domain.contacts.ContactGroup;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.utils.WebUtils;
import ru.radom.kabinet.web.contacts.dto.ContactGroupDto;

import javax.servlet.http.HttpServletRequest;

@Controller
public class GroupsController {

	@Autowired
	private ContactsGroupService contactsGroupService;
	
	@RequestMapping("/contacts/lists")
	public String showGroupsPage(Model model, HttpServletRequest request) {
		model.addAttribute("currentPageTitle", "Организационные списки контактов");
		model.addAttribute("leftSidebarActiveItem", "/contacts");
		String referer = WebUtils.urlDecode(WebUtils.getCookie(request.getCookies(), "EDIT_CONTACTS_LISTS_REFERER", "/contacts"));
		model.addAttribute("referer", referer);
		return "contactsLists";
	}

	@RequestMapping(value = "/contacts/lists/edit", method = RequestMethod.GET)
	public String showGroupEditPage(Model model, @RequestParam(value = "id", required = false) Long id) {
		model.addAttribute("groupId", id);
		model.addAttribute("currentPageTitle", "Организационные списки контактов");
		model.addAttribute("leftSidebarActiveItem", "/contacts");
		//model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Организационные списки", "/contacts/lists").add(group == null ? "Создать" : group.getName(), group == null ? "/contacts/lists/edit" : "/contacts/lists/edit?id=" + group.getId()));
		return "contactsListsEdit";
	}
	@RequestMapping(value = "/contacts/lists/get.json", method = RequestMethod.GET)
	public @ResponseBody
	ContactGroupDto getGroupForEdit(@RequestParam(value = "id", required = true) Long id) {
		ContactGroup contactsGroup = contactsGroupService.getByUserAndId(SecurityUtils.getUser().getId(), id);
		if (contactsGroup == null)
			throw new ResourceNotFoundException();
		return ContactGroupDto.toDto(contactsGroup);
	}

	@RequestMapping(value = "/contacts/lists/edit.json", method = RequestMethod.POST)
	public @ResponseBody String saveGroup(@RequestParam("name") String name, @RequestParam(value = "color",required = false, defaultValue = "1") int color,@RequestParam(value = "id",required = false) Long id) throws Exception {
		contactsGroupService.saveGroup(SecurityUtils.getUser().getId(), name, color, id);
		return "/contacts/lists";
	}

	@RequestMapping(value = "/contacts/lists/delete.json", method = RequestMethod.GET)
	public String deleteGroup(@RequestParam(value = "id", required = true) Long groupId) {
		contactsGroupService.deleteGroup(groupId,SecurityUtils.getUser().getId());
		return "redirect:/contacts/lists";
	}

}

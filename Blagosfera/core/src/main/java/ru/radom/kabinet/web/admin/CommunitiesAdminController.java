package ru.radom.kabinet.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.radom.kabinet.dao.communities.CommunityActivityScopeDao;
import ru.radom.kabinet.json.SerializationManager;
import ru.radom.kabinet.model.communities.CommunityActivityScope;
import ru.radom.kabinet.services.communities.CommunitiesAdminService;
import ru.radom.kabinet.services.communities.CommunityException;
import ru.radom.kabinet.utils.JsonUtils;
import ru.radom.kabinet.web.utils.Breadcrumb;

@Controller
@RequestMapping("/admin/communities")
public class CommunitiesAdminController {

	@Autowired
	private SerializationManager serializationManager;

	@Autowired
	private CommunityActivityScopeDao communityActivityScopeDao;

	@Autowired
	private CommunitiesAdminService communitiesAdminService;
	
	@RequestMapping(value = "/activity_scopes", method = RequestMethod.GET)
	public String showActivityScopesPage(Model model) {
		model.addAttribute("breadcrumb", new Breadcrumb());
		return "adminCommunityActivityScopes";
	}

	@RequestMapping(value = "/activity_scopes/list.json", method = RequestMethod.GET)
	public @ResponseBody String getActivityScopesList() {
		return serializationManager.serializeCollection(communityActivityScopeDao.getAll()).toString();
	}

	@RequestMapping(value = "/activity_scopes/save.json", method = RequestMethod.POST)
	public @ResponseBody String saveActivityScope(@RequestParam(value = "id", required = false) CommunityActivityScope scope, @RequestParam(value = "name", required = false) String name) {
		if (scope == null) {
			scope = new CommunityActivityScope();
		}
		scope.setName(name);
		scope = communitiesAdminService.saveActivityScope(scope);
		return JsonUtils.getSuccessJson().toString();
	}

	@RequestMapping(value = "/activity_scopes/delete.json", method = RequestMethod.POST)
	public @ResponseBody String deleteActivityScope(@RequestParam("id") CommunityActivityScope scope) {
		scope = communitiesAdminService.deleteActivityScope(scope);
		return serializationManager.serializeCollection(communityActivityScopeDao.getAll()).toString();
	}

	@ExceptionHandler(CommunityException.class)
	public @ResponseBody String handleException(CommunityException e) {
		return JsonUtils.getErrorJson(e.getMessage()).toString();
	}
	
}

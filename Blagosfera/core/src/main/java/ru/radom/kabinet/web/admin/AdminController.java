package ru.radom.kabinet.web.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.core.services.cms.HelpSectionService;
import ru.askor.blagosfera.core.services.cms.PagesService;
import ru.askor.blagosfera.core.services.notification.SmsService;
import ru.askor.blagosfera.core.services.security.AuthService;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.core.web.pageedition.services.PageEditionService;
import ru.askor.blagosfera.crypt.domain.HttpException;
import ru.askor.blagosfera.domain.cms.Page;
import ru.askor.blagosfera.domain.notification.sms.SmsNotification;
import ru.askor.blagosfera.domain.notification.sms.SmsNotificationType;
import ru.askor.blagosfera.domain.section.HelpSectionDomain;
import ru.askor.blagosfera.domain.section.SectionDomain;
import ru.askor.blagosfera.domain.settings.SystemSettingsPage;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.SharerService;
import ru.radom.kabinet.collections.RolesList;
import ru.radom.kabinet.dao.RameraTextDao;
import ru.radom.kabinet.dao.RoleDao;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.SmtpServerDao;
import ru.radom.kabinet.dao.applications.ApplicationDao;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.dao.communities.CommunityPermissionDao;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.dao.fields.FieldsGroupDao;
import ru.radom.kabinet.dao.fields.MetaFieldDao;
import ru.radom.kabinet.dao.web.SectionDao;
import ru.radom.kabinet.dto.StringObjectHashMap;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.expressions.Functions;
import ru.radom.kabinet.json.FieldSerializer;
import ru.radom.kabinet.json.SerializationManager;
import ru.radom.kabinet.model.RameraTextEntity;
import ru.radom.kabinet.model.SmtpServer;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.applications.Application;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldsGroupEntity;
import ru.radom.kabinet.model.fields.MetaField;
import ru.radom.kabinet.model.web.Section;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.AdminService;
import ru.radom.kabinet.services.ApplicationException;
import ru.radom.kabinet.services.ApplicationsService;
import ru.radom.kabinet.services.NotificationService;
import ru.radom.kabinet.services.section.SectionService;
import ru.radom.kabinet.utils.CommonConstants;
import ru.radom.kabinet.utils.JsonUtils;
import ru.radom.kabinet.utils.StringUtils;
import ru.radom.kabinet.utils.WebUtils;
import ru.radom.kabinet.web.admin.dto.*;
import ru.radom.kabinet.web.utils.Breadcrumb;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private HelpSectionService helpSectionService;

    @Autowired
    private SharerService sharerService;

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private SectionDao sectionDao;

    @Autowired
    private PageEditionService pageEditionService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private PagesService pagesService;

    @Autowired
    private SerializationManager serializationManager;

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private ApplicationsService applicationsService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private SmtpServerDao smtpServerDao;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FieldSerializer fieldSerializer;

    @Autowired
    private FieldsGroupDao fieldsGroupDao;

    @Autowired
    private FieldDao fieldDao;

    @Autowired
    private MetaFieldDao metaFieldDao;

    @Autowired
    private RameraTextDao rameraTextDao;

    @Autowired
    private AuthService authService;

    @Autowired
    private CommunityPermissionDao communityPermissionDao;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private SmsService smsService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(value = "/admin/smstest", method = RequestMethod.GET)
    public String smstest() {
        return "smstest";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(value = "/admin/smstest/sendsms.json", method = RequestMethod.POST)
    @ResponseBody
    public String smssend(@RequestParam(name = "type") SmsNotificationType type,
                          @RequestParam(name = "tel") String tel,
                          @RequestParam(name = "text") String text) {
        try {
            return smsService.send(new SmsNotification(type, tel, text));
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }  catch (HttpException | NoSuchAlgorithmException | UnsupportedEncodingException | JsonProcessingException e) {
            return ("ERROR");
        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Roles
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @RequestMapping(value = "/admin/roles/{ikp}", method = RequestMethod.GET)
    public String showEditRolesPage(@PathVariable("ikp") String ikp, Model model) {
        UserEntity profile = sharerDao.getByIkp(ikp);
        model.addAttribute("profile", profile);
        model.addAttribute("roles", roleDao.getAll());
        return "adminRoles";
    }

    @RequestMapping(value = "/admin/roles/{ikp}", method = RequestMethod.POST)
    public String saveRoles(@PathVariable("ikp") String ikp, @RequestParam(value = "role_id", required = false) RolesList roles) {
        UserEntity profile = sharerDao.getByIkp(ikp);

        profile.getRoles().clear();
        profile.getRoles().addAll(roles);

        sharerDao.update(profile);
        return "redirect:/admin/roles/" + profile.getIkp();
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Page
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @RequestMapping(value = "/admin/page/{page_id}/edit", method = RequestMethod.GET)
    public String showEditPagePage(@PathVariable("page_id") Long pageId, Model model) {
        model.addAttribute("pageId", pageId);
       /* PageEntity page = pageDao.getById(pageId);
        Section section = sectionDao.getByPage(page);
        model.addAttribute("currentSection", section);
        model.addAttribute("page", page);
        model.addAttribute("section", section);

        Date date = new Date();
        if (page.getCurrentEditor() != null && page.getCurrentEditorEditDate() != null) {
            // Страницу можно редактировать
            if ((date.getTime() - page.getCurrentEditorEditDate().getTime() > PageEntity.MILLISECONDS_FOR_RELEASE_PAGE) ||
                    page.getCurrentEditor().getId().equals(SecurityUtils.getUser().getId())) {
                page.setCurrentEditor(sharerDao.getById(SecurityUtils.getUser().getId()));
                page.setCurrentEditorEditDate(date);
                pageDao.update(page);
            } else { // Страница ещё редактируется
                model.addAttribute("currentEditor", page.getCurrentEditor());
            }
        } else {
            page.setCurrentEditor(sharerDao.getById(SecurityUtils.getUser().getId()));
            page.setCurrentEditorEditDate(date);
            pageDao.update(page);
        }*/
        return "adminPageEdit";
    }

    @RequestMapping(value = "/admin/page/{page_id}/edit.json", method = RequestMethod.POST)
    public
    @ResponseBody
    SuccessResponseDto editPage(
            @PathVariable("page_id") Long pageId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "keywords", required = false) String keywords,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "accessType", required = false) String accessType) throws Exception {
        pagesService.editPage(pageId, SecurityUtils.getUser().getId(), title, description, keywords, content, accessType);
        return SuccessResponseDto.get();
    }

    @RequestMapping(value = "/admin/page/{page_id}/edit/publish.json", method = RequestMethod.POST)
    public
    @ResponseBody
    SuccessResponseDto publishPage(@PathVariable("page_id") Long pageId) throws Exception {
        pagesService.publishPage(pageId);
        return SuccessResponseDto.get();
    }

    @RequestMapping(value = "/admin/page/{page_id}/get.json", method = RequestMethod.GET)
    public
    @ResponseBody
    PageResponseDto getPage(@PathVariable("page_id") Long pageId, @RequestParam(required = false) Boolean lock) {
        Page page = pagesService.getById(pageId);
        if (page == null) {
            throw new RuntimeException("Страница с таким id не найдена!");
        }
        if ((lock != null) && (lock)) {
            pagesService.tryLockPage(pageId);
        }
        PageResponseDto response = new PageResponseDto();
        response.setPage(PageDto.createFromPage(page));
        User currentEditor = pagesService.getCurrentEditor(pageId);
        response.setCurrentEditor(currentEditor != null ? Functions.getSharerPadeg(currentEditor, 5) : null);//имя пользователя нужно в падеже
        SectionDomain section = pagesService.getSectionForPage(pageId);
        response.setPublished(section.isPublished());
        response.setAccessType(section.getAccessType() != null ? section.getAccessType().name() : null);
        response.setSectionLink(section.getLink());
        return response;
    }


    @RequestMapping(value = "/admin/page/{page_id}/edit/unpublish.json", method = RequestMethod.POST)
    public
    @ResponseBody
    SuccessResponseDto unpublishPage(@PathVariable("page_id") Long pageId) throws Exception {
        pagesService.unPublishPage(pageId);
        return SuccessResponseDto.get();
    }

    @RequestMapping(value = "/admin/page/{page_id}/edit/setCurrentEditor.json", method = RequestMethod.POST)
    @ResponseBody
    public SuccessResponseDto setCurrentEditor(@PathVariable("page_id") Long pageId) {
        pagesService.prolongPageEdition(pageId);
        return SuccessResponseDto.get();
    }

    @RequestMapping(value = "/admin/page/{page_id}/edit/releasePage.json", method = RequestMethod.POST)
    @ResponseBody
    public SuccessResponseDto releasePage(@PathVariable("page_id") Long pageId) {
        pagesService.releasePage(pageId);
        return SuccessResponseDto.get();
    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Help
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @RequestMapping(value = "/admin/help", method = RequestMethod.GET)
    public String showHelpPage(@RequestParam(value = "parent_id", required = false) Long parentId, Model model) {
        model.addAttribute("currentHelpSectionId", parentId);
        return "adminHelp";
    }

    @RequestMapping(value = "/admin/help/edit/{id}", method = RequestMethod.GET)
    public String showHelpEditPage(@PathVariable("id") Long helpSectionId, Model model) {
        model.addAttribute("helpSectionId", helpSectionId);
        return "adminHelpEdit";
    }

    @RequestMapping(value = "/admin/help/roots", method = RequestMethod.GET)
    public
    @ResponseBody
    List<HelpSectionDto> getRoots() {
        List<HelpSectionDomain> roots = helpSectionService.getRoots();
        return roots.stream().map(hs -> HelpSectionDto.toDto(hs)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/admin/help/get/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    HelpSectionResponseDto getPageForEdit(@PathVariable("id") Long helpSectionId, @RequestParam(value = "children", required = false) Boolean children) {
        HelpSectionResponseDto helpSectionResponseDto = new HelpSectionResponseDto();
        HelpSectionDomain helpSectionDomain = helpSectionService.getById(helpSectionId);
        if (helpSectionDomain == null) {
            List<HelpSectionDomain> roots = helpSectionService.getRoots();
            helpSectionResponseDto.setChildren(roots.stream().map(hs -> HelpSectionDto.toDto(hs)).collect(Collectors.toList()));
            return helpSectionResponseDto;
        }
        helpSectionResponseDto.setCurrentHelpSection(HelpSectionDto.toDto(helpSectionDomain));
        Page page = pagesService.getById(helpSectionDomain.getPageId());
        helpSectionResponseDto.setEditionsCount(page.getEditionsCount());
        helpSectionResponseDto.setPage(PageDto.createFromPage(page));
        if ((children != null) && (children)) {
            helpSectionResponseDto.setChildren(helpSectionService.getChildren(helpSectionDomain).stream().map(hs -> HelpSectionDto.toDto(hs)).collect(Collectors.toList()));
        }
        return helpSectionResponseDto;
    }

    /**
     * Генерация раздела справки при нажатии на ссылку у раздела портала
     *
     * @param sectionId
     * @return
     */
    @RequestMapping(value = "/admin/help/generate.json", method = RequestMethod.POST)
    public
    @ResponseBody
    HelpSectionDto generateHelpSection(@RequestParam(value = "sectionId") Long sectionId) {
        HelpSectionDomain helpSectionDomain = helpSectionService.generateHelpSection(sectionId);
        return HelpSectionDto.toDto(helpSectionDomain);
    }

    @RequestMapping(value = "/admin/help/create.json", method = RequestMethod.POST)
    public
    @ResponseBody
    HelpSectionDto createHelpSection(@RequestParam(value = "parent_id", required = false) Long parentId, @RequestParam(value = "name") String name) {
        HelpSectionDomain helpSectionDomain = helpSectionService.createHelpSection(parentId, name);
        return HelpSectionDto.toDto(helpSectionDomain);
    }

    @RequestMapping(value = "/admin/help/edit.json", method = RequestMethod.POST)
    public
    @ResponseBody
    String editHelpSection(@RequestParam(value = "id") Long helpSectionId, @RequestParam(value = "name") String name, @RequestParam(value = "title") String title, @RequestParam(value = "description") String description, @RequestParam(value = "keywords") String keywords, @RequestParam(value = "content") String content) throws Exception {
        helpSectionService.updateHelpSection(helpSectionId, name, title, description, keywords, content, SecurityUtils.getUser().getId());
        return name;

    }

    @RequestMapping(value = "/admin/help/delete.json", method = RequestMethod.POST)
    public
    @ResponseBody
    SuccessResponseDto deleteHelpSection(@RequestParam(value = "id") Long helpSectionId) {
        helpSectionService.deleteHelpSection(helpSectionId);
        return SuccessResponseDto.get();
    }

    @RequestMapping(value = "/admin/help/publish.json", method = RequestMethod.POST)
    public
    @ResponseBody
    SuccessResponseDto publishHelpSection(@RequestParam(value = "id") Long helpSectionId) {
        helpSectionService.publishPage(helpSectionId);
        return SuccessResponseDto.get();
    }

    @RequestMapping(value = "/admin/help/unpublish.json", method = RequestMethod.POST)
    public
    @ResponseBody
    SuccessResponseDto unpublishHelpSection(@RequestParam(value = "id") Long helpSectionId) {
        helpSectionService.unPublishPage(helpSectionId);
        return SuccessResponseDto.get();
    }

    @RequestMapping(value = "/admin/help/set_parent.json", method = RequestMethod.POST)
    public
    @ResponseBody
    SuccessResponseDto setParentHelpSection(@RequestParam(value = "id") Long helpSectionId, @RequestParam(value = "parent_id", required = false) Long parentHelpSectionId) {
        helpSectionService.setParent(helpSectionId, parentHelpSectionId);
        return SuccessResponseDto.get();
    }

    @RequestMapping(value = "/admin/help/tree.json", method = RequestMethod.GET)
    public
    @ResponseBody
    List<HelpSectionTreeItem> helpTree(@RequestParam(value = "node", required = false) String parentIdString, @RequestParam(value = "current_help_section_id", required = true) Long currentHelpSectionId) {
        Long parentId = null;
        //TODO выпилить это и заменить параметр на long когда выпилят ext js, тк сейчас он подставляет node=root и происходит classcastexception
        try {
            parentId = Long.parseLong(parentIdString);
        } catch (NumberFormatException e) {

        }
        HelpSectionDomain currentHelpSection = helpSectionService.getById(currentHelpSectionId);
        if (currentHelpSection == null) {
            throw new RuntimeException("current_help_section с таким id не найдена!");
        }
        List<HelpSectionDomain> children = null;
        if (parentId == null) {
            children = helpSectionService.getRoots();
        } else {
            HelpSectionDomain parent = helpSectionService.getById(parentId);
            if (parent == null) {
                throw new RuntimeException("node с таким id не найдена!");
            }
            children = helpSectionService.getChildren(parent);
        }
        List<HelpSectionDomain> hierarchy = helpSectionService.getHierarchy(currentHelpSection);
        return children.stream().filter(child -> child.getId() != currentHelpSectionId).map(child -> {
            HelpSectionTreeItem item = new HelpSectionTreeItem();
            item.setId(child.getId());
            item.setText(helpSectionService.getTitle(child));
            item.setLeaf(helpSectionService.getChildren(child).size() == 0);
            item.setChecked((currentHelpSection.getParentId() != null) && (child.getId().equals(currentHelpSection.getParentId())));
            item.setExpanded(hierarchy.contains(child));
            return item;

        }).collect(Collectors.toList());
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Apps
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @RequestMapping(value = "/admin/apps/edit/{id}", method = RequestMethod.GET)
    public String showApplicationEditPage(@PathVariable("id") Application application, Model model) {
        model.addAttribute("application", application);
        model.addAttribute("breadcrumb", new Breadcrumb());
        List<Section> featuresLibrarySections = sectionDao.getByLinkPrefix("/features_library/");
        model.addAttribute("featuresLibrarySections", featuresLibrarySections);
        model.addAttribute("currentSection", application.getFeaturesLibrarySection());
        Map<Section, String> featuresLibrarySectionsMap = new HashMap<Section, String>();
        for (Section section : featuresLibrarySections) {
            List<Section> hierarchy = sectionDao.getHierarchy(section);
            String label = "";
            for (Section hierarchyElement : hierarchy) {
                if (StringUtils.hasLength(label)) {
                    label += " -> ";
                }
                label += hierarchyElement.getTitle();
            }
            featuresLibrarySectionsMap.put(section, label);
        }
        model.addAttribute("featuresLibrarySectionsMap", featuresLibrarySectionsMap);

        //model.addAttribute("CommunityAssociationFormsGroups", communityAssociationFormsGroupDao.getAll());

        return "adminApplicationEdit";
    }

    @RequestMapping(value = "/admin/apps/create", method = RequestMethod.GET)
    public String showApplicationCreatePage(@RequestParam(value = "features_library_section_id") Section featuresLibrarySection, Model model) {
        model.addAttribute("breadcrumb", new Breadcrumb());
        model.addAttribute("featuresLibrarySection", featuresLibrarySection);
        model.addAttribute("currentSection", featuresLibrarySection);
        List<Section> featuresLibrarySections = sectionDao.getByLinkPrefix("/features_library/");
        model.addAttribute("featuresLibrarySections", featuresLibrarySections);
        Map<Section, String> featuresLibrarySectionsMap = new HashMap<Section, String>();
        for (Section section : featuresLibrarySections) {
            List<Section> hierarchy = sectionDao.getHierarchy(section);
            String label = "";
            for (Section hierarchyElement : hierarchy) {
                if (StringUtils.hasLength(label)) {
                    label += " -> ";
                }
                label += hierarchyElement.getTitle();
            }
            featuresLibrarySectionsMap.put(section, label);
        }
        model.addAttribute("featuresLibrarySectionsMap", featuresLibrarySectionsMap);

        model.addAttribute("defaultLogoUrl", Application.DEFAULT_LOGO_URL);
        //model.addAttribute("CommunityAssociationFormsGroups", communityAssociationFormsGroupDao.getAll());

        return "adminApplicationEdit";
    }

    private Application readApplicationFromRequest(HttpServletRequest request) {
        Application application = applicationDao.getById(ServletRequestUtils.getLongParameter(request, "id", -1L));
        if (application == null) {
            application = new Application();
        }
        application.setName(ServletRequestUtils.getStringParameter(request, "name", null));
        application.setDescription(ServletRequestUtils.getStringParameter(request, "description", null));
        application.setCost(StringUtils.parseMoney(ServletRequestUtils.getStringParameter(request, "cost", "0.00"), BigDecimal.ZERO));
        Section featuresLibrarySection = sectionDao.getById(ServletRequestUtils.getLongParameter(request, "features_library_section_id", -1L));
        application.setFeaturesLibrarySection(featuresLibrarySection);
        application.setIframeUrl(ServletRequestUtils.getStringParameter(request, "iframe_url", null));
        application.setLogoUrl(ServletRequestUtils.getStringParameter(request, "logo_url", null));
        application.setRedirectUri(ServletRequestUtils.getStringParameter(request, "redirect_uri", null));
        application.setForCommunities(ServletRequestUtils.getBooleanParameter(request, "for_communities", false));
/*
        List<CommunityAssociationForm> communityAssociationForms = communityAssociationFormDao.getByIds(ServletRequestUtils.getLongParameters(request, "community_association_form_id"));
        application.setCommunityAssociationForms(communityAssociationForms);*/

        return application;
    }

    @RequestMapping(value = "/admin/apps/save.json", method = RequestMethod.POST)
    public
    @ResponseBody
    String saveApplication(HttpServletRequest request) {
        try {
            Application application = readApplicationFromRequest(request);
            application = applicationsService.saveApplication(application);
            return serializationManager.serialize(application).toString();
        } catch (ApplicationException e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }

    }

    @RequestMapping(value = "/admin/apps/generate_client_id_and_secret.json", method = RequestMethod.POST)
    public
    @ResponseBody
    String generateClientIdAndSecret(@RequestParam(value = "id") Application application) {
        application = applicationsService.generateClientIdAndSecret(application);
        StringObjectHashMap payload = new StringObjectHashMap();
        payload.put("clientId", application.getClientId());
        payload.put("clientSecret", application.getClientSecret());
        return serializationManager.serialize(payload).toString();
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // SystemSettings
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @RequestMapping(value = "/admin/systemSettings", method = RequestMethod.GET)
    public String showAdminPage(Model model) {
        model.addAttribute("currentPageTitle", "Настройки системы");
        model.addAttribute("systemSettingForm", new SystemSettingForm());
        model.addAttribute("smtpServerForm", new SmtpServer());
        return "adminSystemSettingsPage";
    }

    @RequestMapping(value = "/admin/systemSettings/systemSettings.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    @ResponseBody
    public SystemSettingsPage getSystemSettingsList(@RequestParam(value = "key", required = false) String key,
                                                    @RequestParam(value = "description", required = false) String description,
                                                    @RequestParam(value = "page", required = true) int page,
                                                    @RequestParam(value = "limit", required = true) int size) {
        return settingsManager.getSystemSettings(page - 1, size, key, description);
    }

    @RequestMapping(value = "/admin/systemSettings/saveSystemSetting", method = RequestMethod.POST)
    public
    @ResponseBody
    String saveSystemSetting(@ModelAttribute("systemSettingForm") SystemSettingForm systemSettingForm) {
        try {
            adminService.saveSystemSetting(systemSettingForm);
            return JsonUtils.getSuccessJson().toString();
        } catch (Exception e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    @RequestMapping(value = "/admin/systemSettings/deleteSystemSetting", method = RequestMethod.POST)
    public
    @ResponseBody
    String deleteSystemSetting(@RequestParam("id") Long id) {
        try {
            settingsManager.deleteSystemSetting(id);
            return JsonUtils.getSuccessJson().toString();
        } catch (Exception e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    @RequestMapping(value = "/admin/systemSettings/SmtpServers.json", produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE, method = RequestMethod.POST)
    @ResponseBody
    public SmtpServerResponseListDto getSmtpServersList(
            @RequestBody String body,
            @RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "limit", defaultValue = "15") int limit) {
        SmtpServerResponseListDto result;
        try {
            HashMap<String, String> filters = new HashMap<>();
            filters.put("host", getValueOfParameter(body, "host"));

            String sort = getValueOfParameter(body, "sort");
            List<SmtpServer> list = smtpServerDao.getList(filters, start, limit, sort);

            result = SmtpServerResponseListDto.successDto(
                    smtpServerDao.getCount(filters),
                    list
            );
        } catch (Exception e) {
            result = SmtpServerResponseListDto.errorDto();
        }
        return result;
    }

    @RequestMapping(value = "/admin/systemSettings/saveSmtpServer", method = RequestMethod.POST)
    @ResponseBody
    public String saveSmtpServer(@ModelAttribute("smtpServerForm") SmtpServer smtpServer) {
        try {
            adminService.saveSmtpServer(smtpServer);
            return JsonUtils.getSuccessJson().toString();
        } catch (Exception e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    @RequestMapping(value = "/admin/systemSettings/deleteSmtpServer", method = RequestMethod.POST)
    public
    @ResponseBody
    String deleteSmtpServer(@RequestParam("id") Long id) {
        try {
            smtpServerDao.delete(id);
            return JsonUtils.getSuccessJson().toString();
        } catch (Exception e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Notifications
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------
/*
    @RequestMapping(value = "/admin/notifications", method = RequestMethod.GET)
    public String showNotificationsPage(Model model) {
        model.addAttribute("templates", notificationService.getTemplates());
        return "adminNotificationsPage";
    }

    @RequestMapping(value = "/admin/notifications/save.json", method = RequestMethod.POST)
    public
    @ResponseBody
    String saveNotificationTemplate(NotificationTemplate template) {
        notificationService.saveTemplate(template);
        return JsonUtils.getSuccessJson().toString();
    }

    @RequestMapping(value = "/admin/notifications/delete.json", method = RequestMethod.POST)
    public
    @ResponseBody
    String deleteNotificationTemplate(@RequestParam(value = "template_id") NotificationTemplate template) {
        notificationService.deleteTemplate(template);
        return JsonUtils.getSuccessJson().toString();
    }

    @RequestMapping(value = "/admin/notifications/get.json", method = RequestMethod.GET)
    public
    @ResponseBody
    String getNotificationTemplate(@RequestParam(value = "template_id") NotificationTemplate template) {
        return serializationManager.serialize(template).toString();
    }

    @RequestMapping(value = "/admin/notifications/list.json", method = RequestMethod.GET)
    public
    @ResponseBody
    String getNotificationTemplates() {
        return serializationManager.serializeCollection(notificationService.getTemplates()).toString();
    }*/

    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // EditFields
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @RequestMapping(value = "/admin/adminEditFields", method = RequestMethod.GET)
    public String showAdminEditFieldsPage(Model model) {
        model.addAttribute("currentPageTitle", "Редактирование полей");
        model.addAttribute("fieldGroupForm", new FieldsGroupEntity());
        model.addAttribute("fieldForm", new FieldEntity());
        return "adminEditFieldsPage";
    }

    @RequestMapping(value = "/admin/adminEditFields/getField.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public
    @ResponseBody
    String getAdminEditField(@RequestParam(value = "fieldId", defaultValue = "-1") FieldEntity field) {
        try {
            return fieldSerializer.serialize(field).toString();
        } catch (Exception e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    @RequestMapping(value = "/admin/adminEditFields/saveFieldGroup", method = RequestMethod.POST)
    public
    @ResponseBody
    String saveAdminEditFieldGroup(@ModelAttribute("fieldGroup") FieldsGroupEntity fieldsGroup) {
        try {
            fieldsGroupDao.save(fieldsGroup);
            return JsonUtils.getSuccessJson().toString();
        } catch (Exception e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    @RequestMapping(value = "/admin/adminEditFields/saveField", method = RequestMethod.POST)
    public
    @ResponseBody
    String saveAdminEditField(@ModelAttribute("fieldForm") FieldEntity field) {
        try {
            adminService.saveField(field);
            return JsonUtils.getSuccessJson().toString();
        } catch (Exception e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    @RequestMapping(value = "/admin/adminEditFields/saveMetaField", method = RequestMethod.POST)
    public
    @ResponseBody
    String saveAdminEditMetaField(@ModelAttribute("metaFieldForm") MetaField metaField) {
        try {
            metaFieldDao.save(metaField);
            return JsonUtils.getSuccessJson().toString();
        } catch (Exception e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    @RequestMapping(value = "/admin/adminEditFields/deleteField", method = RequestMethod.POST)
    public
    @ResponseBody
    String deleteAdminEditField(@RequestParam("id") Long id, @RequestParam("isMetaField") Boolean isMetaField) {
        try {
            if (!isMetaField) {
                fieldDao.delete(id);
            } else {
                metaFieldDao.delete(id);
            }
            return JsonUtils.getSuccessJson().toString();
        } catch (Exception e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    private String getValueOfParameter(String body, String parameter) {
        String paramValue = "";
        body = WebUtils.urlDecode(body);
        for (String pair : body.split("&")) {
            String[] parts = pair.split("=");
            if (parts.length > 1) {
                String key = parts[0];
                String value = parts[1];
                if (parameter.equals(key)) {
                    paramValue = value.replaceAll("'", "''");
                }
            }
        }
        return paramValue;
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // RameraTexts
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @RequestMapping(value = "/admin/rameraTexts", method = RequestMethod.GET)
    public String showRameraTextsPage(Model model) {
        model.addAttribute("rameraTexts", rameraTextDao.findAll());
        return "adminRameraTextsPage";
    }

    @RequestMapping(value = "/admin/rameraTexts/get.json", method = RequestMethod.GET)
    @ResponseBody
    public String getRameraText(@RequestParam(value = "ramera_text_id") RameraTextEntity rameraText) {
        return serializationManager.serialize(rameraText).toString();
    }

    @RequestMapping(value = "/admin/rameraTexts/delete.json", method = RequestMethod.POST)
    @ResponseBody
    public String deleteRameraText(@RequestParam(value = "ramera_text_id") RameraTextEntity rameraText) {
        rameraTextDao.delete(rameraText);
        return JsonUtils.getSuccessJson().toString();
    }

    @RequestMapping(value = "/admin/rameraTexts/save.json", method = RequestMethod.POST)
    @ResponseBody
    public String saveRameraText(RameraTextEntity rameraText) {
        rameraTextDao.saveOrUpdate(rameraText);
        return JsonUtils.getSuccessJson().toString();
    }

    @RequestMapping(value = "/admin/rameraTexts/list.json", method = RequestMethod.GET)
    @ResponseBody
    public String getRameraTexts() {
        return serializationManager.serializeCollection(rameraTextDao.findAll()).toString();
    }

    @RequestMapping(value = "/admin/activeSessions", method = RequestMethod.GET)
    public String showActiveSessions(Model model) {
        return "adminActiveSessions";
    }

    @RequestMapping(value = "/admin/activeSessions/list.json", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ActiveSessionsResultDto getActiveSessions(HttpServletRequest request, @RequestBody ActiveSessionsRequestDto requestDto) {
        /*String currentSessionId = request.getSession().getId();
        List<ActiveSessionDto> activeSessionDtos = new ArrayList<>();
        for (LoginLogEntry l : activeLoginLogEntries) {
            String visibleSessionId = l.getSessionId();
            boolean isCurrent = false;
            if (org.apache.commons.lang3.StringUtils.equals(currentSessionId, visibleSessionId)) {
                isCurrent = true;
            }
            visibleSessionId = org.apache.commons.lang3.StringUtils.join(StringUtils.splitStringEvery(visibleSessionId, 11), "<br>");
            if (isCurrent) {
                visibleSessionId = "<font color=\"red\"><b>" + visibleSessionId + "</b></font>";
            }
            //l.setSessionId(sessionId);
            activeSessionDtos.add(new ActiveSessionDto(visibleSessionId, l));
        }
        int countActiveLoginLogEntries = sessionsService.countActiveLoginLogEntries(
                requestDto.username, requestDto.device, requestDto.os,
                requestDto.browser, requestDto.ip);
        return new ActiveSessionsResultDto(activeSessionDtos, countActiveLoginLogEntries);*/
        return null;
    }

    @RequestMapping(value = "/admin/activeSessions/close.json", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void closeSession(@RequestBody CloseSessionRequestDto request) {
        authService.closeSession(request.sessionId);
    }

    //------------------------------------------------------------------------------------------------------------
    // Редактор секций
    //------------------------------------------------------------------------------------------------------------

    @RequestMapping(value = "/admin/root_sections/", method = RequestMethod.GET)
    public String getRootSectionsAdminPage(Model model) {
        model.addAttribute("currentPageTitle", "Редактор корневых разделов");
        return "rootSectionsAdminPage";
    }

    @RequestMapping(value = "/admin/root_sections/getSections.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public List<Section> getRootSections() {
        return sectionDao.getRootsWithEditableForwardUrl();
    }

    @RequestMapping(value = "/admin/root_sections/saveSection.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public String saveRootSection(@RequestBody SaveSectionDto saveSectionDto) {
        sectionService.saveRootSection(saveSectionDto);
        return JsonUtils.getSuccessJson().toString();
    }

    // Данные для возможных статичных страниц, которые можно прикрепить к разделу
    @RequestMapping(value = "/admin/root_sections/getPossiblePages.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public List<PageDto> getPossiblePages(@RequestParam(value = "title_query") String titleQuery) {
        return pagesService.findPages(titleQuery).stream().map(page -> PageDto.createFromPage(page)).collect(Collectors.toList());
    }
    //------------------------------------------------------------------------------------------------------------


    //------------------------------------------------------------------------------------------------------------
    // Системные роли объединений
    //------------------------------------------------------------------------------------------------------------

    @RequestMapping(value = "/admin/community_permissions/", method = RequestMethod.GET)
    public String getCommunityPermissionsAdminPage(Model model) {
        model.addAttribute("currentPageTitle", "Редактор ролей объединений");
        return "communityPermissionsAdminPage";
    }

    /**
     * Получить все роли
     *
     * @return
     */
    @RequestMapping(value = "/admin/community_permissions/getPermissions.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public List<CommunityPermissionDto> getPermissions() {
        return CommunityPermissionDto.fromEntities(communityPermissionDao.findAll());
    }

    /**
     * Получить все объединения, которые могут быть привязаны к роли
     *
     * @return
     */
    @RequestMapping(value = "/admin/community_permissions/getPossibleCommunities.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public List<CommunityEntity> getPossibleCommunities() {
        return communityDao.getNotDeletedCommunities();
    }

    /**
     * Сохранить изменения в роли доступа
     *
     * @param communityPermissionDto
     * @return
     */
    @RequestMapping(value = "/admin/community_permissions/savePermission.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public String savePermission(@RequestBody CommunityPermissionDto communityPermissionDto) {
        // TODO Переделать
        //communityPermissionService.saveCommunityPermission(communityPermissionDto);
        return JsonUtils.getSuccessJson().toString();
    }
    //------------------------------------------------------------------------------------------------------------
}

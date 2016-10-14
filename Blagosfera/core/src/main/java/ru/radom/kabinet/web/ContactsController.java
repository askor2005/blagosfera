package ru.radom.kabinet.web;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.askor.blagosfera.core.services.contacts.ContactsGroupService;
import ru.askor.blagosfera.core.services.contacts.ContactsService;
import ru.askor.blagosfera.core.services.security.RosterService;
import ru.askor.blagosfera.core.services.vcard.VcardService;
import ru.askor.blagosfera.domain.Address;
import ru.askor.blagosfera.domain.contacts.Contact;
import ru.radom.kabinet.dao.ContactDao;
import ru.radom.kabinet.dao.ContactsGroupDao;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.expressions.Functions;
import ru.radom.kabinet.json.SerializationManager;
import ru.radom.kabinet.model.ContactStatus;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.security.SecurityUtils;

import ru.radom.kabinet.web.contacts.dto.ContactDto;
import ru.radom.kabinet.web.contacts.dto.ContactGroupDto;
import ru.radom.kabinet.web.contacts.dto.ContactsPageDto;
import ru.radom.kabinet.web.utils.Breadcrumb;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ContactsController {
    @Autowired
    private VcardService vcardService;
    @Autowired
    private ContactsGroupService contactsGroupService;

    @Autowired
    private ContactsService contactsService;

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private ContactsGroupDao contactsGroupDao;

    @Autowired
    private SerializationManager serializationManager;

    @Autowired
    private RosterService rosterService;

    public ContactsController() {
    }
    @RequestMapping(value = "/contacts/download/windows", method = RequestMethod.GET)
    @ResponseBody
    void downloadVcardForWindows(@RequestParam("userId") Long userId,
                                 HttpServletResponse response) {
        vcardService.sendToHttpResponse(SecurityUtils.getUser().getId(),userId,response,"cp1251");
    }
    @RequestMapping(value = "/contacts/download", method = RequestMethod.GET)
    @ResponseBody
    void downloadVcard(@RequestParam("userId") Long userId,HttpServletResponse response) {
        vcardService.sendToHttpResponse(SecurityUtils.getUser().getId(),userId,response,"UTF-8");
    }
    @RequestMapping(value = "/contacts/send/to/email.json", method = RequestMethod.GET)
    @ResponseBody
    SuccessResponseDto sendToEmail(@RequestParam("userId") Long userId,@RequestParam(value = "receiverId",required = false) Long receiverId) {
        vcardService.sendToEmail(receiverId != null ? receiverId : SecurityUtils.getUser().getId(), userId);
        return SuccessResponseDto.get();
    }
    @RequestMapping(value = "/contacts/contacts/lists.json", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public List<ContactGroupDto> groupsJson() {
        return contactsGroupService.getByUser(SecurityUtils.getUser().getId()).stream().map(contactGroupDomain -> ContactGroupDto.toDto(contactGroupDomain)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/contacts/search.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    @ResponseBody
    public String searchJson(@RequestParam(value = "page", defaultValue = "1") int page,
                             @RequestParam(value = "per_page", defaultValue = "20") int perPage,
                             @RequestParam(value = "include_context_sharer", defaultValue = "false") boolean includeContextSharer,
                             @RequestParam(value = "query", required = false) String query,
                             @RequestParam(value = "order_by", defaultValue = "searchString") String orderBy,
                             @RequestParam(value = "asc", defaultValue = "true") boolean asc,
                             @RequestParam(value = "certification_requested", defaultValue = "false") boolean waitingForCertification,
                             @RequestParam(value = "requestedForRegistrationsOnlyToMe",required = false,defaultValue = "false") boolean requestedForRegistrationsOnlyToMe,
                             @RequestParam(value = "ageFrom") Long ageFrom,
                             @RequestParam(value = "ageTo") Long ageTo,
                             @RequestParam(value = "sex") Boolean sex,
                             @RequestParam(value = "country") String country,
                             @RequestParam(value = "city") String city) {
        List<UserEntity> userEntities = sharerDao.search(query, (page - 1) * perPage, perPage, includeContextSharer, orderBy, asc, waitingForCertification, requestedForRegistrationsOnlyToMe,ageFrom,ageTo,sex,country,city);
        JSONArray jsonSharers = serializationManager.serializeCollection(userEntities);
        return jsonSharers.toString();
    }

    @RequestMapping(value = "/contacts/list.json", produces = "application/json;charset=UTF-8", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public List<ContactDto> listJson(@RequestParam(value = "page", defaultValue = "1") int page,
                                     @RequestParam(value = "per_page", defaultValue = "20") int perPage,
                                     @RequestParam(value = "group_id", required = false, defaultValue = "-1") Long groupId,
                                     @RequestParam(value = "sharer_status", required = false) ContactStatus sharerStatus,
                                     @RequestParam(value = "other_status", required = false) ContactStatus otherStatus,
                                     @RequestParam(value = "sharer_id", required = false, defaultValue = "-1") Long userId,
                                     @RequestParam(value = "query", required = false) String query,
                                     @RequestParam(value = "order_by", defaultValue = "searchString") String orderBy,
                                     @RequestParam(value = "asc", defaultValue = "true") boolean asc) {
        List<ContactDto> result = contactsService.searchContactsOrderByOther(
                SecurityUtils.getUser().getId(), null, query,
                sharerStatus, otherStatus,
                groupId >= 0 ? true : false,
                groupId > 0 ? groupId : null,
                page - 1, perPage, orderBy, asc)
                .stream().map(contact -> {
                    ContactDto contactDto = ContactDto.toDto(contact);
                    if (contactDto.getOther() != null) {
                        Address actualAddress =  sharerDao.getActualAddress(contact.getOther().getId());
                        contactDto.setActualCountry(actualAddress != null ? actualAddress.getCountry() : "");
                        contactDto.setActualCity(actualAddress != null ? actualAddress.getCity() : "");
                        if (((actualAddress.getCity() == null) || (actualAddress.getCity().isEmpty())) && (actualAddress.getRegion() != null) &&( (actualAddress.getRegion().equals("Москва")) || (actualAddress.getRegion().equals("Санкт-Петербург")))) {
                            contactDto.setActualCity(actualAddress.getRegion());
                        }
                    }
                    return contactDto;
                }).collect(Collectors.toList());

        for (ContactDto item : result) {
            item.online = rosterService.isUserOnline(item.other.email);
            item.other.online = item.online;
        }

        return result;
    }

    @RequestMapping(value = "/contacts/online.json", produces = "application/json;charset=UTF-8", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public List<ContactDto> listOnline() {
        List<ContactDto> contacts = contactsService.getContacts(SecurityUtils.getUser().getId(), ContactStatus.ACCEPTED, ContactStatus.ACCEPTED)
                .stream()
                .filter(contact -> rosterService.isUserOnline(contact.getOther().getEmail()))
                .map(contact -> ContactDto.toDto(contact))
                .collect(Collectors.toList());

        return contacts;
    }

    @RequestMapping(value = "/contacts/add.json", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ContactDto addContactJson(@RequestParam(value = "other_id", required = true) Long otherId, @RequestParam(value = "group_id", required = false) Long groupId) {
        return ContactDto.toDto(contactsService.addContact(SecurityUtils.getUser().getId(), otherId, groupId));
    }
    @RequestMapping(value = "/contacts/deleteGroup.json", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ContactDto deleteGroupContactJson(@RequestParam(value = "other_id", required = true) Long otherId, @RequestParam(value = "group_id", defaultValue = "-1") Long groupId) {
        return ContactDto.toDto(contactsService.deleteContactGroup(SecurityUtils.getUser().getId(), otherId, groupId));
    }

    @RequestMapping(value = "/contacts/delete.json", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ContactDto deleteContactJson(@RequestParam(value = "other_id", defaultValue = "-1") Long otherId) {
        Contact deletedContact = contactsService.deleteContact(SecurityUtils.getUser().getId(), otherId);
        return ContactDto.toDto(deletedContact);
    }

    @RequestMapping("/contacts/search")
    public String showSearchPage(Model model) {
        model.addAttribute("currentPageTitle", "Поиск контактов");

        List<UserEntity> userEntities = sharerDao.search("", 0, 20, false, "searchString", true,null,null,null,null,null);
        JSONArray jsonSharers = serializationManager.serializeCollection(userEntities);

        model.addAttribute("firstPage", jsonSharers.toString());
        model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Поиск контактов", "/contacts/search"));
        model.addAttribute("contactsSearch",true);
        return "contactsSearch";
    }

    @RequestMapping("/contacts")
    public String showContactsPage(Model model, @RequestParam(value = "group_id", required = false, defaultValue = "-1") Long groupId) {
        model.addAttribute("currentPageTitle", "Список контактов");
        model.addAttribute("groupId", groupId);
        return "contacts";
    }

    @RequestMapping("/contacts/init/info.json")
    @ResponseBody
    public ContactsPageDto getInfoForContactsPage() {
        ContactsPageDto response = new ContactsPageDto();
        response.setDefaultGroupCount(contactsService.getDefaultGroupCount(SecurityUtils.getUser().getId()));
        response.setContactsCount(contactsService.getCount(SecurityUtils.getUser().getId()));
        response.setDefaultGroupCountWord(Functions.getDeclension(response.getDefaultGroupCount(), "контакт", "контакта", "контактов"));
        response.setGroups(contactsGroupService.getByUser(SecurityUtils.getUser().getId()).stream().map(contactGroupDomain -> ContactGroupDto.toDto(contactGroupDomain)).collect(Collectors.toList()));
        return response;
    }

    @RequestMapping("/contacts/new_requests")
    public String showContactsNewRequestsPage(Model model, @RequestParam(value = "group_id", required = false, defaultValue = "-1") Long groupId) {
        model.addAttribute("currentPageTitle", "Список контактов");
        model.addAttribute("groups", contactsGroupDao.getBySharer(SecurityUtils.getUser().getId()));
        model.addAttribute("group", contactsGroupDao.getById(groupId));
        model.addAttribute("defaultGroupCount", contactsService.getDefaultGroupCount(SecurityUtils.getUser().getId()));
        //List<ContactEntity> contacts = contactDao.searchContacts(null, "", 0, 20, ContactStatus.NEW, ContactStatus.ACCEPTED, null, "searchString", true);
        //List<UserEntity> userEntities = new ArrayList<UserEntity>();

        //for (ContactEntity contact : contacts) {
         //   userEntities.add(contact.getOther());
        //}

        //JSONArray jsonSharers = serializationManager.serializeCollection(userEntities);
        //model.addAttribute("firstPage", jsonSharers.toString());
        model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Запросы в список контактов", "/contacts"));
        return "contactsRequests";
    }

    @RequestMapping(value = "/contacts/new_requests_count.json", method = RequestMethod.GET)
    @ResponseBody
    public String getWaitingCount() {
        try {
            return "{\"count\" : " + contactDao.getNewRequestsCount(SecurityUtils.getUser().getId()) + "}";
        } catch (Exception e) {
            return "{\"result\" : \"error\"}";
        }
    }
}

package ru.radom.kabinet.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.core.services.registrator.RegistratorDataService;
import ru.askor.blagosfera.data.jpa.repositories.registrator.RegistrationRequestRepository;
import ru.askor.blagosfera.domain.Address;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.registrator.RegistratorDomain;
import ru.askor.blagosfera.domain.registrator.RegistratorSort;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dto.StringObjectHashMap;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.json.SerializationManager;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.registration.RegistrationRequest;
import ru.radom.kabinet.model.registration.RegistrationRequestStatus;
import ru.radom.kabinet.model.registration.RegistratorLevel;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.ProfileFilling;
import ru.radom.kabinet.services.ProfileService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.services.registration.RegistrationRequestService;
import ru.radom.kabinet.services.registration.RegistratorService;
import ru.radom.kabinet.utils.JsonUtils;
import ru.radom.kabinet.utils.Roles;
import ru.radom.kabinet.web.dto.SelectAddressResponseDto;
import ru.radom.kabinet.web.registrators.dto.RegistratorsPageDto;
import ru.radom.kabinet.web.utils.Breadcrumb;

import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/registrator")
public class RegistratorController {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private RegistrationRequestRepository registrationRequestRepository;

    @Autowired
    private RegistratorService registratorService;

    @Autowired
    private RegistratorDataService registratorDataService;

    @Autowired
    private RegistrationRequestService registrationRequestService;

    @Autowired
    protected SerializationManager serializationManager;

    @Autowired
    private FieldsService fieldsService;

    @Autowired
    private CommunityDataService communityDomainService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private SharerDao sharerDao;

    public class RegistratorSortEditor extends PropertyEditorSupport {

        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            ObjectMapper mapper = new ObjectMapper();

            RegistratorSort value = null;
            try {
                value = new RegistratorSort();
                JsonNode root = mapper.readTree(text).get(0);
                value.setDirection(root.path("direction").asText());
                value.setProperty(root.path("property").asText());
            } catch (IOException e) {
            }

            setValue(value);
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(RegistratorSort.class, new RegistratorSortEditor());
    }

    /**
     * Список регистраторов
     *
     * @param page
     * @param perPage
     * @param nameTemplate
     * @param level
     * @param latitude
     * @param longitude
     * @return
     */
    @RequestMapping(value = "/list.json", method = RequestMethod.POST)
    @ResponseBody
    public List<RegistratorDomain> pageRegistrators(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "per_page", defaultValue = "20") int perPage,
            @RequestParam(value = "nameTemplate") String nameTemplate,
            @RequestParam(value = "level") String level,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam(value = "includeRequestedForRegistration", required = false, defaultValue = "false") boolean includeRequestedForRegistration,
            @RequestParam(value = "sort") RegistratorSort sort,
            @RequestParam(value = "requestedForRegistrationsOnlyToMe", required = false, defaultValue = "false") boolean requestedForRegistrationsOnlyToMe) {
        final List<RegistratorDomain> result = registratorService.page(SecurityUtils.getUser().getId(), page - 1, perPage,
                nameTemplate, RegistratorLevel.geByName(level), latitude, longitude, null, includeRequestedForRegistration,
                sort, requestedForRegistrationsOnlyToMe);
        return result;
    }

    /**
     * Страница с выбором регистратора для пользователя системы
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/select", method = RequestMethod.GET)
    public String selectRegistrator(Model model) throws IOException {
        SelectAddressResponseDto sharerAddress = new SelectAddressResponseDto();
        User currentUser = SecurityUtils.getUser();

        Address address = sharerDao.getActualAddress(currentUser.getId());

        sharerAddress.full = sharerDao.getFullActualAddress(currentUser.getId());
        sharerAddress.latitude = address.getLatitude();
        sharerAddress.longitude = address.getLongitude();

        model.addAttribute("sharerAddress", sharerAddress);
        model.addAttribute("hasRegistrationRequests", registrationRequestRepository.existsForUser(SecurityUtils.getUser().getId()));
        return "selectRegistrator";
    }

    @RequestMapping(value = "/select/init/info.json", method = RequestMethod.GET)
    public
    @ResponseBody
    RegistratorsPageDto getPageInfo() throws IOException {
        User currentUser = SecurityUtils.getUser();
        RegistratorsPageDto registratorsPageDto = new RegistratorsPageDto();
        registratorsPageDto.setSharerAddress(sharerDao.getActualAddress(currentUser.getId()));
        registratorsPageDto.setRegistratorsCount(registratorService.count(SecurityUtils.getUser().getId(), null));
        registratorsPageDto.setCurrentRequest(registrationRequestService.getMy());

        Address address = sharerDao.getActualAddress(currentUser.getId());
        address.setFullAddress(sharerDao.getFullActualAddress(currentUser.getId()));
        registratorsPageDto.setActualAddress(address);

        address = sharerDao.getRegistrationAddress(currentUser.getId());
        address.setFullAddress(sharerDao.getFullRegistrationAddress(currentUser.getId()));
        registratorsPageDto.setRegistrationAddress(address);
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(SecurityUtils.getUser().getEmail());
        registratorsPageDto.setRegistrator(registratorService.isActiveRegistrator(userDetails));
        ProfileFilling profileFilling = profileService.getProfileFilling(currentUser);
        if (profileFilling != null) {
            registratorsPageDto.setProfileFillingPercent(profileFilling.getPercent());
        }
        return registratorsPageDto;
    }

    /**
     * Список всех регистраторов
     *
     * @param latitude
     * @param longitude
     * @return
     */
    @RequestMapping(value = "/all.json", method = RequestMethod.POST)
    @ResponseBody
    public List<RegistratorDomain> allRegistrators(@RequestParam(value = "latitude", required = false) Double latitude, @RequestParam(value = "longitude", required = false) Double longitude,
                                                   @RequestParam(value = "includeRequestedForRegistration", required = false, defaultValue = "false") boolean includeRequestedForRegistration,
                                                   @RequestParam(value = "requestedForRegistrationsOnlyToMe", required = false, defaultValue = "false") boolean requestedForRegistrationsOnlyToMe
    ) {
        return registratorService.page(SecurityUtils.getUser().getId(), null, null, latitude, longitude, null, includeRequestedForRegistration, requestedForRegistrationsOnlyToMe);
    }

    /**
     * Создать запрос на сертификацию от пользователя системы
     *
     * @param registratorId
     * @return
     */
    @RequestMapping(value = "/createRequest", method = RequestMethod.POST)
    @ResponseBody
    public String createRequest(@RequestParam(value = "registratorId") Long registratorId) {
        registrationRequestService.create(registratorId, SecurityUtils.getUser());
        return JsonUtils.getSuccessJson().toString();
    }

    /**
     * Удалить запрос на сертификацию
     *
     * @param requestId
     * @return
     */
    @RequestMapping(value = "/deleteRequest", method = RequestMethod.POST)
    public
    @ResponseBody
    SuccessResponseDto deleteRequest(@RequestParam(value = "requestId") Long requestId) {
        registrationRequestService.deleteRequest(requestId, SecurityUtils.getUser());
        return SuccessResponseDto.get();
    }

    /**
     * Отклонить запрос на сертификацию регистратором
     *
     * @param requestId
     * @param comment
     * @return
     */
    @RequestMapping(value = "/cancelRequest", method = RequestMethod.POST)
    @ResponseBody
    public String cancelRequest(@RequestParam(value = "requestId") Long requestId, @RequestParam(value = "comment") String comment) {
        registrationRequestService.cancelRequest(requestId, comment, SecurityUtils.getUser());
        return JsonUtils.getSuccessJson().toString();
    }

    /**
     * Список запросов на сертификацию
     *
     * @param page
     * @param perPage
     * @param nameTemplate
     * @param status
     * @param orderBy
     * @param asc
     * @return
     */
    @RequestMapping(value = "/listRequest.json", method = RequestMethod.POST)
    @ResponseBody
    public String pageRequests(
            @RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "per_page", defaultValue = "20") int perPage,
            @RequestParam(value = "nameTemplate") String nameTemplate, final @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "order_by", defaultValue = "created") String orderBy, @RequestParam(value = "asc", defaultValue = "false") boolean asc,
            @RequestParam(value = "object_type", defaultValue = Discriminators.SHARER) String objectType) {
        User registrator = SecurityUtils.getUser();
        final List<RegistrationRequest> result = registrationRequestService.searchRequests(
                registrator, (page - 1) * perPage, perPage, nameTemplate, RegistrationRequestStatus.getByName(status), orderBy, asc, objectType);
        return serializationManager.serializeCollection(result).toString();
    }

    /**
     * Страница со списком запросов регистратору на сервтификацию
     *
     * @param model
     * @param response
     * @return
     * @throws IOException
     */
    @PreAuthorize("hasAnyRole('REGISTRATOR_REGISTRATORS_EDITOR', 'REGISTRATOR_COMMUNITIES_EDITOR', 'REGISTRATOR_SHARERS_EDITOR')")
    @RequestMapping(value = "/requests", method = RequestMethod.GET)
    public String requests(final Model model, final HttpServletResponse response,
                           @RequestParam(name = "object_type", defaultValue = Discriminators.SHARER) String objectType) throws IOException {
        if (!Discriminators.SHARER.equals(objectType) && !Discriminators.COMMUNITY.equals(objectType)) {
            objectType = Discriminators.SHARER;
        }
        model.addAttribute("objectType", objectType);
        model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Обработка заявок на идентификацию", "/registrator/requests"));
        return "processRequests";
    }

    /**
     * Получить запрос, который отправлял текущий участник системы на сертификацию
     *
     * @return
     */
    @RequestMapping(value = "/myRequest.json", method = RequestMethod.POST)
    @ResponseBody
    public String getMyRequest() {
        final RegistrationRequest request = registrationRequestService.getMy();
        if (request == null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("hasRequest", false);
            return jsonObject.toString();
        }
        return serializationManager.serialize(request).toString();
    }

    /**
     * @return
     */
    @RequestMapping(value = "/getRequestByObject.json", method = RequestMethod.POST)
    @ResponseBody
    public String getRequestCommunity(@RequestParam(value = "community_id", required = true) Long communityId) {
        Community community = communityDomainService.getByIdMediumData(communityId);
        final RegistrationRequest request = registrationRequestService.getByObject(community);
        if (request == null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("hasRequest", false);
            return jsonObject.toString();
        }
        return serializationManager.serialize(request).toString();
    }

    /**
     * Количество запросов у регистратора
     *
     * @return
     */
    @RequestMapping(value = "/requestsCounts.json", method = RequestMethod.POST)
    @ResponseBody
    public String getRequestsCounts(@RequestParam(value = "object_type", defaultValue = Discriminators.SHARER) String objectType) {
        final Map<String, Object> result = new StringObjectHashMap();
        User registrator = SecurityUtils.getUser();
        result.put("all", registrationRequestService.count(registrator, null, objectType));
        result.put("notProcessed", registrationRequestService.count(registrator, RegistrationRequestStatus.NEW, objectType));
        return serializationManager.serialize(result).toString();
    }

    /*@RequestMapping(value = "/geoUpdate")
    public String updateGeoPosition() {
        List<Sharer> sharers = sharerDao.getNotDeleted();
        for (Sharer sharer : sharers) {
            fieldsService.updateGeoData(sharer);
        }
        return "redirect:/ramera";
    }

    @RequestMapping(value = "/geoUpdate/{ikp}")
    public String updateGeoSingleSharerPosition(@PathVariable("ikp") String ikp) {
        Sharer sharer = sharerDao.getByIkp(ikp);
        fieldsService.updateGeoData(sharer);
        return "redirect:/ramera";
    }*/

    // Возвращает список сертифицированных шарером регистраторов(только нужную инфу)
    @RequestMapping("/verified_registrators_list.json")
    public
    @ResponseBody
    String getCommunitiesVerifiedList() {
        List<User> result = registratorDataService.getVerifiedRegistrators(SecurityUtils.getUser().getId());
        return serializationManager.serializeCollection(result).toString();
    }

}

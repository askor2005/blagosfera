package ru.radom.kabinet.web.communities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.registrator.RegistratorDomain;
import ru.radom.kabinet.json.SerializationManager;
import ru.radom.kabinet.model.registration.RegistrationRequest;
import ru.radom.kabinet.model.registration.RegistratorLevel;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.context.RequestContext;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.registration.RegistrationRequestService;
import ru.radom.kabinet.services.registration.RegistratorService;
import ru.radom.kabinet.utils.CommonConstants;
import ru.radom.kabinet.utils.JsonUtils;

import java.util.Collections;
import java.util.List;

/**
 *
 * Created by vgusev on 04.11.2015.
 */
@Controller
public class CommunitiesRegistratorController {

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    protected SerializationManager serializationManager;

    @Autowired
    private CommunityDataService communityDomainService;

    @Autowired
    private RegistratorService registratorService;

    @Autowired
    private RegistrationRequestService registrationRequestService;

    @Autowired
    private RequestContext radomRequestContext;

    private void check(boolean condition, String message) {
        if (condition) {
            throw new RuntimeException(message);
        }
    }

    private void standardCheck(Community community) {
        boolean isWithOrganization = ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.equals(community.getAccessType());
        check(!SecurityUtils.getUser().getId().equals(communitiesService.getCommunityDirectorId(community)),
                "У Вас нет прав на отправку регистрации от имени данной организации");
        check(community.isVerified(), "Организация уже сертифицированна");
        check(!isWithOrganization, "Объединения вне рамок юр лица не сертифицируются");
    }

    /**
     * Страница выбора регистратора
     * @return
     */
    @RequestMapping(value = "/group/{seolink}/registrator/select", method = RequestMethod.GET)
    public String selectRegistrator(@PathVariable("seolink") String seoLink, Model model) {
        try {
            Community community = communityDomainService.getByIdFullData(radomRequestContext.getCommunityId());

            //CommunityEntity community = radomRequestContext.getCommunity();
            /*model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").addItem(communitiesService.getBreadcrumbCommonItems())
                    .add(community.getName(), community.getLink()).add("Выбор регистратора", community.getLink() + "/registrator/select"));
            model.addAttribute("currentPageTitle", "Выбор регистратора для сертификации организации");*/

            standardCheck(community);

            model.addAttribute("organizationAddress", serializationManager.serialize(community.getCommunityData().getActualAddress()));
            model.addAttribute("registratorsCount", registratorService.count(SecurityUtils.getUser().getId(),Collections.singletonList(RegistratorLevel.LEVEL_3.getMnemo())));
            model.addAttribute("currentRequest", serializationManager.serialize(registrationRequestService.getByCommunityId(radomRequestContext.getCommunityId())));
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "selectCommunityRegistrator";
    }

    /**
     * Создать запрос на сертификацию от организации
     * @param registratorId
     * @return
     */
    @RequestMapping(value = "/communities/createVerificationRequest.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public String createRequest(
            @RequestParam(value = "registrator_id", required = true) Long registratorId,
            @RequestParam(value = "community_id", required = true) Long communityId) {
        Community community = communityDomainService.getByIdMediumData(communityId);
        registrationRequestService.create(registratorId, SecurityUtils.getUser(), community);
        return JsonUtils.getSuccessJson().toString();
    }

    /**
     * Получить запрос на сертификацию
     * @param communityId
     * @return
     */
    @RequestMapping(value = "/communities/getVerificationRequest.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public String getVerificationRequest(@RequestParam(value = "community_id", required = true) Long communityId) {
        Community community = communityDomainService.getByIdMediumData(communityId);
        RegistrationRequest request = registrationRequestService.getByObject(community);
        String result;
        if (request != null) {
            result = serializationManager.serialize(request).toString();
        } else {
            result = JsonUtils.getSuccessJson().toString();
        }
        return result;
    }

    @RequestMapping(value = "/communities/registratorsList.json", method = RequestMethod.POST)
    @ResponseBody
    public List<RegistratorDomain> registratorsList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "per_page", defaultValue = "20") int perPage,
            @RequestParam(value = "nameTemplate") String nameTemplate,
            @RequestParam(value = "level") String level,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude) {
            return registratorService.page(SecurityUtils.getUser().getId(), page - 1, perPage, nameTemplate, RegistratorLevel.geByName(level), latitude, longitude, Collections.singletonList(RegistratorLevel.LEVEL_3.getMnemo()), false,null, false);
    }

    @RequestMapping(value = "/communities/allRegistrators.json", method = RequestMethod.POST)
    @ResponseBody
    public List<RegistratorDomain> allRegistrators(
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude) {
        return registratorService.page(SecurityUtils.getUser().getId(), null, null, latitude, longitude, Collections.singletonList(RegistratorLevel.LEVEL_3.getMnemo()), false, false);
    }

    /**
     * Сертификация организации
     * @param communityId
     * @return
     */
    @RequestMapping(value = "/communities/verified.json", method = RequestMethod.POST)
    @ResponseBody
    public String setVerifiedCommunity(@RequestParam(value = "community_id", required = true) Long communityId) {
        communitiesService.verifiedCommunity(communityId, SecurityUtils.getUser());
        return JsonUtils.getSuccessJson().toString();
    }
}

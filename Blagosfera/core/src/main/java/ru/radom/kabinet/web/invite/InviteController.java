package ru.radom.kabinet.web.invite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.core.services.invite.InvitationDataService;
import ru.askor.blagosfera.core.services.invite.InviteRelationService;
import ru.askor.blagosfera.core.services.security.AuthService;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.domain.invite.Invitation;
import ru.askor.blagosfera.domain.invite.InviteRelationshipTypeDomain;
import ru.radom.kabinet.dao.RameraTextDao;
import ru.radom.kabinet.dto.InvitesTableDataDto;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.model.RameraTextEntity;
import ru.radom.kabinet.model.invite.InviteFilter;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.InvitationService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.CommonConstants;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.Roles;
import ru.radom.kabinet.utils.StringUtils;
import ru.radom.kabinet.web.invite.dto.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class InviteController {
    @Autowired
    private InviteRelationService inviteRelationshipService;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserDataService userDataService;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private InvitationDataService invitationDataService;

    @Autowired
    private RameraTextDao rameraTextDao;

    @Autowired
    private SettingsManager settingsManager;

    /**
     * Данные для страниы приглашений
     *
     * @return
     */
    @RequestMapping("/invite.json")
    @ResponseBody
    public InvitePageDto getInviteInfo() {
        InvitePageDto invitePageDto = new InvitePageDto();
        invitePageDto.setVerified(SecurityUtils.getUser().isVerified());
        invitePageDto.setAdmin(SecurityUtils.getUserDetails().hasRole(Roles.ROLE_ADMIN));
        invitePageDto.setInviteRelationShipTypes(inviteRelationshipService.findAll());
        invitePageDto.setInviteCount(invitationDataService.getInviteCountData(SecurityUtils.getUser().getId()));
        return invitePageDto;
    }

    /*@RequestMapping("/invite")
    public String showInvitePage(Model model) {
        model.addAttribute("currentPageTitle", "Приглашение в систему");
        return "invitePage";
    }*/

    @RequestMapping(value = "invite/invite_relationship_types.json", method = RequestMethod.GET)
    @ResponseBody
    public List<InviteRelationshipTypeDomain> getInviteRelationshipTypeList() {
        return inviteRelationshipService.findAll();
    }

    /**
     * Проверить почту предлагаемого пользователя.
     *
     * @param inviteCheckEmailRequest
     * @return
     */
    @RequestMapping(value = "/invite/validateEmail.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public SuccessResponseDto validateEmail(@RequestBody InviteCheckEmailRequestDto inviteCheckEmailRequest) {
        String email = inviteCheckEmailRequest.getEmail();
        if (StringUtils.isEmpty(email)) {
            throw new RuntimeException("E-mail не введен!");
        }
        if (!StringUtils.checkEmail(email)) {
            throw new RuntimeException("Введен некорректный e-mail!");
        }
        if (userDataService.existsEmail(email)) {
            throw new RuntimeException("Пользователь с указанным Email уже зарегистрирован!");
        }
        if (invitationService.existsInvites(email)) {
            throw new RuntimeException("Пользователю с указанным Email уже было отправленно приглашение!");
        }
        if (email.contains(" ")) {
            throw new RuntimeException("E-mail не должен содержать пробелы!");
        }
        return SuccessResponseDto.get();
    }

    /**
     * @param inviteForm
     * @return
     */
    @RequestMapping(value = "/invite/create.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public SuccessResponseDto createInvite(@RequestBody InviteForm inviteForm) {
        invitationService.createInvite(inviteForm.getEmail(), inviteForm.getInvitedLastName(), inviteForm.getInvitedFirstName(),
                inviteForm.getInvitedFatherName(), inviteForm.getInvitedGender(), inviteForm.isGuarantee(), inviteForm.getHowLongFamiliar(),
                inviteForm.getRelationships(), SecurityUtils.getUser().getId());
        return SuccessResponseDto.get();
    }

    /**
     * @param inviteSendEmailRequest
     * @return
     */
    @RequestMapping(value = "/invite/sendToEmail.json", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public SuccessResponseDto sendToEmail(@RequestBody InviteSendEmailRequestDto inviteSendEmailRequest) {
        Invitation invite = invitationService.getById(inviteSendEmailRequest.getInviteId());
        if (invite != null) {
            if (DateUtils.getDistanceMinutes(invite.getLastDateSending(), new Date(System.currentTimeMillis())) > 1) {
                invitationService.sendToEmail(invite, SecurityUtils.getUser().getId());
                return SuccessResponseDto.get();
            } else {
                throw new RuntimeException("Повторно отправлять сообщения можно не раньше чем через одну минуту.");
            }
        } else {
            throw new RuntimeException("приглашение с таким id не найдены");
        }
    }

    /*@RequestMapping("/invites")
    public String showInvitesPage(Model model, @RequestParam(value = "from_date", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date fromDate, @RequestParam(value = "to_date", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date toDate) {
        model.addAttribute("currentPageTitle", "Список приглашений");
        model.addAttribute("breadcrumb", new Breadcrumb().add("БЛАГОСФЕРА", "/").add("Список приглашений", "/invites"));
        if (toDate == null) {
            toDate = new Date();
        }
        if (fromDate == null) {
            Calendar startDateCalendar = Calendar.getInstance();
            startDateCalendar.setTime(toDate);
            startDateCalendar.add(Calendar.MONTH, -1);
            fromDate = startDateCalendar.getTime();
        }
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        return "invitesPage";
    }*/

    /**
     * @param inviteFilter
     * @return
     */
    @RequestMapping(value = "/invites/invites.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public InvitesTableDataDto getInvites(
            @RequestBody InviteFilter inviteFilter
    ) {
        return invitationDataService.getListByFilter(SecurityUtils.getUser().getId(), inviteFilter);
    }

    /**
     * @return
     */
    @RequestMapping(value = "/invite/addRelationshipType.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public Long addRelationshipType() {
        Long createdId = inviteRelationshipService.create().getId();
        return createdId;
    }

    /**
     * @param id
     * @param name
     * @return
     */
    @RequestMapping(value = "/invite/updateRelationshipType.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public SuccessResponseDto updateRelationshipType(@RequestParam(value = "id") Long id, @RequestParam(value = "name") String name) {
        inviteRelationshipService.update(id, name);
        return SuccessResponseDto.get();
    }

    /**
     * @param ids
     * @return
     */
    @RequestMapping(value = "/invite/updateRelationshipTypeIndexes.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public SuccessResponseDto updateRelationshipTypeIndexes(@RequestBody Long[] ids) {
        inviteRelationshipService.updateIndexes(ids);
        return SuccessResponseDto.get();

    }

    /**
     * @param id
     * @return
     */
    @RequestMapping(value = "/invite/deleteRelationshipType.json", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public SuccessResponseDto deleteRelationshipType(@RequestParam(value = "id") Long id) {
        inviteRelationshipService.delete(id);
        return SuccessResponseDto.get();
    }

    /**
     * Данные для страниы приглашения. Может выпольнить только неавторизованный пользователь.
     *
     * @param acceptInvitationRequestDto
     * @return
     */
    @RequestMapping(value = "/invitationaccept/accept_page_data.json", method = {RequestMethod.POST, RequestMethod.GET}, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public AcceptInvitationPageDataDto getAcceptInvitationPageData(@RequestBody AcceptInvitationRequestDto acceptInvitationRequestDto,HttpServletRequest request,HttpServletResponse response) {
        AcceptInvitationPageDataDto result;
        if (SecurityUtils.getUser() != null) {
            authService.logout(request, response);
        }
        RameraTextEntity offerText = rameraTextDao.getByCode("OFFER");
        Invitation invitation = invitationDataService.getByHashUrl(acceptInvitationRequestDto.getHash());
        String offerTextString;
        if (offerText != null) {
            offerTextString = offerText.getText();

            Set<String> params = new HashSet<>();

            Pattern p = Pattern.compile("\\$\\{([^}]+)\\}");
            Matcher m = p.matcher(offerTextString);
            while (m.find()) {
                params.add(m.group(1));
            }

            for (String param : params) {
                String value = settingsManager.getSystemSetting(param);
                if (value != null) {
                    offerTextString = offerTextString.replace("${" + param + "}", value);
                } else {
                    throw new RuntimeException("Отсутвует системная переменная " + param + " необходимая для формирования текста оферты");
                }
            }
        } else {
            throw new RuntimeException("Отсутвует текст оферты");
        }
        result = new AcceptInvitationPageDataDto(offerTextString, invitation);
        //} else {
        //   result = new AcceptInvitationPageDataDto(true);
        // }
        return result;
    }

    /*@RequestMapping(value = "/invite/{hash}/accept")
    public String acceptInvitation(Model model, HttpServletRequest request, @PathVariable("hash") String hash) {
        Map<String, String> map = invitationService.fillInvitationMap(hash);

        if (map != null) {

            if (map.get("deprecated") != null) {
                model.addAttribute("errorMessage", "Приглашение уже было принято");
                return "inviteAcceptRejectPage";
            }

            model.addAttribute("inviteIsAccept", map.get("inviteIsAccept"));
            model.addAttribute("inviteIsExpire", map.get("inviteIsExpire"));
            model.addAttribute("inviteStatus", map.get("inviteStatus"));
            model.addAttribute("inviteEmail", map.get("inviteEmail"));

        } else {
            model.addAttribute("errorMessage", "Приглашение не найдено");
        }

        model.addAttribute("hash", hash);

        RameraTextEntity offerText = rameraTextDao.getByCode("OFFER");
        if (offerText != null) {
            String offerTextString = offerText.getText();

            Set<String> params = new HashSet<>();

            Pattern p = Pattern.compile("\\$\\{([^}]+)\\}");
            Matcher m = p.matcher(offerTextString);
            while (m.find()) {
                params.add(m.group(1));
            }

            for (String param : params) {
                String value = settingsManager.getSystemSetting(param);
                if (value != null) {
                    offerTextString = offerTextString.replace("${" + param + "}", value);
                } else {
                    throw new RuntimeException("Отсутвует системная переменная " + param + " необходимая для формирования текста оферты");
                }
            }

            model.addAttribute("offerText", offerTextString);
        } else {
            throw new RuntimeException("Отсутвует текст оферты");
        }

        return "inviteAcceptRejectPage";
    }*/

    /**
     * Принять приглашение. Может выпольнить только неавторизованный пользователь.
     *
     * @param acceptRegistrationRequest
     * @return
     */
    @RequestMapping(value = "/invitationaccept/accept.json", method = RequestMethod.POST)
    @ResponseBody
    public AcceptInvitationResult acceptInvitation(@RequestBody AcceptRegistrationRequestDto acceptRegistrationRequest) {
        AcceptInvitationResult result;
        if (SecurityUtils.getUser() != null) {
            result = AcceptInvitationResult.AUTH_USER;
        } else {
            result = invitationService.acceptInvitation(
                    acceptRegistrationRequest.getHash(),
                    acceptRegistrationRequest.getPassword(),
                    acceptRegistrationRequest.getBase64AvatarSrc(),
                    acceptRegistrationRequest.getBase64Avatar(),
                    acceptRegistrationRequest.isNeedSendPassword()
            );
        }
        return result;
    }

    /**
     * Отклонить приглашение. Может выпольнить только неавторизованный пользователь.
     *
     * @param rejectRegistrationRequestDto
     * @return
     */
    @RequestMapping(value = "/invitationaccept/reject.json", method = RequestMethod.POST)
    @ResponseBody
    public RejectInvitationResult rejectInvitation(@RequestBody RejectRegistrationRequestDto rejectRegistrationRequestDto) {
        RejectInvitationResult result;
        if (SecurityUtils.getUser() != null) {
            result = RejectInvitationResult.AUTH_USER;
        } else {
            result = invitationService.rejectInvitation(rejectRegistrationRequestDto.getHash());
        }
        return result;
    }
}
package ru.radom.kabinet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import ru.askor.blagosfera.core.services.invite.InvitationDataService;
import ru.askor.blagosfera.core.services.security.AuthService;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.data.jpa.repositories.invite.InvitationRepository;
import ru.askor.blagosfera.domain.invite.Invitation;
import ru.askor.blagosfera.domain.user.SharerStatus;
import ru.radom.kabinet.dao.RameraTextDao;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.model.RameraTextEntity;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.validator.SharerRegisterValidator;
import ru.radom.kabinet.security.RadomPasswordEncoder;
import ru.radom.kabinet.services.InvitationService;
import ru.radom.kabinet.services.PasswordRecoveryException;
import ru.radom.kabinet.services.ProfileService;
import ru.radom.kabinet.utils.JsonUtils;
import ru.radom.kabinet.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Controller
public class IndexController {
	@Autowired
	private InvitationDataService invitationDataService;

	@Autowired
	private ProfileService profileService;

	@Autowired
	private SharerRegisterValidator registerValidator;

	@Autowired
	private SettingsManager settingsManager;

	@Autowired
	private RameraTextDao rameraTextDao;

	@Autowired
	private SharerDao sharerDao;

	@Autowired
	private InvitationService invitationService;

	@Autowired
	private InvitationRepository invitationRepository;

	@Autowired
	private RadomPasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(registerValidator);
	}

	@RequestMapping("/now")
	public @ResponseBody String showNow() {
		return new Date().toString();
	}

	@RequestMapping(value = "/rules", method = RequestMethod.GET)
	public String showRulesPage(Model model) {
		RameraTextEntity userAgreement = rameraTextDao.getByCode("USER_AGREEMENT");
		model.addAttribute("userAgreement", userAgreement.getText());
		return "rules";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String showLoginPage(Model model, HttpServletRequest request) {
		if ((SecurityContextHolder.getContext().getAuthentication() != null) && (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken))) {
			return "redirect:/";
		} else {
			model.addAttribute("remoteAddr", request.getRemoteAddr());
			return "index";
		}
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String showRegisterPage(Model model) {
		model.addAttribute("signupIsClosed", settingsManager.getSystemSetting("application.signup-is-closed"));
		model.addAttribute("sharer", new UserEntity());
		return "register";
	}

	// TODO Переделать
	/*
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public Object register(@ModelAttribute("sharer") @Valid Sharer sharer, BindingResult result) {
		registerValidator.validate(sharer, result); // TODO Почему то не вызывается валидатор сам
		if (!result.hasErrors()) {
			profileService.register(sharer);
			return new RedirectView("/activate");
		} else {
			ModelAndView view = new ModelAndView("register");
			view.addObject("signupIsClosed", settingsManager.getSystemSetting("application.signup-is-closed"));
			view.addObject("sharer", sharer);
			view.addObject("errors", result.getAllErrors());
			return view;
		}

	}*/

	@RequestMapping(value = "/activate", method = RequestMethod.GET)
	public Object showActivatePage(@RequestParam(value = "code", required = false) String code, @RequestParam(value = "timezone", required = false) String timezone, HttpServletRequest request, HttpServletResponse response) {
		if (StringUtils.hasLength(code)) {
			if (profileService.activate(code,request.getRemoteAddr(),timezone)) {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				if (auth != null) {
					new SecurityContextLogoutHandler().logout(request, response, auth);
				}
				SecurityContextHolder.getContext().setAuthentication(null);
				return new RedirectView("/activated");
			} else {
				return new RedirectView("/activate?error");
			}
		} else {
			return "activate";
		}
	}

	@RequestMapping(value = "/activated", method = RequestMethod.GET)
	public Object showActivatedPage() {
		return "activated";
	}

	@RequestMapping(value = "/recovery/complete.json", method = RequestMethod.POST)
    @ResponseBody
    public String initChangePassword(@RequestParam("code") String code, @RequestParam("password") String password, @RequestParam("confirm") String confirm) {
		try {
			UserEntity userEntity = profileService.completeRecoveryPassword(code, password, confirm);
			return "{\"email\":\"" + userEntity.getEmail() + "\"}";
		} catch (PasswordRecoveryException e) {
			return JsonUtils.getErrorJson(e.getMessage()).toString();
		}
	}

	@RequestMapping(value = "/recovery/init", method = RequestMethod.GET)
	public String showInitPasswordRecoveryPage() {
		return "recoveryInit";
	}

	@RequestMapping(value = "/recovery/complete", method = RequestMethod.GET)
	public String showCompletePasswordRecoveryPage(@RequestParam(value = "code", required = true) String code) {

		UserEntity userEntity = sharerDao.getByPasswordRecoveryCode(code);

		//Если пользователь действительно восттавнавливает пароль и еще не полностью прошел регистрацию,
		//то перенаправляем его на страницу ввода проверочного кода, предварительно выслав ему на почту
		//письмо с новым проверочным кодом
		if (userEntity != null && SharerStatus.NEED_CHANGE_PASSWORD.equals(userEntity.getStatus())) {
			Invitation invite = invitationDataService.findFirstByEmail(userEntity.getEmail());

			if (invite != null) {
				//Отправка письма с новыми данными для авторизации
				// TODO Переделать
				//invitationService.changeAuthDataOfInvited(userEntity, StringUtils.randomString(8), invite);

				//Редирект на страницу ввода проверочного кода
				return "redirect:/ng/#/invitationaccept/" + invite.getHashUrl();
			}

		}

		return "recoveryComplete";
	}
}
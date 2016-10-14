package ru.askor.blagosfera.core.services.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.askor.blagosfera.core.exception.RecaptchaException;
import ru.askor.blagosfera.core.security.RecaptchaService;
import ru.askor.blagosfera.core.util.cache.WebUtils;
import ru.askor.blagosfera.data.redis.services.UserSessionDataService;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.user.UserAuthenticatonEvent;
import ru.askor.blagosfera.domain.events.user.UserAuthenticatonEventType;
import ru.askor.blagosfera.domain.exception.AuthenticationException;
import ru.askor.blagosfera.domain.sessions.UserSession;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;
import ru.radom.kabinet.model.log.LoginType;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.rememberme.PersistentTokenBasedRememberMeServices;
import ru.radom.kabinet.services.ProfileService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Transactional(noRollbackFor = {AuthenticationException.class,
                                RecaptchaException.class,
                                BadCredentialsException.class,
                                SessionAuthenticationException.class})
@Service("authService")
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private UserSessionDataService userSessionDataService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier("sas")
    private SessionAuthenticationStrategy sessionAuthenticationStrategy;

    @Autowired
    @Qualifier("rememberMeServices")
    private PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices;

    @Autowired
    private RecaptchaService recaptchaService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    private SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();

    public AuthServiceImpl() {
    }

    @Override
    public void login(String username, boolean checkPassword, String password, boolean rememberMe,
                      boolean checkCaptcha, String captchaResponse,
                      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, RecaptchaException {
        if (checkCaptcha) {
            recaptchaService.verify(WebUtils.getRemoteIp(request), captchaResponse);
        }

        Authentication authentication;
        UserSession userSession = null;
        UserDetailsImpl userDetails = null;

        if (checkPassword) {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

            try {
                authentication = authenticationManager.authenticate(token);
            } catch (BadCredentialsException | SessionAuthenticationException e) {
                throw new AuthenticationException(AuthenticationException.LOGIN_FAILED, e);
            }

            if (authentication != null) {
                userDetails = (UserDetailsImpl) authentication.getPrincipal();
                userSession = new UserSession(request, username, LoginType.PASSWORD);
            }
        } else {
            userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
            authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            userSession = new UserSession(request, username, LoginType.BIO);
        }

        if (authentication == null || !authentication.isAuthenticated()) return;

        if (rememberMe) persistentTokenBasedRememberMeServices.onLoginSuccess(request, response, authentication);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        sessionAuthenticationStrategy.onAuthentication(authentication, request, response);

        blagosferaEventPublisher.publishEvent(new UserAuthenticatonEvent(this, userSession, userDetails, UserAuthenticatonEventType.LOGIN));

        // TODO возможно это нужно сделать чтобы показывать инструкцию после логина, причем для старого клиента через редирект а для нового как-то иначе
        /*
        UserEntity userEntity = sharerDao.getByEmail(authentication.getName());
		if (!sharerSettingDao.getBoolean(userEntity.getId(), "profile.instruction-showed", false)) {
			getRedirectStrategy().sendRedirect(request, response, "/instruction");
		} else {
			super.onAuthenticationSuccess(request, response, authentication);
		}
         */
    }

    @Override
    public void login(String username, String password, boolean rememberMe, String captchaResponse,
                      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, RecaptchaException {
        login(username, true, password, rememberMe, true, captchaResponse, request, response);
    }

    @Override
    public void login(String username, String password, boolean rememberMe,
                      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, RecaptchaException {
        login(username, true, password, rememberMe, false, null, request, response);
    }

    @Override
    public void login(String username, boolean rememberMe,
                      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, RecaptchaException {
        login(username, false, null, rememberMe, false, null, request, response);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            persistentTokenBasedRememberMeServices.logout(request, response, authentication);
            securityContextLogoutHandler.logout(request, response, authentication);
        }
    }

    @Override
    public void restorePassword(String login, String captchaResponse, HttpServletRequest request, HttpServletResponse response) throws RecaptchaException {
        profileService.restorePasswordInit(login, captchaResponse, request);
    }

    @Override
    public void closeSession(String sessionId) {
        Assert.notNull(sessionId);
        Assert.isTrue(!sessionId.isEmpty());
        SessionInformation sessionInformation = sessionRegistry.getSessionInformation(sessionId);

        if (sessionInformation != null) {
            if ((sessionInformation.getPrincipal() instanceof UserDetailsImpl) && (SecurityUtils.getUserDetails().getUsername().equals(((UserDetailsImpl) sessionInformation.getPrincipal()).getUsername()))) {
                sessionInformation.expireNow();
            }
        }

        //userSessionDataService.delete(sessionId);
    }

    @Override
    public void closeOtherSessions(String currentSessionId) {
        Assert.notNull(currentSessionId);
        Assert.isTrue(!currentSessionId.isEmpty());

        for (UserSession userSession : userSessionDataService.getUserSessions(SecurityUtils.getUserDetails().getUsername())) {
            if (!userSession.getSessionId().equals(currentSessionId)) {
                SessionInformation sessionInformation = sessionRegistry.getSessionInformation(userSession.getSessionId());

                if (sessionInformation != null) {
                    sessionInformation.expireNow();
                }

                //userSessionDataService.delete(userSession.getSessionId());
            }
        }
    }
}

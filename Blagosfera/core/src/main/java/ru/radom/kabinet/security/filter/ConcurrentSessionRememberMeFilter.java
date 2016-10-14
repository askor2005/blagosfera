package ru.radom.kabinet.security.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;
import ru.askor.blagosfera.core.services.security.AuthService;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.user.UserAuthenticatonEvent;
import ru.askor.blagosfera.domain.events.user.UserAuthenticatonEventType;
import ru.askor.blagosfera.domain.sessions.UserSession;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;
import ru.radom.kabinet.model.log.LoginType;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Maxim Nikitin on 04.03.2016.
 */
public class ConcurrentSessionRememberMeFilter extends GenericFilterBean implements ApplicationEventPublisherAware {

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    @Autowired
    private AuthService authService;

    // remember me
    private ApplicationEventPublisher applicationEventPublisher;
    private AuthenticationManager authenticationManager;
    private RememberMeServices rememberMeServices;

    // concurrent session
    private SessionRegistry sessionRegistry;
    private String expiredUrl;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public ConcurrentSessionRememberMeFilter(AuthenticationManager authenticationManager, RememberMeServices rememberMeServices,
                                             SessionRegistry sessionRegistry, String expiredUrl) {
        // remember me
        Assert.notNull(authenticationManager, "authenticationManager cannot be null");
        Assert.notNull(rememberMeServices, "rememberMeServices cannot be null");
        this.authenticationManager = authenticationManager;
        this.rememberMeServices = rememberMeServices;

        // concurrent session
        Assert.notNull(sessionRegistry, "SessionRegistry required");
        Assert.isTrue(expiredUrl == null || UrlUtils.isValidRedirectUrl(expiredUrl), expiredUrl + " isn\'t a valid redirect URL");
        this.sessionRegistry = sessionRegistry;
        this.expiredUrl = expiredUrl;
    }

    @Override
    public void afterPropertiesSet() {
        // remember me
        Assert.notNull(authenticationManager, "authenticationManager must be specified");
        Assert.notNull(rememberMeServices, "rememberMeServices must be specified");

        // concurrent session
        Assert.notNull(sessionRegistry, "SessionRegistry required");
        Assert.isTrue(expiredUrl == null || UrlUtils.isValidRedirectUrl(expiredUrl), expiredUrl + " isn\'t a valid redirect URL");
    }

    // remember me

    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        UserSession userSession = new UserSession(request, userDetails.getUsername(), LoginType.REMEMBER_ME);
        blagosferaEventPublisher.publishEvent(new UserAuthenticatonEvent(this, userSession, userDetails, UserAuthenticatonEventType.LOGIN));
    }

    protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    // concurrent session

    private void doLogout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
    }

    // composite filter, if session expired, try to autologin before proceeding or redirecting to expired url

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);

        if (session != null) {
            SessionInformation info = sessionRegistry.getSessionInformation(session.getId());

            if (info != null) {
                if (info.isExpired()) {
                    doLogout(request, response);

                    if (!autologin(request, response)) {
                        boolean oldFuckingClient = false;

                        if (!request.getServletPath().startsWith("/api/") && request.getServletPath().startsWith("/ng/")) {
                            oldFuckingClient = true;
                        }

                        if (oldFuckingClient) {
                            redirectStrategy.sendRedirect(request, response, expiredUrl);
                            return;
                        }
                    }
                }

                sessionRegistry.refreshLastRequest(info.getSessionId());
            }

            chain.doFilter(request, response);
        } else doFilterRememberMe(request, response, chain);
    }

    public void doFilterRememberMe(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        autologin(request, response);
        chain.doFilter(request, response);
    }

    private boolean autologin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            Authentication rememberMeAuth = rememberMeServices.autoLogin(request, response);

            if (rememberMeAuth != null) {
                try {
                    rememberMeAuth = authenticationManager.authenticate(rememberMeAuth);
                    SecurityContextHolder.getContext().setAuthentication(rememberMeAuth);
                    onSuccessfulAuthentication(request, response, rememberMeAuth);

                    if (logger.isDebugEnabled())
                        logger.debug("SecurityContextHolder populated with remember-me token: \'" + SecurityContextHolder.getContext().getAuthentication() + "\'");

                    if (applicationEventPublisher != null)
                        applicationEventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(SecurityContextHolder.getContext().getAuthentication(), getClass()));

                    return true;
                } catch (AuthenticationException e) {
                    if (logger.isDebugEnabled())
                        logger.debug("SecurityContextHolder not populated with remember-me token, as AuthenticationManager rejected Authentication returned by RememberMeServices: \'" + rememberMeAuth + "\'; invalidating remember-me token", e);

                    rememberMeServices.loginFail(request, response);
                    onUnsuccessfulAuthentication(request, response, e);
                }
            }
        } else if (logger.isDebugEnabled())
            logger.debug("SecurityContextHolder not populated with remember-me token, as it already contained: \'" + SecurityContextHolder.getContext().getAuthentication() + "\'");

        return false;
    }
}

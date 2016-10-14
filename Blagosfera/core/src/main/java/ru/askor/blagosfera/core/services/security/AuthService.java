package ru.askor.blagosfera.core.services.security;

import ru.askor.blagosfera.core.exception.RecaptchaException;
import ru.askor.blagosfera.domain.exception.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthService {

    void login(String username, boolean checkPassword, String password, boolean rememberMe,
               boolean checkCaptcha, String captchaResponse,
               HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, RecaptchaException;

    void login(String username, String password, boolean rememberMe, String captchaResponse,
               HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, RecaptchaException;

    void login(String username, String password, boolean rememberMe,
               HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, RecaptchaException;

    void login(String username, boolean rememberMe,
               HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, RecaptchaException;

    void logout(HttpServletRequest request, HttpServletResponse response);

    void restorePassword(String username, String captchaResponse, HttpServletRequest request, HttpServletResponse response) throws RecaptchaException;

    void closeSession(String sessionId);

    void closeOtherSessions(String currentSessionId);
}

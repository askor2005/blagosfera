package ru.askor.blagosfera.core.services.support;

import ru.askor.blagosfera.core.exception.RecaptchaException;
import ru.askor.blagosfera.domain.support.SupportRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by vtarasenko on 18.05.2016.
 */
public interface SupportRequestService {
    public void createSupportRequest(HttpServletRequest httpServletRequest, String email, String theme, String description, Long supportRequestTypeId, String captcha) throws Exception;

    public void validateCreateRequest(HttpServletRequest httpServletRequest, String email, String theme, String description, Long supportRequestTypeId, String captcha) throws Exception;

    public void sendToEmail(SupportRequest supportRequest);
    public void close(Long id);
}

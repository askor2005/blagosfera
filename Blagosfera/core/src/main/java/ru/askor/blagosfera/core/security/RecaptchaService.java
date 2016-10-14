package ru.askor.blagosfera.core.security;

import ru.askor.blagosfera.core.exception.RecaptchaException;

/**
 * Created by Maxim Nikitin on 05.04.2016.
 */
public interface RecaptchaService {

    RecaptchaResponse verify(String remoteIp, String response) throws RecaptchaException;

    String getSitekey();
}

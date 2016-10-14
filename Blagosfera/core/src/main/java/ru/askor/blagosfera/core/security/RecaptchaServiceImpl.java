package ru.askor.blagosfera.core.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.askor.blagosfera.core.exception.RecaptchaException;

import java.util.Collections;

/**
 * Created by Maxim Nikitin on 05.04.2016.
 */
@Service("recaptchaService")
public class RecaptchaServiceImpl implements RecaptchaService {

    private final RestTemplate restTemplate;

    @Value("${recaptcha.url}")
    private String recaptchaUrl;

    @Value("${recaptcha.secret-key}")
    private String recaptchaSecretKey;

    @Value("${recaptcha.sitekey}")
    private String recaptchaSitekey;

    public RecaptchaServiceImpl() {
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

    @Override
    public RecaptchaResponse verify(String remoteIp, String response) throws RecaptchaException {
        try {
            RecaptchaResponse recaptchaResponse = restTemplate.postForEntity(
                    recaptchaUrl,
                    createBody(recaptchaSecretKey, remoteIp, response),
                    RecaptchaResponse.class).getBody();

            if (!recaptchaResponse.isSuccess()) {
                if (recaptchaResponse.getErrorCodes() == null) {
                    recaptchaResponse.setErrorCodes(Collections.singletonList(RecaptchaResponse.NULL_ERROR_CODES));
                }

                throw new RecaptchaException(recaptchaResponse.getErrorCodes().iterator().next(), recaptchaResponse);
            }

            return recaptchaResponse;
        } catch (RestClientException e) {
            throw new RecaptchaException("Recaptcha API not available due to exception", e);
        }
    }

    @Override
    public String getSitekey() {
        return recaptchaSitekey;
    }

    private MultiValueMap<String, String> createBody(String secret, String remoteIp, String response) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("secret", secret);
        form.add("remoteip", remoteIp);
        form.add("response", response);
        return form;
    }
}

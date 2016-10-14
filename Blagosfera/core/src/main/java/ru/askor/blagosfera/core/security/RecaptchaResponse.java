package ru.askor.blagosfera.core.security;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

/**
 * Created by Maxim Nikitin on 05.04.2016.
 */
public class RecaptchaResponse {

    public static final String MISSING_INPUT_SECRET = "missing-input-secret"; // The secret parameter is missing.
    public static final String INVALID_INPUT_SECRET = "invalid-input-secret"; // The secret parameter is invalid or malformed.
    public static final String MISSING_INPUT_RESPONSE = "missing-input-response"; // The response parameter is missing.
    public static final String INVALID_INPUT_RESPONSE = "invalid-input-response"; // The response parameter is invalid or malformed.
    public static final String NULL_ERROR_CODES = "null-error-codes"; // success = false, errorCodes = null

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("error-codes")
    private Collection<String> errorCodes;

    RecaptchaResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Collection<String> getErrorCodes() {
        return errorCodes;
    }

    public void setErrorCodes(Collection<String> errorCodes) {
        this.errorCodes = errorCodes;
    }
}

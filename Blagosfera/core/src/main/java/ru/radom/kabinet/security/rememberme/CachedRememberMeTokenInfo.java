package ru.radom.kabinet.security.rememberme;

/**
 *
 * Created by vgusev on 08.02.2016.
 */
public class CachedRememberMeTokenInfo {

    private String value;
    long cachingTime;

    public CachedRememberMeTokenInfo(String tokenValue, long cachingTime) {
        this.value = tokenValue;
        this.cachingTime = cachingTime;
    }

    /**
     * Gets token date and time of token caching as milliseconds
     * @return Date and time of token caching
     */
    public long getCachingTime() {
        return cachingTime;
    }

    public String getValue() {
        return value;
    }
}
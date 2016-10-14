package ru.radom.kabinet.security.rememberme;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * Created by vgusev on 06.12.2015.
 */
public class PersistentTokenBasedRememberMeServices extends org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices {

    /**
     *  время жизни куки (2 недели)
     */
    private static final int PERSISTENT_TOKEN_REMEMBER_ME_HOURS = 168;

    /**
     * Запрос очистки записи с токеном при выходе из системы. Поведение по умолчанию: удаление всех записей пользователя,
     * что приводит к логауту в других браузерах
     */
    private final static String REMOVE_TOKEN_QUERY = "DELETE FROM persistent_logins WHERE series = ? AND token = ?";

    /**
     * Максимальное количество записей кеша токенов
     */
    private static final int TOKEN_CACHE_MAX_SIZE = 100;

    /**
     * jdbc репозиторий токенов
     */
    private JdbcTokenRepositoryImpl tokenRepository;

    /**
     * Мапа с кешем токенов
     */
    private final Map<String, CachedRememberMeTokenInfo> tokenCache = new ConcurrentHashMap<>();

    /**
     * Время между запросами на авторизацию. 5 секунд должно хватить. Хотя...
     */
    private int cachedTokenValidityTime = 5 * 1000;

    private static final Object lock = new Object();

    public PersistentTokenBasedRememberMeServices(
            String key, UserDetailsService userDetailsService,
            JdbcTokenRepositoryImpl tokenRepository, int cachedTokenValidityTime) {
        super(key, userDetailsService, tokenRepository);
        this.tokenRepository = tokenRepository;
        this.cachedTokenValidityTime = cachedTokenValidityTime;
    }

    /**
     * Переопределённый метод логаута. По умолчанию при логауте удаляются все токены пользователя.
     * В этом методе удаляется только текущий токен
     * @param request запрос
     * @param response ответ
     * @param authentication инф. об аутентификации
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String cookie = extractRememberMeCookie(request);
        if (cookie != null) {
            String[] seriesAndToken = decodeCookie(cookie);
            if (logger.isDebugEnabled()) {
                logger.debug("Logout of user " + (authentication == null ? "Unknown" : authentication.getName()));
            }
            cancelCookie(request, response);
            tokenRepository.getJdbcTemplate().update(REMOVE_TOKEN_QUERY, seriesAndToken);
            tokenCache.remove(seriesAndToken[0]);
            validateTokenCache();
        }
    }

    /**
     * Solution for preventing "remember-me" bug. Some browsers sends preloading requests to server to speed-up
     * page loading. It may cause error when response of preload request not returned to client and second request
     * from client was send. This method implementation stores token in cache for <link>CACHED_TOKEN_VALIDITY_TIME</link>
     * milliseconds and check token presence in cache before process authentication. If there is no equivalent token in
     * cache authentication performs normally. If equivalent present in cache we should not update token in database.
     * This approach can provide acceptable security level and prevent errors.
     * {@inheritDoc}
     * @see <a href="http://jira.jtalks.org/browse/JC-1743">JC-1743</a>
     * @see <a href="https://developers.google.com/chrome/whitepapers/prerender?csw=1">PageEntity preloading in Google Chrome</a>
     */
    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) {
        if (cookieTokens.length != 2) {
            throw new InvalidCookieException("Cookie token did not contain " + 2 +
                    " tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
        }

        final String presentedSeries = cookieTokens[0];
        final String presentedToken = cookieTokens[1];

        PersistentRememberMeToken token = tokenRepository.getTokenForSeries(presentedSeries);

        if (token == null) {
            // No series match, so we can't authenticate using this cookie
            throw new RememberMeAuthenticationException("No persistent token found for series id: " + presentedSeries);
        }

        UserDetails details = null;
        boolean isNeedLoginSecurity = false;
        // Лочим блок с проверкой кеша и кешированием
        synchronized (lock) {
            if (isTokenCached(presentedSeries, presentedToken)) {
                //tokenCache.remove(presentedSeries);
                details = getUserDetailsService().loadUserByUsername(token.getUsername());
                rewriteCookie(token, request, response);
            } else {
                cacheToken(token);
                isNeedLoginSecurity = true;
            }
        }

        if (isNeedLoginSecurity) {
            /* IMPORTANT: We should store token in cache before calling <code>loginWithSpringSecurity</code> method.
               Because execution of this method can take a long time.
             */
            cacheToken(token);
            try {
                details = loginWithSpringSecurity(cookieTokens, request, response);
                //We should remove token from cache if cookie really was stolen or other authentication error occurred
            } catch (RememberMeAuthenticationException ex) {
                tokenCache.remove(token.getSeries());
                throw ex;
            }
        }
        validateTokenCache();

        return details;
    }

    /**
     * Calls PersistentTokenBasedRememberMeServices#processAutoLoginCookie method.
     * Needed for possibility to test.
     */
    UserDetails loginWithSpringSecurity(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) {
        return super.processAutoLoginCookie(cookieTokens, request, response);
    }

    /**
     * Sets valid cookie to response
     * Needed for possibility to test.
     */
    void rewriteCookie(PersistentRememberMeToken token, HttpServletRequest request, HttpServletResponse response) {
        setCookie(new String[] {token.getSeries(), token.getTokenValue()}, getTokenValiditySeconds(), request, response);
    }

    /**
     * Stores token in cache.
     * @param token Token to be stored
     * @see CachedRememberMeTokenInfo
     */
    private void cacheToken(PersistentRememberMeToken token) {
        if (tokenCache.size() >= TOKEN_CACHE_MAX_SIZE) {
            validateTokenCache();
        }
        CachedRememberMeTokenInfo tokenInfo = new CachedRememberMeTokenInfo(token.getTokenValue(), System.currentTimeMillis());
        tokenCache.put(token.getSeries(), tokenInfo);
    }

    /**
     * Removes from cache tokens which were stored more than <link>CACHED_TOKEN_VALIDITY_TIME</link> milliseconds ago.
     */
    private void validateTokenCache() {
        tokenCache.entrySet().stream().filter(entry -> !isTokenInfoValid(entry.getValue())).forEach(tokenCache::remove);
    }

    /**
     * Checks if given tokenInfo valid.
     * @param tokenInfo Token info to be checked
     * @return <code>true</code> tokenInfo was stored in cache less than <link>CACHED_TOKEN_VALIDITY_TIME</link> milliseconds ago.
     * <code>false</code> otherwise.
     * @see CachedRememberMeTokenInfo
     */
    private boolean isTokenInfoValid(CachedRememberMeTokenInfo tokenInfo) {
        if ((System.currentTimeMillis() - tokenInfo.getCachingTime()) >= cachedTokenValidityTime) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Checks if token with given series and value stored in cache
     * @param series series to be checked
     * @param value value to be checked
     * @return <code>true</code> if token stored in cache< <code>false</code> otherwise.
     */
    private boolean isTokenCached(String series, String value) {
        if (tokenCache.containsKey(series) && isTokenInfoValid(tokenCache.get(series))
                && value.equals(tokenCache.get(series).getValue())) {
            return true;
        }
        return false;
    }

    /**
     * Переопределённый метод получения времени жизни токена.
     * По умолчанию 1 день. Значение задаётся настройкой в часах
     * @return количество секунд
     */
    @Override
    protected int getTokenValiditySeconds() {
        return PERSISTENT_TOKEN_REMEMBER_ME_HOURS * 60 * 60;
    }

    @Override
    public void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
        String username = successfulAuthentication.getName();
        this.logger.debug("Creating new persistent login for user " + username);
        PersistentRememberMeToken persistentToken = new PersistentRememberMeToken(username, this.generateSeriesData(), this.generateTokenData(), new Date());

        try {
            this.tokenRepository.createNewToken(persistentToken);
            this.addCookie(persistentToken, request, response);
        } catch (Exception var7) {
            this.logger.error("Failed to save persistent token ", var7);
        }
    }

    private void addCookie(PersistentRememberMeToken token, HttpServletRequest request, HttpServletResponse response) {
        this.setCookie(new String[]{token.getSeries(), token.getTokenValue()}, this.getTokenValiditySeconds(), request, response);
    }
}

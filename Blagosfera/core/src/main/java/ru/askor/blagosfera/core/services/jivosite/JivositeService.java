package ru.askor.blagosfera.core.services.jivosite;

import ru.askor.blagosfera.domain.jivosite.JivositeUserInfo;

/**
 * Created by vtarasenko on 25.07.2016.
 */
public interface JivositeService {
    JivositeUserInfo getUserInfo(Long userId);

    String getUserToken(Long userId);
}

package ru.askor.blagosfera.domain.jivosite;

import lombok.Data;

/**
 * Created by vtarasenko on 25.07.2016.
 */
@Data
public class JivositeInfo {
    private JivositeUserInfo userInfo;
    private String userToken;
    private boolean authorized;
}


package ru.askor.blagosfera.core.services.vcard;

import ru.askor.blagosfera.domain.Address;
import ru.askor.blagosfera.domain.user.User;

import java.io.IOException;

/**
 * Created by vtarasenko on 13.06.2016.
 */
public interface VcardGenerator {
    public String generate(User user, byte[] avatar, Address officeAddress,
                           Address actualAddress, String timezone,
                           String registratorOfficePhone, String registratorMobilePhone, String registratorPhone, String appUrl) throws IOException;
}

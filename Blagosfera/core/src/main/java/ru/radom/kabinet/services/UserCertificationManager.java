package ru.radom.kabinet.services;

import ru.askor.blagosfera.domain.certification.UserCertificationDocType;
import ru.askor.blagosfera.domain.certification.UserCertificationException;
import ru.askor.blagosfera.domain.certification.UserCertificationSession;
import ru.askor.blagosfera.domain.user.User;

import java.util.Date;
import java.util.List;

public interface UserCertificationManager {

    UserCertificationSession startCertificationSession(User registratorUser, User user) throws UserCertificationException;

    UserCertificationSession finishCertificationSession(User registratorUser, User user);

    UserCertificationSession getActiveCertificationSession(String sessionId);

    Date getActiveSessionEndDate(String sessionId);

    void setUserAgreed(String sessionId, boolean userAgreed);

    List<UserCertificationDocType> getDocTypes();
}

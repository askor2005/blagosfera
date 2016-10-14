package ru.radom.kabinet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.data.jpa.repositories.certification.CertificationRequestRepository;
import ru.askor.blagosfera.data.jpa.repositories.certification.UserCertificationDocTypeRepository;
import ru.askor.blagosfera.data.jpa.repositories.certification.UserCertificationSessionRepository;
import ru.askor.blagosfera.domain.certification.UserCertificationDocType;
import ru.askor.blagosfera.domain.certification.UserCertificationException;
import ru.askor.blagosfera.domain.certification.UserCertificationSession;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.model.UserCertificationSessionEntity;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.registration.RegistrationRequest;
import ru.radom.kabinet.model.registration.RegistrationRequestStatus;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Transactional
@Service("userCertificationManager")
public class UserCertificationManagerImpl implements UserCertificationManager {

    public static final String SESSION_MAX_LIFETIME_IN_MINUTES_KEY = "certification.session.lifetime";
    public static final int SESSION_MAX_LIFETIME_IN_MINUTES_DEFAULT_VAL = 30;
    public static final String SESSION_MAX_LIFETIME_IN_MINUTES_DESCRIPTION = "Максимальное время жизни сессии идентификации пользователя, в минутах.";

    @Autowired
    private CertificationRequestRepository certificationRequestRepository;

    @Autowired
    private UserCertificationSessionRepository userCertificationSessionRepository;

    @Autowired
    private UserCertificationDocTypeRepository userCertificationDocTypeRepository;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private SharerDao sharerDao;

    public UserCertificationManagerImpl() {
    }

    @Deprecated
    private UserCertificationSession startCertificationSession(UserEntity registrator, UserEntity user) throws UserCertificationException {
        List<RegistrationRequest> requests = certificationRequestRepository.findAllByRegistrator_IdAndStatus(registrator.getId(), RegistrationRequestStatus.NEW);
        boolean requestExists = false;
        for (RegistrationRequest request : requests) {
            if (request.getObject() instanceof UserEntity && request.getObject().getId().equals(user.getId())) {
                requestExists = true;
                break;
            }
        }
        if (!requestExists) throw new UserCertificationException("Не найдена заявка на идентификацию.");
        UserCertificationSessionEntity userCertificationSession = getActiveUserCertificationSession(registrator, user, true, true);
        userCertificationSession.setStartDate(new Date());
        userCertificationSession = userCertificationSessionRepository.save(userCertificationSession);
        return userCertificationSession.toDomain();
    }

    public UserCertificationSession startCertificationSession(User registratorUser, User user) throws UserCertificationException {
        // TODO Переделать
        UserEntity registrator = sharerDao.getById(registratorUser.getId());
        UserEntity userEntity = sharerDao.getById(user.getId());
        return startCertificationSession(registrator, userEntity);
    }

    @Deprecated
    private UserCertificationSession finishCertificationSession(UserEntity registrator, UserEntity user) {
        UserCertificationSessionEntity userCertificationSession = getActiveUserCertificationSession(registrator, user, false, false);

        if (userCertificationSession != null) {
            userCertificationSession.setEndDate(new Date());
            userCertificationSession.setSuccess(true);
            userCertificationSessionRepository.save(userCertificationSession);
            return userCertificationSession.toDomain();
        } else return null;
    }

    @Override
    public UserCertificationSession finishCertificationSession(User registratorUser, User user) {
        // TODO Переделать
        UserEntity registrator = sharerDao.getById(registratorUser.getId());
        UserEntity userEntity = sharerDao.getById(user.getId());
        return finishCertificationSession(registrator, userEntity);
    }

    @Override
    public UserCertificationSession getActiveCertificationSession(String sessionId) {
        if (sessionId == null) return null;
        UserCertificationSessionEntity userCertificationSession = checkIfExpired(userCertificationSessionRepository.findOneBySessionIdAndEndDateNull(sessionId));
        if (userCertificationSession != null) return userCertificationSession.toDomain();
        else return null;
    }

    @Override
    public Date getActiveSessionEndDate(String sessionId) {
        UserCertificationSessionEntity userCertificationSession = userCertificationSessionRepository.findOneBySessionIdAndEndDateNull(sessionId);
        if (userCertificationSession != null) {
            int sessionMaxLifetime = getSessionMaxLifetime();
            Date startDate = new Date();
            startDate.setTime(userCertificationSession.getStartDate().getTime() + sessionMaxLifetime * 60 * 1000);
            return startDate;
        } else return null;
    }

    @Override
    public void setUserAgreed(String sessionId, boolean userAgreed) {
        UserCertificationSessionEntity userCertificationSession = userCertificationSessionRepository.findOneBySessionIdAndEndDateNull(sessionId);
        if (userCertificationSession != null) {
            userCertificationSession.setUserAgreed(userAgreed);
            userCertificationSession = userCertificationSessionRepository.save(userCertificationSession);
        }
    }

    @Override
    public List<UserCertificationDocType> getDocTypes() {
        return userCertificationDocTypeRepository.findAll().stream().map(item -> item.toDomain()).collect(Collectors.toList());
    }

    private int getSessionMaxLifetime() {
        String value = settingsManager.getSystemSetting(SESSION_MAX_LIFETIME_IN_MINUTES_KEY);

        if (value == null) {
            settingsManager.setSystemSetting(SESSION_MAX_LIFETIME_IN_MINUTES_KEY, String.valueOf(SESSION_MAX_LIFETIME_IN_MINUTES_DEFAULT_VAL), SESSION_MAX_LIFETIME_IN_MINUTES_DESCRIPTION);
            return SESSION_MAX_LIFETIME_IN_MINUTES_DEFAULT_VAL;
        }

        return Integer.valueOf(value);
    }

    private UserCertificationSessionEntity getActiveUserCertificationSession(UserEntity registrator, UserEntity user, boolean createIfNotExists, boolean checkIfExpired) {
        UserCertificationSessionEntity userCertificationSession = userCertificationSessionRepository.findOneByRegistrator_IdAndUser_IdAndEndDateNull(registrator.getId(), user.getId());

        if (userCertificationSession != null && checkIfExpired) userCertificationSession = checkIfExpired(userCertificationSession);
        if (userCertificationSession == null && createIfNotExists) userCertificationSession = createUserCertificationSession(registrator, user);

        return userCertificationSession;
    }

    private UserCertificationSessionEntity checkIfExpired(UserCertificationSessionEntity userCertificationSession) {
        if (userCertificationSession != null) {
            int sessionMaxLifetime = getSessionMaxLifetime();
            long duration = new Date().getTime() - userCertificationSession.getStartDate().getTime();
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);

            if (diffInMinutes > sessionMaxLifetime) {
                userCertificationSession.setEndDate(new Date());
                userCertificationSessionRepository.save(userCertificationSession);
                return null;
            }
        }

        return userCertificationSession;
    }

    private UserCertificationSessionEntity createUserCertificationSession(UserEntity registrator, UserEntity user) {
        UserCertificationSessionEntity userCertificationSession = new UserCertificationSessionEntity();
        userCertificationSession.setRegistrator(registrator);
        userCertificationSession.setUser(user);
        userCertificationSession.setStartDate(new Date());
        userCertificationSession.setSuccess(false);
        userCertificationSession.setSessionId(UUID.randomUUID().toString());

        userCertificationSession = userCertificationSessionRepository.save(userCertificationSession);
        return userCertificationSession;
    }
}

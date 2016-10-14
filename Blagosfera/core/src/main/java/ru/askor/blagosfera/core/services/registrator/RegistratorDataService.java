package ru.askor.blagosfera.core.services.registrator;

import ru.askor.blagosfera.domain.registrator.RegistratorDomain;
import ru.askor.blagosfera.domain.registrator.RegistratorSort;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.registration.RegistratorLevel;

import java.util.List;

/**
 * Created by vtarasenko on 14.04.2016.
 */
public interface RegistratorDataService {
    List<RegistratorDomain> search(Long currentUserId, String nameTemplate, int page, int pageSize,
                                   RegistratorLevel filterLevel, Double latitude, Double longitude,
                                   List<String> excludeLevels, boolean includeRequestedForRegistration, RegistratorSort registratorSort, boolean requestedForRegistrationsOnlyToMe);

    List<RegistratorDomain> search(Long currentUserId, String nameTemplate,
                                   RegistratorLevel filterLevel, Double latitude, Double longitude,
                                   List<String> excludeLevels, boolean includeRequestedForRegistration, boolean requestedForRegistrationsOnlyToMe);

    Long count(Long currentUserId, String nameTemplate,
               RegistratorLevel filterLevel,
               List<String> excludeLevels);

    List<User> getVerifiedRegistrators(Long userId);

    User getByRegistratorId(Long registratorId);

    RegistratorDomain getRegistratorDtoById(Long id);

    Integer getRegistratorLevelById(Long id);

    String getRegistratorLevel(final Long registratorId);
}

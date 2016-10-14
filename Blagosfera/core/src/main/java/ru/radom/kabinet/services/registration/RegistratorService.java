package ru.radom.kabinet.services.registration;

import ru.askor.blagosfera.domain.registrator.RegistratorDomain;
import ru.askor.blagosfera.domain.registrator.RegistratorSort;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;
import ru.radom.kabinet.model.registration.RegistratorLevel;

import java.util.List;

/**
 * Created by vtarasenko on 14.04.2016.
 */
public interface RegistratorService {
    List<RegistratorDomain> page(Long userId, Integer page, Integer perPage, String nameTemplate,
                                 RegistratorLevel level, Double latitude, Double longitude, List<String> excludeLevels, boolean includeRequestedForRegistration, RegistratorSort sort, boolean requestedForRegistrationsOnlyToMe);

    List<RegistratorDomain> page(Long currentUserId, String nameTemplate,
                                 RegistratorLevel level, Double latitude, Double longitude, List<String> excludeLevels, boolean includeRequestedForRegistration, boolean requestedForRegistrationsOnlyToMe);

    Long count(Long currentUserId, List<String> excludeLevels);

    void setActiveRegistrator(Long registratorId, boolean active);

    boolean isActiveRegistrator(UserDetailsImpl userDetails);
}

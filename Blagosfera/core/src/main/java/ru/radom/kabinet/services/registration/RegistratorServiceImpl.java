package ru.radom.kabinet.services.registration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.core.services.registrator.RegistratorDataService;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.domain.registrator.RegistratorDomain;
import ru.askor.blagosfera.domain.registrator.RegistratorSort;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.model.registration.RegistratorLevel;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.Roles;

import java.util.List;

@Service
public class RegistratorServiceImpl implements RegistratorService {

    private static final String REGISTRAOR_ACTIVE_SETTINGS_ATTR_NAME = "registratorIsActive";

    @Autowired
    private SharerDao sharerDao;
    @Autowired
    private RegistratorDataService registratorDataService;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private UserDataService userDataService;

    @Override
    public List<RegistratorDomain> page(Long userId, final Integer page, Integer perPage, String nameTemplate,
                                        RegistratorLevel level, Double latitude, Double longitude, List<String> excludeLevels, boolean includeRequestedForRegistration, RegistratorSort registratorSort, boolean requestedForRegistrationsOnlyToMe){
        if(latitude == null || longitude == null){
            latitude = sharerDao.getActualAddress(SecurityUtils.getUser().getId()).getLatitude();
            longitude = sharerDao.getActualAddress(SecurityUtils.getUser().getId()).getLongitude();
        }
        return registratorDataService.search(userId,nameTemplate,page,perPage,level,latitude,longitude,excludeLevels, includeRequestedForRegistration,registratorSort, requestedForRegistrationsOnlyToMe);
    }
    @Override
    public List<RegistratorDomain> page(Long currentUserId, String nameTemplate,
                                        RegistratorLevel level, Double latitude, Double longitude, List<String> excludeLevels, boolean includeRequestedForRegistration, boolean requestedForRegistrationsOnlyToMe){
        if(latitude == null || longitude == null){
            latitude = sharerDao.getActualAddress(SecurityUtils.getUser().getId()).getLatitude();
            longitude = sharerDao.getActualAddress(SecurityUtils.getUser().getId()).getLongitude();
        }
        return registratorDataService.search(currentUserId, nameTemplate, level, latitude, longitude, excludeLevels, includeRequestedForRegistration, requestedForRegistrationsOnlyToMe);
    }

    @Override
    public Long count(Long currentUserId, List<String> excludeLevels){
        return registratorDataService.count(currentUserId,null,null,null);
    }

    @Override
    public void setActiveRegistrator(Long registratorId, boolean active) {
        User user = userDataService.getByIdMinData(registratorId);
        settingsManager.setUserSetting(REGISTRAOR_ACTIVE_SETTINGS_ATTR_NAME, String.valueOf(active), user);
    }

    @Override
    public boolean isActiveRegistrator(UserDetailsImpl userDetails) {
        boolean result = userDetails.hasAnyRole(Roles.REGISTRATOR_ROLES);
        result = result && settingsManager.getUserSettingAsBoolean(REGISTRAOR_ACTIVE_SETTINGS_ATTR_NAME, userDetails.getUser().getId(), true);
        return result;
    }
}

package ru.radom.kabinet.services.communities.organization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.settings.SharerSettingDao;
import ru.radom.kabinet.model.settings.SharerSetting;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.communities.organization.dto.CreateOrganizationTempDataDto;

import java.util.Collections;
import java.util.Map;

/**
 *
 * Created by vgusev on 01.02.2016.
 */
@Service
@Transactional
public class RegisterOrganizationService {

    @Autowired
    private SharerSettingDao sharerSettingDao;

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private SharerDao sharerDao;

    private static final String CREATE_ORGANIZATION_SHARER_SETTINGS_KEY = "organization.create.sharer.settings";

    public void saveOrganizationTempData(CreateOrganizationTempDataDto createOrganizationTempDataDto, Long userId) {
        String savedJson = serializeService.toJson(createOrganizationTempDataDto);
        sharerSettingDao.set(sharerDao.getById(userId), CREATE_ORGANIZATION_SHARER_SETTINGS_KEY, savedJson);
    }

    public Map<String, Object> getOrganizationTempData(Long userId) {
        Map<String, Object> result;
        String organizationJson = sharerSettingDao.get(userId, CREATE_ORGANIZATION_SHARER_SETTINGS_KEY, null);
        if (organizationJson == null) {
            result = Collections.emptyMap();
        } else {
            try {
                result = serializeService.jsonToMap(organizationJson);
            } catch (Exception e) {
                result = Collections.emptyMap();
            }
        }
        return result;
    }

    public void clearOrganizationTempData(Long userId) {
        SharerSetting sharerSetting = sharerSettingDao.getByKey(CREATE_ORGANIZATION_SHARER_SETTINGS_KEY, userId);
        if (sharerSetting != null) {
            sharerSettingDao.delete(sharerSetting);
        }
    }
}
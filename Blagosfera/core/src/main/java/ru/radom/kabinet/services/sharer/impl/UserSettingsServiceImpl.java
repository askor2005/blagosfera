package ru.radom.kabinet.services.sharer.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.settings.SharerSettingDao;
import ru.radom.kabinet.model.settings.SharerSetting;
import ru.radom.kabinet.services.sharer.UserSettingsService;

import java.util.List;

/**
 *
 * Created by vgusev on 18.03.2016.
 */
@Service
@Transactional
public class UserSettingsServiceImpl implements UserSettingsService {

    @Autowired
    private SharerSettingDao sharerSettingDao;

    @Autowired
    private SharerDao sharerDao;

    @Override
    public String get(User user, String key) {
        return sharerSettingDao.get(user.getId(), key);
    }
    @Override
    public Long get(User user,String  key,long defaultValue) {
        return sharerSettingDao.getLong(user.getId(), key,defaultValue);
    }

    @Override
    public String get(User user, String key, String defaultValue) {
        return sharerSettingDao.get(user.getId(), key, defaultValue);
    }

    @Override
    public boolean getBoolean(User user, String key, boolean defaultValue) {
        return sharerSettingDao.getBoolean(user.getId(), key, defaultValue);
    }

    @Override
    public List<Long> getLongsList(User user, String key, List<Long> defaultValue) {
        return sharerSettingDao.getLongsList(sharerDao.getById(user.getId()), key, defaultValue);
    }

    @Override
    public void set(User user, String key, String value) {
        sharerSettingDao.set(sharerDao.getById(user.getId()), key, value);
    }

    public void delete(User user, String key) {
        SharerSetting sharerSetting = sharerSettingDao.getByKey(key, user.getId());
        if (sharerSetting != null) {
            sharerSettingDao.delete(sharerSetting);
        }
    }
}

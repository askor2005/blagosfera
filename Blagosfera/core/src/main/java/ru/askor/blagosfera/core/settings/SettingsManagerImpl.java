package ru.askor.blagosfera.core.settings;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.data.jpa.repositories.settings.UserSettingRepository;
import ru.askor.blagosfera.data.jpa.services.settings.SystemSettingService;
import ru.askor.blagosfera.domain.settings.SystemSetting;
import ru.askor.blagosfera.domain.settings.SystemSettingsPage;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.settings.SharerSetting;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.utils.VarUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Maxim Nikitin on 17.02.2016.
 */
@Transactional
@Service("settingsManager")
public class SettingsManagerImpl implements SettingsManager {

    @Autowired
    private SystemSettingService systemSettingService;

    @Autowired
    private UserSettingRepository userSettingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SerializeService serializeService;

    private List<SystemSettingObserver> observers = new ArrayList<>();

    public SettingsManagerImpl() {
    }

    @Override
    public String getSystemSetting(String key) {
        return systemSettingService.getSystemSetting(key);
    }

    @Override
    public String getSystemSetting(String key, String defaultValue) {
        String value = getSystemSetting(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public int getSystemSettingAsInt(String key, int defaultValue) {
        String value = getSystemSetting(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    @Override
    public String setSystemSetting(String key, String value, String description) {
        systemSettingService.setSystemSetting(key, value, description);
        notifyObservers(key, value, description);
        return value;
    }

    @Override
    public List<SystemSetting> getSystemSettings(List<String> keys) {
        return systemSettingService.getSystemSettings(keys);
    }

    @Override
    public SystemSettingsPage getSystemSettings(int page, int size, String keyFilter, String descriptionFilter) {
        return systemSettingService.getSystemSettings(page, size, keyFilter, descriptionFilter);
    }

    @Override
    public void deleteSystemSetting(Long settingId) {
        systemSettingService.deleteSystemSetting(settingId);
    }

    @Override
    public List<Integer> getSystemSettingAsIntegers(String key) {
        String value = getSystemSetting(key);
        List<Integer> list = new ArrayList<>();

        try {
            for (String part : value.split(",")) {
                list.add(Integer.parseInt(part));
            }

            return list;
        } catch (Exception e) {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public boolean getSystemSettingAsBool(String key, boolean defaultValue) {
        String value = getSystemSetting(key);
        return VarUtils.getBool(value, defaultValue);
    }

    @Override
    public <T> T getSystemSettingsAsObject(String key, Class<T> clazz, T defaultValue) {
        return serializeService.toObject(getSystemSetting(key), clazz, defaultValue);
    }

    @Override
    public String getUserSetting(String key, Long userId) {
        SharerSetting userSetting = userSettingRepository.findOneByUser_IdAndKey(userId, key);
        if (userSetting == null) return null;
        return userSetting.getValue();
    }

    @Override
    public boolean getUserSettingAsBoolean(String key, Long userId, boolean defaultValue) {
        String value = getUserSetting(key, userId);
        return VarUtils.getBool(value, defaultValue);
    }

    @Override
    public String setUserSetting(String key, String value, User user) {
        SharerSetting userSetting = userSettingRepository.findOneByUser_IdAndKey(user.getId(), key);

        if (userSetting == null) {
            UserEntity userEntity = userRepository.findOne(user.getId());
            userSetting = new SharerSetting();
            userSetting.setUser(userEntity);
            userSetting.setKey(key);
        }

        userSetting.setValue(value);
        userSettingRepository.save(userSetting);
        return value;
    }

    @Override
    public void deleteUserSetting(String key, Long userId) {
        SharerSetting setting = userSettingRepository.findOneByUser_IdAndKey(userId, key);
        if (setting != null) userSettingRepository.delete(setting);
    }

    @Override
    public void registerObserver(SystemSettingObserver observer) {
        if (!observers.contains(observer)) observers.add(observer);
    }

    @Override
    public void unregisterObserver(SystemSettingObserver observer) {
        if (observers.contains(observer)) observers.remove(observer);
    }

    private void notifyObservers(String key, String value, String description) {
        for (SystemSettingObserver observer : observers) {
            observer.onSystemSettingChange(key, value, description);
        }
    }

    @Override
    public <T> T getUserSettingAsObject(String key, Long userId, Class<T> clazz, T defaultValue) {
        String json = getUserSetting(key, userId);
        T result = defaultValue;
        if (json != null) {
            result = serializeService.toObject(json, clazz, defaultValue);
        }
        return result;
    }

    @Override
    public void setUserSettingObject(String key, Object value, User user) {
        String json = serializeService.toJson(value);
        setUserSetting(key, json, user);
    }
}

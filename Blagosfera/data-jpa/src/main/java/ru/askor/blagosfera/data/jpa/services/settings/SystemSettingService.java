package ru.askor.blagosfera.data.jpa.services.settings;

import ru.askor.blagosfera.domain.settings.SystemSetting;
import ru.askor.blagosfera.domain.settings.SystemSettingsPage;

import java.util.List;

/**
 * Created by Maxim Nikitin on 10.03.2016.
 */
public interface SystemSettingService {

    /**
     * получить значение системной настройки по ключу
     *
     * @param key
     * @return
     */
    String getSystemSetting(String key);

    /**
     * записать системную настройку
     *
     * @param key
     * @param value
     * @param description
     * @return
     */
    String setSystemSetting(String key, String value, String description);

    /**
     * удалить системную настройку по id
     *
     * @param settingId
     */
    void deleteSystemSetting(Long settingId);

    /**
     * получить список настроек по ключам
     *
     * @param keys
     * @return
     */
    List<SystemSetting> getSystemSettings(List<String> keys);

    /**
     * получить страницу настроек
     *
     * @param page
     * @param size
     * @param keyFilter
     * @param descriptionFilter
     * @return
     */
    SystemSettingsPage getSystemSettings(int page, int size, String keyFilter, String descriptionFilter);
}

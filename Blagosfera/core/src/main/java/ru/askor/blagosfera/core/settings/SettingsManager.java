package ru.askor.blagosfera.core.settings;

import ru.askor.blagosfera.domain.settings.SystemSetting;
import ru.askor.blagosfera.domain.settings.SystemSettingsPage;
import ru.askor.blagosfera.domain.user.User;

import java.util.List;

/**
 * Created by Maxim Nikitin on 17.02.2016.
 */
public interface SettingsManager {

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

    /**
     * получить значение системной настройки или значение по-умолчанию
     *
     * @param key
     * @param defaultValue
     * @return
     */
    String getSystemSetting(String key, String defaultValue);

    /**
     * получить значение системной настройки в виде целого числа или значение по-умолчанию
     *
     * @param key
     * @param defaultValue
     * @return
     */
    int getSystemSettingAsInt(String key, int defaultValue);

    /**
     * получить значение системной настройки в виде массива целых чисел
     *
     * @param key
     * @return
     */
    List<Integer> getSystemSettingAsIntegers(String key);

    /**
     * получить значение системной настройки в виде булевой переменной
     *
     * @param key
     * @param defaultValue
     * @return
     */
    boolean getSystemSettingAsBool(String key, boolean defaultValue);

    /**
     * получить значние настройки в виде объекта из json строки
     *
     * @param key
     * @param defaultValue
     * @param <T>
     * @return
     */
    <T> T getSystemSettingsAsObject(String key, Class<T> clazz, T defaultValue);

    /**
     * получить значение пользовательской настройки
     *
     * @param key
     * @param userId
     * @return
     */
    String getUserSetting(String key, Long userId);

    /**
     *
     * @param key
     * @param userId
     * @return
     */
    boolean getUserSettingAsBoolean(String key, Long userId, boolean defaultValue);

    /**
     * сохранить пользовательскую настройку
     *
     * @param key
     * @param value
     * @param user
     * @return
     */
    String setUserSetting(String key, String value, User user);

    void deleteUserSetting(String key, Long userId);

    /**
     *
     * @param key
     * @param clazz
     * @param defaultValue
     * @param <T>
     * @return
     */
    <T> T getUserSettingAsObject(String key, Long userId, Class<T> clazz, T defaultValue);

    /**
     *
     * @param key
     * @param value
     * @param user
     */
    void setUserSettingObject(String key, Object value, User user);

    /**
     * зарегистрировать слушатель изменений настроек
     *
     * @param systemSettingObserver
     */
    void registerObserver(SystemSettingObserver systemSettingObserver);

    /**
     * удалить из списка слушатель изменений настроек
     *
     * @param systemSettingObserver
     */
    void unregisterObserver(SystemSettingObserver systemSettingObserver);
}

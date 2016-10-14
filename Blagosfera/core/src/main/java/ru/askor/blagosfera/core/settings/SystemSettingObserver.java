package ru.askor.blagosfera.core.settings;

/**
 * Created by Maxim Nikitin on 17.02.2016.
 */
public interface SystemSettingObserver {

    void onSystemSettingChange(String key, String val, String desc);
}

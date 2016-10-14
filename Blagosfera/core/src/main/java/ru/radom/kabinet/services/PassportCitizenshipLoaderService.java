package ru.radom.kabinet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.core.settings.SystemSettingObserver;

/**
 * Created by ebelyaev on 25.09.2015.
 */
@Service
public class PassportCitizenshipLoaderService implements ApplicationListener<ContextRefreshedEvent>, SystemSettingObserver {

    @Autowired
    private SettingsManager settingsManager;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (PassportCitizenshipSettings.getInstance() == null) {
            // Настройка страно-зависимых полей для гражданства
            PassportCitizenshipSettings.init(settingsManager.getSystemSetting(PassportCitizenshipSettings.SETTINGS_KEY));
        }
    }

    @Override
    public void onSystemSettingChange(String key, String val, String desc) {
        if (PassportCitizenshipSettings.SETTINGS_KEY.equals(key))
            PassportCitizenshipSettings.init(val);
    }
}

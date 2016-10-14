package ru.radom.kabinet.services.communities.organizationmember.behavior.po;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.core.settings.SystemSettingObserver;

/**
 *
 * Created by vgusev on 21.10.2015.
 */
@Service
public class POOrganizationMemberSettingsLoaderService implements ApplicationListener<ContextRefreshedEvent>, SystemSettingObserver {

    @Autowired
    private SettingsManager settingsManager;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (POOrganizationMemberSettings.getInstance() == null) {
            // Настройки документов пайщиков юр лиц в ПО
            POOrganizationMemberSettings.init(settingsManager.getSystemSetting(POOrganizationMemberSettings.SETTINGS_KEY));
        }
    }

    @Override
    public void onSystemSettingChange(String key, String val, String desc) {
        if (POOrganizationMemberSettings.SETTINGS_KEY.equals(key))
            POOrganizationMemberSettings.init(val);
    }
}
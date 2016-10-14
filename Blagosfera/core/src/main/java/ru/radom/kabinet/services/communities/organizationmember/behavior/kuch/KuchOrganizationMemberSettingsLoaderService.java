package ru.radom.kabinet.services.communities.organizationmember.behavior.kuch;

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
public class KuchOrganizationMemberSettingsLoaderService implements ApplicationListener<ContextRefreshedEvent>, SystemSettingObserver {

    @Autowired
    private SettingsManager settingsManager;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (KuchOrganizationMemberSettings.getInstance() == null) {
            // Настройки документов пайщиков юр лиц в КУч ПО
            KuchOrganizationMemberSettings.init(settingsManager.getSystemSetting(KuchOrganizationMemberSettings.SETTINGS_KEY));
        }
    }

    @Override
    public void onSystemSettingChange(String key, String val, String desc) {
        if (KuchOrganizationMemberSettings.SETTINGS_KEY.equals(key))
            KuchOrganizationMemberSettings.init(val);
    }
}
package ru.radom.kabinet.services.communities.sharermember.behavior.kuch;

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
public class KuchSharerCommunityMemberSettingsLoaderService implements ApplicationListener<ContextRefreshedEvent>, SystemSettingObserver {

    @Autowired
    private SettingsManager settingsManager;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (KuchSharerCommunityMemberSettings.getInstance() == null) {
            // Настройки документов пайщиков физ лиц в КУч ПО
            KuchSharerCommunityMemberSettings.init(settingsManager.getSystemSetting(KuchSharerCommunityMemberSettings.SETTINGS_KEY));
        }
    }

    @Override
    public void onSystemSettingChange(String key, String val, String desc) {
        if (KuchSharerCommunityMemberSettings.SETTINGS_KEY.equals(key))
            KuchSharerCommunityMemberSettings.init(val);
    }
}
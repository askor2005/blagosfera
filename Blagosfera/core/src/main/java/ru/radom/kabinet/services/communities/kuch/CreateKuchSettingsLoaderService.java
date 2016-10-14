package ru.radom.kabinet.services.communities.kuch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.core.settings.SystemSettingObserver;
import ru.radom.kabinet.model.communities.postappointbehavior.impl.settings.PlotBuhgalterPostSettings;
import ru.radom.kabinet.voting.settings.RegistrationInBatchVotingSettings;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Сервис загрузки настроек в бины для создяния КУч
 * Created by vgusev on 18.09.2015.
 */
@Service
public class CreateKuchSettingsLoaderService implements ApplicationListener<ContextRefreshedEvent>, SystemSettingObserver {

    @Autowired
    private SettingsManager settingsManager;

    public CreateKuchSettingsLoaderService() {
    }

    @PostConstruct
    public void init() {
        settingsManager.registerObserver(this);
    }

    @PreDestroy
    public void destroy() {
        settingsManager.unregisterObserver(this);
    }

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (CreateKuchFirstMeetingSettings.getInstance() == null) {
            // Настройки собрания создяния КУч 1го этапа
            CreateKuchFirstMeetingSettings.init(settingsManager.getSystemSetting(CreateKuchFirstMeetingSettings.SETTINGS_KEY));
            // Настройки собрания создяния КУч 2го этапа
            CreateKuchSecondMeetingSettings.init(settingsManager.getSystemSetting(CreateKuchSecondMeetingSettings.SETTINGS_KEY));
            // Общие настройки создяния КУч
            CommonCreateKuchSettings.init(settingsManager.getSystemSetting(CommonCreateKuchSettings.SETTINGS_KEY));
            // Настройки шаблонов документов принятия бухгалтера КУч ПО
            PlotBuhgalterPostSettings.init(settingsManager.getSystemSetting(PlotBuhgalterPostSettings.SETTINGS_KEY));
            // Настройки общих текстовок на странице регистрации в собрании
            RegistrationInBatchVotingSettings.init(settingsManager.getSystemSetting(RegistrationInBatchVotingSettings.SETTINGS_KEY));
        }
    }

    @Override
    public void onSystemSettingChange(String key, String val, String desc) {
        switch (key) {
            case CreateKuchFirstMeetingSettings.SETTINGS_KEY:
                CreateKuchFirstMeetingSettings.init(val);
                break;
            case CreateKuchSecondMeetingSettings.SETTINGS_KEY:
                CreateKuchSecondMeetingSettings.init(val);
                break;
            case CommonCreateKuchSettings.SETTINGS_KEY:
                CommonCreateKuchSettings.init(val);
                break;
            case PlotBuhgalterPostSettings.SETTINGS_KEY:
                PlotBuhgalterPostSettings.init(val);
                break;
            case RegistrationInBatchVotingSettings.SETTINGS_KEY:
                RegistrationInBatchVotingSettings.init(val);
                break;
        }
    }
}

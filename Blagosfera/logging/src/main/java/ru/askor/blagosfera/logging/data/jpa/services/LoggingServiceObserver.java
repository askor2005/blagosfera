package ru.askor.blagosfera.logging.data.jpa.services;

import ru.askor.blagosfera.logging.domain.LoggingSettings;

/**
 * Created by Maxim Nikitin on 14.03.2016.
 */
public interface LoggingServiceObserver {

    void onLoggingSettingsChanged(LoggingSettings loggingSettings);
}

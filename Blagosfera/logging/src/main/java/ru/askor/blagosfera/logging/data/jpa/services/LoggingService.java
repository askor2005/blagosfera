package ru.askor.blagosfera.logging.data.jpa.services;

import ru.askor.blagosfera.logging.domain.CashboxOperationLogItem;
import ru.askor.blagosfera.logging.domain.LoggingSettings;

/**
 * Created by Maxim Nikitin on 14.03.2016.
 */
public interface LoggingService {

    CashboxOperationLogItem saveCashboxOperationLogItem(CashboxOperationLogItem logItem);

    Long logExecutionStart(String threadName, String requestId, String sessionId, String username,
                           String targetClassName, String targetMethodName, Long parentId, String[] args);

    void logExecutionFinish(Long id, long duration, boolean exceptionThrown, String result);

    LoggingSettings getLoggingSettings();

    void setLoggingSettings(LoggingSettings loggingSettings);

    void notifyObservers(LoggingSettings loggingSettings);

    void registerObserver(LoggingServiceObserver loggingServiceListener);

    void unregisterObserver(LoggingServiceObserver loggingServiceListener);
}

package ru.askor.blagosfera.logging.data.jpa.services;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.logging.data.jpa.entities.*;
import ru.askor.blagosfera.logging.data.jpa.repositories.*;
import ru.askor.blagosfera.logging.domain.CashboxOperationLogItem;
import ru.askor.blagosfera.logging.domain.LoggingSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxim Nikitin on 14.03.2016.
 */
@Transactional
@Service("cashboxLogService")
public class LoggingServiceImpl implements LoggingService {

    private static final Long SETTINGS_ID = 1L;

    @Autowired
    private CashboxOperationLogItemRepository cashboxOperationLogRepository;

    @Autowired
    private ExecutionFlowRepository executionFlowRepository;

    @Autowired
    private ExecutionFlowArgRepository executionFlowArgRepository;

    @Autowired
    private ExecutionFlowResultRepository executionFlowResultRepository;

    @Autowired
    private LoggingSettingsRepository loggingSettingsRepository;

    private List<LoggingServiceObserver> observers = new ArrayList<>();

    public LoggingServiceImpl() {
    }

    @Override
    public CashboxOperationLogItem saveCashboxOperationLogItem(CashboxOperationLogItem logItem) {
        return cashboxOperationLogRepository.save(new CashboxOperationLogItemEntity(logItem)).toDomain();
    }

    @Override
    public Long logExecutionStart(String threadName, String requestId, String sessionId, String username,
                                  String targetClassName, String targetMethodName, Long parentId, String[] args) {
        ExecutionFlowEntity executionFlowEntity = new ExecutionFlowEntity();
        executionFlowEntity.setDate(LocalDateTime.now());
        executionFlowEntity.setThreadName(threadName);
        executionFlowEntity.setRequestId(requestId);
        executionFlowEntity.setSessionId(sessionId);
        executionFlowEntity.setUsername(username);
        executionFlowEntity.setTargetClassName(targetClassName);
        executionFlowEntity.setTargetMethodName(targetMethodName);
        executionFlowEntity.setParentId(parentId);

        for (String arg : args) {
            executionFlowEntity.getArgs().add(new ExecutionFlowArgEntity(arg).setExecutionFlow(executionFlowEntity));
        }

        return executionFlowRepository.save(executionFlowEntity).getId();
    }

    @Override
    public void logExecutionFinish(Long id, long duration, boolean exceptionThrown, String result) {
        ExecutionFlowEntity executionFlowEntity = executionFlowRepository.findOne(id);
        executionFlowEntity.setDuration(duration);
        executionFlowEntity.setExceptionThrown(exceptionThrown);
        executionFlowEntity.setResult(result == null ? null : new ExecutionFlowResultEntity(result).setExecutionFlow(executionFlowEntity));
        executionFlowRepository.save(executionFlowEntity);
    }

    @Override
    public LoggingSettings getLoggingSettings() {
        LoggingSettingsEntity loggingSettingsEntity = loggingSettingsRepository.findOne(SETTINGS_ID);

        if (loggingSettingsEntity == null) {
            loggingSettingsEntity = new LoggingSettingsEntity();
            loggingSettingsEntity.setId(SETTINGS_ID);
            loggingSettingsEntity = loggingSettingsRepository.save(loggingSettingsEntity);
        }

        return loggingSettingsEntity.toDomain();
    }

    @Override
    public void setLoggingSettings(LoggingSettings loggingSettings) {
        LoggingSettingsEntity loggingSettingsEntity = loggingSettingsRepository.findOne(SETTINGS_ID);

        if (loggingSettingsEntity == null) {
            loggingSettingsEntity = new LoggingSettingsEntity();
            loggingSettingsEntity.setId(SETTINGS_ID);
        }

        loggingSettingsEntity.setAuditLevel(loggingSettings.getAuditLevel());
        loggingSettingsEntity.getWhitelist().clear();
        loggingSettingsEntity.getWhitelist().addAll(loggingSettings.getWhitelist());
        loggingSettingsEntity.getBlacklist().clear();
        loggingSettingsEntity.getBlacklist().addAll(loggingSettings.getBlacklist());
        loggingSettingsRepository.save(loggingSettingsEntity);

        notifyObservers(loggingSettings);
    }

    @Override
    public void notifyObservers(LoggingSettings loggingSettings) {
        for (LoggingServiceObserver observer : observers) {
            observer.onLoggingSettingsChanged(loggingSettings);
        }
    }

    @Override
    public void registerObserver(LoggingServiceObserver observer) {
        if (!observers.contains(observer)) observers.add(observer);
    }

    @Override
    public void unregisterObserver(LoggingServiceObserver observer) {
        if (observers.contains(observer)) observers.remove(observer);
    }
}

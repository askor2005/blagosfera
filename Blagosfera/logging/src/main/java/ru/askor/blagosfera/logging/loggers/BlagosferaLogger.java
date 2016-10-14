package ru.askor.blagosfera.logging.loggers;

import com.google.gson.Gson;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.logging.data.jpa.services.LoggingService;
import ru.askor.blagosfera.logging.data.jpa.services.LoggingServiceObserver;
import ru.askor.blagosfera.logging.domain.AuditLevel;
import ru.askor.blagosfera.logging.domain.LoggingConstants;
import ru.askor.blagosfera.logging.domain.LoggingSettings;

import javax.annotation.PostConstruct;
import java.util.ArrayDeque;
import java.util.Deque;

@Transactional
@Component("blagosferaLogger")
public class BlagosferaLogger implements LoggingConstants, LoggingServiceObserver {

    private final Logger LOGGER = LoggerFactory.getLogger("blagosfera");

    @Autowired
    private LoggingService loggingService;

    private final static Gson gson = new Gson();

    private ThreadLocal<Deque<Long>> executionChainHolder = new ThreadLocal<>();

    private LoggingSettings settings;

    public BlagosferaLogger() {
    }

    @PostConstruct
    private void init() {
        settings = loggingService.getLoggingSettings();
        loggingService.registerObserver(this);
    }

    public Object logExecutionFlow(ProceedingJoinPoint joinPoint) throws Throwable {
        long time = 0L;
        Deque<Long> executionChain = new ArrayDeque<>();
        String targetClassName;
        String targetMethodName = joinPoint.getSignature().getName();
        Object target = joinPoint.getTarget();

        if (AopUtils.isAopProxy(target)) {
            targetClassName = AopProxyUtils.proxiedUserInterfaces(target)[0].getName();
        } else {
            targetClassName = target.getClass().getName();
        }

        boolean loggingEnabled = settings.getAuditLevel() != AuditLevel.OFF;
        boolean logPayload = settings.getAuditLevel() == AuditLevel.DEVELOPMENT;

        if (loggingEnabled) {
            if (settings.getWhitelist().size() > 0) {
                loggingEnabled = false;

                for (String item : settings.getWhitelist()) {
                    if (targetClassName.contains(item)) loggingEnabled = true;
                }
            }
        }

        if (loggingEnabled) {
            if (settings.getBlacklist().size() > 0) {
                for (String item : settings.getBlacklist()) {
                    if (targetClassName.contains(item)) loggingEnabled = false;
                }
            }
        }

        if (loggingEnabled) {
            String threadName = Thread.currentThread().getName();
            String[] args = new String[joinPoint.getArgs().length];

            if (logPayload) {
                for (int i = 0; i < joinPoint.getArgs().length; i++) {
                    args[i] = serializeObject(joinPoint.getArgs()[i]);
                }
            }

            if (executionChainHolder.get() == null) {
                executionChainHolder.set(executionChain);
            }

            executionChain = executionChainHolder.get();
            Long parentId = executionChain.peek();

            logExecutionStart(targetClassName, targetMethodName, args);
            Long executionId = loggingService.logExecutionStart(threadName, MDC.get(REQUEST_ID), MDC.get(SESSION_ID), MDC.get(USERNAME),
                    targetClassName, targetMethodName, parentId, args);

            executionChain.push(executionId);
            time = System.currentTimeMillis();
        }

        try {
            Object result = joinPoint.proceed();

            if (loggingEnabled) {
                time = System.currentTimeMillis() - time;
                loggingService.logExecutionFinish(executionChain.peek(), time, false, logPayload ? serializeObject(result) : null);
            }

            return result;
        } catch (Throwable e) {
            e.printStackTrace();
            if (loggingEnabled) {
                time = System.currentTimeMillis() - time;
                String errorValue = serializeException(e);
                LOGGER.error(errorValue);
                loggingService.logExecutionFinish(executionChain.peek(), time, true, errorValue);
            }

            throw e;
        } finally {
            if (loggingEnabled) {
                logExecutionFinish(targetClassName, targetMethodName, time);
                executionChain.pop();
            }
        }
    }

    private void logExecutionStart(String targetClassName, String targetMethodName, String[] args) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append(targetClassName).append(".").append(targetMethodName).append("(");

        for (Object arg : args) {
            logMessage.append(arg).append(",");
        }

        if (args.length > 0) {
            logMessage.deleteCharAt(logMessage.length() - 1);
        }

        logMessage.append(")");
        LOGGER.debug(logMessage.toString());
    }

    private void logExecutionFinish(String targetClassName, String targetMethodName, long time) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.setLength(0);
        logMessage.append(targetClassName).append(".").append(targetMethodName)
                .append(" Execution time -- ").append(time).append("ms.");
        LOGGER.debug(logMessage.toString());
    }

    private static String serializeObject(Object o) {
        if (o == null) return null;
        String result = o.toString();

        try {
            result = gson.toJson(o);
        } catch (Throwable e) {
            result += "[serialization failed]";
        }

        return result;
    }

    private static String serializeException(Throwable e) {
        StringBuilder logMessage = new StringBuilder();
        boolean first = true;

        for (Throwable item : ExceptionUtils.getThrowableList(e)) {
            if (first) {
                logMessage.append("An exception occurred: ");
                first = false;
            } else {
                logMessage.append("\nCaused by: ");
            }

            logMessage.append(ExceptionUtils.getStackTrace(item));
        }

        return logMessage.toString();
    }

    @Override
    public void onLoggingSettingsChanged(LoggingSettings loggingSettings) {
        synchronized (settings) {
            settings = loggingSettings;
        }
    }
}

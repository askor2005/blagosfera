package ru.askor.blagosfera.logging.web;

import org.slf4j.MDC;
import ru.askor.blagosfera.logging.domain.LoggingConstants;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class LoggingContextListener implements HttpSessionListener, LoggingConstants {

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        MDC.put(SESSION_ID, httpSessionEvent.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
    }
}

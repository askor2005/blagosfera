package ru.askor.blagosfera.logging.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import ru.askor.blagosfera.logging.domain.LoggingConstants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

public class LoggingContextFilter implements Filter, LoggingConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingContextFilter.class);

    @Override
    public void init(FilterConfig fc) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        MDC.put(REQUEST_ID, UUID.randomUUID().toString().replaceAll("-", ""));

        StringBuilder msg = new StringBuilder();
        if (req instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) req;
            HttpSession session = httpRequest.getSession(false);

            if (session != null) MDC.put(SESSION_ID, session.getId());

            String username = null;

            try {
                username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            } catch (Throwable ignored) {
            }

            if (username != null) MDC.put(USERNAME, username);

            msg.append("\n\tStarting ");
            msg.append(httpRequest.getMethod());
            msg.append(" request for URL '");
            msg.append(httpRequest.getRequestURL());
            if (httpRequest.getMethod().equalsIgnoreCase("get") && httpRequest.getQueryString() != null) {
                msg.append('?');
                msg.append(httpRequest.getQueryString());
            }
            msg.append("'.");
        } else {
            msg.append("\n\tStarting new request for Server '");
            msg.append(req.getScheme());
            msg.append(":\\");
            msg.append(req.getServerName());
            msg.append(':');
            msg.append(req.getServerPort());
            msg.append('/');
        }

        LOGGER.debug(msg.toString());

        long startTime = System.currentTimeMillis();

        chain.doFilter(req, resp);

        msg.setLength(0);
        msg.append("\n\tRequest processing complete. Time Elapsed -- ");
        msg.append(System.currentTimeMillis() - startTime);
        msg.append("ms.");

        LOGGER.debug(msg.toString());

        MDC.remove(SESSION_ID);
        MDC.remove(REQUEST_ID);
        MDC.remove(USERNAME);
    }

    @Override
    public void destroy() {
    }
}

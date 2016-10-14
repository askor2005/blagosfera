package ru.radom.kabinet.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import ru.radom.kabinet.dao.notifications.NotificationDao;
import ru.radom.kabinet.model.notifications.NotificationEntity;
import ru.radom.kabinet.security.SecurityUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик оповещений, которые уже состоялись
 * Created by vgusev on 09.09.2015.
 */
public class NotificationInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NotificationInterceptor.class);

    @Autowired
    private NotificationDao notificationDao;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = URLDecoder.decode(URLDecoder.decode(request.getRequestURI(), "UTF-8"), "UTF-8");
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.equals("")) {
            uri = uri + "?" + queryString;
        }

        // Ищем оповещения по урлу
        List<NotificationEntity> notifications = SecurityUtils.getUserDetails() != null ? notificationDao.getList(SecurityUtils.getUser().getId(), uri, false) : new ArrayList<>();
        if (notifications != null && notifications.size() > 0) { // Если оповещения найдены, то помечаем их как прочитанное
            for (NotificationEntity notification : notifications) {
                notification.setRead(true);
                notificationDao.saveOrUpdate(notification);
                logger.info("notification " + notification.getId() + " market as readed");
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }
}

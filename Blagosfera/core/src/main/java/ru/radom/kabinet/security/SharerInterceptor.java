package ru.radom.kabinet.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import ru.askor.blagosfera.domain.user.SharerStatus;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.services.NotificationService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

public class SharerInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private SharerDao sharerDao;

	@Autowired
	private NotificationService notificationService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String uri = URLDecoder.decode(URLDecoder.decode(request.getRequestURI(), "UTF-8"), "UTF-8");

		if (SecurityUtils.getUserDetails() != null) {
            UserEntity userEntity = sharerDao.getById(SecurityUtils.getUser().getId());

            if (userEntity.isArchived() && (!uri.endsWith(".json")) && (!uri.startsWith("/stomp_endpoint"))) {
                if (!uri.equals(userEntity.getLink())) {
                    response.sendRedirect(userEntity.getLink());
                    return false;
                }
            }

            // TODO Переделать
            /*if ((notificationService.hasUnreadBlockingNotifications()) && (!uri.equals("/notify")) && (!uri.endsWith(".json"))) {
                response.sendRedirect("/notify?blocking");
                return false;
            }*/

            // Если пользователю необходимо поменять пароль, то входим в этот блок
            if (SharerStatus.NEED_CHANGE_PASSWORD.equals(userEntity.getStatus())) {

                //Разрешаются запросы только с целью изменения пароля, либо для получения страницы с инструкцией
                if (!"/sharer/change_password.json".equals(request.getRequestURI())
                        && !request.getRequestURI().equals(userEntity.getChRootUrl())) {

                    //перенаправляем на единственную доступную страницу
                    response.sendRedirect(userEntity.getChRootUrl());
                    return false;
                }
            }
		}

		return super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);

        String uri = URLDecoder.decode(URLDecoder.decode(request.getRequestURI(), "UTF-8"), "UTF-8");

        // TODO Нужно ли?
        // TODO Переделать

        if ((!uri.endsWith(".json"))
                && (!uri.startsWith("/stomp_endpoint"))
                && (modelAndView != null)
                && (modelAndView.getModel() != null)) {
            modelAndView.getModel().put("sharer", SecurityUtils.getUser());
        }

		/*
		if ((modelAndView != null) && (modelAndView.getViewName() != null) && (!modelAndView.getViewName().startsWith("redirect:"))) {
			Sharer sharer = radomRequestContext.getCurrentSharer();
			if (sharer != null) {
                modelAndView.getModel().putAll(sharerModelService.fillMapForStandardModel(sharer, request));
			}
		}*/
	}
}

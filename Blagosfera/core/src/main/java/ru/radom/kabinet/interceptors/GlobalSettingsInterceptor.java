package ru.radom.kabinet.interceptors;

/**
 * Created by vtarasenko on 26.07.2016.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.dao.web.SectionDao;
import ru.radom.kabinet.security.context.RequestContext;
import ru.radom.kabinet.services.section.SectionService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class GlobalSettingsInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private ru.askor.blagosfera.data.jpa.services.settings.SystemSettingService systemSettingsService;

    @Autowired
    private SectionDao sectionDao;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private RequestContext radomRequestContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView == null) {
            return;
        }
        Map<String, Object> model = modelAndView.getModel();

        if (model == null) {
            return;
        }
        model.put("jivositeKey", systemSettingsService.getSystemSetting("jivositeKey"));
    }
}


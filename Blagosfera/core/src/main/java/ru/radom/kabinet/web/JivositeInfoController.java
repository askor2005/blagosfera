package ru.radom.kabinet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.askor.blagosfera.core.services.jivosite.JivositeService;
import ru.askor.blagosfera.domain.jivosite.JivositeInfo;
import ru.askor.blagosfera.domain.jivosite.JivositeUserInfo;
import ru.radom.kabinet.security.SecurityUtils;

/**
 * Created by vtarasenko on 25.07.2016.
 */
@Controller
@RequestMapping("/jivosite")
public class JivositeInfoController {
    @Autowired
    private JivositeService jivositeService;
    @RequestMapping(value = "/info.json",method = RequestMethod.GET)
    public @ResponseBody JivositeInfo getInfo() {
        JivositeInfo jivositeInfo = new JivositeInfo();
        if (SecurityUtils.getUser() != null) {
            jivositeInfo.setUserInfo(jivositeService.getUserInfo(SecurityUtils.getUser().getId()));
            jivositeInfo.setUserToken(jivositeService.getUserToken(SecurityUtils.getUser().getId()));
            jivositeInfo.setAuthorized(true);
        }
        else {
            jivositeInfo.setAuthorized(false);
        }
        return jivositeInfo;
    }
}

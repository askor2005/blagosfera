package ru.radom.kabinet.web.ras;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.askor.blagosfera.core.settings.SettingsManager;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Maxim Nikitin on 08.02.2016.
 */
@Controller
@RequestMapping("/ras")
public class RASController {

    public static final String RAS_VERSION_MIN_KEY = "ras.version.min";
    public static final String RAS_VERSION_CURRENT_KEY = "ras.version.current";

    public static final String RAS_VERSION_MIN_DESC = "Минимальная совместимая версия RAS";
    public static final String RAS_VERSION_CURRENT_DESC = "Текущая версия RAS";

    public static final String RAS_DOWNLOAD_URL_KEY = "ras.download.url";

    @Autowired
    private SettingsManager systemSettingManager;

    public RASController() {
    }

    @RequestMapping(value = "version/min", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String minVersion() {
        String version = systemSettingManager.getSystemSetting(RAS_VERSION_MIN_KEY);

        if (version == null) {
            version = "0.0.0";
            systemSettingManager.setSystemSetting(RAS_VERSION_MIN_KEY, version, RAS_VERSION_MIN_DESC);
        }

        return version;
    }

    @RequestMapping(value = "version/current", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String currentVersion() {
        String version = systemSettingManager.getSystemSetting(RAS_VERSION_CURRENT_KEY);

        if (version == null) {
            version = "0.0.0";
            systemSettingManager.setSystemSetting(RAS_VERSION_CURRENT_KEY, version, RAS_VERSION_CURRENT_DESC);
        }

        return version;
    }

    @RequestMapping(value = "download", method = RequestMethod.GET)
    public void download(HttpServletResponse response) {
        response.setHeader("Location", systemSettingManager.getSystemSetting(RAS_DOWNLOAD_URL_KEY));
        response.setStatus(302);
    }
}

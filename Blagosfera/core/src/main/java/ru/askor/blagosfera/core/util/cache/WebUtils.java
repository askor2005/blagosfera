package ru.askor.blagosfera.core.util.cache;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Maxim Nikitin on 06.04.2016.
 */
public class WebUtils {

    private WebUtils() {
    }

    public static String getRemoteIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

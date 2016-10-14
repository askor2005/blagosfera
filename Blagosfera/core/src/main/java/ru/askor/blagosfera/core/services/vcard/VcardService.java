package ru.askor.blagosfera.core.services.vcard;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by vtarasenko on 14.06.2016.
 */
public interface VcardService {

    void  sendToHttpResponse(Long currentUserId, Long userId,HttpServletResponse response,String charset);

    void sendToEmail(Long currentUserId, Long userId);
}

package ru.askor.blagosfera.core.services.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.core.util.NumberUtils;
import ru.askor.blagosfera.crypt.domain.HttpException;
import ru.askor.blagosfera.crypt.tls.HTTP;
import ru.askor.blagosfera.crypt.tls.Response;
import ru.askor.blagosfera.domain.notification.sms.SmsNotification;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by mnikitin on 15.06.2016.
 */
@Transactional
@Service("smsService")
public class SmsServiceImpl implements SmsService {

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private ObjectMapper objectMapper;

    public SmsServiceImpl() {
    }

    @Async
    @Override
    public String send(SmsNotification notification) throws JsonProcessingException, NoSuchAlgorithmException, UnsupportedEncodingException, HttpException {
        String smsEnabled = settingsManager.getSystemSetting("sms_service.enabled");

        if ((smsEnabled == null) || !"true".equalsIgnoreCase(smsEnabled)) return "DISABLED";

        String url = settingsManager.getSystemSetting("sms_service.url");

        if ((url == null) || url.isEmpty()) throw new IllegalArgumentException("sms_service.wrong_url");

        String apiKey = settingsManager.getSystemSetting("sms_service.api_key");

        if ((apiKey == null) || apiKey.isEmpty()) throw new IllegalArgumentException("sms_service.wrong_api_key");

        String allParams = notification.getDataAsString() + apiKey;
        String md5 = NumberUtils.bytesToHex(MessageDigest.getInstance("MD5").digest(allParams.getBytes("UTF-8")));
        notification.setChecksum(md5);
        String json = objectMapper.writeValueAsString(notification);
        Response response = new HTTP().doPost(url, json, ContentType.APPLICATION_JSON, null);
        return response.getDataAsString();
    }
}

package ru.askor.blagosfera.core.services.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.askor.blagosfera.crypt.domain.HttpException;
import ru.askor.blagosfera.domain.notification.sms.SmsNotification;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by mnikitin on 15.06.2016.
 */
public interface SmsService {

    String send(SmsNotification notification) throws JsonProcessingException, NoSuchAlgorithmException, UnsupportedEncodingException, HttpException;
}

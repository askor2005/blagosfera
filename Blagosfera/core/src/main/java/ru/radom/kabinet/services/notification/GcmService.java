package ru.radom.kabinet.services.notification;

import org.json.JSONObject;
import ru.askor.blagosfera.domain.notification.Notification;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.model.chat.ChatMessage;

import java.util.List;

/**
 * Интерфейс сервиса для реализации логики работы с gcm.
 */
public interface GcmService {

    /**
     * Позволяет привязать к sharer'у очередное устройство,
     * на которое будет осуществляться рассылка push уведомлений
     * @param userId получатель
     * @param deviceId идентификатор зарегистрированного устройства получателя
     */
    void registerDeviceId(Long userId, String deviceId);

    /**
     * Позволяет отправить запрос к GCM с просьбой выслать уведомление
     * на устройства пользователя и зарегистрировать на каждый такой запрос
     * статус push уведомления (GcmPushNotificationStatus), связанный с уведомлением системы
     *
     * На момент написания метода GCM не поддерживает полезную нагрузку.
     * Поэтому все, что мы можем - сообщить сервису о том, что мы бы хотели уведомить устройство.
     * @param userEntity получатель уведомления
     * @param notification исходное уведомление
     */
    void sendNotificationToSharer(Notification notification, User userEntity);

    /**
     * Позволяет отправить запрос к GCM с просьбой выслать уведомление
     * на устройства пользователей и зарегистрировать на каждый такой запрос
     * статус push уведомления (GcmPushNotificationStatus), связанный с уведомлением системы
     *
     * На момент написания метода GCM не поддерживает полезную нагрузку.
     * Поэтому все, что мы можем - сообщить сервису о том, что мы бы хотели уведомить устройство.
     * @param users получатели уведомления
     * @param notification исходное уведомление
     */
    void sendNotificationToSharers(Notification notification, List<User> users);

    /**
     * Позволяет отправить запрос к GCM с просьбой выслать уведомление
     * на устройства пользователя и зарегистрировать на каждый такой запрос
     * статус push уведомления (GcmPushNotificationStatus), связанный с сообщением чата.
     *
     * На момент написания метода GCM не поддерживает полезную нагрузку.
     * Поэтому все, что мы можем - сообщить сервису о том, что мы бы хотели уведомить устройство.
     * @param chatMessage исходное сообщение чата
     * @param user получатель уведомления
     */
    void sendChatNotificationToSharer(ChatMessage chatMessage, User user);

    /**
     * Позволяет отправить запрос к GCM с просьбой выслать уведомление
     * на устройства пользователей и зарегистрировать на каждый такой запрос
     * статус push уведомления (GcmPushNotificationStatus), связанный с сообщением чата.
     *
     * На момент написания метода GCM не поддерживает полезную нагрузку.
     * Поэтому все, что мы можем - сообщить сервису о том, что мы бы хотели уведомить устройство.
     * @param chatMessage исходное сообщение чата
     * @param users получатели уведомления
     */
    void sendChatNotificationToSharers(ChatMessage chatMessage, List<User> users);

    /**
     * Позволяет подготовить последнее непоказанное push уведомление в формате JSON.
     * После отработки метода, push уведомление считается показанным на указанном устройстве.
     * @param deviceId идентификатор зарегистрированного устройства
     * @return Json объект, содержащий необходимую информацию для показа уведомления и взаимодействия с ним.
     */
    JSONObject getLastNotShowedNotificationAsJsonByDeviceId(String deviceId);
}

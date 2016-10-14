package ru.radom.kabinet.services.notification;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.data.jpa.repositories.notifications.GcmDeviceRepository;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.notification.PushToDevicesCallbackEvent;
import ru.askor.blagosfera.domain.notification.Notification;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.notifications.GcmPushNotificationStatusDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.chat.ChatMessage;
import ru.radom.kabinet.model.notifications.extra.GcmDevice;
import ru.radom.kabinet.model.notifications.extra.GcmPushNotificationStatus;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

@Service("GcmService")
public class GcmServiceImpl implements GcmService, PushToDevicesCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(GcmServiceImpl.class);

    private final String keyForGcmApiKey = "gcm.apiKey";
    private final String gcmApiKeyNotExistMessage = "Необходима системная переменная gcm.apiKey";

    @Autowired
    private GcmDeviceRepository gcmDeviceRepository;

    @Autowired
    private GcmPushNotificationStatusDao gcmPushNotificationStatusDao;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    @Autowired
    private SharerDao sharerDao;

    @Override
    public void registerDeviceId(Long userId, String deviceId) {
        UserEntity userEntity = sharerDao.getById(userId);
        //Проверяем, есть ли уже в базе такой идентификатор
        GcmDevice gcmDevice = gcmDeviceRepository.findOneByDeviceId(deviceId);

        //Если нет, то регистрируем новый
        if (gcmDevice == null) {
            gcmDevice = new GcmDevice();
            gcmDevice.setUser(userEntity);
            gcmDevice.setDeviceId(deviceId);
            gcmDeviceRepository.save(gcmDevice);
        } else {
            //Если есть, то проверяем попытку регистрации чужого устройства
            if (!gcmDevice.getUser().equals(userEntity)) {
                //Перерегистрируем устройство на другого пользователя
                gcmDevice.setUser(userEntity);
                gcmDeviceRepository.save(gcmDevice);
            }

            //Если устройство уже принадлежит текущему пользователю, то ничего не делаем
        }
    }

    @Override
    @Transactional
    public void sendNotificationToSharer(Notification notification, User user) {
        String apiKey = settingsManager.getSystemSetting(keyForGcmApiKey);

        if (apiKey == null || apiKey.isEmpty()) {
            LOGGER.info(gcmApiKeyNotExistMessage);
            return;
        }

        //Получаем список идентификаторов устройсв
        List<String> deviceIds = getDeviceIdsBySharer(user.getId());

        //Не стоит спамить запросы с пустым телом
        if (deviceIds.isEmpty()) {
            return;
        }
        //Обнуляем старые уведомления, связанные с чатом
        gcmPushNotificationStatusDao.setIsPushedAllStatusesBySharerAndNotification(user.getId(), notification.getSubject());

        //Генерируем статусы уведомления для получателя
        List<GcmPushNotificationStatus> statuses = generateStatusesFromNotification(user.getId(), notification);

        blagosferaEventPublisher.publishEvent(new PushToDevicesCallbackEvent(this, deviceIds, apiKey, statuses, this));
    }

    @Override
    public void sendNotificationToSharers(Notification notification, List<User> users) {
        String apiKey = settingsManager.getSystemSetting(keyForGcmApiKey);

        if (apiKey == null || apiKey.isEmpty()) {
            LOGGER.info(gcmApiKeyNotExistMessage);
            return;
        }

        for (User user : users) {
            sendNotificationToSharer(notification, user);
        }
    }


    @Override
    @Transactional
    public void sendChatNotificationToSharer(ChatMessage chatMessage, User user) {
        String apiKey = settingsManager.getSystemSetting(keyForGcmApiKey);

        if (apiKey == null || apiKey.isEmpty()) {
            LOGGER.info(gcmApiKeyNotExistMessage);
            return;
        }

        //Получаем список идентификаторов устройсв
        List<String> deviceIds = getDeviceIdsBySharer(user.getId());

        //Не стоит спамить запросы с пустым телом
        if (deviceIds.isEmpty()) {
            return;
        }
        //Обнуляем старые уведомления, связанные с чатом
        gcmPushNotificationStatusDao.setIsPushedForAllChatStatusesBySharer(user.getId());

        //Генерируем статусы уведомления для получателя
        List<GcmPushNotificationStatus> statuses = generateStatusesFromChatMessage(user.getId(), chatMessage);

        blagosferaEventPublisher.publishEvent(new PushToDevicesCallbackEvent(this, deviceIds, apiKey, statuses, this));
    }

    @Override
    public void sendChatNotificationToSharers(ChatMessage chatMessage, List<User> users) {

        String apiKey = settingsManager.getSystemSetting(keyForGcmApiKey);

        if (apiKey == null || apiKey.isEmpty()) {
            LOGGER.info(gcmApiKeyNotExistMessage);
            return;
        }

        for (User user : users) {
            sendChatNotificationToSharer(chatMessage, user);
        }
    }

    @Override
    @Transactional
    public JSONObject getLastNotShowedNotificationAsJsonByDeviceId(String deviceId) {
        /**
         * Чтобы избежать возможности множественной обработки одного и того же статуса
         * используем идентификатор устройства в качестве семафора
         */
        synchronized (deviceId.intern()) {
            //Ищем устройство
            GcmDevice gcmDevice = gcmDeviceRepository.findOneByDeviceId(deviceId);

            if (gcmDevice == null) {
                return JsonUtils.getErrorJson();
            }

            //Ищем последнее непрочитанное и непоказанное push уведомление
            GcmPushNotificationStatus gcmPushNotificationStatus = gcmPushNotificationStatusDao
                    .getLastUnreadAndNotShowed(gcmDevice.getUser(), deviceId);

            if (gcmPushNotificationStatus == null) {
                return JsonUtils.getJson("result", "no_content");
            }

            //Отмеаем его как показанное и сохраняем
            gcmPushNotificationStatus.setIsPushed(true);
            gcmPushNotificationStatusDao.update(gcmPushNotificationStatus);

            return prepareGcmNotificationStatusAsJson(gcmPushNotificationStatus);
        }
    }

    /**
     * Позволяет подготовить Json объект на основании статуса push уведомления
     * @param gcmPushNotificationStatus статус push уведомления
     * @return Json объект, содержащий необходимую информацию для показа уведомления и взаимодействия с ним
     */
    private JSONObject prepareGcmNotificationStatusAsJson(GcmPushNotificationStatus gcmPushNotificationStatus) {
        // TODO Переделать
        /*JSONObject result = JsonUtils.getSuccessJson();

        Notification notification = gcmPushNotificationStatus.getNotification();
        ChatMessage chatMessage = gcmPushNotificationStatus.getChatMessage();

        if (notification != null) {
            //Исходный объект статуса - уведомление системы
            result.put("title", notification.getSubject());
            result.put("body", notification.getShortText());
            result.put("icon", ((User) notification.getSender()).getAvatar());
            result.put("tag", notification.getSubject());
            result.put("notificationId", notification.getId());

            //Вычиляем ссылку. По умолчанию она будет вести на страницу уведомлений
            String defaultLink = "/notify";
            String link = defaultLink;

            for (NotificationLinkEntity notificationLink : notification.getLinks()) {
                //При необходимости выполнить какое-либо действие вставляем ссылку на страницу уведомлений
                if (notificationLink.isAjax()) {
                    link = defaultLink;
                    break;
                }

                if (notificationLink.getUrl() != null & !notificationLink.getUrl().isEmpty()) {
                    link = notificationLink.getUrl();
                }
            }

            result.put("link", link);
        } else if (chatMessage != null) {
            //Исходный объект статуса - сообщение в чат
            result.put("title", chatMessage.getSender().getFullName());
            result.put("body", chatMessage.getText());
            result.put("icon", chatMessage.getSender().getAvatar());
            result.put("tag", "chat");
            result.put("sharerId", gcmPushNotificationStatus.getSharer().getId());
            result.put("chatMessageId", chatMessage.getId());
            result.put("link", "/chat#" + chatMessage.getDialog().getId());
        } else {
            //Если мы здесь - значит данные обрабатываемой сущности не консистентны
            throw new RuntimeException("The GcmPushNotificationStatus with id="
                    + gcmPushNotificationStatus.getId() + " is not consistent.");
        }

        return result;*/
        return null;
    }

    /**
     * Позволяет отправить пустые push уведомления на список устройств
     * @param deviceIds список устройств-получателей
     * @param apiKey api ключ для взаимодействия с GCM
     */
    @Override
    public void pushToDevices(List<String> deviceIds, String apiKey, List<GcmPushNotificationStatus> statuses) {
        try {
            HttpPost httpPost = prepareHttpPost(apiKey, deviceIds);
            executeHttpPost(httpPost);
        } catch (Exception e) {
            //В случае ошибки удаляем статусы push уведомлений
            for (GcmPushNotificationStatus status : statuses) {
                gcmPushNotificationStatusDao.delete(status);
            }

            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * Подготавливает тело запроса к gcm
     * @param apiKey api ключ для взаимодействия с gcm
     * @param deviceIds список идентификаторов устройств, на которые отправляются уведомления
     * @return
     */
    private HttpPost prepareHttpPost(String apiKey, List<String> deviceIds) throws Exception{
            HttpPost httpPost = new HttpPost("https://android.googleapis.com/gcm/send");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "key=" + apiKey);

            JSONObject body = new JSONObject();
            body.put("registration_ids", deviceIds);

            httpPost.setEntity(new StringEntity(body.toString()));
            return httpPost;
    }

    /**
     * Отправляет запрос к GCM и в случае отсутствия успеха делает запись в лог
     * @param httpPost post запрос к GCM
     * @return true - код ответа 200, false - иначе
     */
    private void executeHttpPost(HttpPost httpPost) throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(httpPost);

        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != 200) {

            String message = null;

            if (statusCode == 400) {
                //Наш запрос составлен неправильно
                message = "Request for GCM could not be parsed as JSON with body "
                        + EntityUtils.toString(httpPost.getEntity());
            } else if (statusCode == 401) {
                //Мы используем неверный API KEY
                message = "There was an error authenticating the sender account.";
            } else if (statusCode >= 500 && statusCode <= 599) {
                //GCM сервер недоступен
                message = "GCM server is not available.";
            }

            if (message == null) {
                message = "Unknown status code of response from GCM. Status code: " + statusCode;
            }

            throw new RuntimeException(message);
        }
    }

    /**
     * Позволяет сохранить в бд список статусов push уведомлений, связанных с уведомлением и его получателем.
     * @param userId получатель уведомления
     * @param notification уведомление
     * @return список сохраненных в БД объектов класса GcmPushNotificationStatus
     */
    private List<GcmPushNotificationStatus> generateStatusesFromNotification(
            Long userId, Notification notification) {
        // TODO Переделать
        /*List<GcmPushNotificationStatus> result = new ArrayList<>();

        List<GcmDevice> devices = gcmDeviceRepository.findAllByUserId(userId);

        for (GcmDevice device : devices) {
            GcmPushNotificationStatus gcmPushNotificationStatus = new GcmPushNotificationStatus();
            gcmPushNotificationStatus.setSharer(device.getUser());
            gcmPushNotificationStatus.setDeviceId(device.getDeviceId());
            gcmPushNotificationStatus.setNotification(notification);
            gcmPushNotificationStatus.setIsPushed(false);
            gcmPushNotificationStatusDao.save(gcmPushNotificationStatus);
            result.add(gcmPushNotificationStatus);
        }

        return result;*/
        return null;
    }

    /**
     * Позволяет сохранить в бд список статусов push уведомлений, связанных с сообщением в чате и его получателем.
     * @param userId получатель сообщения
     * @param chatMessage сообщение в чат
     * @return список сохраненных в БД объектов класса GcmPushNotificationStatus
     */
    private List<GcmPushNotificationStatus> generateStatusesFromChatMessage(
            Long userId, ChatMessage chatMessage) {
        List<GcmPushNotificationStatus> result = new ArrayList<>();

        List<GcmDevice> devices = gcmDeviceRepository.findAllByUserId(userId);

        for (GcmDevice device : devices) {
            GcmPushNotificationStatus gcmPushNotificationStatus = new GcmPushNotificationStatus();
            gcmPushNotificationStatus.setSharer(sharerDao.getById(userId));
            gcmPushNotificationStatus.setDeviceId(device.getDeviceId());
            gcmPushNotificationStatus.setChatMessage(chatMessage);
            gcmPushNotificationStatus.setIsPushed(false);
            gcmPushNotificationStatusDao.save(gcmPushNotificationStatus);
            result.add(gcmPushNotificationStatus);
        }

        return result;
    }

    /**
     * Позволяет получить список зарегистрированных идентификаторов устройств пользователя
     * @param userId пользователь
     * @return список зарегистрированных идентификаторов устройств пользователя
     */
    private List<String> getDeviceIdsBySharer(Long userId) {
        List<String> result = new ArrayList<>();

        List<GcmDevice> devices = gcmDeviceRepository.findAllByUserId(userId);

        for (GcmDevice device : devices) {
            result.add(device.getDeviceId());
        }

        return result;
    }
}

package ru.radom.kabinet.web.notifications;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.data.jpa.repositories.notifications.GcmDeviceRepository;
import ru.radom.kabinet.dao.notifications.GcmPushNotificationStatusDao;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.notification.GcmService;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.utils.JsonUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Контроллер для обработки запросов, свзанных с системой Push уведомлений
 */
@Controller
public class PushController {

    private final String gcmProjectIdKey = "gcm.projectId";

    private final Logger logger = LoggerFactory.getLogger(PushController.class);

    @Autowired
    private GcmService gcmService;

    @Autowired
    private GcmDeviceRepository gcmDeviceRepository;

    @Autowired
    private GcmPushNotificationStatusDao gcmPushNotificationStatusDao;

    @Autowired
    private SettingsManager settingsManager;

    @RequestMapping(value = "/push/gcm/register.json", method = RequestMethod.POST)
    public
    @ResponseBody
    String gcmRegister(@RequestParam(value = "deviceId", required = true) String deviceId) {
        try {
            gcmService.registerDeviceId(SecurityUtils.getUser().getId(), deviceId);
            //Успешно зарегистрировались
            return JsonUtils.getSuccessJson().toString();
        } catch (Exception e) {
            logger.warn("Exception when processed device registration with id " + deviceId +
                    " for user with id " + SecurityUtils.getUser().getId() + "/n" +
                    ExceptionUtils.getStackTrace(e));

            return JsonUtils.getErrorJson().toString();

        }
    }


    /**
     * Отправляет на сторону клиента Json с информацией о последнем непрочитанном уведомлении
     * для указанного устройства либо с ошибкой, если такого нет или произошло исключение
     * @param deviceId
     * @return
     */
    @RequestMapping(value = "/push/gcm/last.json", method = RequestMethod.GET)
    public
    @ResponseBody
    String lastUnreadNotificationForPush(@RequestParam(value = "deviceId", required = true) String deviceId) {

        try {
            JSONObject jsonObject = gcmService.getLastNotShowedNotificationAsJsonByDeviceId(deviceId);
            return jsonObject.toString();
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return JsonUtils.getErrorJson().toString();
        }
    }

    /**
     * Отмечает все статусы, связанные с исходном уведомлением, флагом isPushed
     * Отрабатывает в двух случаях:
     *  1. Указан notificationId;
     *  2. Указан chatMessageId и sharerId.
     *  В остальных случаях не несет полезной функциональности
     * @param notificationId идентификатор исходного уведомления для push рассылки
     * @param chatMessageId идентификатор исходного сообщения в чат для push рассылки
     * @param sharerId идентификатор получателя сообщения в чат
     * @return
     */
    @RequestMapping(value = "/push/gcm/read", method = RequestMethod.POST)
    public @ResponseBody String setGcmPushNotificationStatusesRead(
            @RequestParam(value="notificationId") Long notificationId,
            @RequestParam(value="chatMessageId") Long chatMessageId,
            @RequestParam(value="sharerId") Long sharerId) {

        if (notificationId != null) {
            gcmPushNotificationStatusDao.setIsPushedForAllByNotificationId(notificationId);
        } else if (chatMessageId != null & sharerId != null) {
            gcmPushNotificationStatusDao.setIsPushedForAllByChatMessageIdAndSharerId(chatMessageId, sharerId);
        }

        return JsonUtils.getSuccessJson().toString();
    }

    /**
     * Генерирует js файл с описанием функционала service-worker'а
     * Метод нужен, чтобы добавить необходимые заголовки в ответ на запрос ресурса.
     * Также в файл вносится доменное имя приложения для дальнейшей работы с ним на стороне клиента.
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/push/gcm/pushWorker", method = RequestMethod.GET)
    public void generateGcmPushWorker(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {
            //<-----HEADERS------>

            //нужен, чтобы позволить service-worker'у следить за всеми окнами с приложением
            response.addHeader("Service-Worker-Allowed", "/");
            response.addHeader("Content-Type", "application/x-javascript; charset=utf-8 ");
            //отключаем кэширование
            response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.addHeader("Pragma", "no-cache");
            response.addHeader("Expires", "0");


            //<-----CONTENT------>

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            //Получаем .js файл, форматируем и отправляем на вывод.
            File pushWorkerJs = new File(request.getServletContext().getRealPath("/js/notifications/gcm-service-worker.js"));
            String content = new String(Files.readAllBytes(pushWorkerJs.toPath()), StandardCharsets.UTF_8);

            content = String.format(content, settingsManager.getSystemSetting("application.url"));
            out.write(content.getBytes());

            response.getOutputStream().write(out.toByteArray());
            response.getOutputStream().flush();
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }


    /**
     * Генерирует manifest файл с информаций, необходимой для подписки клиента на gcm
     */
    @RequestMapping(value = "/push/gcm/manifest.json", method = RequestMethod.GET)
    public void generateGcmManifest(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String gcmProjectId = settingsManager.getSystemSetting(gcmProjectIdKey);

        if (gcmProjectId == null || gcmProjectId.isEmpty()) {
            logger.error("Необходима системная настройка gcm.projectId");
            throw new RuntimeException("Необходима системная настройка gcm.projectId");
        }

        //<-----CONTENT------>
        response.addHeader("Content-Type", "application/json; charset=utf-8 ");

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        //Получаем .js файл, форматируем и отправляем на вывод.
        File pushWorkerJs = new File(request.getServletContext().getRealPath("/json/manifest.json"));
        String content = new String(Files.readAllBytes(pushWorkerJs.toPath()), StandardCharsets.UTF_8);
        content = String.format(content, gcmProjectId);
        out.write(content.getBytes());

        response.getOutputStream().write(out.toByteArray());
        response.getOutputStream().flush();
    }
}

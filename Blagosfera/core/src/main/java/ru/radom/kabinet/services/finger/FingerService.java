package ru.radom.kabinet.services.finger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.services.notification.SmsService;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.crypt.domain.HttpException;
import ru.askor.blagosfera.crypt.tls.HTTP;
import ru.askor.blagosfera.crypt.tls.HttpFile;
import ru.askor.blagosfera.crypt.tls.Response;
import ru.askor.blagosfera.data.jpa.repositories.RameraTextsRepository;
import ru.askor.blagosfera.domain.notification.sms.SmsNotification;
import ru.askor.blagosfera.domain.notification.sms.SmsNotificationType;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.bio.FingerTokenDao;
import ru.radom.kabinet.dao.settings.SharerSettingDao;
import ru.radom.kabinet.model.RameraTextEntity;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.bio.FingerToken;
import ru.radom.kabinet.model.bio.TokenStatus;
import ru.radom.kabinet.util.MapsUtils;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Stream;

@Transactional(noRollbackFor = FingerException.class)
@Service("fingerService")
public class FingerService {

    private static final String FINGERILLA_SERVER_URL = "http://172.18.3.18/FingerillaWeb/";
    private static final String FINGERILLA_API_KEY = "caa4e6ef7ad92bb9e735d52ca0f285aa";
    private static final int FINGERILLA_SCORES_TRESHOLD = 75;
    private static final String FINGER_LIST_KEY = "bio.finger.exists";

    private static final String FINGER_PRIORITY_KEY = "bio.finger.priority";
    private static final String FINGER_PRIORITY_DEFAULT_VAL = "10,10,10,10,10,10,10,10,10,10";
    private static final String FINGER_PRIORITY_DESC = "Вероятности выбора пальца для сканирования, %";

    public static final Map<String, String> FINGERILLA_ERRORS = MapsUtils.unmodifiableMap(Stream.of(
            MapsUtils.entry("Error 1004", "Сервер проверки отпечатков недоступен"), // wrong ip
            MapsUtils.entry("Error 1007", "Для Вашего ЛИК не предоставлено эталонное изображение в базу данных отпечатков."),
            MapsUtils.entry("Error 1008", "Ваш ЛИК не содержится в базе данных отпечатков."),
            MapsUtils.entry("Error 1012", "Ошибка передачи данных на сервер авторизации для сверки биометрических параметров."),
            MapsUtils.entry("Error 1021", "Вы не можете добавить один и тот же палец несколько раз."),
            MapsUtils.entry("Error 1023", "Неверный ЛИК регистратора."),
            MapsUtils.entry("Error 1099", "Не удалось сохранить отпечаток. Скорее всего Вы отсканировали не тот палец."),
            MapsUtils.entry("ERROR=LOW_QUALITY", "Низкое качество отпечатка."),
            MapsUtils.entry("ERROR=BUILD_TEMPLATE_FAILED", "Ошибка обработки отпечатка пальца. Попробуйте снова."),
            MapsUtils.entry("ERROR=LOW_MEM", "Не хватает памяти для обработки отпечатка пальца. Обратитесь к администратору."),
            MapsUtils.entry("ERROR=INVALID_DATA", "Ошибочные данные. Попробуйте снова."),
            MapsUtils.entry("ERROR=MAX_BUF_OVERFLOW", "Не хватает памяти для обработки отпечатка пальца. Обратитесь к администратору."),
            MapsUtils.entry("ERROR=FAILED_READ_IMAGE", "Ошибка чтения изображения."),
            MapsUtils.entry("ERROR=FAILED_ALLOCATE_NEW_TEMPLATE", "Ошибка создания шаблона."),
            MapsUtils.entry("ERROR=FAILED_GET_COMBINED_TEMPLATE", "Ошибка создания шаблона."),
            MapsUtils.entry("ERROR=FAILED_FREE_TEMPLATE", "Ошибка создания шаблона."),
            MapsUtils.entry("ERROR=FAILED_RESET_MERGE", "Ошибка создания шаблона."),
            MapsUtils.entry("ERROR=FAILED_OPEN_NIST_DATA_FILE", "Ошибка создания шаблона."),
            MapsUtils.entry("ERROR=FAILED_ADD_IMAGE_TO_TEMPLATE", "Ошибка создания шаблона."),
            MapsUtils.entry("ERROR=IKP not exists in FingerillaDB", "Указанный ЛИК не найден.")));

    @Autowired
    private FingerTokenDao fingerTokenDao;

    @Autowired
    private SharerSettingDao sharerSettingDao;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private SmsService smsService;

    @Autowired
    private RameraTextsRepository rameraTextsRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(FingerService.class);

    @Value("${skip.finger.check}")
    private boolean skipFingerCheck;

    private void checkFinger(String ikp, byte[] file, int finger) {
        List<HttpFile> files = new ArrayList<>();
        files.add(new HttpFile("file", "image.bmp", file));

        Map<String, String> parameters = new HashMap<>();
        parameters.put("action", "match");
        parameters.put("type", "bmp");
        parameters.put("ikp", ikp);
        parameters.put("api_key", FINGERILLA_API_KEY);
        parameters.put("finger", String.valueOf(finger));

        try {
            Response httpResponse = new HTTP().doPostMultipart(FINGERILLA_SERVER_URL, files, parameters, null);

            if (httpResponse.getStatus() != 200) {
                throw new FingerException("Ошибка обращения к серверу проверки отпечатков");
            }

            String response = httpResponse.getDataAsString().trim();
            checkForError(response);

            if (response.startsWith("SCORE=")) {
                if (Integer.parseInt(response.replace("SCORE=", "")) < FINGERILLA_SCORES_TRESHOLD) {
                    throw new FingerException("Отпечаток не соответствует Вашему ЛИК");
                }
            } else throw new FingerException("Неизвестная ошибка проверки отпечатка");
        } catch (HttpException e) {
            LOGGER.error(e.getMessage());
            throw new FingerException("Сервер проверки отпечатков недоступен");
        }
    }

    public String saveFinger(String registratorIkp, String ikp, String finger, byte[] file) {
        List<HttpFile> files = new ArrayList<>();
        files.add(new HttpFile("file", "finger" + finger + ".fpd", file));

        Map<String, String> parameters = new HashMap<>();
        parameters.put("action", "create");
        parameters.put("type", "bmp");
        parameters.put("ikp", ikp);
        parameters.put("registrator_ikp", registratorIkp);
        parameters.put("api_key", FINGERILLA_API_KEY);
        parameters.put("finger", finger);

        try {
            Response httpResponse = new HTTP().doPostMultipart(FINGERILLA_SERVER_URL, files, parameters, null);

            if (httpResponse.getStatus() != 200) {
                throw new FingerException("Ошибка обращения к серверу проверки отпечатков");
            }

            String response = httpResponse.getDataAsString().trim();
            checkForError(response);

            if (response.equals("SUCCESS")) {
                UserEntity userEntity = sharerDao.getByIkp(ikp);
                List<Integer> exists = sharerSettingDao.getIntegersList(userEntity, FINGER_LIST_KEY, new ArrayList<>());
                exists.add(Integer.parseInt(finger));
                sharerSettingDao.set(userEntity, FINGER_LIST_KEY, Joiner.on(",").join(exists));
            } else if (response.startsWith("MORE:")) {
                response = response.substring(5);
            } else throw new FingerException("Неизвестная ошибка проверки отпечатка");

            return response;
        } catch (HttpException e) {
            LOGGER.error(e.getMessage(), e);
            throw new FingerException("Сервер проверки отпечатков недоступен");
        }
    }

    public FingerToken initToken(Long userId, String ip) {
        FingerToken token;
        String mode = settingsManager.getUserSetting("identification_mode", userId);

        if ("sms".equals(mode)) {
            token = fingerTokenDao.getActiveByUserIdAndIp(userId, ip);

            if (token != null) {
                int timeout = settingsManager.getSystemSettingAsInt("mobile_phone.verification_timeout", 300);
                if (!DateUtils.isOlderThan(token.getInitDate(), Calendar.SECOND, timeout)) return token;
            }
        }

        token = new FingerToken();
        token.setUser(sharerDao.getById(userId));
        token.setStatus(TokenStatus.INIT);
        token.setInitDate(new Date());
        token.setRequestId(StringUtils.randomString(256));
        token.setIp(ip);
        token.setFinger(nextFinger(userId));
        token.setSmsCode(StringUtils.randomNumericString());
        fingerTokenDao.save(token);

        if ("sms".equals(mode)) {
            String phoneNumber = settingsManager.getUserSetting("mobile_phone.number", userId);

            if (phoneNumber != null) {
                RameraTextEntity messageEntity = rameraTextsRepository.findOneByCode("SMS_VERIFICATION_CODE");

                if (messageEntity != null) {
                    String text = messageEntity.getText().replaceAll("%code%", token.getSmsCode());

                    try {
                        String number = phoneNumber.trim().replaceAll(" ", "").replaceAll("-", "");
                        text = smsService.send(new SmsNotification(SmsNotificationType.SMS, number, text));
                    } catch (JsonProcessingException | NoSuchAlgorithmException | HttpException | UnsupportedEncodingException ignored) {
                    }
                }
            }
        }

        return token;
    }

    public FingerToken getToken(String ikp, String requestId, byte[] file) {
        FingerToken token = fingerTokenDao.get(ikp, requestId);

        if (token == null) throw new FingerException("Токен не найден");
        if (!token.getStatus().equals(TokenStatus.INIT)) throw new FingerException("Неверный статус токена");

        int timeout = settingsManager.getSystemSettingAsInt("mobile_phone.verification_timeout", 300);

        if (DateUtils.isOlderThan(token.getInitDate(), Calendar.SECOND, timeout)) {
            token.setStatus(TokenStatus.EXPIRED);
            fingerTokenDao.update(token);
            throw new FingerException("Временный код запроса устарел");
        }

        if (!skipFingerCheck) checkFinger(ikp, file, token.getFinger());

        token.setGetDate(new Date());
        token.setStatus(TokenStatus.SENT);
        token.setValue(StringUtils.randomString(256));
        fingerTokenDao.update(token);
        return token;
    }

    public int getSecondsLeft(FingerToken token) {
        int timeout = settingsManager.getSystemSettingAsInt("mobile_phone.verification_timeout", 300);
        if (DateUtils.isOlderThan(token.getInitDate(), Calendar.SECOND, timeout)) return 0;
        return timeout - DateUtils.getDistanceSeconds(token.getInitDate(), new Date());
    }

    public FingerToken getToken(String ikp, String requestId, String verificationCode) {
        FingerToken token = fingerTokenDao.get(ikp, requestId);

        if (token == null) throw new FingerException("Токен не найден");
        if (!token.getStatus().equals(TokenStatus.INIT)) throw new FingerException("Неверный статус токена");

        int timeout = settingsManager.getSystemSettingAsInt("mobile_phone.verification_timeout", 300);

        if (DateUtils.isOlderThan(token.getInitDate(), Calendar.SECOND, timeout)) {
            token.setStatus(TokenStatus.EXPIRED);
            fingerTokenDao.update(token);
            throw new FingerException("Временный код запроса устарел");
        }

        if (token.getSmsCode() == null) throw new FingerException("Неверный код подтверждения");

        if (!token.getSmsCode().equals(verificationCode)) {
            token.setStatus(TokenStatus.EXPIRED);
            fingerTokenDao.update(token);
            throw new FingerException("Неверный код подтверждения");
        }

        token.setGetDate(new Date());
        token.setStatus(TokenStatus.SENT);
        token.setValue(StringUtils.randomString(256));
        fingerTokenDao.update(token);
        return token;
    }

    public FingerToken useToken(User user, String value) {
        if (user == null) throw new FingerException("Участник не существует");

        FingerToken token = fingerTokenDao.get(user.getId(), value);

        if (token == null) throw new FingerException("Токен не найден");
        if (!token.getStatus().equals(TokenStatus.SENT)) throw new FingerException("Неверный статус токена");

        if (DateUtils.isOlderThan(token.getGetDate(), Calendar.SECOND, 600)) {
            token.setStatus(TokenStatus.EXPIRED);
            fingerTokenDao.update(token);
            throw new FingerException("Токен устарел");
        }

        token.setStatus(TokenStatus.USED);
        fingerTokenDao.update(token);
        return token;
    }

    public void deleteFinger(String ikp, Integer finger, String registratorIkp) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("action", "delete");
        parameters.put("ikp", ikp);
        parameters.put("registrator_ikp", registratorIkp);
        parameters.put("api_key", FINGERILLA_API_KEY);
        parameters.put("finger", finger.toString());

        try {
            Response response = new HTTP().doPost(FINGERILLA_SERVER_URL, parameters, null);

            if (response.getStatus() != 200) {
                throw new FingerException("Ошибка обращения к серверу проверки отпечатков, статус " + String.valueOf(response.getStatus()));
            }

            if (response.getDataAsString().equals("SUCCESS")) {
                UserEntity userEntity = sharerDao.getByIkp(ikp);
                List<Integer> exists = sharerSettingDao.getIntegersList(userEntity, FINGER_LIST_KEY, Collections.EMPTY_LIST);
                while (exists.contains(finger)) exists.remove(finger);
                sharerSettingDao.set(userEntity, FINGER_LIST_KEY, Joiner.on(",").join(exists));
            } else {
                throw new FingerException("Ошибка обработки ответа сервера проверки отпечатков");
            }
        } catch (HttpException e) {
            LOGGER.error(e.getMessage());
            throw new FingerException("Сервер проверки отпечатков недоступен");
        }
    }

    private int nextFinger(Long userId) {
        int[] fingers = getExistingFingers(userId);
        int[] priorities = getFingersPriority();
        if (fingers.length == 0) return 0;
        if (priorities.length == 0) return 0;

        int finger = 0;
        double[] fingersPriorities = new double[fingers.length];
        double scale;
        double prioritiesSum = 0.0;

        for (int i = 0; i < fingers.length; i++) {
            fingersPriorities[i] = priorities[fingers[i] - 1];
            prioritiesSum += fingersPriorities[i];
        }

        scale = 100.0 / prioritiesSum;

        for (int i = 0; i < fingersPriorities.length; i++) {
            fingersPriorities[i] = (int) (fingersPriorities[i] * scale);
        }

        double randomNumber = (double) (((int) (Math.random() * 100)) + 1);
        double maximum = 0.0;

        for (int i = 0; i < fingers[i]; i++) {
            maximum += fingersPriorities[i];

            if (randomNumber <= maximum) {
                finger = fingers[i];
                break;
            }
        }

        return finger;
    }

    private int[] getFingersPriority() {
        String fingersPriority = settingsManager.getSystemSetting(FINGER_PRIORITY_KEY);

        if (fingersPriority == null || fingersPriority.isEmpty()) {
            fingersPriority = settingsManager.setSystemSetting(FINGER_PRIORITY_KEY, FINGER_PRIORITY_DEFAULT_VAL, FINGER_PRIORITY_DESC);
        }

        int[] priorities = new int[10];
        int i = 0;

        for (String priority : fingersPriority.split(",")) {
            priorities[i] = Integer.parseInt(priority);
            i++;
        }

        return priorities;
    }

    private int[] getExistingFingers(Long userId) {
        String fingersSetting = settingsManager.getUserSetting(FINGER_LIST_KEY, userId);

        if (fingersSetting == null || fingersSetting.isEmpty()) {
            return new int[0];
        }

        String[] existingFingers = fingersSetting.split(",");
        int[] fingers = new int[existingFingers.length];
        int i = 0;

        for (String existingFinger : existingFingers) {
            fingers[i] = Integer.parseInt(existingFinger);
            i++;
        }

        return fingers;
    }

    private void checkForError(String response) {
        String error = FINGERILLA_ERRORS.get(response);
        if (error != null) throw new FingerException(error);
        if (response.startsWith("Error ") || response.startsWith("ERROR=")) throw new FingerException("Неизвестная ошибка проверки отпечатка");
    }
}

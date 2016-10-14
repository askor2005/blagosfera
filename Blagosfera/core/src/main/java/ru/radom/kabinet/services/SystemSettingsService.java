package ru.radom.kabinet.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.domain.settings.SystemSetting;
import ru.radom.blagosferabp.activiti.BPMBlagosferaUtils;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Otts Alexey on 26.11.2015.<br/>
 * Сервис для работы с SystemSettingEntity
 */
@Service
public class SystemSettingsService {

    /**
     * Шаблон для проверки значения на целочисленность
     */
    private final Pattern integerPattern = Pattern.compile("[\\d\\s]+");

    /**
     * Шаблон для проверки, что значение дробное число
     */
    private final Pattern decimalPattern = Pattern.compile("[\\d\\s]+\\.[\\d\\s]*");


    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Получить системные атрибуты по ключам, ожидаем одиночный ключ или их список
     */
    // -TODO Переделать на BPMHandler
    /*@Transactional(readOnly = true)
    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "core.system.settings.get", durable = "true"),
        exchange = @Exchange(value = "task-exchange", durable = "true"),
        key = "core.system.settings.get"
    ))
    public void getSystemSettingsWorker(Message message) {
        BPMBlagosferaUtils.commonRabbitTaskExecutorWithConverter(rabbitTemplate, message, (Object data) -> {
            if(data instanceof String) {
                String key = (String) data;
                HashMap<String, Object> res = new HashMap<>(1);
                res.put(key, tryConvert(settingsManager.getSystemSetting(key)));
                return res;
            } else {
                Collection<String> keys;
                if(data instanceof Collection) {
                    keys = (Collection<String>) data;
                } else if(data instanceof Map) {
                    keys = ((Map<String, ?>)data).keySet();
                } else {
                    return new HashMap<>(0);
                }
                return getByKeysWithSmartConversion(keys);
            }
        });
    }*/

    /**
     * Получить сразу много ключей. При этом произведется конвертирование в подходящий формат данных.
     * Если какого то ключа нет в настройках, то в этот ключ будет записан null
     */
    public Map<String, Object> getByKeysWithSmartConversion(Collection<String> keys) {
        if(CollectionUtils.isEmpty(keys)) {
            return Collections.emptyMap();
        }
        List<String> filteredKeys = keys.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if(filteredKeys.isEmpty()) {
            return Collections.emptyMap();
        }
        List<SystemSetting> settings = settingsManager.getSystemSettings(filteredKeys);
        Map<String, Object>  res = new HashMap<>();
        for (SystemSetting setting : settings) {
            filteredKeys.remove(setting.getKey());
            res.put(setting.getKey(), tryConvert(setting.getValue()));
        }
        for (String key : filteredKeys) {
            res.put(key, null);
        }
        return res;
    }

    /**
     * Пытаемся конвертировать значение
     * @return
     * <ul>
     *     <li>Map&lt;String, Object&gt;</code> - если значение вылидный json объект</li>
     *     <li>List&lt;Object&gt; - если значение является перечислением простых объектов через запятую</li>
     *     <li>Long - если число целочисленное</li>
     *     <li>Double - если в числе есть одна '.'</li>
     *     <li>String - если ничего не удалось сделать со значением</li>
     * </ul>
     */
    private Object tryConvert(String value) {
        String trimmed = value.trim();
        if(trimmed.startsWith("{") && trimmed.endsWith("}")) {
            try {
                return objectMapper.readValue(trimmed, Map.class);
            } catch (IOException e) {
                return value;
            }
        } else if(trimmed.contains("{")) { //какой то сложный объект, не трогаем его
            return value;
        } else if(trimmed.contains(",")) { //список простых объектов
            String[] splited = value.split(",");
            List<Object> values = new ArrayList<>(splited.length);
            for (String s : splited) {
                values.add(tryConvertSimpleType(s));
            }
            return values;
        } else {
            return tryConvertSimpleType(value);
        }
    }

    /**
     * Пытаемся конвертировать значение
     * @return
     * <ul>
     *     <li>Long - если число целочисленное</li>
     *     <li>Double - если в числе есть одна '.'</li>
     *     <li>String - если ничего не удалось сделать со значением</li>
     * </ul>
     */
    private Object tryConvertSimpleType(String value) {
        Matcher integerMatcher = integerPattern.matcher(value);
        if(integerMatcher.matches()) {
            try {
                return Long.parseLong(value.replaceAll("\\s", ""));
            } catch (NumberFormatException e) {
                return value;
            }
        }
        Matcher decimalMatcher = decimalPattern.matcher(value);
        if(decimalMatcher.matches()) {
            try {
                return Double.parseDouble(value.replaceAll("\\s", ""));
            } catch (NumberFormatException e) {
                return value;
            }
        }
        return value;
    }

    /**
     * Позволяет получить url приложения с учетом протокола и домена из системных настроек
     * @return url, получившийся из соединения протокола и домена, либо значение application.url,
     *         если отсутствуют вышеуказанные системные параметры.
     */
    public String getApplicationUrl() {

        String protocol = settingsManager.getSystemSetting("application.protocol");
        String domain = settingsManager.getSystemSetting("application.domain");

        if (protocol == null || domain == null || protocol.isEmpty() || domain.isEmpty()) {
            return settingsManager.getSystemSetting("application.url");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(protocol);
        sb.append("://");
        sb.append(domain);

        return sb.toString();
    }
}

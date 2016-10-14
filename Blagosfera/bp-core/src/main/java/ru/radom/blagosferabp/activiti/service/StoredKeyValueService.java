package ru.radom.blagosferabp.activiti.service;

import ru.radom.blagosferabp.activiti.dto.StoredKeyValueDTO;
import ru.radom.blagosferabp.activiti.model.StoredKeyValue;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Otts Alexey on 20.11.2015.<br/>
 * Сервис для работы {@link StoredKeyValue}
 */
public interface StoredKeyValueService {

    /**
     * Обновить значения в хранилище
     */
    List<StoredKeyValue> updateValues(Map<String, ?> values);

    /**
     * Получить данные по ключам
     */
    List<StoredKeyValueDTO> getValues(Iterable<String> keys);

    /**
     * Получить данные не младше даты
     */
    List<StoredKeyValueDTO> getValues(Date date);

    /**
     * Получить данные по ключам не младше даты
     */
    List<StoredKeyValueDTO> getValues(Iterable<String> keys, Date date);

    /**
     * Очистить все переданные ключи
     */
    void clearAll(Iterable<String> keys);
}

package ru.radom.blagosferabp.activiti.service.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.FastOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.blagosferabp.activiti.dao.StoredKeyValueDAO;
import ru.radom.blagosferabp.activiti.dto.StoredKeyValueDTO;
import ru.radom.blagosferabp.activiti.dto.util.StoredKeyValueConverter;
import ru.radom.blagosferabp.activiti.model.StoredKeyValue;
import ru.radom.blagosferabp.activiti.service.StoredKeyValueService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Otts Alexey on 20.11.2015.<br/>
 * Реализация для {@link StoredKeyValueService}
 */
@Service
public class StoredKeyValueServiceImpl implements StoredKeyValueService {

    /**
     * Минимальный размер массива для хранения сериализованного объекта
     */
    private final int MIN_SIZE = 128;

    /**
     * Максимальный размер массива для хранения сериализованного объекта
     */
    private final int MAX_SIZE = 1048576;

    @Autowired
    private StoredKeyValueDAO storedKeyValueDAO;

    @Override
    @Transactional
    public List<StoredKeyValue> updateValues(Map<String, ?> values) {
        Kryo kryo = new Kryo();
        List<StoredKeyValue> existedKeys = storedKeyValueDAO.getByKeysForUpdate(values.keySet());
        for (StoredKeyValue existedKey : existedKeys) {
            Object value = values.remove(existedKey.getKey());
            FastOutput fastOutput = new FastOutput(MIN_SIZE, MAX_SIZE);
            kryo.writeClassAndObject(fastOutput, value);
            existedKey.setLastModified(new Date());
            existedKey.setValue(fastOutput.getBuffer());
        }

        List<StoredKeyValue> res = new ArrayList<>(existedKeys);
        for (Map.Entry<String, ?> entry : values.entrySet()) {
            StoredKeyValue keyValue = new StoredKeyValue();
            keyValue.setKey(entry.getKey());
            FastOutput fastOutput = new FastOutput(MIN_SIZE, MAX_SIZE);
            kryo.writeClassAndObject(fastOutput, entry.getValue());
            keyValue.setLastModified(new Date());
            keyValue.setValue(fastOutput.getBuffer());
            storedKeyValueDAO.save(keyValue);
            res.add(keyValue);
        }
        storedKeyValueDAO.flush();
        return res;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoredKeyValueDTO> getValues(Iterable<String> keys) {
        List<StoredKeyValue> values = storedKeyValueDAO.findAll(keys);
        return convert(values);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoredKeyValueDTO> getValues(Date date) {
        List<StoredKeyValue> values = storedKeyValueDAO.getOlderThan(date);
        return convert(values);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoredKeyValueDTO> getValues(Iterable<String> keys, Date date) {
        List<StoredKeyValue> values = storedKeyValueDAO.getAllOlderThan(keys, date);
        return convert(values);
    }

    private List<StoredKeyValueDTO> convert(List<StoredKeyValue> values) {
        StoredKeyValueConverter converter = new StoredKeyValueConverter();
        return values.stream().map(converter::convert).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void clearAll(Iterable<String> keys) {
        for (String key : keys) {
            storedKeyValueDAO.delete(key);
        }
        storedKeyValueDAO.flush();
    }
}

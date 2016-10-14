package ru.radom.kabinet.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 22.12.2015.
 */
@Service
public class SerializeService {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     *
     * @param object
     * @return
     */
    public <T> T toPrimitiveObject(Object object) {
        TypeReference mapRef;
        if (object instanceof Collection) {
            mapRef = new TypeReference<List<Object>>() {};
        } else {
            mapRef = new TypeReference<Map<String, Object>>() {};
        }
        return objectMapper.convertValue(object, mapRef);
    }

    /**
     * Сконвертировать объект в Map
     * @param object
     * @return
     */
    public Map<String, Object> toMap(Object object) {
        TypeReference mapRef = new TypeReference<Map<String, Object>>() {};
        return objectMapper.convertValue(object, mapRef);
    }

    /**
     * Сконвертировать Map в объект
     * @param fromMap
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T toObject(Map<String, Object> fromMap, Class<T> clazz) {
        return objectMapper.convertValue(fromMap, clazz);
    }

    /**
     *
     * @param json
     * @param defaultValue
     * @param <T>
     * @return
     */
    public <T> T toObject(String json, Class<T> clazz, T defaultValue) {
        T result;
        try {
            result = objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            result = defaultValue;
        }
        return result;
    }

    /**
     *
     * @param object
     * @return
     */
    public String toJson(Object object) {
        String result = null;
        try {
            result = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            ExceptionUtils.check(true, e.getMessage());
        }
        return result;
    }

    /**
     *
     * @param json
     * @return
     */
    public Map<String, Object> jsonToMap(String json) {
        Map<String, Object> result = null;
        try {
            result = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            ExceptionUtils.check(true, e.getMessage());
        }
        return result;
    }
}

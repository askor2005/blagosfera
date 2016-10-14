package ru.askor.blagosfera.core.util.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * Created by Maxim Nikitin on 25.03.2016.
 */
class JsonSerializer implements Serializer {

    private static final byte[] EMPTY_ARRAY = new byte[0];
    private final ObjectMapper mapper;

    JsonSerializer() {
        mapper = new ObjectMapper();
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE));
    }

    @Override
    public byte[] serialize(Object source) throws SerializationException {
        if (source == null) {
            return EMPTY_ARRAY;
        } else {
            try {
                return this.mapper.writeValueAsBytes(source);
            } catch (JsonProcessingException e) {
                throw new SerializationException("Could not write JSON: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public Object deserialize(byte[] source) throws SerializationException {
        if (source == null || source.length == 0) {
            return null;
        } else {
            try {
                return this.mapper.readValue(source, Object.class);
            } catch (Exception e) {
                throw new SerializationException("Could not read JSON: " + e.getMessage(), e);
            }
        }
    }
}

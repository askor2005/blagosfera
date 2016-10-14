package ru.askor.blagosfera.core.util.cache;

import org.springframework.data.redis.serializer.SerializationException;

/**
 * Created by Maxim Nikitin on 25.03.2016.
 */
public class RedisSerializer implements org.springframework.data.redis.serializer.RedisSerializer<Object> {

    private Serializer serializer;

    public RedisSerializer() {
        this(false);
    }

    public RedisSerializer(boolean useJson) {
        if (useJson)
            serializer = new JsonSerializer();
        else
            serializer = new FstSerializer();
    }

    @Override
    public byte[] serialize(Object source) throws SerializationException {
        return serializer.serialize(source);
    }

    @Override
    public Object deserialize(byte[] source) throws SerializationException {
        return serializer.deserialize(source);
    }
}

package ru.askor.blagosfera.core.util.cache;

import org.springframework.data.redis.serializer.SerializationException;

/**
 * Created by Maxim Nikitin on 25.03.2016.
 */
public interface Serializer {

    byte[] serialize(Object source) throws SerializationException;

    Object deserialize(byte[] source) throws SerializationException;
}

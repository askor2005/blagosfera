package ru.askor.blagosfera.core.util.cache;

//import org.nustaq.serialization.FSTConfiguration;
import de.ruedigermoeller.serialization.FSTConfiguration;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.Serializable;

/**
 * Created by Maxim Nikitin on 25.03.2016.
 */
class FstSerializer implements Serializer {

    private static FSTConfiguration conf;

    FstSerializer() {
        conf = FSTConfiguration.createDefaultConfiguration();
    }

    @Override
    public byte[] serialize(Object source) throws SerializationException {
        return conf.asByteArray((Serializable) source);
    }

    @Override
    public Object deserialize(byte[] source) throws SerializationException {
        return conf.asObject(source);
    }
}

package ru.radom.kabinet.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ru.radom.kabinet.utils.VarUtils;

import java.io.IOException;
import java.util.Date;

/**
 * Десириалайзер для jackson из строки в Date переменную
 * Created by vgusev on 19.12.2015.
 */
public class TimeStampDateDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return new Date(VarUtils.getLong(jsonParser.getValueAsString(), 0l));
    }
}

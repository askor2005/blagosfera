package ru.radom.kabinet.services.bpmhandlers.community.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.utils.VarUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * Created by vgusev on 20.01.2016.
 */
public class StringByCommaToSetDeserializer extends JsonDeserializer<Set<Long>> {

    @Override
    public Set<Long> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String rawData = jsonParser.getValueAsString();
        Set<Long> result = null;
        if (!StringUtils.isBlank(rawData)) {
            result = new HashSet<>();
            String[] parts = rawData.split(",");
            for (String part : parts) {
                Long value = VarUtils.getLong(part, null);
                ExceptionUtils.check(value == null, part + " не является числом");
                result.add(value);
            }
        }
        return result;
    }
}

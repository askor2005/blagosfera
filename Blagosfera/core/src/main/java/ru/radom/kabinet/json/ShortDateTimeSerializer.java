package ru.radom.kabinet.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ru.radom.kabinet.utils.DateUtils;

import java.io.IOException;
import java.util.Date;

/**
 *
 * Created by vgusev on 09.03.2016.
 */
public class ShortDateTimeSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider arg2) throws IOException, JsonProcessingException {
        gen.writeString(DateUtils.formatDate(value, DateUtils.Format.DATE_TIME_SHORT));
    }
}
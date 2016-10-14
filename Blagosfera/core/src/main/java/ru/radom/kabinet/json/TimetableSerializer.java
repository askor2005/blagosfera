package ru.radom.kabinet.json;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.dto.Timetable;

/**
 * Created by rkorablin on 30.04.2015.
 */
@Component("timetableSerializer")
public class TimetableSerializer extends AbstractSerializer<Timetable> {
    @Override
    public JSONObject serializeInternal(Timetable object) {
        JSONObject jsonObject = new JSONObject();
        JSONArray combinedDays = new JSONArray();
        for (Timetable.Day day : object.getCombinedDays()) {
            combinedDays.put(serializeDay(day));
        }
        jsonObject.put("combinedDays", combinedDays);
        jsonObject.put("now", object.getNow());
        return jsonObject;
    }

    private JSONObject serializeDay(Timetable.Day day) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", day.getTitle());
        jsonObject.put("text", day.getText());
        return jsonObject;
    }
}

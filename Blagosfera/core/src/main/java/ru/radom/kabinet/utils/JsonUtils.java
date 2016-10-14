package ru.radom.kabinet.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class JsonUtils {

	private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

	public static Map<String, Object> toMap(JSONObject object) throws JSONException {
		Map<String, Object> map = new HashMap();
		Iterator keys = object.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			map.put(key, fromJson(object.get(key)));
		}
		return map;
	}

	public static List toList(JSONArray array) throws JSONException {
		List list = new ArrayList();
		for (int i = 0; i < array.length(); i++) {
			list.add(fromJson(array.get(i)));
		}
		return list;
	}

	private static Object fromJson(Object json) throws JSONException {
		if (json instanceof JSONObject) {
			return toMap((JSONObject) json);
		} else if (json instanceof JSONArray) {
			return toList((JSONArray) json);
		} else {
			return json;
		}
	}

	public static JSONObject getErrorJson(String message) {
		return getErrorJson(message, null);
	}

	public static JSONObject getErrorJson() {
		return getErrorJson(null, null);
	}
	
	public static JSONObject getErrorJson(String message, JSONObject errorsJson) {
		message = message == null ? "Произошла ошибка!" : message;
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result", "error");
		jsonObject.put("message", message);
		if (errorsJson != null) {
			jsonObject.put("errors", errorsJson);
		}
		return jsonObject;
	}

	public static JSONObject getSuccessJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result", "success");
		return jsonObject;
	}

	public static JSONObject getJson(String key, String value) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(key, value);
		return jsonObject;
	}

	public static String getString(JSONObject jsonObject, String key, String defaultValue) {
		try {
			return jsonObject.getString(key);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return defaultValue;
		}
	}
}

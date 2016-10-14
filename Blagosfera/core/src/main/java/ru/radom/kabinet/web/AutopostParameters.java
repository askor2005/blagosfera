package ru.radom.kabinet.web;

import java.util.HashMap;
import java.util.Map;

public class AutopostParameters {

	private Map<String, String> map;
	private String action;

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void put(String key, String value) {
		if (map == null) {
			map = new HashMap<String, String>();
		}
		map.put(key, value);
	}
	
}

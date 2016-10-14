package ru.radom.kabinet.services.communities;

import java.util.Map;

public class CommunityException extends RuntimeException {
	private Map<String, String> map;

	public CommunityException(String message) {
		super(message);
	}

	public CommunityException(String message, Map<String, String> map) {
		this(message);
		this.map = map;
	}

	public Map<String, String> getMap() {
		return map;
	}
}